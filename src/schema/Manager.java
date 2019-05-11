package schema;

import java.io.*;
import java.util.HashMap;

public class Manager {
    HashMap<String, Database> databases;
    public static final String persistFileName = "databases.dat";
    public static final String metadataPath = "./data/metadata/";
    public static final String dataPath = "./data/";
    public static int maxPageNum = 256;
    public static int maxPageSize = 4096;

    public Manager() throws Exception {
        this.databases = new HashMap<>();
        recoverDatabases();
        dirInit();
    }

    public void setPageAttr(int num,int size){
        this.maxPageNum = num;
        this.maxPageSize = size;
    }

    public void dirInit() {
        File dataPath = new File(this.dataPath);
        if (!dataPath.exists())
            dataPath.mkdir();
        File metadataPath = new File(this.metadataPath);
        if (!metadataPath.exists())
            metadataPath.mkdirs();
    }

    public Database createDatabase(String name) throws Exception {
        Database database = null;
        try {
            database = new Database(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        databases.put(name, database);
        return database;
    }

    public void persistDatabases() {
        if (databases.size() == 0)
            return;
        File file = new File(this.persistFileName);
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

    public void recoverDatabases() throws Exception {
        File file = new File(this.persistFileName);
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

    public Database useDatabase(String name){
        return databases.get(name);
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