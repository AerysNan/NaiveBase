package storage;

import schema.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

public class BufferPool {
    String databaseName;
    String name;
    HashMap<Integer, Page> pages;
    HashMap<Integer, Long> times;
    private int pageNum;

    public BufferPool(String databaseName, String name) {
        this.databaseName = databaseName;
        this.name = name;
        this.pages = new HashMap<>();
        this.times = new HashMap<>();
    }

    public void addPage(Page page) throws IOException {
        if (pages.size() >= Table.maxPageNum) {
            putLRUPageToDisk();
        }
        pages.put(page.getId(), page);
        times.put(page.getId(), System.currentTimeMillis());
    }

    public void recoverPageNum() throws Exception{
        Page page;
        if(pages.size() == 0){
            page = new Page(this.databaseName, this.name,0);
        }else {
            page = DeserializePerson(Page.concatPageName(this.databaseName, this.name, pageNum));
        }
        page.setDirty();
        addPage(page);
    }

    public void updatePageNum() throws IOException{
        Page page = new Page(this.databaseName, this.name, ++pageNum);
        page.setDirty();
        addPage(page);
    }

    public void addRow(Row row) throws Exception {
        int size = pages.get(pageNum).add(row);
        times.put(pages.get(pageNum).getId(), System.currentTimeMillis());
        if (size >= Table.maxPageSize) {
            updatePageNum();
        }
    }

    public void getRow(int pageId) {
        times.put(pageId, System.currentTimeMillis());
    }

    public void deleteRow(Row row, int pageId) {
        times.put(pageId, System.currentTimeMillis());
        pages.get(pageId).remove(row);
    }

    public void updateRow(Row oldRow, Row newRow, int pageId) {
        times.put(pageId, System.currentTimeMillis());
        pages.get(pageId).update(oldRow, newRow);
    }

    private int getLRUCachePageId() {
        List<Map.Entry<Integer, Long>> list = new ArrayList(times.entrySet());
        Collections.sort(list, (o1, o2) -> (o1.getValue().compareTo(o2.getValue())));
        return list.get(0).getKey();
    }

    public void setPageNum(int num) {
        if (num > pageNum) {
            pageNum = num;
        }
    }

    public int size() {
        return pages.size();
    }

    public Page putLRUPageToDisk() throws IOException {
        int id = getLRUCachePageId();
        Page page = pages.get(id);
        if (page.isDirty()) {
            SerializePage(page);
        }
        pages.remove(id);
        times.remove(id);
        return page;
    }

    public Page getPageFromDisk(Entry entry) throws Exception {
        int pageId = entry.getPageId();
        String name = Page.concatPageName(this.databaseName, this.name, pageId);
        Page page = DeserializePerson(name);
        pages.put(pageId, page);
        times.put(pageId, System.currentTimeMillis());
        return page;
    }

    public static void SerializePage(Page page) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(page.getName())));
        oos.writeObject(page);
        oos.close();
    }

    public static Page DeserializePerson(String name) throws Exception, IOException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(name)));
        Page page = (Page) ois.readObject();
        ois.close();
        return page;
    }

    public int getInsertPageId() {
        return pageNum;
    }

    public void commit() throws IOException {
        for (Page page : pages.values()) {
            if (page.isDirty()) {
                SerializePage(page);
            }
        }
    }
}
