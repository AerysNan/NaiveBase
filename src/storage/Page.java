package storage;

import schema.Entry;
import global.Global;

import java.util.ArrayList;

public class Page {

    private String path;
    private int id;
    private int size;
    private boolean isDirty;
    private ArrayList<Entry> primaryEntries;

    public Page(String databaseName, String tableName, int id) {
        this.path = concatPageName(databaseName, tableName, id);
        this.primaryEntries = new ArrayList<>();
        this.id = id;
    }

    public static String concatPageName(String databaseName, String tableName, int id) {
        return Global.dataPath + databaseName + "-" + tableName + "-" + id + ".dat";
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty() {
        isDirty = true;
    }

    public int getID() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public ArrayList<Entry> getPrimaryEntries() {
        return primaryEntries;
    }

    public int addRow(Entry entry, int size) {
        primaryEntries.add(entry);
        this.size += size;
        return this.size;
    }

    public void deleteRow(Entry entry, int size) {
        primaryEntries.remove(entry);
        this.size -= size;
    }

    public void updateSize(int oldSize, int newSize) {
        this.size -= oldSize;
        this.size += newSize;
    }

    public void updatePrimaryEntry(Entry oldEntry, Entry newEntry) {
        primaryEntries.remove(oldEntry);
        primaryEntries.add(newEntry);
    }
}