package query;

import exception.NumberCompareNotNumberException;
import exception.StringCompareNotStringException;
import schema.*;

import java.util.*;

public class SimpleTable extends QueryTable implements Iterator<Row> {
    public Table table;
    private WhereCondition whereCondition;
    private Iterator<Row> iterator;
    private LinkedList<Row> queue;
    private LinkedList<Row> buffer;

    public SimpleTable(Table table) {
        this.table = table;
        this.iterator = table.iterator();
        this.buffer = new LinkedList<>();
        this.queue = new LinkedList<>();
    }

    public void setWhereCondition(WhereCondition whereCondition) {
        this.whereCondition = whereCondition;
    }

    public void figure() {
        if (whereCondition == null) {
            while (iterator.hasNext()) {
                queue.add(iterator.next());
                break;
            }
        } else {
            if (!(whereCondition.comparer.type.equals(ComparerType.COLUMN) || whereCondition.comparee.type.equals(ComparerType.COLUMN))) {
                boolean result = staticTypeCheck(whereCondition);
                if (result) {
                    while (iterator.hasNext()) {
                        queue.add(iterator.next());
                        break;
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
                while (iterator.hasNext()) {
                    Row row = iterator.next();
                    int result;
                    if (table.columns.get(foundComparer).getType().equals(Type.STRING))
                        result = (row.getEntries().get(foundComparer)).compareTo(row.getEntries().get(foundComparee));
                    else {
                        Double comparer = (Double.parseDouble(String.valueOf(row.getEntries().get(foundComparer))));
                        Double comparee = (Double.parseDouble(String.valueOf(row.getEntries().get(foundComparee))));
                        result = comparer.compareTo(comparee);
                    }
                    boolean right = comparatorTypeCheck(whereCondition.type, result);
                    if (right) {
                        queue.add(row);
                        break;
                    }
                }
            } else {
                if (whereCondition.comparer.type.equals(ComparerType.COLUMN)) {
                    queue.addAll(getRowsFromColumnCompare(table, whereCondition));
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
                    queue.addAll(getRowsFromColumnCompare(table, newWhereCondition));
                }
            }
        }
    }


    public ArrayList<Row> getRowsFromColumnCompare(Table table, WhereCondition whereCondition) {
        ArrayList<Row> rowList = new ArrayList<>();
        int index = columnFind(table.columns, (String) whereCondition.comparer.value);
        if (table.columns.get(index).getPrimary() == 1) {
            switch (whereCondition.type) {
                case EQ: {
                    Row row = table.index.get(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                    rowList.add(row);
                    break;
                }
                case NE: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index))) != 0)
                            rowList.add(row);
                    }
                    break;
                }
                case LT: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index))) < 0)
                            rowList.add(row);
                    }
                    break;
                }
                case LE: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index))) <= 0)
                            rowList.add(row);
                    }
                    break;
                }
                case GT: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index))) > 0)
                            rowList.add(row);
                    }
                    break;
                }
                case GE: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index))) >= 0)
                            rowList.add(row);
                    }
                    break;
                }
            }
        } else
            switch (whereCondition.type) {
                case EQ: {
                    ArrayList<Row> rows = table.getBySecondaryIndex(table.columns.get(index), new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                    rowList.addAll(rows);
                    break;
                }
                case NE: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index))) != 0) {
                            ArrayList<Row> rows = table.getBySecondaryIndex(table.columns.get(index), row.getEntries().get(index));
                            rowList.addAll(rows);
                        }
                    }
                    break;
                }
                case LT: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index).getName()).headMap(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                    rowList.addAll(getRowsFromSortedMap(secondaryIndex, index));
                    break;
                }
                case LE: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index).getName()).headMap(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                    rowList.addAll(getRowsFromSortedMap(secondaryIndex, index));
                    ArrayList<Row> rows = table.getBySecondaryIndex(table.columns.get(index), new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                    rowList.addAll(rows);
                    break;
                }
                case GT: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index).getName()).tailMap(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                    if (secondaryIndex.size() != 0)
                        secondaryIndex.remove(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                    rowList.addAll(getRowsFromSortedMap(secondaryIndex, index));
                    break;
                }
                case GE: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index).getName()).tailMap(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                    rowList.addAll(getRowsFromSortedMap(secondaryIndex, index));
                    break;
                }
            }
        return rowList;
    }

    public ArrayList<Row> getRowsFromSortedMap(SortedMap sortedMap, int index) {
        ArrayList<Row> result = new ArrayList<>();
        if (sortedMap.size() == 0)
            return result;
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
        return !buffer.isEmpty() || !queue.isEmpty();
    }

    @Override
    public Row next() {
        if (buffer.isEmpty()) {
            while (!queue.isEmpty()) {
                buffer.add(queue.poll());
            }
            figure();
        }
        return buffer.poll();
    }
}