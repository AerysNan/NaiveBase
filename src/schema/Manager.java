package schema;

import server.Context;
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
    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean skipCheck;
    private HashMap<String, Database> databases;
    private HashSet<String> users;

    public Manager() {
        this.skipCheck = true;
        this.databases = new HashMap<>();
        this.users = new HashSet<>();
        recoverDatabases();
        Context adminContext = new Context(adminUserName, adminDatabaseName);
        createDatabaseIfNotExists(adminContext);
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
        Database database = getDatabase(context.databaseName);
        try {
            database.lock.readLock().lock();
            if (!database.tables.containsKey(tableName))
                throw new RelationNotExistsException(tableName);
        } finally {
            database.lock.readLock().unlock();
        }
        database = getDatabase(adminDatabaseName);
        Table table = database.getTable(userTableName);
        try {
            table.lock.writeLock().lock();
            if (!table.contains(new Entry[]{new Entry(username)}))
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
                        )),
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
        } finally {
            table.lock.writeLock().unlock();
        }
    }

    public void removeAuth(String username, String tableName, int level, Context context) {
        if (username.equals(adminUserName))
            return;
        Database database = getDatabase(context.databaseName);
        try {
            database.lock.readLock().lock();
            if (!database.tables.containsKey(tableName))
                throw new RelationNotExistsException(tableName);
        } finally {
            database.lock.readLock().unlock();
        }
        database = getDatabase(adminDatabaseName);
        Table table = database.getTable(userTableName);
        try {
            table.lock.writeLock().lock();
            if (!table.contains(new Entry[]{new Entry(username)}))
                throw new UserNotExistException(username);
            int currentAuth = getAuth(username, context.databaseName, tableName);
            String databaseName = context.databaseName;
            context.databaseName = adminDatabaseName;
            if (currentAuth != 0)
                update(authTableName, "authority",
                        new Expression(new Comparer(ComparerType.NUMBER, String.valueOf(~level & currentAuth))), null, context);
            context.databaseName = databaseName;
        } finally {
            table.lock.writeLock().unlock();
        }
    }

    private int getAuth(String username, String databaseName, String tableName) {
        Database database = getDatabase(adminDatabaseName);
        Table table = database.getTable(authTableName);
        try {
            Row row = table.get(new Entry[]{null, new Entry(username), new Entry(databaseName), new Entry(tableName)});
            ArrayList<Entry> entries = row.getEntries();
            return (int) entries.get(0).value;
        } catch (Exception e) {
            return -1;
        }
    }

    private boolean notAuthorized(String tableName, int level, Context context) {
        if (context.username.equals(adminUserName))
            return false;
        try {
            int currentAuth = getAuth(context.username, context.databaseName, tableName);
            return currentAuth <= 0 || (currentAuth & (1 << level)) <= 0;
        } catch (Exception e) {
            return true;
        }
    }

    public void createUser(String username, String password, Context context) {
        if (!skipCheck && username.equals(adminUserName))
            throw new ReservedNameException(username);
        Database database = getDatabase(adminDatabaseName);
        Table table = database.getTable(userTableName);
        try {
            table.lock.writeLock().lock();
            if (table.contains(new Entry[]{new Entry(username)}))
                throw new UserAlreadyExistsException(username);
            String databaseName = context.databaseName;
            context.databaseName = adminDatabaseName;
            insert(userTableName, new String[]{toLiteral(username), toLiteral(encrypt(password))}, null, context);
            context.databaseName = databaseName;
        } finally {
            table.lock.writeLock().unlock();
        }
    }

    public void dropUser(String username, boolean exists, Context context) {
        Database database = getDatabase(adminDatabaseName);
        Table table = database.getTable(userTableName);
        try {
            table.lock.writeLock().lock();
            if (!table.contains(new Entry[]{new Entry(username)})) {
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
        } finally {
            table.lock.writeLock().unlock();
        }
    }

    public String showMeta(String tableName, Context context) {
        Database database = getDatabase(context.databaseName);
        Table table = database.getTable(tableName);
        ArrayList<Cell> header = new ArrayList<>();
        header.add(new Cell("NAME"));
        header.add(new Cell("TYPE"));
        header.add(new Cell("PRIMARY"));
        header.add(new Cell("NOTNULL"));
        List<List<Cell>> body = new ArrayList<>();
        for (Column c : table.columns) {
            ArrayList<Cell> row = new ArrayList<>();
            row.add(new Cell(c.name));
            row.add(new Cell(c.type.toString()));
            String primary;
            if (c.primary == 1)
                primary = "true";
            else if (c.primary == 2)
                primary = "composite";
            else
                primary = "false";
            row.add(new Cell(primary));
            row.add(new Cell(String.valueOf(c.notNull)));
            body.add(row);
        }
        return new PrintFormat.ConsoleTableBuilder().addHeaders(header).addRows(body).build().getContent();
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

    private void createDatabaseIfNotExists(Context context) {
        if (!context.username.equals(adminUserName))
            throw new NoAuthorityException();
        try {
            lock.writeLock().lock();
            if (databases.containsKey(global.Global.adminDatabaseName))
                return;
            createDatabase(global.Global.adminDatabaseName, context);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void switchDatabase(String databaseName, Context context) {
        try {
            lock.readLock().lock();
            if (!databases.containsKey(databaseName))
                throw new DatabaseNotExistsException(databaseName);
            context.databaseName = databaseName;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void createDatabase(String name, Context context) {
        if (!context.username.equals(adminUserName))
            throw new NoAuthorityException();
        if (!skipCheck && name.equals(adminDatabaseName))
            throw new ReservedNameException(name);
        try {
            lock.writeLock().lock();
            if (databases.containsKey(name))
                throw new DatabaseAlreadyExistsException(name);
            Database database = new Database(name);
            databases.put(name, database);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void dirInit() {
        File path = new File(dataPath);
        if (!path.exists())
            path.mkdir();
        File mPath = new File(metadataPath);
        if (!mPath.exists())
            mPath.mkdir();
    }

    public void deleteDatabase(String name, boolean exist, Context context) {
        if (!context.username.equals(adminUserName))
            throw new NoAuthorityException();
        try {
            lock.writeLock().lock();
            if (!databases.containsKey(name)) {
                if (exist)
                    throw new DatabaseNotExistsException(name);
                else return;
            }
            databases.get(name).deleteAllTable();
            databases.remove(name);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void deleteTable(String tableName, boolean exist, Context context) {
        if (notAuthorized(tableName, AUTH_DROP, context))
            throw new NoAuthorityException();
        Database database = getDatabase(context.databaseName);
        try {
            database.lock.writeLock().lock();
            if (!database.tables.containsKey(tableName)) {
                if (exist)
                    throw new RelationNotExistsException(tableName);
                else return;
            }
            database.deleteTable(tableName);
            database.tables.remove(tableName);
        } finally {
            database.lock.writeLock().unlock();
        }
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
        Database database = getDatabase(context.databaseName);
        database.createView(viewName, columnsProjected, queryTables, selectLogic);
    }

    public void dropView(String viewName, boolean exists, Context context) {
        Database database = getDatabase(context.databaseName);
        database.dropView(viewName, exists);
    }

    public void insert(String tableName, String[] values, String[] columnNames, Context context) {
        if (notAuthorized(tableName, AUTH_INSERT, context))
            throw new NoAuthorityException();
        Database database = getDatabase(context.databaseName);
        Table table = database.getTable(tableName);
        table.insert(values, columnNames);
    }

    public Row get(String tableName, Entry[] entries, Context context) {
        Database database = databases.get(context.databaseName);
        if (!database.tables.containsKey(tableName))
            throw new RelationNotExistsException(tableName);
        return database.tables.get(tableName).get(entries);
    }

    public String showDatabases() {
        ArrayList<Cell> header = new ArrayList<>() {{
            add(new Cell("Database"));
        }};
        List<List<Cell>> body = new ArrayList<>();
        try {
            lock.readLock().lock();
            for (String s : databases.keySet()) {
                ArrayList<Cell> line = new ArrayList<>() {{
                    add(new Cell(s));
                }};
                body.add(line);
            }
        } finally {
            lock.readLock().unlock();
        }
        if (body.size() == 0)
            return "Empty set.";
        return new PrintFormat.ConsoleTableBuilder().addHeaders(header).addRows(body).build().getContent() + "\n"
                + body.size() + (body.size() == 1 ? " row" : " rows") + " in set.";
    }

    public String showTables(String name) {
        Database database = getDatabase(name);
        ArrayList<Cell> header = new ArrayList<>() {{
            add(new Cell("Tables_in_" + name));
        }};
        List<List<Cell>> body = new ArrayList<>();
        try {
            database.lock.readLock().lock();
            for (String s : database.tables.keySet()) {
                ArrayList<Cell> line = new ArrayList<>() {{
                    add(new Cell(s));
                }};
                body.add(line);
            }
        } finally {
            database.lock.readLock().unlock();
        }
        if (body.size() == 0)
            return "Empty set.";
        return new PrintFormat.ConsoleTableBuilder().addHeaders(header).addRows(body).build().getContent() + "\n"
                + body.size() + (body.size() == 1 ? " row" : " rows") + " in set.";
    }

    public String select(String[] columnsProjected, QueryTable[] queryTables, Logic selectLogic, boolean distinct, Context context) {
        for (QueryTable queryTable : queryTables) {
            if (queryTable instanceof SimpleTable) {
                if (notAuthorized(((SimpleTable) queryTable).getTable().tableName, AUTH_SELECT, context))
                    throw new NoAuthorityException();
            } else if (queryTable instanceof JointTable) {
                for (Table table : ((JointTable) queryTable).getTables())
                    if (notAuthorized(table.tableName, AUTH_SELECT, context))
                        throw new NoAuthorityException();
            }
        }
        Database database = getDatabase(context.databaseName);
        return database.select(columnsProjected, queryTables, selectLogic, distinct);
    }

    public String delete(String tableName, Logic logic, Context context) {
        if (notAuthorized(tableName, AUTH_DELETE, context))
            throw new NoAuthorityException();
        Database database = getDatabase(context.databaseName);
        Table table = database.getTable(tableName);
        return table.delete(logic);
    }

    public String update(String tableName, String columnName, Expression expression, Logic logic, Context context) {
        if (notAuthorized(tableName, AUTH_UPDATE, context))
            throw new NoAuthorityException();
        Database database = getDatabase(context.databaseName);
        Table table = database.getTable(tableName);
        return table.update(columnName, expression, logic);
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
        Database database = getDatabase(context.databaseName);
        database.createTable(tableName, columns);
        addAuth(context.username, tableName, AUTH_MAX, new Context(adminUserName, context.databaseName));
    }

    public Database getDatabase(String name) {
        try {
            lock.readLock().lock();
            if (!databases.containsKey(name))
                throw new DatabaseNotExistsException(name);
            return databases.get(name);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void quit() {
        try {
            lock.writeLock().lock();
            persistDatabases();
            for (Database database : databases.values())
                database.quit();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public QueryTable getSingleJointTable(String tableName, Context context) {
        Database database = getDatabase(context.databaseName);
        try {
            database.lock.readLock().lock();
            if (database.tables.containsKey(tableName))
                return new SimpleTable(database.tables.get(tableName));
            if (database.views.containsKey(tableName))
                return new VirtualTable(tableName, database.views.get(tableName));
        } finally {
            database.lock.readLock().unlock();
        }
        throw new RelationNotExistsException(tableName);
    }

    public QueryTable getMultipleJointTable(ArrayList<String> tableNames, Logic logic, Context context) {
        Database database = getDatabase(context.databaseName);
        ArrayList<Table> tables = new ArrayList<>();
        try {
            database.lock.readLock().lock();
            for (String tableName : tableNames) {
                if (!database.tables.containsKey(tableName))
                    throw new RelationNotExistsException(tableName);
                tables.add(database.tables.get(tableName));
            }
        } finally {
            database.lock.readLock().unlock();
        }
        return new JointTable(tables, logic);
    }
}