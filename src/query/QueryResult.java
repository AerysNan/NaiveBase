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

        public QueryRecord(int id) {
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

    ArrayList<Column> header;
    ArrayList<Integer> index;
    String[] selectProjects;
    int queryResultNum;

    public QueryResult(ArrayList<Column> header, String[] selectProjects) {
        this.header = header;
        this.selectProjects = selectProjects;
        this.queryResultNum = 0;
        if (selectProjects != null) {
            this.index = new ArrayList<>();
            for (int i = 0; i < selectProjects.length; i++) {
                if (!hasColumn(selectProjects[i]))
                    throw new ColumnNotFoundException(selectProjects[i]);
                else {
                    index.add(i);
                }
            }
        }
    }

    public String generateQueryRecord(Row row) {
        QueryRecord record = new QueryRecord(++queryResultNum);
        if (index == null) {
            for (int i = 0; i < row.getEntries().size(); i++)
                if (header.get(i).getName() != "uid")
                    record.add(row.getEntries().get(i));
        } else {
            for (int i = 0; i < index.size(); i++)
                if (header.get(i).getName() != "uid")
                    record.add(row.getEntries().get(index.get(i)));
        }
        return record.toString();
    }


    private boolean hasColumn(String name) {
        for (Column c : header)
            if (name.equals(c.getName()))
                return true;
        return false;
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