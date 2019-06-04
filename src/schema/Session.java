package schema;

import exception.*;
import format.Cell;
import format.PrintFormat;
import query.*;
import type.ColumnType;
import type.ComparatorType;
import type.ComparerType;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static global.Global.*;

public class Session {
    private HashMap<String, Database> databases;
    private String current;
    private String user;
    private boolean skipCheck;

    public Session() {
        this.skipCheck = true;
        this.user = adminUserName;
        this.databases = new HashMap<>();
        recoverDatabases();
        createDatabaseIfNotExists(adminDatabaseName);
        this.current = adminDatabaseName;
        dirInit();
        if (!databases.get(current).tables.containsKey(authTableName)) {
            createTable(authTableName,
                    new Column[] { new Column("authority", ColumnType.INT, 0, true, -1),
                            new Column("username", ColumnType.STRING, 2, true, maxNameLength),
                            new Column("database_name", ColumnType.STRING, 2, true, maxNameLength),
                            new Column("table_name", ColumnType.STRING, 2, true, maxNameLength) });
        }
        if (!databases.get(current).tables.containsKey(userTableName)) {
            createTable(userTableName, new Column[] { new Column("username", ColumnType.STRING, 1, true, maxNameLength),
                    new Column("password", ColumnType.STRING, 0, true, maxNameLength) });
            createUser(adminUserName, getAdminPassword());
        }
        skipCheck = false;
    }

    public void addAuth(String username, String tableName, int level) {
        if (username.equals(adminUserName))
            return;
        Database database = databases.get(current);
        if (!database.tables.containsKey(tableName))
            throw new TableNotExistsException(tableName);
        String currentStash = current;
        current = adminDatabaseName;
        Table t = databases.get(adminDatabaseName).tables.get(userTableName);
        if (!t.contains(new Entry[] { new Entry(username) }))
            throw new UserNotExistException(username);
        int currentAuth = getAuth(username, currentStash, tableName);
        if (currentAuth < 0)
            insert(authTableName, new String[] { String.valueOf(level), toLiteral(username), toLiteral(currentStash),
                    toLiteral(tableName) }, null);
        else
            update(authTableName, "authority",
                    new Expression(new Comparer(ComparerType.NUMBER, String.valueOf(level | currentAuth))), null);
        current = currentStash;
    }

    public void removeAuth(String username, String tableName, int level) {
        if (username.equals(adminUserName))
            return;
        Database database = databases.get(current);
        if (!database.tables.containsKey(tableName))
            throw new TableNotExistsException(tableName);
        String currentStash = current;
        current = adminDatabaseName;
        Table t = databases.get(adminDatabaseName).tables.get(userTableName);
        if (!t.contains(new Entry[] { new Entry(username) }))
            throw new UserNotExistException(username);
        int currentAuth = getAuth(username, currentStash, tableName);
        if (currentAuth == 0)
            return;
        else
            update(authTableName, "authority",
                    new Expression(new Comparer(ComparerType.NUMBER, String.valueOf(~level & currentAuth))), null);
        current = currentStash;
    }

    private int getAuth(String username, String databaseName, String tableName) {
        Table t = databases.get(adminDatabaseName).tables.get(authTableName);
        try {
            Row row = t.get(new Entry[] { null, new Entry(username), new Entry(databaseName), new Entry(tableName) });
            ArrayList<Entry> entries = row.getEntries();
            return (int) entries.get(0).value;
        } catch (Exception e) {
            return -1;
        }
    }

    public void createUser(String username, String password) {
        if (!skipCheck && username.equals(adminUserName))
            throw new ReservedNameException(username);
        Table t = databases.get(adminDatabaseName).tables.get(userTableName);
        if (t.contains(new Entry[]{new Entry(username)}))
            throw new UserAlreadyExistsException(user);
        String currentStash = current;
        current = adminDatabaseName;
        insert(userTableName, new String[]{toLiteral(username), toLiteral(encrypt(password))}, null);
        current = currentStash;
    }

    public void dropUser(String username, boolean exists) {
        Table t = databases.get(adminDatabaseName).tables.get(userTableName);
        if (!t.contains(new Entry[] { new Entry(username) })) {
            if (exists)
                throw new UserNotExistException(username);
            else
                return;
        }
        String currentStash = current;
        current = adminDatabaseName;
        delete(userTableName, new Logic(new Condition(new Expression(new Comparer(ComparerType.COLUMN, "username")),
                new Expression(new Comparer(ComparerType.STRING, username)), ComparatorType.EQ)));
        delete(authTableName, new Logic(new Condition(new Expression(new Comparer(ComparerType.COLUMN, "username")),
                new Expression(new Comparer(ComparerType.STRING, username)), ComparatorType.EQ)));
        current = currentStash;
    }

