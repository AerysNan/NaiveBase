package schema;

import java.io.Serializable;

public class Entry implements Comparable<Entry>,Serializable {

    private static final long serialVersionUID = -5809782578272943999L;
    int id;
    Object value;
    private transient Table table;

    public Entry(int id, Object value) {
        this.id = id;
        this.value = value;
    }

    void setTable(Table table) {
        this.table = table;
    }

    @Override
    public int compareTo(Entry e) {
        return table.compareEntries(this, e);
    }

    public String toString() {
        return value.toString();
    }
}