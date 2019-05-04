package schema;

public class Entry implements Comparable<Entry> {
    int id;
    Object value;
    Table table;

    public Entry(int id, Object value, Table table) {
        this.id = id;
        this.value = value;
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