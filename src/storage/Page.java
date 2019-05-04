package storage;

import com.google.gson.Gson;
import schema.Entry;
import schema.Row;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Page {
    String name;
    FileWriter fileWriter;
    int size;
    Gson gson;

    public Page(String name, int id) throws IOException {
        this.gson = new Gson();
        this.name = "./data/" + name + "_" + id + ".dat";
        File file = new File(this.name);
        if (!file.exists())
            file.createNewFile();
        fileWriter = new FileWriter(file);
    }

    public int write(Row row) throws IOException {
        String string = row.toString();
        size += string.length();
        fileWriter.write(string + '\n');
        return size;
    }

    public void close() throws IOException {
        fileWriter.close();
    }
}
