package schema;

import java.io.*;
import java.util.HashMap;

public class Manager {
    HashMap<String, Database> databases;
    private static final String persistFileName = "databases.dat";

    public Manager() throws IOException {
        this.databases = new HashMap<>();
        recoverDatabases();
    }

    public Database createDatabase(String name) {
        Database database = new Database(name);
        databases.put(name, database);
        return database;
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