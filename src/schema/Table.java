package schema;

import index.BPlusTree;
import storage.Page;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Table {
    private String name;
    private ArrayList<Column> columns;
    private ArrayList<Column> primaryKeys;
    private BPlusTree<Entry, Row> index;

    private Page page;
    private int pageNum;
    private static final int maxPageSize = 4096;

    public Table(String name, Column[] columns) throws IOException {
        this.name = name;
        this.columns = new ArrayList<>(Arrays.asList(columns));
        this.primaryKeys = new ArrayList<>();
        for (Column c : columns)
            if (c.primary)
                primaryKeys.add(c);
        this.index = new BPlusTree<>();
        this.pageNum = 0;
        this.page = new Page(name, pageNum);
    }

    public void recover() {
        File path = new File("./data/");
        File[] files = path.listFiles();
        for (File f : files) {
            if (!f.getName().startsWith(name))
                continue;
            // recover from file
        }
    }

    public void insert(Entry[] entries) throws IOException {
        if (entries.length != columns.size())
            // throw exception
            return;
        Row row = new Row(entries);
        for (int i = 0; i < entries.length; i++)
            if (columns.get(i).primary)
                index.put(entries[i], row);
        if (page.write(row) > maxPageSize)
            createNewPage();
    }

    private void createNewPage() throws IOException {
        page.close();
        page = new Page(name, ++pageNum);
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

    public void commit() throws IOException {
        page.close();
    }
}