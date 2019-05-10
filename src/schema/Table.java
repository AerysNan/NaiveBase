package schema;

import index.*;
import storage.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Table {
    String name;
    ArrayList<Column> columns;
    BPlusTree<Entry, Row> index;
    BufferPool bufferPool;

    public static final int maxPageNum = 256;
    public static final int maxPageSize = 4096;

    public Table(String name, Column[] columns) throws Exception {
        this.name = name;
        this.columns = new ArrayList<>(Arrays.asList(columns));
        this.index = new BPlusTree<>();
        this.bufferPool = new BufferPool(name);
        recoverTable();
    }

    public void recoverTable() throws Exception {
        File path = new File(Manager.dataPath);
        File[] files = path.listFiles();
        if (files.length == 1 && files[0].getName() == "metadata") {
            return;
        }
        for (File f : files) {
            //TODO reduce time complexity
            if (!f.getName().startsWith(name))
                continue;
            Page page = BufferPool.DeserializePerson(f.getName());
            int pageId = page.getId();
            int size = bufferPool.size();
            for(Row row:page.getRows()) {
                for (int i = 0; i < columns.size(); i++)
                    if (columns.get(i).primary) {
                        row.getEntries().get(i).setPageId(pageId);
                        index.put(row.getEntries().get(i), size < maxPageNum ? row : null);
                        break;
                    }
            }
            bufferPool.setPageNum(pageId);
            if(size < maxPageNum) {
                bufferPool.addPage(page);
            }
        }
        bufferPool.updatePageNum();
    }


    public void insert(Entry[] entries) throws Exception {
        if (entries.length != columns.size())
            throw new IOException("Column and Entry size error");
        int pageId = bufferPool.getInsertPageId();
        Row row = new Row(entries);
        for (int i = 0; i < entries.length; i++) {
            if (columns.get(i).primary) {
                row.getEntries().get(i).setPageId(pageId);
                index.put(row.getEntries().get(i), row);
                break;
            }
        }
        bufferPool.addRow(row);
    }

    public Row get(Entry entry) throws Exception{
        //需保证entry 为主键
        Entry intactEntry = index.containsKey(entry);
        if(intactEntry == null){
            throw  new IOException("key doesn't exist");
        }else{
            Row ans = index.get(intactEntry);
            if(ans == null){
                Page oldPage = bufferPool.putLRUPageToDisk();
                for(Row row:oldPage.getRows()){
                    for (int i = 0; i < columns.size(); i++)
                        if (columns.get(i).primary) {
                            index.put(row.getEntries().get(i), null);
                            break;
                        }
                 }
                 Page newPage = bufferPool.getPageFromDisk(intactEntry);
                 for(Row row:newPage.getRows()){
                     for (int i = 0; i < columns.size(); i++)
                         if (columns.get(i).primary) {
                             index.put(row.getEntries().get(i), row);
                             break;
                         }
                 }
                 return index.get(intactEntry);
            }else{
                bufferPool.getRow(intactEntry.getPageId());
                return ans;
            }
        }
    }

    public void delete(Entry[] entries) throws Exception {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).primary) {
                Row row = get(entries[i]);
                index.remove(row.getEntries().get(i));
                bufferPool.deleteRow(row,row.getEntries().get(i).getPageId());
                break;
            }
        }
    }

    public void update(Entry[] entries) throws Exception {
        Row newRow = new Row(entries);
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).primary) {
                Row oldRow = get(entries[i]);
                index.put(oldRow.getEntries().get(i),newRow);
                bufferPool.updateRow(oldRow,newRow,newRow.getEntries().get(i).getPageId());
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


    public void commit() throws IOException {
        bufferPool.commit();
    }
}