    public boolean login(String username, String password) {
        Database database = databases.get(adminDatabaseName);
        try {
            Row row = database.tables.get(userTableName).get(new Entry[] { new Entry(username) });
            if (row.getEntries().get(1).value.equals(password)) {
                user = username;
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private String getAdminPassword() {
        FileReader reader;
        try {
            reader = new FileReader(adminPassWordFileName);
        } catch (Exception e) {
            throw new InternalException("failed to open configuration file.");
        }
        BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            return bufferedReader.readLine();
        } catch (Exception e) {
            throw new InternalException("failed to read from configuration file.");
        }
    }

    private void createDatabaseIfNotExists(String name) {
        if (!user.equals(adminUserName))
            throw new NoAuthorityException();
        if (!skipCheck && name.equals(authTableName))
            throw new ReservedNameException(name);
        if (databases.containsKey(name))
            return;
        createDatabase(name);
    }

    public void switchDatabase(String name) {
        if (!databases.containsKey(name))
            throw new DatabaseNotExistsException(name);
        current = name;
    }

    public void createDatabase(String name) {
        if (!user.equals(adminUserName))
            throw new NoAuthorityException();
        if (!skipCheck && name.equals(adminDatabaseName))
            throw new ReservedNameException(name);
        if (databases.containsKey(name))
            throw new DatabaseAlreadyExistsException(name);
        Database database = new Database(name);
        databases.put(name, database);
    }

    private void dirInit() {
        File path = new File(dataPath);
        if (!path.exists())
            path.mkdir();
        File mPath = new File(metadataPath);
        if (!mPath.exists())
            mPath.mkdir();
    }

    public void deleteDatabaseIfExist(String name) {
        if (!databases.containsKey(name))
            return;
        deleteDatabase(name);
    }

    public void deleteDatabase(String name) {
        if (!name.equals(adminDatabaseName))
            throw new NoAuthorityException();
        if (!databases.containsKey(name))
            throw new DatabaseNotExistsException(name);
        databases.get(name).deleteAllTable();
        databases.remove(name);
    }

    public void deleteTableIfExist(String tableName) {
        if (!databases.get(current).tables.containsKey(tableName))
            return;
        deleteTable(tableName);
    }

    public void deleteTable(String tableName) {
        if (!authorized(current, tableName, AUTH_DROP))
            throw new NoAuthorityException();
        if (!databases.get(current).tables.containsKey(tableName))
            throw new TableNotExistsException(tableName);
        databases.get(current).deleteTable(tableName);
        databases.get(current).tables.remove(tableName);
    }

    private void persistDatabases() {
        if (databases.size() == 0)
            return;
        File file = new File(persistFileName);
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file, false);
            for (String name : databases.keySet())
                fileWriter.write(name + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void insert(String tableName, String[] values, String[] columnNames) {
        if (!authorized(current, tableName, AUTH_INSERT))
            throw new NoAuthorityException();
        Database database = databases.get(current);
        if (!database.tables.containsKey(tableName))
            throw new TableNotExistsException(tableName);
        database.tables.get(tableName).insert(values, columnNames);
    }

    public Row get(String tableName, Entry[] entries) {
        Database database = databases.get(current);
        if (!database.tables.containsKey(tableName))
            throw new TableNotExistsException(tableName);
        return database.tables.get(tableName).get(entries);
    }

    public String showDatabases() {
        ArrayList<Cell> header = new ArrayList<>() {
            {
                add(new Cell("Database"));
            }
        };
        List<List<Cell>> body = new ArrayList<>();
        for (String s : databases.keySet()) {
            ArrayList<Cell> line = new ArrayList<>() {
                {
                    add(new Cell(s));
                }
            };
            body.add(line);
        }
        if (body.size() == 0)
            return "Empty set.";
        return new PrintFormat.ConsoleTableBuilder().addHeaders(header).addRows(body).build().getContent() + "\n"
                + body.size() + (body.size() == 1 ? " row" : " rows") + " in set.";
    }

    public String showTables(String name) {
        if (!databases.containsKey(name))
            throw new DatabaseNotExistsException(name);
        ArrayList<Cell> header = new ArrayList<>() {
            {
                add(new Cell("Tables_in_" + name));
            }
        };
        List<List<Cell>> body = new ArrayList<>();
        for (String s : databases.get(name).tables.keySet()) {
            ArrayList<Cell> line = new ArrayList<>() {
                {
                    add(new Cell(s));
                }
            };
            body.add(line);
        }
        if (body.size() == 0)
            return "Empty set.";
        return new PrintFormat.ConsoleTableBuilder().addHeaders(header).addRows(body).build().getContent() + "\n"
                + body.size() + (body.size() == 1 ? " row" : " rows") + " in set.";
    }

    public String select(String[] columnsProjected, QueryTable[] queryTables, Logic selectLogic, boolean distinct) {
        for (QueryTable queryTable : queryTables) {
            if (queryTable instanceof SimpleTable) {
                if (!authorized(current, ((SimpleTable) queryTable).getTable().tableName, AUTH_SELECT))
                    throw new NoAuthorityException();
            } else {
                for (Table t : ((JointTable) queryTable).getTables())
                    if (!authorized(current, t.tableName, AUTH_SELECT))
                        throw new NoAuthorityException();
            }
        }
        return databases.get(current).select(columnsProjected, queryTables, selectLogic, distinct);
    }

    public String delete(String tableName, Logic logic) {
        if (!authorized(current, tableName, AUTH_DELETE))
            throw new NoAuthorityException();
        Database database = databases.get(current);
        if (!database.tables.containsKey(tableName))
            throw new TableNotExistsException(tableName);
        return database.tables.get(tableName).delete(logic);
    }

    public String update(String tableName, String columnName, Expression expression, Logic logic) {
        if (!authorized(current, tableName, AUTH_UPDATE))
            throw new NoAuthorityException();
        Database database = databases.get(current);
        if (!database.tables.containsKey(tableName))
            throw new TableNotExistsException(tableName);
        return database.tables.get(tableName).update(columnName, expression, logic);
    }

    private boolean authorized(String databaseName, String tableName, int level) {
        if (user.equals(adminUserName))
            return true;
        Table t = databases.get(adminDatabaseName).tables.get(authTableName);
        try {
            Row row = t.get(new Entry[] { null, new Entry(user), new Entry(databaseName), new Entry(tableName) });
            ArrayList<Entry> entries = row.getEntries();
            return (((int) entries.get(0).value) & (1 << level)) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void recoverDatabases() {
        File file = new File(persistFileName);
        if (!file.exists())
            return;
        FileReader fileReader;
        try {
            fileReader = new FileReader(file);
        } catch (IOException e) {
            throw new InternalException("failed to open database file.");
        }
        BufferedReader reader = new BufferedReader(fileReader);
        String databaseName;
        while (true) {
            try {
                if ((databaseName = reader.readLine()) == null)
                    break;
            } catch (IOException e) {
                throw new InternalException("failed to read from database file.");
            }
            Database database = new Database(databaseName);
            databases.put(databaseName, database);
        }
        try {
            reader.close();
        } catch (IOException e) {
            throw new InternalException("failed to close database file reader.");
        }
    }

    public void createTable(String tableName, Column[] columns) {
        if (!skipCheck && current.equals(adminDatabaseName))
            if (tableName.equals(authTableName) || tableName.equals(userTableName))
                throw new ReservedNameException(tableName);
        databases.get(current).createTable(tableName, columns);
        addAuth(user, tableName, AUTH_MAX);
    }

    public Database getDatabase(String name) {
        if (!databases.containsKey(name))
            throw new DatabaseNotExistsException(name);
        return databases.get(name);
    }

    public void quit() {
        persistDatabases();
        for (Database d : databases.values())
            d.quit();
    }

    public SimpleTable getSingleJointTable(String tableName) {
        Database database = databases.get(current);
        if (!database.tables.containsKey(tableName))
            throw new TableNotExistsException(tableName);
        return new SimpleTable(database.tables.get(tableName));
    }

    public JointTable getMultipleJointTable(ArrayList<String> tableNames, Logic logic) {
        Database database = databases.get(current);
        ArrayList<Table> tables = new ArrayList<>();
        for (String tableName : tableNames) {
            if (!database.tables.containsKey(tableName))
                throw new TableNotExistsException(tableName);
            tables.add(database.tables.get(tableName));
        }
        return new JointTable(tables, logic);
    }
}