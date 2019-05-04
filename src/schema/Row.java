package schema;

import java.util.ArrayList;
import java.util.Arrays;

public class Row {
    private ArrayList<Entry> entries;

    public Row(Entry[] entries) {
        this.entries = new ArrayList<>(Arrays.asList(entries));
    }

    public void add(Entry e) {
        entries.add(e);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry e : entries)
            sb.append(e.toString()).append(',');
        return sb.toString();
    }
}
