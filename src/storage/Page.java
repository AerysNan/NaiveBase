package storage;

import schema.Row;

import java.io.Serializable;
import java.util.ArrayList;

public class Page implements Serializable{

    private static final long serialVersionUID = -5809782578272943999L;
    private String name;
    private int size;
    private transient boolean isDirty;
    private ArrayList<Row> rows;

    public Page(String databaseName, String name, int id){
        this.name = concatPageName(databaseName, name,id);
        this.rows = new ArrayList<>();
    }

    public static String concatPageName(String databaseName, String name,int id){
        return "./data/" + databaseName + "_" + name + "_" + id + ".dat";
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(){
        isDirty = true;
    }

    public int getId(){
        return Integer.parseInt(name.split("_")[2].replace(".dat", ""));
    }

    public String getName(){
        return name;
    }

    public ArrayList<Row> getRows(){
        return rows;
    }

    public int add(Row row){
        rows.add(row);
        size += row.toString().length();
        return size;
    }

    public void remove(Row row){
        rows.remove(row);
        setDirty();
    }

    public void update(Row oldRow,Row newRow){
        rows.remove(oldRow);
        rows.add(newRow);
    }

}
