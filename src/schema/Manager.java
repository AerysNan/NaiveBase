package schema;

import exception.*;
import query.*;
import java.io.*;
import java.util.HashMap;

import static global.Global.*;

public class Manager {
    private HashMap<String, Database> databases;
    private String current;

    public Manager() {
        this.databases = new HashMap<>();
        recoverDatabases();
        createDatabaseIfNotExists(adminDatabaseName);
        current = adminDatabaseName;
        dirInit();
    }

    private void createDatabaseIfNotExists(String name) {
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
        if (name.equals(adminDatabaseName))
            throw new NoRemovalAuthorityException(name);
        if (!databases.containsKey(name))
            throw new DatabaseNotExistsException(name);
        databases.get(name).deleteAllTable();
        databases.remove(name);
    }

    public void deleteTableIfExist(String name) {
        if (databases.get(current).tables.containsKey(name))
            return;
        deleteTable(name);
    }

    public void deleteTable(String name) {
        if (!databases.get(current).tables.containsKey(name))
            throw new TableNotExistsException(name);
        databases.get(current).deleteTable(name);
        databases.get(current).tables.remove(name);
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
        StringBuilder sb = new StringBuilder();
        for (String s : databases.keySet())
            sb.append(s).append(' ');
        if (sb.length() == 0)
            return "--EMPTY--";
        return sb.toString();
    }

    public String showTables(String name) {
        if (!databases.containsKey(name))
            throw new DatabaseNotExistsException(name);
        StringBuilder sb = new StringBuilder();
        for (String s : databases.get(name).tables.keySet())
            sb.append(s).append(' ');
        if (sb.length() == 0)
            return "--EMPTY--";
        return sb.toString();
    }

    public String select(String[] columnsProjected, QueryTable[] tablesQueried, Condition whereCondition) {
        return databases.get(current).select(columnsProjected, tablesQueried, whereCondition);
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
                if ((databaseName = reader.readLine()) == null) break;
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

    public void createTable(String name, Column[] columns) {
        databases.get(current).createTable(name, columns);
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

    public JointTable getMultipleJointTable(String tableName1, String tableName2, Condition whereCondition) {
        Database database = databases.get(current);
        if (!database.tables.containsKey(tableName1))
            throw new TableNotExistsException(tableName1);
        if (!database.tables.containsKey(tableName2))
            throw new TableNotExistsException(tableName2);
        return new JointTable(database.tables.get(tableName1), database.tables.get(tableName2), whereCondition);
    }
}