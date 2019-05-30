package query;

import schema.Row;
import schema.Table;
import schema.Entry;
import schema.Column;
import exception.*;
import schema.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class JointTable extends QueryTable implements Iterator<Row> {
    class QueryRow extends Row {

        public QueryRow(Row leftRow, Row rightRow) {
            super(0);
            this.entries = new ArrayList<>();
            entries.addAll(leftRow.getEntries());
            entries.addAll(rightRow.getEntries());
        }
    }

    private ArrayList<Column> columns;
    public Table table1;
    public Table table2;
    private Condition onCondition;
    private Iterator<Row> iterator1;
    private Iterator<Row> iterator2;


    public JointTable(Table table1, Table table2, Condition onCondition) {
        this.table1 = table1;
        this.table2 = table2;
        this.onCondition = onCondition;
        this.iterator1 = table1.iterator();
        this.iterator2 = table2.iterator();
        this.buffer = new LinkedList<>();
        this.queue = new LinkedList<>();
        this.isFirst = true;
        this.columns = new ArrayList<>();
        this.columns.addAll(table1.columns);
        this.columns.addAll(table2.columns);
    }

    @Override
    public void figure() {
        if (onCondition.type.equals(ComparatorType.EQ) && ((onCondition.comparer.type.equals(ComparerType.COLUMN) && !onCondition.comparee.type.equals(ComparerType.COLUMN)) ||
                (!onCondition.comparer.type.equals(ComparerType.COLUMN) && onCondition.comparee.type.equals(ComparerType.COLUMN)))) {
            if (onCondition.comparer.type.equals(ComparerType.COLUMN)) {
                EQCompareFigure(onCondition);
            } else {
                Condition newOnCondition = swapCondition(onCondition);
                EQCompareFigure(newOnCondition);
            }
        } else {
            while (iterator1.hasNext()) {
                Row leftRow = iterator1.next();
                while (iterator2.hasNext()) {
                    Row rightRow = iterator2.next();
                    QueryRow row = new QueryRow(leftRow, rightRow);
                    if (satisfiedCondition(onCondition, row) && satisfiedCondition(whereCondition, row)) {
                        queue.add(row);
                    }
                }
                iterator2 = table2.iterator();
                if (queue.size() == 0) {
                    continue;
                }
                break;
            }
        }
    }

    public void EQCompareFigure(Condition onCondition) {
        int index = getIndex(onCondition.comparer);
        if (!columns.get(index).getType().equals(Type.STRING) &&
                onCondition.comparee.type.equals(ComparerType.STRING)) {
            throw new InvalidComparisionException();
        }
        if (columns.get(index).getType().equals(Type.STRING) &&
                !onCondition.comparee.type.equals(ComparerType.STRING)) {
            throw new InvalidComparisionException();
        }
        if (columns.get(index).getPrimary() == 1) {
            if (index >= table1.columns.size()) {
                Row row = null;
                try {
                    row = table2.index.get(new Entry(0, (Comparable) table2.parseValue(String.valueOf(onCondition.comparee.value), index - table1.columns.size())));
                } catch (KeyNotExistException e) {
                    return;
                }
                while (iterator1.hasNext()) {
                    Row newRow = new QueryRow(iterator1.next(), row);
                    if (satisfiedCondition(whereCondition, newRow)) {
                        queue.add(newRow);
                        break;
                    }
                }
            } else {
                Row row = null;
                try {
                    row = table1.index.get(new Entry(0, (Comparable) table1.parseValue(String.valueOf(onCondition.comparee.value), index)));
                } catch (KeyNotExistException e) {
                    return;
                }
                while (iterator2.hasNext()) {
                    Row newRow = new QueryRow(row, iterator2.next());
                    if (satisfiedCondition(whereCondition, newRow)) {
                        queue.add(newRow);
                        break;
                    }
                }
            }
        } else {
            if (index >= table1.columns.size()) {
                ArrayList<Row> rows = table2.getBySecondaryIndex(table2.columns.get(index - table1.columns.size()), new Entry(0, (Comparable) table2.parseValue(String.valueOf(onCondition.comparee.value), index - table1.columns.size())));
                if (rows == null) {
                    return;
                }
                while (iterator1.hasNext()) {
                    for (Row row : rows) {
                        Row newRow = new QueryRow(iterator1.next(), row);
                        if (satisfiedCondition(whereCondition, newRow)) {
                            queue.add(newRow);
                        }
                    }
                    break;
                }
            } else {
                ArrayList<Row> rows = table1.getBySecondaryIndex(table1.columns.get(index), new Entry(0, (Comparable) table1.parseValue(String.valueOf(onCondition.comparee.value), index)));
                if (rows == null) {
                    return;
                }
                while (iterator2.hasNext()) {
                    for (Row row : rows) {
                        Row newRow = new QueryRow(row, iterator2.next());
                        if (satisfiedCondition(whereCondition, newRow)) {
                            queue.add(newRow);
                        }
                    }
                    break;
                }
            }
        }
    }

    public int getIndex(Comparer comparer) {
        int index = 0;
        int found = 0;
        if (!String.valueOf(comparer.value).contains(".")) {
            for (int i = 0; i < columns.size(); i++) {
                if (String.valueOf(comparer.value).equals(columns.get(i).getName())) {
                    found++;
                    index = i;
                }
            }
            if (found < 1) {
                throw new ColumnNotFoundException(String.valueOf(comparer.value));
            }
            if (found > 1) {
                throw new AmbiguousColumnNameException();
            }
        } else {
            String[] tableInfo = tableInfoCheck(String.valueOf(comparer.value));
            if (tableInfo[0].equals(table1.tableName)) {
                index = columnFind(table1.columns, tableInfo[1]);
            } else if (tableInfo[0].equals(table2.tableName)) {
                index = columnFind(table2.columns, tableInfo[1]) + table1.columns.size();
            } else {
                if (String.valueOf(comparer.value).contains(".")) {
                    String name = tableInfoCheck(String.valueOf(comparer.value))[0];
                    throw new TableNotExistsException(name);
                } else {
                    throw new TableNotExistsException(String.valueOf(comparer.value));
                }
            }
        }
        return index;
    }


    public boolean satisfiedCondition(Condition condition, Row row) {
        if (condition == null) {
            return true;
        } else {
            if (!(condition.comparer.type.equals(ComparerType.COLUMN) || condition.comparee.type.equals(ComparerType.COLUMN))) {
                return staticTypeCheck(condition);
            } else if (condition.comparer.type.equals(ComparerType.COLUMN) && condition.comparee.type.equals(ComparerType.COLUMN)) {
                int foundComparer = getIndex(condition.comparer);
                int foundComparee = getIndex(condition.comparee);
                if (!columns.get(foundComparer).getType().equals(Type.STRING) &&
                        columns.get(foundComparee).getType().equals(Type.STRING)) {
                    throw new InvalidComparisionException();
                }
                if (columns.get(foundComparer).getType().equals(Type.STRING) &&
                        !columns.get(foundComparee).getType().equals(Type.STRING)) {
                    throw new InvalidComparisionException();
                }
                int result;
                if (columns.get(foundComparer).getType().equals(Type.STRING))
                    result = (row.getEntries().get(foundComparer)).compareTo(row.getEntries().get(foundComparee));
                else {
                    Double comparer = (Double.parseDouble(String.valueOf(row.getEntries().get(foundComparer))));
                    Double comparee = (Double.parseDouble(String.valueOf(row.getEntries().get(foundComparee))));
                    result = comparer.compareTo(comparee);
                }
                return comparatorTypeCheck(condition.type, result);
            } else if (condition.comparer.type.equals(ComparerType.COLUMN)) {
                return conditionJudge(condition, row);
            } else {
                Condition newCondition = swapCondition(condition);
                return conditionJudge(newCondition, row);
            }
        }
    }

    public boolean conditionJudge(Condition condition, Row row) {
        int foundComparer = getIndex(condition.comparer);
        int result;
        if (columns.get(foundComparer).getType().equals(Type.STRING)) {
            result = (row.getEntries().get(foundComparer)).compareTo(new Entry(foundComparer, String.valueOf(condition.comparee.value)));
        } else {
            Double comparer = (Double.parseDouble(String.valueOf(row.getEntries().get(foundComparer))));
            Double comparee = (Double.parseDouble(String.valueOf(condition.comparee.value)));
            result = comparer.compareTo(comparee);
        }
        return comparatorTypeCheck(condition.type, result);
    }

    public String[] tableInfoCheck(String info) {
        String[] tableInfo = info.split("\\.");
        if (tableInfo.length != 2)
            throw new ColumnNameFormatException();
        return tableInfo;
    }
}