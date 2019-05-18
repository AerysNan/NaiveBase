package query;

import schema.Row;
import schema.Table;

import java.util.Iterator;

public class JointTable implements Iterator<Row> {
    private Iterator<Row> iterator;

    public JointTable(Table table) {
        iterator = table.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Row next() {
        return iterator.next();
    }
}