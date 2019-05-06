package storage;

import schema.Row;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Page {
    String name;
    int size;
    ArrayList<Row>rows;

    public Page(String name, int id){
        this.name = "./data/" + name + "_" + id + ".dat";
        this.rows = new ArrayList<>();
    }

    public int add(Row row){
        rows.add(row);
        size += row.toString().length();
        return size;
    }

    public void persistPage() throws IOException{
        File file = new File(this.name);
        FileWriter fileWriter = new FileWriter(file, false);
        for (Row row : rows) {
            fileWriter.write(row.toString() + "\n");
        }
        fileWriter.close();
    }
}
