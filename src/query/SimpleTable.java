package query;

import schema.*;
import java.util.*;

public class SimpleTable extends QueryTable implements Iterator<Row> {
    public Table table;

    private Iterator<Row> iterator;

    private Iterator<ArrayList<Row>> mapIterator;


    public SimpleTable(Table table) {
        this.table = table;
        this.iterator = table.iterator();
        this.buffer = new LinkedList<>();
        this.queue = new LinkedList<>();
        this.isFirst = true;
    }

    @Override
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
                columnTypeCheck(table, table, foundComparer, foundComparee);
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
                    getRowsFromColumnCompare(table, whereCondition);
                } else {
                    Condition newWhereCondition = swapCondition(whereCondition);
                    getRowsFromColumnCompare(table, newWhereCondition);
                }
            }
        }
    }


    public void getRowsFromColumnCompare(Table table, Condition whereCondition) {
        int index = columnFind(table.columns, (String) whereCondition.comparer.value);
        if (table.columns.get(index).getPrimary() == 1) {
            switch (whereCondition.type) {
                case EQ: {
                    if (isFirst) {
                        Row row = table.index.get(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                        queue.add(row);
                    }
                    break;
                }
                case NE: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index))) != 0) {
                            queue.add(row);
                            break;
                        }
                    }
                    break;
                }
                case LT: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index))) < 0) {
                            queue.add(row);
                            break;
                        }
                    }
                    break;
                }
                case LE: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index))) <= 0) {
                            queue.add(row);
                            break;
                        }
                    }
                    break;
                }
                case GT: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index))) > 0) {
                            queue.add(row);
                            break;
                        }
                    }
                    break;
                }
                case GE: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index))) >= 0) {
                            queue.add(row);
                            break;
                        }
                    }
                    break;
                }
            }
        } else
            switch (whereCondition.type) {
                case EQ: {
                    if (isFirst) {
                        ArrayList<Row> rows = table.getBySecondaryIndex(table.columns.get(index), new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                        queue.addAll(rows);
                    }
                    break;
                }
                case NE: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index))) != 0) {
                            ArrayList<Row> rows = table.getBySecondaryIndex(table.columns.get(index), row.getEntries().get(index));
                            queue.addAll(rows);
                        }
                    }
                    break;
                }
                case LT: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index).getName()).headMap(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                    if (this.mapIterator == null)
                        this.mapIterator = secondaryIndex.entrySet().iterator();
                    getRowsFromSortedMap(secondaryIndex, index);
                    break;
                }
                case LE: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index).getName()).headMap(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                    if (this.mapIterator == null)
                        this.mapIterator = secondaryIndex.entrySet().iterator();
                    if (mapIterator.hasNext()) {
                        getRowsFromSortedMap(secondaryIndex, index);
                    } else {
                        ArrayList<Row> rows = table.getBySecondaryIndex(table.columns.get(index), new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                        queue.addAll(rows);
                    }
                    break;
                }
                case GT: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index).getName()).tailMap(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                    if (secondaryIndex.size() != 0)
                        secondaryIndex.remove(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                    if (this.mapIterator == null)
                        this.mapIterator = secondaryIndex.entrySet().iterator();
                    getRowsFromSortedMap(secondaryIndex, index);
                    break;
                }
                case GE: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index).getName()).tailMap(new Entry(index, (Comparable) table.parseValue(String.valueOf(whereCondition.comparee.value), index)));
                    if (this.mapIterator == null)
                        this.mapIterator = secondaryIndex.entrySet().iterator();
                    getRowsFromSortedMap(secondaryIndex, index);
                    break;
                }
            }
    }

    public void getRowsFromSortedMap(SortedMap sortedMap, int index) {
        if (sortedMap.size() == 0)
            return;
        while (mapIterator.hasNext()) {
            Map.Entry pair = (Map.Entry) mapIterator.next();
            ArrayList<Row> rows = table.getBySecondaryIndex(table.columns.get(index), (Entry) pair.getKey());
            queue.addAll(rows);
            return;
        }
    }
}