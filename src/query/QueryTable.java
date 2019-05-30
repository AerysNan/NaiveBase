package query;

import exception.ColumnNotFoundException;
import exception.InvalidComparisionException;
import schema.Column;
import schema.Row;
import schema.Table;
import schema.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class QueryTable implements Iterator<Row> {
    public LinkedList<Row> queue;
    public LinkedList<Row> buffer;
    public Condition whereCondition;
    public boolean isFirst;

    public void figure() {
    }

    public void setWhereCondition(Condition whereCondition) {
        this.whereCondition = whereCondition;
    }

    public static boolean staticTypeCheck(Condition whereCondition) {
        assert whereCondition.comparer.type != ComparerType.COLUMN;
        assert whereCondition.comparee.type != ComparerType.COLUMN;
        switch (whereCondition.comparer.type) {
        case NULL:
            if (whereCondition.comparee.type.equals(ComparerType.NULL)) {
                return whereCondition.type.equals(ComparatorType.EQ) || whereCondition.type.equals(ComparatorType.LE)
                        || whereCondition.type.equals(ComparatorType.GE);
            } else {
                throw new InvalidComparisionException();
            }
        case STRING:
            if (whereCondition.comparee.type.equals(ComparerType.STRING)) {
                int result = whereCondition.comparer.value.compareTo(whereCondition.comparee.value);
                return comparatorTypeCheck(whereCondition.type, result);
            } else
                throw new InvalidComparisionException();
        case NUMBER:
            if (whereCondition.comparee.type.equals(ComparerType.NUMBER)) {
                Double comparer = (Double.parseDouble(String.valueOf(whereCondition.comparer.value)));
                Double comparee = (Double.parseDouble(String.valueOf(whereCondition.comparee.value)));
                int result = comparer.compareTo(comparee);
                return comparatorTypeCheck(whereCondition.type, result);
            } else
                throw new InvalidComparisionException();
        }
        return false;
    }

    public static boolean comparatorTypeCheck(ComparatorType type, int result) {
        switch (type) {
        case NE:
            return result != 0;
        case EQ:
            return result == 0;
        case LT:
            return result < 0;
        case LE:
            return result <= 0;
        case GT:
            return result > 0;
        case GE:
            return result >= 0;
        }
        return false;
    }

    public int columnFind(ArrayList<Column> columns, String name) {
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

    public Condition swapCondition(Condition whereCondition) {
        Comparer newComparer = new Comparer(whereCondition.comparee);
        Comparer newComparee = new Comparer(whereCondition.comparer);
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
        return new Condition(newComparer, newComparee, newType);
    }

    public void columnTypeCheck(Table table1, Table table2, int index1, int index2) {
        if (!table1.columns.get(index1).getType().equals(Type.STRING)
                && table2.columns.get(index2).getType().equals(Type.STRING)) {
            throw new InvalidComparisionException();
        }
        if (table1.columns.get(index1).getType().equals(Type.STRING)
                && !table2.columns.get(index2).getType().equals(Type.STRING)) {
            throw new InvalidComparisionException();
        }
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
            while (!queue.isEmpty()) {
                buffer.add(queue.poll());
            }
            figure();
        }
        if (buffer.isEmpty()) {
            return null;
        }
        return buffer.poll();
    }
}