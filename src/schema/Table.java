package schema;

import index.BPlusTree;
import storage.Page;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Table {
    String name;
    ArrayList<Column> columns;
    ArrayList<Column> primaryKeys;
    BPlusTree<Entry, Row> index;
    ArrayList<Page> pages;

    private static final int maxPageSize = 4096;

    public Table(String name, Column[] columns) throws IOException {
        this.name = name;
        this.columns = new ArrayList<>(Arrays.asList(columns));
        this.primaryKeys = new ArrayList<>();
        for (Column c : columns)
            if (c.primary)
                primaryKeys.add(c);
        this.index = new BPlusTree<>();
        this.pages = new ArrayList<>();
        createNewPage();
        recoverTable();
    }

    public void recoverTable() throws IOException {
        File path = new File("./data/");
        File[] files = path.listFiles();
        if (files.length == 1 && files[0].getName() == "metadata/") {
            return;
        }
        for (File f : files) {
            //TODO reduce time complexity
            if (!f.getName().startsWith(name))
                continue;
            FileReader fileReader = new FileReader(f);
            BufferedReader buffer = new BufferedReader(fileReader);
            String strRow = null;
            while ((strRow = buffer.readLine()) != null) {
                Entry entryList[] = parseRow(strRow);
                Row tmpRow = new Row(entryList);
                if (addRowToPage(tmpRow) > maxPageSize)
                    createNewPage();
                for (int i = 0; i < entryList.length; i++)
                    if (columns.get(i).primary)
                        index.put(entryList[i], tmpRow);
            }
        }
    }


    public void insert(Entry[] entries) throws IOException {
        if (entries.length != columns.size())
            throw new IOException("Column and Entry size error");
        Row row = new Row(entries);
        for (int i = 0; i < entries.length; i++)
            if (columns.get(i).primary)
                index.put(entries[i], row);
        if (addRowToPage(row) > maxPageSize)
            createNewPage();
    }

    private void createNewPage() throws IOException {
        Page page = new Page(name, pages.size());
        pages.add(page);
    }

    private int addRowToPage(Row row){
        return pages.get(pages.size()-1).add(row);
    }

    public int compareEntries(Entry e1, Entry e2) {
        assert e1.id == e2.id;
        int index = e1.id;
        switch (columns.get(index).type) {
            case INT:
                return ((Integer) e1.value).compareTo((Integer) e2.value);
            case LONG:
                return ((Long) e1.value).compareTo((Long) e2.value);
            case FLOAT:
                return ((Float) e1.value).compareTo((Float) e2.value);
            case DOUBLE:
                return ((Double) e1.value).compareTo((Double) e2.value);
            case STRING:
                return ((String) e1.value).compareTo((String) e2.value);
        }
        return 0;
    }

    public Entry[] parseRow(String strRow){
        String[] strEntryList = strRow.split(",");
        ArrayList<Entry> entryArrayList = new ArrayList<>();
        for (int i = 0; i < strEntryList.length; i++) {
            switch (columns.get(i).type) {
                case INT:
                    entryArrayList.add(new Entry(i, Integer.parseInt(strEntryList[i]), this));
                    break;
                case LONG:
                    entryArrayList.add(new Entry(i, Long.parseLong(strEntryList[i]), this));
                    break;
                case FLOAT:
                    entryArrayList.add(new Entry(i, Float.parseFloat(strEntryList[i]), this));
                    break;
                case DOUBLE:
                    entryArrayList.add(new Entry(i, Double.parseDouble(strEntryList[i]), this));
                    break;
                case STRING:
                    entryArrayList.add(new Entry(i, strEntryList[i], this));
                    break;
            }
        }
        return entryArrayList.toArray(new Entry[entryArrayList.size()]);
    }


    public void commit() throws IOException {
        for(Page page:pages){
            page.persistPage();
        }
    }
}