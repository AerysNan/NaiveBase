package schema;

import exception.*;
import index.BPlusTree;
import query.Logic;
import type.ComparatorType;
import type.ComparerType;
import query.Condition;
import query.Expression;
import storage.Page;
import type.ColumnType;
import type.LogicalOpType;

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
    private boolean hasUID;
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
            if (column.type == ColumnType.STRING && value != null && String.valueOf(value).length() > column.maxLength)
                throw new StringExceedMaxLengthException(column.name);
            entries[i] = new Entry((Comparable) value);
        }
        if (hasUID) {
            Entry uidEntry = new Entry(++uid);
            entries[entries.length - 1] = uidEntry;
            if (hasComposite) {
                CompositeKey compositeKey = getCompositeKey(new ArrayList<>(Arrays.asList(entries)));
                if (compositeKeyMap.containsKey(compositeKey))
                    throw new DuplicateKeyException();
                compositeKeyMap.put(compositeKey, uidEntry);
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

    private CompositeKey getCompositeKey(ArrayList<Entry> entries) {
        ArrayList<Entry> list = new ArrayList<>();
        int n = entries.size();
        for (int i = 0; i < n; i++)
            if (columns.get(i).primary == 2)
                list.add(entries.get(i));
        return new CompositeKey(list);
    }

    public boolean contains(Entry[] entries) {
        Entry entry;
        if (entries.length == 1)
            entry = entries[0];
        else {
            CompositeKey compositeKey = getCompositeKey(new ArrayList<>(Arrays.asList(entries)));
            if (!compositeKeyMap.containsKey(compositeKey))
                return false;
            entry = compositeKeyMap.get(compositeKey);
        }
        return index.contains(entry);
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
            result.add(get(new Entry[]{e}));
        return result;
    }

    String delete(Logic logic) {
        Iterator<Row> iterator = index.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            Row row = iterator.next();
            if (failedLogic(logic, row))
                continue;
            count++;
            Entry key = row.getEntries().get(primaryIndex);
            pages.get(row.getPageID()).deleteRow(key, row.toString().length());
            index.remove(key);
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).primary == 1)
                    continue;
                deleteSecondaryIndex(row, i);
            }
            if (hasComposite) {
                CompositeKey compositeKey = getCompositeKey(row.getEntries());
                compositeKeyMap.remove(compositeKey);
            }
        }
        return "Deleted " + count + " rows.";
    }

    String update(String columnName, Expression expression, Logic logic) {
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
        switch (getColumnType(columnName)) {
            case NULL:
                if (column.primary == 1 || column.notNull)
                    throw new ColumnMismatchException();
                break;
            case STRING:
                if (column.type != ColumnType.STRING)
                    throw new ColumnMismatchException();
                break;
            case NUMBER:
                if (column.type == ColumnType.STRING)
                    throw new ColumnMismatchException();
                break;
            default:
                break;
        }
        int count = 0;
        for (Row row : index) {
            if (failedLogic(logic, row))
                continue;
            count++;
            int oldSize = row.toString().length();
            Entry oldEntry = row.getEntries().get(columnIndex);
            Entry newEntry = new Entry(comparerValueToEntryValue(evalExpressionValue(expression, row), columnIndex));
            CompositeKey oldCompositeKey = null;
            if(hasComposite)
                oldCompositeKey = getCompositeKey(row.getEntries());
            if (column.primary == 1) {
                for (int i = 0; i < columns.size(); i++)
                    if (i != primaryIndex)
                        deleteSecondaryIndex(row, i);
                row.entries.set(columnIndex, newEntry);
                pages.get(row.getPageID()).updatePrimaryEntry(oldEntry, newEntry);
                if (index.contains(newEntry))
                    index.update(newEntry, row);
                else {
                    index.remove(oldEntry);
                    index.put(newEntry, row);
                }
                for (int i = 0; i < columns.size(); i++)
                    if (i != primaryIndex)
                        insertSecondaryIndex(row, i);
            } else {
                deleteSecondaryIndex(row, columnIndex);
                row.entries.set(columnIndex, newEntry);
                insertSecondaryIndex(row, columnIndex);
            }
            if(hasComposite) {
                compositeKeyMap.remove(oldCompositeKey);
                ArrayList<Entry> entries = row.getEntries();
                compositeKeyMap.put(getCompositeKey(entries), entries.get(entries.size() - 1));
            }
            int newSize = row.toString().length();
            pages.get(row.getPageID()).updateSize(oldSize, newSize);
        }
        return "Updated " + count + " rows.";
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

    public Comparable comparerValueToEntryValue(Comparable value, int index) {
        if (value == null)
            return null;
        switch (columns.get(index).type) {
            case STRING:
            case DOUBLE:
                return value;
            case INT:
                return ((Number) value).intValue();
            case FLOAT:
                return ((Number) value).floatValue();
            case LONG:
                return ((Number) value).longValue();
        }
        return null;
    }

    private Object parseValue(String s, int index) {
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

    public boolean failedLogic(Logic logic, Row row) {
        if (logic == null)
            return false;
        if (logic.terminal)
            return failedCondition(logic.condition, row);
        if (logic.logicalOpType == LogicalOpType.AND)
            return failedLogic(logic.left, row) || failedLogic(logic.right, row);
        return failedLogic(logic.left, row) && failedLogic(logic.right, row);
    }

    private boolean failedCondition(Condition condition, Row row) {
        if (condition == null) {
            return false;
        } else {
            ComparerType t1 = evalExpressionType(condition.left);
            ComparerType t2 = evalExpressionType(condition.right);
            if (condition.type == ComparatorType.EQ) {
                if (t1 == ComparerType.NULL && t2 == ComparerType.NULL)
                    return false;
                if (t1 == ComparerType.NULL || t2 == ComparerType.NULL)
                    return true;
                if (t1 == t2) {
                    Comparable v1 = evalExpressionValue(condition.left, row);
                    Comparable v2 = evalExpressionValue(condition.right, row);
                    if (t1 == ComparerType.STRING)
                        return v1.compareTo(v2) != 0;
                    if (v1 == null || v2 == null)
                        throw new InvalidComparisionException();
                    double d1 = ((Number) v1).doubleValue();
                    double d2 = ((Number) v2).doubleValue();
                    return d1 != d2;
                }
                throw new InvalidComparisionException();
            }
            if (condition.type == ComparatorType.NE) {
                if (t1 == ComparerType.NULL && t2 == ComparerType.NULL)
                    return true;
                if (t1 == ComparerType.NULL || t2 == ComparerType.NULL)
                    return false;
                if (t1 == t2) {
                    Comparable v1 = evalExpressionValue(condition.left, row);
                    Comparable v2 = evalExpressionValue(condition.right, row);
                    if (t1 == ComparerType.STRING)
                        return v1.compareTo(v2) == 0;
                    if (v1 == null || v2 == null)
                        throw new InvalidComparisionException();
                    double d1 = ((Number) v1).doubleValue();
                    double d2 = ((Number) v2).doubleValue();
                    return d1 == d2;
                }
                throw new InvalidComparisionException();
            }
            if (!comparisionTypeCheck(condition))
                throw new InvalidComparisionException();
            Comparable v1 = evalExpressionValue(condition.left, row);
            Comparable v2 = evalExpressionValue(condition.right, row);
            if (v1 == null || v2 == null)
                throw new InvalidComparisionException();
            if (t1 == ComparerType.STRING) {
                if (condition.type == ComparatorType.GT)
                    return v1.compareTo(v2) <= 0;
                if (condition.type == ComparatorType.GE)
                    return v1.compareTo(v2) < 0;
                if (condition.type == ComparatorType.LT)
                    return v1.compareTo(v2) >= 0;
                if (condition.type == ComparatorType.LE)
                    return v1.compareTo(v2) > 0;
                return true;
            }
            double d1 = ((Number) v1).doubleValue();
            double d2 = ((Number) v2).doubleValue();
            if (condition.type == ComparatorType.GT)
                return d1 <= d2;
            if (condition.type == ComparatorType.GE)
                return d1 < d2;
            if (condition.type == ComparatorType.LT)
                return d1 >= d2;
            if (condition.type == ComparatorType.LE)
                return d1 > d2;
            return true;
        }
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

    public boolean comparisionTypeCheck(Condition whereCondition) {
        ComparerType t1 = evalExpressionType(whereCondition.left);
        ComparerType t2 = evalExpressionType(whereCondition.right);
        return t1 != null && t1 != ComparerType.NULL && t1 == t2;
    }


    private ComparerType evalExpressionType(Expression expression) {
        if (expression.terminal) {
            switch (expression.comparer.type) {
                case STRING:
                    return ComparerType.STRING;
                case NUMBER:
                    return ComparerType.NUMBER;
                case COLUMN:
                    return getColumnType((String) expression.comparer.value);
                default:
                    return ComparerType.NULL;
            }
        } else {
            ComparerType t1 = evalExpressionType(expression.left);
            ComparerType t2 = evalExpressionType(expression.right);
            if (t1 == ComparerType.NUMBER && t2 == ComparerType.NUMBER)
                return ComparerType.NUMBER;
            throw new InvalidExpressionException();
        }
    }

    private int getColumnIndex(String columnName) {
        for (int i = 0; i < columns.size(); i++)
            if (columns.get(i).getName().equals(columnName))
                return i;
        return -1;
    }

    private ComparerType getColumnType(String columnName) {
        int i = getColumnIndex(columnName);
        if (i < 0)
            throw new ColumnNotFoundException(columnName);
        switch (columns.get(i).type) {
            case LONG:
            case FLOAT:
            case INT:
            case DOUBLE:
                return ComparerType.NUMBER;
            case STRING:
                return ComparerType.STRING;
        }
        throw new ColumnNotFoundException(columnName);
    }

    private Comparable getColumnValue(String columnName, Row row) {
        int i = getColumnIndex(columnName);
        if (i < 0)
            throw new ColumnNotFoundException(columnName);
        return row.getEntries().get(i).value;
    }

    private boolean hasColumn(String columnName) {
        return getColumnIndex(columnName) >= 0;
    }

    private Comparable evalExpressionValue(Expression expression, Row row) {
        if (expression.terminal) {
            switch (expression.comparer.type) {
                case NUMBER:
                case STRING:
                case NULL:
                    return expression.comparer.value;
                case COLUMN:
                    return getColumnValue((String) expression.comparer.value, row);
                default:
                    return null;
            }
        } else {
            Comparable v1 = evalExpressionValue(expression.left, row);
            Comparable v2 = evalExpressionValue(expression.right, row);
            if (v1 == null || v2 == null)
                throw new InvalidExpressionException();
            double d1 = ((Number) v1).doubleValue();
            double d2 = ((Number) v2).doubleValue();
            switch (expression.numericOpType) {
                case ADD:
                    return d1 + d2;
                case DIV:
                    return d1 / d2;
                case SUB:
                    return d1 - d2;
                case MUL:
                    return d1 * d2;
                default:
                    return null;
            }
        }
    }

    @Override
    public Iterator<Row> iterator() {
        return index.iterator();
    }
}