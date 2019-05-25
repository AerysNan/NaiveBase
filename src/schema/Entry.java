package schema;

import java.io.Serializable;

public class Entry implements Comparable<Entry>,Serializable {

    private static final long serialVersionUID = -5809782578272943999L;
    int id;
    Comparable value;

    public Entry(int id, Comparable value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public int compareTo(Entry e) {
        return value.compareTo(e.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        Entry e = (Entry) obj;
        return value.equals(e.value);
    }

    public String toString() {
        return value.toString();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}