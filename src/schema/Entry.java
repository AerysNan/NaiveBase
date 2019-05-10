package schema;

import java.io.Serializable;

public class Entry implements Comparable<Entry>,Serializable {
    private static final long serialVersionUID = -5809782578272943999L;
    int id;
    Object value;
    Table table;
    int pageId;

    public Entry(int id, Object value, Table table) {
        this.id = id;
        this.value = value;
        this.table = table;
    }

    public void setPageId(int index) {
        this.pageId = index;
    }

    public int getPageId() {
        return pageId;
    }

    @Override
    public int compareTo(Entry e) {
        return table.compareEntries(this, e);
    }

    public String toString() {
        return value.toString();
    }
}