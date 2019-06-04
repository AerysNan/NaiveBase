package query;

import schema.Column;
import schema.Row;

import java.util.ArrayList;
import java.util.Iterator;

public class View implements Iterable {
    public String name;
    public ArrayList<Row> rows;
    public ArrayList<Column> columns;

    public View(ArrayList<Column> columns) {
        super();
        this.rows = new ArrayList<>();
        this.columns = columns;
    }

    public void insert(Row row) {
        rows.add(row);
    }

    public void reduceColumns(ArrayList<Integer> index) {
        ArrayList<Column> newColumns = new ArrayList<>();
        for (Integer i : index)
            newColumns.add(columns.get(i));
        columns = newColumns;
    }

    @Override
    public Iterator iterator() {
        return rows.iterator();
    }
}
