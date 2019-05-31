package query;

import exception.ColumnNotFoundException;
import schema.Column;
import schema.Entry;
import schema.Row;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringJoiner;

public class QueryResult implements Iterator<QueryResult.QueryRecord> {
    class QueryRecord {
        int id;
        ArrayList<Entry> entries;

        QueryRecord(int id) {
            this.id = id;
            this.entries = new ArrayList<>();
        }

        public void add(Entry entry) {
            entries.add(entry);
        }

        public String toString() {
            if (entries.size() == 0)
                return "";
            StringJoiner sj = new StringJoiner(", ");
            for (Entry entry : entries)
                sj.add(entry.toString());
            return id + " | " + sj.toString();
        }
    }

    private ArrayList<Column> header;
    private ArrayList<Integer> index;
    private int queryResultNum;

    public QueryResult(ArrayList<Column> header, String[] selectProjects) {
        this.header = header;
        this.queryResultNum = 0;
        if (selectProjects != null) {
            this.index = new ArrayList<>();
            for (String selectProject : selectProjects) {
                int pos = getColumnIndex(selectProject);
                if (pos < 0)
                    throw new ColumnNotFoundException(selectProject);
                else
                    index.add(pos);
            }
        }
    }

    public String generateQueryRecord(Row row) {
        QueryRecord record = new QueryRecord(++queryResultNum);
        if (index == null) {
            for (int i = 0; i < row.getEntries().size(); i++)
                if (!header.get(i).getName().equals("uid"))
                    record.add(row.getEntries().get(i));
        } else {
            for (int i = 0; i < index.size(); i++)
                if (!header.get(i).getName().equals("uid"))
                    record.add(row.getEntries().get(index.get(i)));
        }
        return record.toString();
    }


    private int getColumnIndex(String name) {
        for (int i = 0; i < header.size(); i++)
            if (name.equals(header.get(i).getName()))
                return i;
        return -1;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public QueryRecord next() {
        return null;
    }
}