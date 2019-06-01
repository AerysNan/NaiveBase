package query;

import exception.ColumnNotFoundException;
import schema.Column;
import schema.Row;
import type.ComparatorType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class QueryTable implements Iterator<Row> {
    LinkedList<Row> queue;
    LinkedList<Row> buffer;
    Logic selectLogic;
    boolean isFirst;
    ArrayList<Column> columns;

    public abstract void figure();

    public void setSelectLogic(Logic selectLogic) {
        this.selectLogic = selectLogic;
    }

    int columnFind(ArrayList<Column> columns, String name) {
        int found = -1;
        for (int i = 0; i < columns.size(); i++) {
            if (name.equals(columns.get(i).getName())) {
                found = i;
            }
        }
        if (found == -1)
            throw new ColumnNotFoundException(name);
        return found;
    }

    Condition swapCondition(Condition whereCondition) {
        Expression left = new Expression(whereCondition.right);
        Expression right = new Expression(whereCondition.left);
        ComparatorType newType = whereCondition.type;
        if (whereCondition.type == ComparatorType.GE) {
            newType = ComparatorType.LE;
        } else if (whereCondition.type == ComparatorType.LE) {
            newType = ComparatorType.GE;
        } else if (whereCondition.type == ComparatorType.GT) {
            newType = ComparatorType.LT;
        } else if (whereCondition.type == ComparatorType.LT) {
            newType = ComparatorType.GT;
        }
        return new Condition(left, right, newType);
    }

    @Override
    public boolean hasNext() {
        return isFirst || !buffer.isEmpty() || !queue.isEmpty();
    }

    @Override
    public Row next() {
        if (buffer.isEmpty()) {
            if (isFirst) {
                figure();
                isFirst = false;
            }
            while (!queue.isEmpty())
                buffer.add(queue.poll());
            figure();
        }
        if (buffer.isEmpty())
            return null;
        return buffer.poll();
    }
}