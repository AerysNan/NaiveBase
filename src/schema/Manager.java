package schema;

import connection.Context;
import exception.*;
import format.Cell;
import format.PrintFormat;
import query.*;
import type.ColumnType;
import type.ComparatorType;
import type.ComparerType;
import type.LogicalOpType;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static global.Global.*;

public class Manager {
    public static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private HashMap<String, Database> databases;
    private boolean skipCheck;
    private HashSet<String> users;

    public Manager() {
        this.skipCheck = true;
        this.databases = new HashMap<>();
        this.users = new HashSet<>();
        recoverDatabases();
        Context adminContext = new Context(adminUserName, adminDatabaseName);
        createDatabaseIfNotExists(adminDatabaseName, adminContext);
        dirInit();
        if (!databases.get(adminDatabaseName).tables.containsKey(authTableName)) {
            createTable(authTableName,
                    new Column[]{
                            new Column("authority", ColumnType.INT, 0, true, -1),
                            new Column("username", ColumnType.STRING, 2, true, maxNameLength),
                            new Column("database_name", ColumnType.STRING, 2, true, maxNameLength),
                            new Column("table_name", ColumnType.STRING, 2, true, maxNameLength)
                    },
                    adminContext
            );
        }
        if (!databases.get(adminDatabaseName).tables.containsKey(userTableName)) {
            createTable(userTableName,
                    new Column[]{
                            new Column("username", ColumnType.STRING, 1, true, maxNameLength),
                            new Column("password", ColumnType.STRING, 0, true, maxNameLength)
                    },
                    adminContext
            );
            createUser(adminUserName, getAdminPassword(), new Context(adminUserName, adminDatabaseName));
        }
        skipCheck = false;
    }

    public void addAuth(String username, String tableName, int level, Context context) {
        if (username.equals(adminUserName))
            return;
        Database database = databases.get(context.databaseName);
        if (!database.tables.containsKey(tableName))
            throw new RelationNotExistsException(tableName);
        Table t = databases.get(adminDatabaseName).tables.get(userTableName);
        if (!t.contains(new Entry[]{new Entry(username)}))
            throw new UserNotExistException(username);
        int currentAuth = getAuth(username, context.databaseName, tableName);
        String databaseName = context.databaseName;
        context.databaseName = adminDatabaseName;
        if (currentAuth < 0)
            insert(authTableName, new String[]{String.valueOf(level), toLiteral(username), toLiteral(databaseName),
                    toLiteral(tableName)}, null, context);
        else {
            Logic logic = new Logic(
                    new Logic(new Condition(
                            new Expression(new Comparer(ComparerType.COLUMN, "username")),
                            new Expression(new Comparer(ComparerType.STRING, username)),
                            ComparatorType.EQ
                    )
                    ),
                    new Logic(
                            new Logic(new Condition(
                                    new Expression(new Comparer(ComparerType.COLUMN, "database_name")),
                                    new Expression(new Comparer(ComparerType.STRING, databaseName)),
                                    ComparatorType.EQ
                            )),
                            new Logic(new Condition(
                                    new Expression(new Comparer(ComparerType.COLUMN, "table_name")),
                                    new Expression(new Comparer(ComparerType.STRING, tableName)),
                                    ComparatorType.EQ
                            )),
                            LogicalOpType.AND
                    ),
                    LogicalOpType.AND
            );
            update(authTableName, "authority",
                    new Expression(new Comparer(ComparerType.NUMBER, String.valueOf(level | currentAuth))), logic, context);
        }
        context.databaseName = databaseName;
    }

