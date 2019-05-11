package schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Row implements Serializable {

    private static final long serialVersionUID = -5809782578272943999L;
    private ArrayList<Entry> entries;

    public Row(Entry[] entries) {
        this.entries = new ArrayList<>(Arrays.asList(entries));
    }

    public void add(Entry e) {
        entries.add(e);
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry e : entries)
            sb.append(e.toString()).append(',');
        return sb.toString();
    }
}