package schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Row implements Serializable {

    private static final long serialVersionUID = -5809782578272943999L;
    private ArrayList<Entry> entries;
    private int pageID;

    public Row(int pageID) {
        this.pageID = pageID;
    }

    public Row(Entry[] entries, int pageID) {
        this.entries = new ArrayList<>(Arrays.asList(entries));
        this.pageID = pageID;
    }

    int getPageID() {
        return pageID;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public String toString() {
        if (entries == null)
            return "EMPTY";
        StringBuilder sb = new StringBuilder();
        for (Entry e : entries)
            sb.append(e.toString()).append(',');
        return sb.toString();
    }
}