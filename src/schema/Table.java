package schema;

import index.BPlusTree;
import storage.Page;

import java.io.*;
import java.util.*;

public class Table {
    String databaseName;
    String tableName;
    ArrayList<Column> columns;
    transient BPlusTree<Entry, Row> index;

    HashMap<Integer, Page> pages;
    HashMap<Integer, Long> times;
    private int pageNum;

    public Table(String databaseName, String tableName, Column[] columns) throws Exception {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.columns = new ArrayList<>(Arrays.asList(columns));
        this.index = new BPlusTree<>();
        pages = new HashMap<>();
        times = new HashMap<>();
        recoverTable();
        recoverPageNum();
    }

    public void recoverTable() throws Exception {
        File path = new File(Manager.dataPath);
        File[] files = path.listFiles();
        if (files.length == 1 && files[0].getName() == "metadata") {
            return;
        }
        for (File f : files) {
            if (!f.getName().startsWith(databaseName + "_" + tableName))
                continue;
            ArrayList<Row> rows = Deserialize(Manager.dataPath + f.getName());
            if (rows == null || rows.size() == 0) {
                File unUsedFile = new File(Manager.dataPath + f.getName());
                unUsedFile.delete();
                continue;
            }
            Page page = null;
            for (Row row : rows) {
                for (int i = 0; i < columns.size(); i++) {
                    row.getEntries().get(i).setTable(this);
                    if (columns.get(i).primary) {
                        index.put(row.getEntries().get(i), pages.size() < Manager.maxPageNum ? row : null);
                        int pageId = row.getEntries().get(i).getPageId();
                        page = new Page(this.databaseName, this.tableName, pageId);
                        page.addRow(row.getEntries().get(i), row.toString().length());
                        setPageNum(pageId);
                        break;
                    }
                }
            }
            if (pages.size() < Manager.maxPageNum) {
                addPage(page);
            }
        }
    }

    public void recoverPageNum() throws Exception {
        Page page;
        int pageSize = 0;
        if (pages.size() == 0) {
            page = new Page(this.databaseName, this.tableName, 0);
        } else {
            ArrayList<Row> rows = Deserialize(Page.concatPageName(this.databaseName, this.tableName, pageNum));
            for(Row row:rows){
                pageSize += row.toString().length();
            }
            page = new Page(this.databaseName, this.tableName, pageNum);
            for (Row row : rows) {
                for (int i = 0; i < columns.size(); i++) {
                    if (columns.get(i).primary) {
                        if(pageSize < Manager.maxPageSize) {
                            index.put(row.getEntries().get(i), row);
                        }
                        page.addRow(row.getEntries().get(i), row.toString().length());
                        break;
                    }
                }
            }
        }
        if (pageSize >= Manager.maxPageSize) {
            updatePageNum();
        } else {
            addPage(page);
        }
    }

    public void addPage(Page page) throws IOException {
        if (pages.size() >= Manager.maxPageNum) {
            putLRUPageToDisk();
        }

        pages.put(page.getId(), page);
        times.put(page.getId(), System.currentTimeMillis());
    }

