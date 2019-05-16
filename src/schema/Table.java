package schema;

import exception.*;
import index.BPlusTree;
import storage.Page;

import java.io.*;
import java.util.*;

import static global.Global.*;

public class Table {
    private String databaseName;
    String tableName;
    ArrayList<Column> columns;
    private long uid;
    private transient BPlusTree<Entry, Row> index;

    private HashMap<Integer, Page> pages;
    private HashMap<Integer, Long> times;
    private int pageNum;

    Table(String databaseName, String tableName, Column[] columns) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.columns = new ArrayList<>(Arrays.asList(columns));
        this.uid = columns[columns.length - 1].name.equals("uid") ? 0 : -1;
        this.index = new BPlusTree<>();
        pages = new HashMap<>();
        times = new HashMap<>();
        recoverTable();
        allocateNewPage();
    }

    private void recoverTable() {
        File path = new File(dataPath);
        File[] files = path.listFiles();
        if (files == null)
            throw new InternalException("failed to get table data files.");
        if (files.length == 1 && files[0].getName().equals("metadata"))
            return;
        int maxPage = Integer.MIN_VALUE;
        boolean hasUID = uid >= 0;
        int columnSize = columns.size();
        for (File f : files) {
            String databaseName = f.getName().split("_")[0];
            String tableName = f.getName().split("_")[1];
            if (!(this.databaseName.equals(databaseName) && this.tableName.equals(tableName)))
                continue;
            ArrayList<Row> rows;
            try {
                rows = Deserialize(dataPath + f.getName());
            } catch (Exception e) {
                throw new InternalException("failed to open table data file.");
            }
            if (rows == null || rows.size() == 0) {
                File unusedFile = new File(dataPath + f.getName());
                unusedFile.delete();
                continue;
            }
            int pageID = rows.get(0).getPageID();
            Page page = new Page(databaseName, tableName, pageID);
            for (Row row : rows) {
                for (int i = 0; i < columnSize; i++) {
                    Entry entry = row.getEntries().get(i);
                    entry.setTable(this);
                    if (columns.get(i).primary) {
                        index.put(entry, pages.size() < maxPageNum ? row : new Row(row.getPageID()));
                        page.addRow(row.getEntries().get(i), row.toString().length());
                        maxPage = maxPage < pageID ? pageID : maxPage;
                        break;
                    }
                }
                if (hasUID)
                    uid = Math.max(uid, (Long) (row.getEntries().get(columnSize - 1).value));
            }
            if (pages.size() < maxPageNum)
                addPage(page);
        }
        this.pageNum = maxPage;
    }

    private void addPage(Page page) {
        if (pages.size() >= maxPageNum)
            putLRUPageToDisk();
        pages.put(page.getID(), page);
        times.put(page.getID(), System.currentTimeMillis());
    }

    void insert(String[] values, String[] columnNames) {
        if (columnNames != null) {
            if (columnNames.length != values.length)
                throw new ColumnMismatchException();
            for (String name : columnNames)
                if (!hasColumn(name))
                    throw new ColumnMismatchException();

        }
        Entry[] entries = new Entry[columns.size()];
        int offset = uid >= 0 ? 1 : 0;
        for (int i = 0; i < entries.length - offset; i++) {
            Object value = null;
            Column column = columns.get(i);
            if (columnNames == null || columnNames.length == 0)
                try {
                    value = parseValue(values[i], i);
                } catch (Exception e) {
                    throw new ValueFormatException(column.name);
                }
            else {
                boolean found = false;
                for (int j = 0; j < columnNames.length; j++)
                    if (columnNames[j].equals(column.name)) {
                        try {
                            value = parseValue(values[j], i);
                        } catch (Exception e) {
                            throw new ValueFormatException(column.name);
                        }
                        found = true;
                        break;
                    }
                if (!found && column.notNull)
                    throw new NullValueException(column.name);
            }
            if (column.type == Type.STRING && value != null && String.valueOf(value).length() > column.maxLength)
                throw new StringExceedMaxLengthException(column.name);
            entries[i] = new Entry(i, value);
        }
        if (uid >= 0)
            entries[entries.length - 1] = new Entry(entries.length - 1, ++uid);
        Row row = new Row(entries, pageNum);
        for (Entry e : entries)
            e.setTable(this);
        for (int i = 0; i < entries.length; i++) {
            if (columns.get(i).primary) {
                index.put(row.getEntries().get(i), row);
                int size = pages.get(pageNum).addRow(row.getEntries().get(i), row.toString().length());
                pages.get(pageNum).setDirty();
                if (size >= maxPageSize)
                    allocateNewPage();
                break;
            }
        }
        times.put(pages.get(pageNum).getID(), System.currentTimeMillis());
    }

    private boolean hasColumn(String name) {
        for (Column c : columns)
            if (name.equals(c.name))
                return true;
        return false;
    }

    public Row get(Entry entry) {
        Row row = index.get(entry);
        times.put(row.getPageID(), System.currentTimeMillis());
        if (row.getEntries() == null) {
            if (pageNum >= maxPageNum)
                putLRUPageToDisk();
            getPageFromDisk(row.getPageID());
            return index.get(entry);
        } else {
            return row;
        }
    }

    public void delete(Entry[] entries) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).primary) {
                Row row = get(entries[i]);
                index.remove(row.getEntries().get(i));
                Entry entry = row.getEntries().get(i);
                pages.get(row.getPageID()).deleteRow(entry, row.toString().length());
                times.put(row.getPageID(), System.currentTimeMillis());
                pages.get(row.getPageID()).setDirty();
                return;
            }
        }
    }

    public void update(Entry[] entries) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).primary) {
                Row oldRow = get(entries[i]);
                int pageID = oldRow.getPageID();
                Row newRow = new Row(entries, pageID);
                index.put(oldRow.getEntries().get(i), newRow);
                Entry entry = oldRow.getEntries().get(i);
                pages.get(pageID).updateRow(oldRow.toString().length(), newRow.toString().length());
                times.put(pageID, System.currentTimeMillis());
                pages.get(pageID).setDirty();
                break;
            }
        }
    }

    public void deleteAllPage() {
        File path = new File(dataPath);
        File[] files = path.listFiles();
        if (files.length == 1 && files[0].getName().equals("metadata"))
            return;
        for (File f : files) {
            String databaseName = f.getName().split("_")[0];
            String tableName = f.getName().split("_")[1];
            if (!(this.databaseName.equals(databaseName) && this.tableName.equals(tableName)))
                continue;
            File deleteFile = new File(dataPath + f.getName());
            deleteFile.delete();
        }
    }

    int compareEntries(Entry e1, Entry e2) {
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

    private Object parseValue(String s, int index) {
        if (s.equals("null")) {
            if (columns.get(index).notNull)
                throw new NullValueException(columns.get(index).name);
            else return null;
        }
        switch (columns.get(index).type) {
            case DOUBLE:
                return Double.parseDouble(s);
            case INT:
                return Integer.parseInt(s);
            case FLOAT:
                return Float.parseFloat(s);
            case LONG:
                return Long.parseLong(s);
            case STRING:
                return s.substring(1, s.length() - 1);
        }
        return null;
    }

    private void Serialize(Page page) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(page.getPath())));
        ArrayList<Row> rowList = new ArrayList<>();
        for (Entry entry : page.getPrimaryEntries()) {
            Row row = index.get(entry);
            rowList.add(row);
        }
        oos.writeObject(rowList);
        oos.close();
    }

    private ArrayList<Row> Deserialize(String path) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)));
        ArrayList<Row> rows = (ArrayList<Row>) ois.readObject();
        ois.close();
        return rows;
    }

    private int getLRUCachePageID() {
        List<Map.Entry<Integer, Long>> list = new ArrayList(times.entrySet());
        list.sort(Comparator.comparing(Map.Entry::getValue));
        return list.get(0).getKey();
    }

    private void putLRUPageToDisk() {
        int id = getLRUCachePageID();
        Page page = pages.get(id);
        if (page.isDirty()) {
            try {
                Serialize(page);
            } catch (IOException e) {
                throw new InternalException("failed to write dirty page to disk.");
            }
        }
        for (Entry entry : page.getPrimaryEntries())
            index.update(entry, new Row(id));
        pages.remove(id);
        times.remove(id);
    }

    private void getPageFromDisk(int pageID) {
        String path = Page.concatPageName(this.databaseName, this.tableName, pageID);
        ArrayList<Row> rows;
        try {
            rows = Deserialize(path);
        } catch (Exception e) {
            throw new InternalException("failed to deserialize table file.");
        }
        if (rows == null || rows.size() == 0)
            return;
        Page page = new Page(this.databaseName, this.tableName, pageID);
        for (Row row : rows) {
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).primary) {
                    Entry entry = row.getEntries().get(i);
                    page.addRow(entry, row.toString().length());
                    entry.setTable(this);
                    index.update(entry, row);
                    break;
                }
            }
        }
        pages.put(pageID, page);
        times.put(pageID, System.currentTimeMillis());
    }

    private void allocateNewPage() {
        Page page = new Page(this.databaseName, this.tableName, ++pageNum);
        addPage(page);
    }

    void commit() {
        for (Page page : pages.values()) {
            if (page.isDirty()) {
                try {
                    Serialize(page);
                } catch (IOException e) {
                    throw new InternalException("failed to write dirty page to disk.");
                }
            }
        }
    }
}