package schema;

import java.util.ArrayList;

public class CompositeKey {
    private ArrayList<Entry> entries;

    CompositeKey(ArrayList<Entry> entries) {
        this.entries = new ArrayList<>(entries);
    }

    @Override
    public int hashCode() {
        return entries.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        CompositeKey key = (CompositeKey) obj;
        int n = entries.size();
        if (key.entries.size() != n)
            return false;
        for (int i = 0; i < n; i++)
            if (!entries.get(i).equals(key.entries.get(i)))
                return false;
        return true;
    }
}
