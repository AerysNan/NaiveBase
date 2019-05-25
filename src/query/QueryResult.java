package query;

import schema.Column;
import schema.Entry;
import schema.Row;

import java.util.ArrayList;
import java.util.Iterator;

public class QueryResult implements Iterator<QueryResult.QueryRecord> {
    class QueryRecord {
        int id;
        ArrayList<Entry> entries;
    }

    ArrayList<Column> header;
    ArrayList<JointTable> jointTables;

    public QueryResult(ArrayList<Column> header){
        this.header = header;
    }

    public void addRow(Row row){

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