    public void removeAuth(String username, String tableName, int level, Context context) {
        if (username.equals(adminUserName))
            return;
        Database database = databases.get(context.databaseName);
        if (!database.tables.containsKey(tableName))
            throw new RelationNotExistsException(tableName);
        Table t = databases.get(adminDatabaseName).tables.get(userTableName);
        if (!t.contains(new Entry[]{new Entry(username)}))
            throw new UserNotExistException(username);
        int currentAuth = getAuth(username, context.databaseName, tableName);
        String databaseName = context.databaseName;
        context.databaseName = adminDatabaseName;
        if (currentAuth != 0)
            update(authTableName, "authority",
                    new Expression(new Comparer(ComparerType.NUMBER, String.valueOf(~level & currentAuth))), null, context);
        context.databaseName = databaseName;
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

    public void createUser(String username, String password, Context context) {
        if (!skipCheck && username.equals(adminUserName))
            throw new ReservedNameException(username);
        Table t = databases.get(adminDatabaseName).tables.get(userTableName);
        if (t.contains(new Entry[]{new Entry(username)}))
            throw new UserAlreadyExistsException(username);
        String databaseName = context.databaseName;
        context.databaseName = adminDatabaseName;
        insert(userTableName, new String[]{toLiteral(username), toLiteral(encrypt(password))}, null, context);
        context.databaseName = databaseName;
    }

    public void dropUser(String username, boolean exists, Context context) {
        Table t = databases.get(adminDatabaseName).tables.get(userTableName);
        if (!t.contains(new Entry[] { new Entry(username) })) {
            if (exists)
                throw new UserNotExistException(username);
            else
                return;
        }
        String databaseName = context.databaseName;
        context.databaseName = adminDatabaseName;
        delete(userTableName, new Logic(new Condition(new Expression(new Comparer(ComparerType.COLUMN, "username")),
                new Expression(new Comparer(ComparerType.STRING, username)), ComparatorType.EQ)), context);
        delete(authTableName, new Logic(new Condition(new Expression(new Comparer(ComparerType.COLUMN, "username")),
                new Expression(new Comparer(ComparerType.STRING, username)), ComparatorType.EQ)), context);
        context.databaseName = databaseName;
    }

    public void logout(String username) {
        users.remove(username);
    }

    public void login(String username, String password) {
        if (users.contains(username))
            throw new UserAlreadyLoggedInException(username);
        Database database = databases.get(adminDatabaseName);
        try {
            Row row = database.tables.get(userTableName).get(new Entry[]{new Entry(username)});
            if (row.getEntries().get(1).value.equals(password)) {
                users.add(username);
                return;
            }
            throw new WrongUserOrPasswordException();
        } catch (Exception e) {
            throw new WrongUserOrPasswordException();
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

    private void createDatabaseIfNotExists(String name, Context context) {
        if (!context.username.equals(adminUserName))
            throw new NoAuthorityException();
        if (!skipCheck && name.equals(authTableName))
            throw new ReservedNameException(name);
        if (databases.containsKey(name))
            return;
        createDatabase(name, context);
    }

    public void switchDatabase(String databaseName, Context context) {
        if (!databases.containsKey(databaseName))
            throw new DatabaseNotExistsException(databaseName);
        context.databaseName = databaseName;
    }

    public void createDatabase(String name, Context context) {
        if (!context.username.equals(adminUserName))
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

    public void deleteDatabaseIfExist(String name, Context context) {
        if (!databases.containsKey(name))
            return;
        deleteDatabase(name, context);
    }

    public void deleteDatabase(String name, Context context) {
        if (!context.username.equals(adminUserName))
            throw new NoAuthorityException();
        if (!databases.containsKey(name))
            throw new DatabaseNotExistsException(name);
        databases.get(name).deleteAllTable();
        databases.remove(name);
    }

    public void deleteTableIfExist(String tableName, Context context) {
        if (!databases.get(context.databaseName).tables.containsKey(tableName))
            return;
        deleteTable(tableName, context);
    }

    public void deleteTable(String tableName, Context context) {
        if (!authorized(tableName, AUTH_DROP, context))
            throw new NoAuthorityException();
        Database database = databases.get(context.databaseName);
        if (!database.tables.containsKey(tableName))
            throw new RelationNotExistsException(tableName);
        database.deleteTable(tableName);
        database.tables.remove(tableName);
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

    public void createView(String viewName, String[] columnsProjected, QueryTable[] queryTables, Logic selectLogic, Context context) {
        databases.get(context.databaseName).createView(viewName, columnsProjected, queryTables, selectLogic);
    }

    public void dropView(String viewName, boolean exists, Context context) {
        databases.get(context.databaseName).dropView(viewName, exists);
    }

    public void insert(String tableName, String[] values, String[] columnNames, Context context) {
        if (!authorized(tableName, AUTH_INSERT, context))
            throw new NoAuthorityException();
        Database database = databases.get(context.databaseName);
        if (!database.tables.containsKey(tableName))
            throw new RelationNotExistsException(tableName);
        database.tables.get(tableName).insert(values, columnNames);
    }

    public Row get(String tableName, Entry[] entries, Context context) {
        Database database = databases.get(context.databaseName);
        if (!database.tables.containsKey(tableName))
            throw new RelationNotExistsException(tableName);
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
            ArrayList<Cell> line = new ArrayList<>() {{
                add(new Cell(s));
            }};
            body.add(line);
        }
        if (body.size() == 0)
            return "Empty set.";
        return new PrintFormat.ConsoleTableBuilder().addHeaders(header).addRows(body).build().getContent() + "\n"
                + body.size() + (body.size() == 1 ? " row" : " rows") + " in set.";
    }

    public String select(String[] columnsProjected, QueryTable[] queryTables, Logic selectLogic, boolean distinct, Context context) {
        for (QueryTable queryTable : queryTables) {
            if (queryTable instanceof SimpleTable) {
                if (!authorized(((SimpleTable) queryTable).getTable().tableName, AUTH_SELECT, context))
                    throw new NoAuthorityException();
            } else if (queryTable instanceof JointTable) {
                for (Table t : ((JointTable) queryTable).getTables())
                    if (!authorized(t.tableName, AUTH_SELECT, context))
                        throw new NoAuthorityException();
            }
        }
        return databases.get(context.databaseName).select(columnsProjected, queryTables, selectLogic, distinct);
    }

    public String delete(String tableName, Logic logic, Context context) {
        if (!authorized(tableName, AUTH_DELETE, context))
            throw new NoAuthorityException();
        Database database = databases.get(context.databaseName);
        if (!database.tables.containsKey(tableName))
            throw new RelationNotExistsException(tableName);
        return database.tables.get(tableName).delete(logic);
    }

    public String update(String tableName, String columnName, Expression expression, Logic logic, Context context) {
        if (!authorized(tableName, AUTH_UPDATE, context))
            throw new NoAuthorityException();
        Database database = databases.get(context.databaseName);
        if (!database.tables.containsKey(tableName))
            throw new RelationNotExistsException(tableName);
        return database.tables.get(tableName).update(columnName, expression, logic);
    }

    private boolean authorized(String tableName, int level, Context context) {
        if (context.username.equals(adminUserName))
            return true;
        Table t = databases.get(adminDatabaseName).tables.get(authTableName);
        try {
            Row row = t.get(new Entry[]{null, new Entry(context.username), new Entry(context.databaseName), new Entry(tableName)});
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

    public void createTable(String tableName, Column[] columns, Context context) {
        if (!skipCheck && context.databaseName.equals(adminDatabaseName))
            if (tableName.equals(authTableName) || tableName.equals(userTableName))
                throw new ReservedNameException(tableName);
        databases.get(context.databaseName).createTable(tableName, columns);
        addAuth(context.username, tableName, AUTH_MAX, context);
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

    public QueryTable getSingleJointTable(String tableName, Context context) {
        Database database = databases.get(context.databaseName);
        if (database.tables.containsKey(tableName))
            return new SimpleTable(database.tables.get(tableName));
        if (database.views.containsKey(tableName))
            return new VirtualTable(tableName, database.views.get(tableName));
        throw new RelationNotExistsException(tableName);
    }

    public QueryTable getMultipleJointTable(ArrayList<String> tableNames, Logic logic, Context context) {
        Database database = databases.get(context.databaseName);
        ArrayList<Table> tables = new ArrayList<>();
        for (String tableName : tableNames) {
            if (!database.tables.containsKey(tableName))
                throw new RelationNotExistsException(tableName);
            tables.add(database.tables.get(tableName));
        }
        return new JointTable(tables, logic);
    }
}