    public void addRow(Row row) throws Exception {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).primary) {
                int size = pages.get(pageNum).addRow(row.getEntries().get(i), row.toString().length());
                pages.get(pageNum).setDirty();
                if (size >= Manager.maxPageSize) {
                    updatePageNum();
                }
                break;
            }
        }
        times.put(pages.get(pageNum).getId(), System.currentTimeMillis());
    }


    public void insert(Entry[] entries) throws Exception {
        if (entries.length != columns.size())
            throw new IOException("Column and Entry size error");
        Row row = new Row(entries);
        for (int i = 0; i < entries.length; i++) {
            if (columns.get(i).primary) {
                row.getEntries().get(i).setPageId(pageNum);
                index.put(row.getEntries().get(i), row);
                break;
            }
        }
        addRow(row);
    }

    public Row get(Entry entry) throws Exception {
        //需保证entry 为主键
        Entry intactEntry = index.containsKey(entry);
        if (intactEntry == null) {
            throw new IOException("key doesn't exist");
        } else {
            times.put(entry.getPageId(), System.currentTimeMillis());
            Row ans = index.get(intactEntry);
            if (ans == null) {
                putLRUPageToDisk();
                getPageFromDisk(intactEntry);
                if(pages.size() < 8){
                    int a = 0;
                }
                return index.get(intactEntry);
            } else {
                return ans;
            }
        }
    }

    public void delete(Entry[] entries) throws Exception {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).primary) {
                Row row = get(entries[i]);
                index.remove(row.getEntries().get(i));
                Entry entry = row.getEntries().get(i);
                pages.get(entry.getPageId()).deleteRow(entry, row.toString().length());
                times.put(entry.getPageId(), System.currentTimeMillis());
                pages.get(entry.getPageId()).setDirty();
                break;
            }
        }
    }

    public void update(Entry[] entries) throws Exception {
        Row newRow = new Row(entries);
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).primary) {
                Row oldRow = get(entries[i]);
                index.put(oldRow.getEntries().get(i), newRow);
                Entry entry = oldRow.getEntries().get(i);
                pages.get(entry.getPageId()).updateRow(entry, oldRow.toString().length(), newRow.toString().length());
                times.put(entry.getPageId(), System.currentTimeMillis());
                pages.get(entry.getPageId()).setDirty();
                break;
            }
        }

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

    public void Serialize(Page page) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(page.getPath())));
        ArrayList<Row> rowList = new ArrayList<>();
        for (Entry entry:page.getPrimaryEntries()) {
            Row row = this.index.get(entry);
            rowList.add(row);
        }
        oos.writeObject(rowList);
        oos.close();
    }

    public ArrayList<Row> Deserialize(String path) throws Exception, IOException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)));
        try {
            ArrayList<Row> rows = (ArrayList<Row>) ois.readObject();
            ois.close();
            return rows;
        }catch (EOFException e){
            ois.close();
        }
        return null;
    }

    private int getLRUCachePageId() {
        List<Map.Entry<Integer, Long>> list = new ArrayList(times.entrySet());
        Collections.sort(list, (o1, o2) -> (o1.getValue().compareTo(o2.getValue())));
        return list.get(0).getKey();
    }

    public void putLRUPageToDisk() throws IOException {
        int id = getLRUCachePageId();
        Page page = pages.get(id);
        if (page.isDirty()) {
            Serialize(page);
        }
        for (Entry entry : page.getPrimaryEntries()) {
            this.index.put(entry, null);
        }
        pages.remove(id);
        times.remove(id);
    }

    public Page getPageFromDisk(Entry entry) throws Exception {
        int pageId = entry.getPageId();
        String path = Page.concatPageName(this.databaseName, this.tableName, pageId);
        ArrayList<Row> rows = Deserialize(path);
        if (rows == null || rows.size() == 0) {
            return null;
        }
        Page page = new Page(this.databaseName, this.tableName, pageId);
        for (Row row : rows) {
            page.addRow(entry, row.toString().length());
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).primary) {
                    row.getEntries().get(i).setTable(this);
                    index.put(row.getEntries().get(i), row);
                    break;
                }
            }
        }
        pages.put(pageId, page);
        times.put(pageId, System.currentTimeMillis());
        return page;
    }

    public void setPageNum(int num) {
        if (num > pageNum) {
            pageNum = num;
        }
    }

    public void updatePageNum() throws IOException {
        Page page = new Page(this.databaseName, this.tableName, ++pageNum);
        addPage(page);
    }

    public int pagesSize(){
        return pages.size();
    }

    public BPlusTree<Entry,Row> getIndex(){
        return index;
    }

    public void commit() throws IOException {
        for (Page page : pages.values()) {
            if (page.isDirty()) {
                Serialize(page);
            }
        }
    }
}