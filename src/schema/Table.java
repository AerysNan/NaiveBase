package schema;

import exception.*;
import index.BPlusTree;
import storage.Page;

import java.io.*;
import java.util.*;

import static global.Global.*;

public class Table implements Iterable<Row> {
    private String databaseName;
    String tableName;
    ArrayList<Column> columns;
    private long uid;
    private transient BPlusTree<Entry, Row> index;
    private boolean hasComposite;
    private boolean hasUID;
    private HashMap<CompositeKey, Entry> compositeKeyMap;
    private HashMap<Integer, Page> pages;
    private HashMap<Integer, Long> times;
    private int pageNum;

    Table(String databaseName, String tableName, Column[] columns) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.columns = new ArrayList<>(Arrays.asList(columns));
        this.compositeKeyMap = new HashMap<>();
        this.uid = columns[columns.length - 1].name.equals("uid") ? 0 : -1;
        this.index = new BPlusTree<>();
        pages = new HashMap<>();
        times = new HashMap<>();
        for (Column c : columns) {
            if (c.primary == 2)
                hasComposite = true;
            if (c.name.equals("uid"))
                hasUID = true;
        }
        recoverTable();
        allocateNewPage();
    }

    private void recoverTable() {
        File path = new File(dataPath);
        File[] files = path.listFiles();
        if (files == null)
            throw new InternalException("failed to get table data files.");
        if (files.length == 0)
            return;
        int maxPage = Integer.MIN_VALUE;
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
                ArrayList<Entry> entries = row.getEntries();
                for (int i = 0; i < columnSize; i++) {
                    Entry entry = entries.get(i);
                    if (columns.get(i).primary == 1) {
                        index.put(entry, pages.size() < maxPageNum ? row : new Row(row.getPageID()));
                        page.addRow(row.getEntries().get(i), row.toString().length());
                        maxPage = maxPage < pageID ? pageID : maxPage;
                        break;
                    }
                }
                if (hasUID)
                    uid = Math.max(uid, (Long) (entries.get(columnSize - 1).value));
                if (hasComposite)
                    compositeKeyMap.put(getCompositeKey(entries), entries.get(columnSize - 1));

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

        int derivativeColumn = hasUID ? 1 : 0;
        for (int i = 0; i < entries.length - derivativeColumn; i++) {
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
            if (column.type == ColumnType.STRING && value != null && String.valueOf(value).length() > column.maxLength)
                throw new StringExceedMaxLengthException(column.name);
            entries[i] = new Entry(i, (Comparable) value);
        }
        if (hasUID) {
            //FIXME: ensure composite key doesn't collide
            Entry uidEntry = new Entry(entries.length - 1, ++uid);
            entries[entries.length - 1] = uidEntry;
            if (hasComposite)
                compositeKeyMap.put(getCompositeKey(new ArrayList<>(Arrays.asList(entries))), uidEntry);
        }
        Row row = new Row(entries, pageNum);
        for (int i = 0; i < entries.length; i++) {
            if (columns.get(i).primary == 1) {
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

    private CompositeKey getCompositeKey(ArrayList<Entry> entries) {
        ArrayList<Entry> list = new ArrayList<>();
        int n = entries.size();
        for (int i = 0; i < n; i++)
            if (columns.get(i).primary == 2)
                list.add(entries.get(i));
        return new CompositeKey(list);
    }

    Row get(Entry[] entries) {
        Entry entry;
        if (entries.length == 1)
            entry = entries[0];
        else {
            CompositeKey compositeKey = getCompositeKey(new ArrayList<>(Arrays.asList(entries)));
            entry = compositeKeyMap.get(compositeKey);
        }
        Row row = index.get(entry);
        times.put(row.getPageID(), System.currentTimeMillis());
        if (row.getEntries() == null) {
            if (pageNum >= maxPageNum)
                putLRUPageToDisk();
            getPageFromDisk(row.getPageID());
            return index.get(entry);
        } else
            return row;
    }

    public void delete(Entry[] entries) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).primary == 1) {
                Row row = get(entries);
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
            if (columns.get(i).primary == 1) {
                Row oldRow = get(entries);
                int pageID = oldRow.getPageID();
                Row newRow = new Row(entries, pageID);
                Entry entry = oldRow.getEntries().get(i);
                index.put(entry, newRow);
                pages.get(pageID).updateRow(oldRow.toString().length(), newRow.toString().length());
                times.put(pageID, System.currentTimeMillis());
                pages.get(pageID).setDirty();
                break;
            }
        }
    }

    void deleteAllPage() {
        File path = new File(dataPath);
        File[] files = path.listFiles();
        if (files == null)
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
        if (page == null)
            return;
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
                if (columns.get(i).primary == 1) {
                    Entry entry = row.getEntries().get(i);
                    page.addRow(entry, row.toString().length());
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

    @Override
    public Iterator<Row> iterator() {
        return index.iterator();
    }
}