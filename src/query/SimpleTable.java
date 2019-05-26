package query;

import exception.NumberCompareNotNumberException;
import exception.StringCompareNotStringException;
import schema.*;

import java.util.*;

public class SimpleTable extends QueryTable implements Iterator<Row> {
    public Table table;
    private WhereCondition whereCondition;
    private Iterator<Row> iterator;

    public SimpleTable(Table table, WhereCondition whereCondition) {
        this.table = table;
        this.whereCondition = whereCondition;
        this.iterator = table.iterator();
    }

    public ArrayList<Row> figure() {
        ArrayList<Row> rowList = new ArrayList<>();
        if (whereCondition == null) {
            while (hasNext()) {
                rowList.add(next());
            }
        } else {
            if (!(whereCondition.comparer.type.equals(ComparerType.COLUMN) || whereCondition.comparee.type.equals(ComparerType.COLUMN))) {
                boolean result = staticTypeCheck(whereCondition);
                if (result) {
                    while (hasNext()) {
                        rowList.add(next());
                    }
                }
            } else if (whereCondition.comparer.type.equals(ComparerType.COLUMN) && whereCondition.comparee.type.equals(ComparerType.COLUMN)) {
                int foundComparer = columnFind(table.columns, (String) whereCondition.comparer.value);
                int foundComparee = columnFind(table.columns, (String) whereCondition.comparee.value);
                if (table.columns.get(foundComparer).getType().equals(Type.STRING) &&
                        !table.columns.get(foundComparee).getType().equals(Type.STRING)) {
                    throw new StringCompareNotStringException();
                }
                if (!table.columns.get(foundComparer).getType().equals(Type.STRING) &&
                        table.columns.get(foundComparee).getType().equals(Type.STRING)) {
                    throw new NumberCompareNotNumberException();
                }
                while (hasNext()) {
                    Row row = next();
                    int result;
                    if (table.columns.get(foundComparer).getType().equals(Type.STRING))
                        result = ((String) whereCondition.comparer.value).compareTo((String) whereCondition.comparee.value);
                    else
                        result = ((Double) whereCondition.comparer.value).compareTo((Double) whereCondition.comparee.value);
                    boolean right = comparatorTypeCheck(whereCondition.type, result);
                    if (right) {
                        rowList.add(row);
                    }
                }
            } else {
                if (whereCondition.comparer.type.equals(ComparerType.COLUMN)) {
                    rowList.addAll(getRowsFromcolumnCompare(table, whereCondition));
                } else {
                    Comparer newComparer = new Comparer(whereCondition.comparee);
                    Comparer newComparee = new Comparer(whereCondition.comparer);
                    if (whereCondition.type == ComparatorType.GE) {
                        whereCondition.type = ComparatorType.LE;
                    } else if (whereCondition.type == ComparatorType.LE) {
                        whereCondition.type = ComparatorType.GE;
                    } else if (whereCondition.type == ComparatorType.GT) {
                        whereCondition.type = ComparatorType.LT;
                    } else if (whereCondition.type == ComparatorType.LT) {
                        whereCondition.type = ComparatorType.GT;
                    }
                    WhereCondition newWhereCondition = new WhereCondition(newComparer, newComparee, whereCondition.type);
                    rowList.addAll(getRowsFromcolumnCompare(table, newWhereCondition));
                }
            }
        }
        return rowList;
    }


    public ArrayList<Row> getRowsFromcolumnCompare(Table table, WhereCondition whereCondition) {
        ArrayList<Row> rowList = new ArrayList<>();
        int index = columnFind(table.columns, (String) whereCondition.comparer.value);
        if (table.columns.get(index).getPrimary() == 1) {
            switch (whereCondition.type) {
                case EQ: {
                    Row row = table.index.get(new Entry(index, (Comparable) whereCondition.comparer.value));
                    rowList.add(row);
                    break;
                }
                case NE: {
                    while (hasNext()) {
                        Row row = next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) whereCondition.comparer.value)) != 0)
                            rowList.add(row);
                    }
                    break;
                }
                case GT: {
                    while (hasNext()) {
                        Row row = next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) whereCondition.comparer.value)) < 0)
                            rowList.add(row);
                    }
                    break;
                }
                case GE: {
                    while (hasNext()) {
                        Row row = next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) whereCondition.comparer.value)) <= 0)
                            rowList.add(row);
                    }
                    break;
                }
                case LT: {
                    while (hasNext()) {
                        Row row = next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) whereCondition.comparer.value)) > 0)
                            rowList.add(row);
                    }
                    break;
                }
                case LE: {
                    while (hasNext()) {
                        Row row = next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) whereCondition.comparer.value)) >= 0)
                            rowList.add(row);
                    }
                    break;
                }
            }
        } else
            switch (whereCondition.type) {
                case EQ: {
                    ArrayList<Row> rows = table.getBySecondaryIndex(table.columns.get(index), new Entry(index, (Comparable) whereCondition.comparee.value));
                    rowList.addAll(rows);
                    break;
                }
                case NE: {
                    while (hasNext()) {
                        Row row = next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) whereCondition.comparer.value)) != 0) {
                            ArrayList<Row> rows = table.getBySecondaryIndex(table.columns.get(index), new Entry(index, (Comparable) whereCondition.comparee.value));
                            rowList.addAll(rows);
                        }

                    }
                    break;
                }
                case LT: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index)).headMap(new Entry(index, (Comparable) whereCondition.comparee.value));
                    rowList.addAll(getRowsFromSortedMap(secondaryIndex, index));
                    break;
                }
                case LE: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index)).headMap(new Entry(index, (Comparable) whereCondition.comparee.value));
                    rowList.addAll(getRowsFromSortedMap(secondaryIndex, index));
                    ArrayList<Row> rows = table.getBySecondaryIndex(table.columns.get(index), new Entry(index, (Comparable) whereCondition.comparee.value));
                    rowList.addAll(rows);
                    break;
                }
                case GT: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index)).tailMap(new Entry(index, (Comparable) whereCondition.comparee.value));
                    secondaryIndex.remove(new Entry(index, (Comparable) whereCondition.comparee.value));
                    rowList.addAll(getRowsFromSortedMap(secondaryIndex, index));
                    break;
                }
                case GE: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index)).tailMap(new Entry(index, (Comparable) whereCondition.comparee.value));
                    rowList.addAll(getRowsFromSortedMap(secondaryIndex, index));
                    break;
                }
            }
        return rowList;
    }

    public ArrayList<Row> getRowsFromSortedMap(SortedMap sortedMap, int index) {
        ArrayList<Row> result = new ArrayList<>();
        Iterator s = sortedMap.entrySet().iterator();
        while (s.hasNext()) {
            Map.Entry pair = (Map.Entry) s.next();
            ArrayList<Row> rows = table.getBySecondaryIndex(table.columns.get(index), (Entry) pair.getKey());
            result.addAll(rows);
        }
        return result;
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