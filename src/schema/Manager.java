package schema;

import exception.DatabaseAlreadyExistsException;
import exception.DatabaseNotExistsException;
import exception.NoRemovalAuthorityException;

import java.io.*;
import java.util.HashMap;

public class Manager {
    private HashMap<String, Database> databases;
    private String adminDatabaseName = "admin";
    private String currentDatabase;
    private static final String persistFileName = "databases.dat";

    public Manager() throws IOException {
        this.databases = new HashMap<>();
        recoverDatabases();
        createDatabaseIfNotExists(adminDatabaseName);
        currentDatabase = adminDatabaseName;
    }

    private void createDatabaseIfNotExists(String name) {
        if (databases.containsKey(name))
            return;
        createDatabase(name);
    }

    public void switchDatabase(String name) {
        if (!databases.containsKey(name))
            throw new DatabaseNotExistsException(name);
        currentDatabase = name;
    }

    public void createDatabase(String name) {
        if (databases.containsKey(name))
            throw new DatabaseAlreadyExistsException(name);
        Database database = new Database(name);
        databases.put(name, database);
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
        databases.remove(name);
    }

    public void persistDatabases() {
        if (databases.size() == 0)
            return;
        File file = new File(persistFileName);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, false);
            for (String name : databases.keySet())
                fileWriter.write(name + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String showDatabases() {
        StringBuilder sb = new StringBuilder();
        for (String s : databases.keySet())
            sb.append(s).append(' ');
        return sb.toString();
    }

    public void recoverDatabases() throws IOException {
        File file = new File(persistFileName);
        if (!file.exists())
            return;
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);
        String tmpDataBaseName;
        while ((tmpDataBaseName = reader.readLine()) != null) {
            Database database = new Database(tmpDataBaseName);
            databases.put(tmpDataBaseName, database);
        }
        reader.close();
    }

    public void quit() {
        persistDatabases();
        for (Database d : databases.values()) {
            try {
                d.quit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}