package query;

import exception.InvalidComparisionException;
import exception.KeyNotExistException;
import global.Global;
import schema.Entry;
import schema.Row;
import schema.Table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

public class SimpleTable extends QueryTable implements Iterator<Row> {
    private Table table;
    private Iterator<Row> iterator;
    private Iterator<ArrayList<Row>> mapIterator;

    public SimpleTable(Table table) {
        super();
        this.table = table;
        this.iterator = table.iterator();
        this.columns = table.columns;
    }

    public void reset() {
        this.queue.clear();
        this.buffer.clear();
        this.isFirst = true;
        this.iterator = table.iterator();
    }

    @Override
    public ArrayList<MetaInfo> generateMeta() {
        return new ArrayList<>() {{
            add(new MetaInfo(table.tableName, table.columns));
        }};
    }

    @Override
    public void figure() {
        if (selectLogic == null) {
            if (iterator.hasNext())
                queue.add(iterator.next());
        } else {
            if (selectLogic.terminal) {
                Condition selectCondition = selectLogic.condition;
                if (selectCondition.left.isConstExpression() && selectCondition.right.isConstExpression()) {
                    if (!Global.failedConstCondition(selectCondition))
                        if (iterator.hasNext())
                            queue.add(iterator.next());
                    return;
                } else if (selectCondition.left.isSimpleColumn() && selectCondition.right.isConstExpression()) {
                    getRowsFromColumnComparision(table, selectCondition);
                    return;
                } else if (selectCondition.right.isSimpleColumn() && selectCondition.left.isConstExpression()) {
                    getRowsFromColumnComparision(table, swapCondition(selectCondition));
                    return;
                }
            }
            while (iterator.hasNext()) {
                Row row = iterator.next();
                if (table.failedLogic(selectLogic, row))
                    continue;
                queue.add(row);
                break;
            }
        }
    }

    private void getRowsFromColumnComparision(Table table, Condition whereCondition) {
        if (!table.comparisionTypeCheck(whereCondition))
            throw new InvalidComparisionException();
        Comparable constValue = Global.evalConstExpressionValue(whereCondition.right);
        Comparer comparer = whereCondition.left.comparer;
        int index = columnFind(table.columns, (String) comparer.value);
        if (table.columns.get(index).getPrimary() == 1) {
            switch (whereCondition.type) {
                case EQ: {
                    if (isFirst) {
                        try {
                            Row row = table.index.get(new Entry(table.comparerValueToEntryValue(constValue, index)));
                            queue.add(row);
                        } catch (KeyNotExistException e) {
                            return;
                        }
                    }
                    break;
                }
                case NE: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(table.comparerValueToEntryValue(constValue, index))) != 0) {
                            queue.add(row);
                            break;
                        }
                    }
                    break;
                }
                case LT: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(table.comparerValueToEntryValue(constValue, index))) < 0) {
                            queue.add(row);
                            break;
                        }
                    }
                    break;
                }
                case LE: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(table.comparerValueToEntryValue(constValue, index))) <= 0) {
                            queue.add(row);
                            break;
                        }
                    }
                    break;
                }
                case GT: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(table.comparerValueToEntryValue(constValue, index))) > 0) {
                            queue.add(row);
                            break;
                        }
                    }
                    break;
                }
                case GE: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(table.comparerValueToEntryValue(constValue, index))) >= 0) {
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
                        ArrayList<Row> rows = table.getBySecondaryIndex(table.columns.get(index), new Entry(table.comparerValueToEntryValue(constValue, index)));
                        if (rows == null)
                            return;
                        queue.addAll(rows);
                    }
                    break;
                }
                case NE: {
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (row.getEntries().get(index).compareTo(new Entry(table.comparerValueToEntryValue(constValue, index))) != 0) {
                            ArrayList<Row> rows = table.getBySecondaryIndex(table.columns.get(index), row.getEntries().get(index));
                            if (rows == null)
                                return;
                            queue.addAll(rows);
                        }
                    }
                    break;
                }
                case LT: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index).getName()).headMap(new Entry(table.comparerValueToEntryValue(constValue, index)));
                    if (this.mapIterator == null)
                        this.mapIterator = secondaryIndex.entrySet().iterator();
                    getRowsFromSortedMap(secondaryIndex, index);
                    break;
                }
                case LE: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index).getName()).headMap(new Entry(table.comparerValueToEntryValue(constValue, index)));
                    if (this.mapIterator == null)
                        this.mapIterator = secondaryIndex.entrySet().iterator();
                    if (mapIterator.hasNext())
                        getRowsFromSortedMap(secondaryIndex, index);
                    else {
                        ArrayList<Row> rows = table.getBySecondaryIndex(table.columns.get(index), new Entry(table.comparerValueToEntryValue(constValue, index)));
                        if (rows == null)
                            return;
                        queue.addAll(rows);
                    }
                    break;
                }
                case GT: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index).getName()).tailMap(new Entry(table.comparerValueToEntryValue(constValue, index)));
                    if (secondaryIndex.size() != 0)
                        secondaryIndex.remove(new Entry(table.comparerValueToEntryValue(constValue, index)));
                    if (this.mapIterator == null)
                        this.mapIterator = secondaryIndex.entrySet().iterator();
                    getRowsFromSortedMap(secondaryIndex, index);
                    break;
                }
                case GE: {
                    SortedMap secondaryIndex = table.secondaryIndexList.get(table.columns.get(index).getName()).tailMap(new Entry(table.comparerValueToEntryValue(constValue, index)));
                    if (this.mapIterator == null)
                        this.mapIterator = secondaryIndex.entrySet().iterator();
                    getRowsFromSortedMap(secondaryIndex, index);
                    break;
                }
            }
    }

    private void getRowsFromSortedMap(SortedMap sortedMap, int index) {
        if (sortedMap.size() == 0)
            return;
        if (mapIterator.hasNext()) {
            Map.Entry pair = (Map.Entry) mapIterator.next();
            ArrayList<Row> rows = table.getBySecondaryIndex(table.columns.get(index), (Entry) pair.getKey());
            if (rows == null)
                return;
            queue.addAll(rows);
        }
    }

    public Table getTable() {
        return table;
    }
}