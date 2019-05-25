package query;

import schema.Row;
import schema.Table;

import java.util.Iterator;

public class JointTable extends QueryTable implements Iterator<Row> {

    private Table table1;
    private Table table2;
    private WhereCondition whereCondition;
    private Iterator<Row> iterator1;

    public JointTable(Table table1,Table table2,WhereCondition whereCondition) {
        this.table1 = table1;
        this.table2 = table2;
        this.whereCondition = whereCondition;
        this.iterator1 = table1.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator1.hasNext();
    }

    @Override
    public Row next() {
        return iterator1.next();
    }
}