package schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

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
        StringJoiner sj = new StringJoiner(",");
        for (Entry e : entries)
            sj.add(e.toString());
        return sj.toString();
    }
}