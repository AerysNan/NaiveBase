package schema;

import exception.*;
import index.BPlusTree;
import query.ComparatorType;
import query.Comparer;
import query.ComparerType;
import query.Condition;
import storage.Page;

import java.io.*;
import java.util.*;

import static global.Global.*;

public class Table implements Iterable<Row> {
    private String databaseName;
    public String tableName;
    public ArrayList<Column> columns;
    public BPlusTree<Entry, Row> index;
    public HashMap<String, TreeMap<Entry, ArrayList<Entry>>> secondaryIndexList;
    private long uid;
    private boolean hasComposite;
    boolean hasUID;
    private int primaryIndex;
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
        for (Column c : columns) {
            if (c.primary == 2)
                hasComposite = true;
            if (c.name.equals("uid"))
                hasUID = true;
        }
        this.index = new BPlusTree<>();
        this.secondaryIndexList = new HashMap<>();
        for (int i = 0; i < columns.length; i++) {
            if (this.columns.get(i).getPrimary() == 1)
                primaryIndex = i;
            else {
                TreeMap<Entry, ArrayList<Entry>> treeMap = new TreeMap<>();
                secondaryIndexList.put(this.columns.get(i).name, treeMap);
            }
        }
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
                    } else
                        insertSecondaryIndex(row, i);
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

    public void insert(String[] values, String[] columnNames) {
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
                    throw new ValueFormatException();
                }
            else {
                boolean found = false;
                for (int j = 0; j < columnNames.length; j++)
                    if (columnNames[j].equals(column.name)) {
                        try {
                            value = parseValue(values[j], i);
                        } catch (Exception e) {
                            throw new ValueFormatException();
                        }
                        found = true;
                        break;
                    }
                if (!found && column.notNull)
                    throw new NullValueException(column.name);
            }
            if (column.type == Type.STRING && value != null && String.valueOf(value).length() > column.maxLength)
                throw new StringExceedMaxLengthException(column.name);
            entries[i] = new Entry(i, (Comparable) value);
        }
        if (hasUID) {
            Entry uidEntry = new Entry(entries.length - 1, ++uid);
            entries[entries.length - 1] = uidEntry;
            if (hasComposite) {
                CompositeKey compositeKey = getCompositeKey(new ArrayList<>(Arrays.asList(entries)));
                if (compositeKeyMap.containsKey(compositeKey))
                    throw new DuplicateKeyException();
                compositeKeyMap.put(getCompositeKey(new ArrayList<>(Arrays.asList(entries))), uidEntry);
            }
        }
        Row row = new Row(entries, pageNum);
        for (int i = 0; i < entries.length; i++) {
            if (columns.get(i).primary == 1) {
                index.put(row.getEntries().get(i), row);
                int size = pages.get(pageNum).addRow(row.getEntries().get(i), row.toString().length());
                pages.get(pageNum).setDirty();
                if (size >= maxPageSize)
                    allocateNewPage();
            } else
                insertSecondaryIndex(row, i);
        }
        times.put(pages.get(pageNum).getID(), System.currentTimeMillis());
    }

    private void insertSecondaryIndex(Row row, int i) {
        TreeMap<Entry, ArrayList<Entry>> secondaryIndex = secondaryIndexList.get(columns.get(i).getName());
        ArrayList<Entry> primaryEntryList = secondaryIndex.get(row.getEntries().get(i));
        if (primaryEntryList == null)
            primaryEntryList = new ArrayList<>();
        primaryEntryList.add(row.getEntries().get(primaryIndex));
        secondaryIndex.put(row.getEntries().get(i), primaryEntryList);
        secondaryIndexList.put(columns.get(i).name, secondaryIndex);
    }

    private void deleteSecondaryIndex(Row row, int i) {
        TreeMap<Entry, ArrayList<Entry>> secondaryIndex = secondaryIndexList.get(columns.get(i).getName());
        ArrayList<Entry> primaryEntryList = secondaryIndex.get(row.getEntries().get(i));
        if (primaryEntryList == null)
            return;
        primaryEntryList.remove(row.getEntries().get(primaryIndex));
        if (primaryEntryList.isEmpty())
            secondaryIndex.remove(row.getEntries().get(i));
        secondaryIndexList.put(columns.get(i).name, secondaryIndex);
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

    public Row get(Entry[] entries) {
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

    public ArrayList<Row> getBySecondaryIndex(Column column, Entry entry) {
        ArrayList<Entry> primaryIndex = secondaryIndexList.get(column.name).get(entry);
        ArrayList<Row> result = new ArrayList<>();
        if (primaryIndex == null)
            return null;
        for (Entry e : primaryIndex)
            result.add(get(new Entry[] { e }));
        return result;
    }

    String delete(Condition deleteCondition) {
        Iterator<Row> iterator = index.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            Row row = iterator.next();
            if (!satisfiedCondition(deleteCondition, row))
                continue;
            count++;
            Entry key = row.getEntries().get(primaryIndex);
            index.remove(key);
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).primary == 1)
                    continue;
                deleteSecondaryIndex(row, i);
            }
        }
        return "Deleted " + count + " rows.";
    }

    String update(String columnName, Comparer comparer, Condition updateCondition) {
        int columnIndex = -1;
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).name.equals(columnName)) {
                columnIndex = i;
                break;
            }
        }
        if (columnIndex < 0)
            throw new ColumnNotFoundException(columnName);
        Column column = columns.get(columnIndex);
        int optIndex = -1;
        switch (comparer.type) {
            case NULL:
                if (column.primary == 1 || column.notNull)
                    throw new ColumnMismatchException();
                break;
            case STRING:
                if (column.type != Type.STRING)
                    throw new ColumnMismatchException();
                break;
            case NUMBER:
                if (column.type == Type.STRING)
                    throw new ColumnMismatchException();
                break;
            case COLUMN:
                for (int i = 0; i < columns.size(); i++)
                    if (columns.get(i).name.equals(comparer.value))
                        optIndex = i;
                if (optIndex == -1)
                    throw new ColumnNotFoundException((String) comparer.value);
                if (columns.get(optIndex).type != column.type)
                    throw new ColumnMismatchException();
                break;
            default:
                break;
        }
        int count = 0;
        for (Row row : index) {
            if (!satisfiedCondition(updateCondition, row))
                continue;
            count++;
            Entry newEntry = new Entry(columnIndex, getComparerValue(row, comparer));
            if (column.primary == 1) {
                for (int i = 0; i < columns.size(); i++)
                    deleteSecondaryIndex(row, i);
                row.entries.set(columnIndex, newEntry);
                index.update(newEntry, row);
                for (int i = 0; i < columns.size(); i++)
                    insertSecondaryIndex(row, i);
            } else {
                deleteSecondaryIndex(row, columnIndex);
                row.entries.set(columnIndex, newEntry);
                insertSecondaryIndex(row, columnIndex);
            }
        }
        return "Updated " + count + " rows.";
    }

    private Comparable getComparerValue(Row row, Comparer comparer) {
        switch (comparer.type) {
        case COLUMN:
            for (int i = 0; i < columns.size(); i++)
                if (columns.get(i).getName().equals(comparer.value))
                    return row.getEntries().get(i).value;
            throw new ColumnNotFoundException((String) comparer.value);
        case NUMBER:
        case STRING:
            return comparer.value;
        case NULL:
        default:
            return null;
        }
    }

    public void deleteAllPage() {
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

    public int getPageNum() {
        return pageNum;
    }

    public Object parseValue(String s, int index) {
        if (s.equals("null")) {
            if (columns.get(index).notNull)
                throw new NullValueException(columns.get(index).name);
            else
                return null;
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

    public boolean satisfiedCondition(Condition condition, Row row) {
        if (condition == null) {
            return true;
        } else {
            if (!(condition.comparer.type.equals(ComparerType.COLUMN) || condition.comparee.type.equals(ComparerType.COLUMN))) {
                return staticTypeCheck(condition);
            } else if (condition.comparer.type.equals(ComparerType.COLUMN) && condition.comparee.type.equals(ComparerType.COLUMN)) {
                int foundComparer = getIndex(condition.comparer);
                int foundComparee = getIndex(condition.comparee);
                if (!columns.get(foundComparer).getType().equals(Type.STRING) &&
                        columns.get(foundComparee).getType().equals(Type.STRING)) {
                    throw new InvalidComparisionException();
                }
                if (columns.get(foundComparer).getType().equals(Type.STRING) &&
                        !columns.get(foundComparee).getType().equals(Type.STRING)) {
                    throw new InvalidComparisionException();
                }
                int result;
                if (columns.get(foundComparer).getType().equals(Type.STRING))
                    result = (row.getEntries().get(foundComparer)).compareTo(row.getEntries().get(foundComparee));
                else {
                    Double comparer = (Double.parseDouble(String.valueOf(row.getEntries().get(foundComparer))));
                    Double comparee = (Double.parseDouble(String.valueOf(row.getEntries().get(foundComparee))));
                    result = comparer.compareTo(comparee);
                }
                return comparatorTypeCheck(condition.type, result);
            } else if (condition.comparer.type.equals(ComparerType.COLUMN)) {
                return conditionJudge(condition, row);
            } else {
                Condition newCondition = swapCondition(condition);
                return conditionJudge(newCondition, row);
            }
        }
    }

    public Condition swapCondition(Condition whereCondition) {
        Comparer newComparer = new Comparer(whereCondition.comparee);
        Comparer newComparee = new Comparer(whereCondition.comparer);
        ComparatorType newType = whereCondition.type;
        if (whereCondition.type == ComparatorType.GE) {
            newType = ComparatorType.LE;
        } else if (whereCondition.type == ComparatorType.LE) {
            newType = ComparatorType.GE;
        } else if (whereCondition.type == ComparatorType.GT) {
            newType = ComparatorType.LT;
        } else if (whereCondition.type == ComparatorType.LT) {
            newType = ComparatorType.GT;
        }
        return new Condition(newComparer, newComparee, newType);
    }

    public boolean conditionJudge(Condition condition, Row row) {
        int foundComparer = getIndex(condition.comparer);
        int result;
        if (columns.get(foundComparer).getType().equals(Type.STRING)) {
            result = (row.getEntries().get(foundComparer)).compareTo(new Entry(foundComparer, String.valueOf(condition.comparee.value)));
        } else {
            Double comparer = (Double.parseDouble(String.valueOf(row.getEntries().get(foundComparer))));
            Double comparee = (Double.parseDouble(String.valueOf(condition.comparee.value)));
            result = comparer.compareTo(comparee);
        }
        return comparatorTypeCheck(condition.type, result);
    }

    private int getIndex(Comparer comparer) {
        for (int i = 0; i < columns.size(); i++)
            if (columns.get(i).getName().equals(comparer.value))
                return i;
        throw new ColumnNotFoundException((String) comparer.value);
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