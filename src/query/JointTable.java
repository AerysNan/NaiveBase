package query;

import exception.*;
import global.Global;
import schema.Entry;
import schema.Row;
import schema.Table;
import type.ColumnType;
import type.ComparatorType;
import type.ComparerType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class JointTable extends QueryTable implements Iterator<Row> {
    class QueryRow extends Row {
        QueryRow(Row leftRow, Row rightRow) {
            super(0);
            this.entries = new ArrayList<>();
            entries.addAll(leftRow.getEntries());
            entries.addAll(rightRow.getEntries());
        }
    }

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
        if (onCondition.type.equals(ComparatorType.EQ) && ((onCondition.left.isSimpleColumn() && onCondition.right.isConstExpression() ||
                (onCondition.right.isSimpleColumn() && onCondition.left.isConstExpression())))) {
            if (onCondition.left.isSimpleColumn())
                EQCompareFigure(onCondition);
            else
                EQCompareFigure(swapCondition(onCondition));
        } else {
            while (iterator1.hasNext()) {
                Row leftRow = iterator1.next();
                while (iterator2.hasNext()) {
                    Row rightRow = iterator2.next();
                    QueryRow row = new QueryRow(leftRow, rightRow);
                    if (failedCondition(onCondition, row) || failedCondition(whereCondition, row))
                        continue;
                    queue.add(row);
                }
                iterator2 = table2.iterator();
                if (queue.size() == 0)
                    continue;
                break;
            }
        }
    }

    private void EQCompareFigure(Condition onCondition) {
        Comparer comparer = onCondition.left.comparer;
        ComparerType comparerType = Global.evalConstExpressionType(onCondition.right);
        Comparable value = Global.evalConstExpressionValue(onCondition.right);
        int index = getColumnIndex((String) comparer.value);
        if (!columns.get(index).getType().equals(ColumnType.STRING) &&
                comparerType.equals(ComparerType.STRING))
            throw new InvalidComparisionException();
        if (columns.get(index).getType().equals(ColumnType.STRING) &&
                !comparerType.equals(ComparerType.STRING))
            throw new InvalidComparisionException();
        if (columns.get(index).getPrimary() == 1) {
            if (index >= table1.columns.size()) {
                Row row;
                try {
                    row = table2.index.get(new Entry(0, table2.comparerValueToEntryValue(value, index - table1.columns.size())));
                } catch (KeyNotExistException e) {
                    return;
                }
                while (iterator1.hasNext()) {
                    Row newRow = new QueryRow(iterator1.next(), row);
                    if (failedCondition(whereCondition, newRow))
                        continue;
                    queue.add(newRow);
                    break;
                }
            } else {
                Row row;
                try {
                    row = table1.index.get(new Entry(0, table1.comparerValueToEntryValue(value, index)));
                } catch (KeyNotExistException e) {
                    return;
                }
                while (iterator2.hasNext()) {
                    Row newRow = new QueryRow(row, iterator2.next());
                    if (failedCondition(whereCondition, newRow))
                        continue;
                    queue.add(newRow);
                    break;
                }
            }
        } else {
            if (index >= table1.columns.size()) {
                ArrayList<Row> rows = table2.getBySecondaryIndex(table2.columns.get(index - table1.columns.size()), new Entry(0, table2.comparerValueToEntryValue(value, index - table1.columns.size())));
                if (rows == null)
                    return;
                if (iterator1.hasNext()) {
                    for (Row row : rows) {
                        Row newRow = new QueryRow(iterator1.next(), row);
                        if (failedCondition(whereCondition, newRow))
                            continue;
                        queue.add(newRow);
                    }
                }
            } else {
                ArrayList<Row> rows = table1.getBySecondaryIndex(table1.columns.get(index), new Entry(0, table1.comparerValueToEntryValue(value, index)));
                if (rows == null)
                    return;
                if (iterator2.hasNext()) {
                    for (Row row : rows) {
                        Row newRow = new QueryRow(row, iterator2.next());
                        if (failedCondition(whereCondition, newRow))
                            continue;
                        queue.add(newRow);
                    }
                }
            }
        }
    }

    private int getColumnIndex(String columnName) {
        int index = 0;
        int found = 0;
        if (!columnName.contains(".")) {
            for (int i = 0; i < columns.size(); i++) {
                if (columnName.equals(columns.get(i).getName())) {
                    found++;
                    index = i;
                }
            }
            if (found < 1)
                throw new ColumnNotFoundException(columnName);
            if (found > 1)
                throw new AmbiguousColumnNameException();
        } else {
            String[] tableInfo = splitColumnFullName(columnName);
            if (tableInfo[0].equals(table1.tableName))
                index = columnFind(table1.columns, tableInfo[1]);
            else if (tableInfo[0].equals(table2.tableName))
                index = columnFind(table2.columns, tableInfo[1]) + table1.columns.size();
            else {
                if (columnName.contains(".")) {
                    String name = splitColumnFullName(columnName)[0];
                    throw new TableNotExistsException(name);
                } else
                    throw new TableNotExistsException(columnName);
            }
        }
        return index;
    }

    private boolean failedCondition(Condition condition, Row row) {
        if (condition == null) {
            return false;
        } else {
            ComparerType t1 = evalExpressionType(condition.left);
            ComparerType t2 = evalExpressionType(condition.right);
            if (condition.type == ComparatorType.EQ) {
                if (t1 == ComparerType.NULL && t2 == ComparerType.NULL)
                    return false;
                if (t1 == ComparerType.NULL || t2 == ComparerType.NULL)
                    return true;
                if (t1 == t2) {
                    Comparable v1 = evalExpressionValue(condition.left, row);
                    Comparable v2 = evalExpressionValue(condition.right, row);
                    if (t1 == ComparerType.STRING)
                        return v1 != v2;
                    if (v1 == null || v2 == null)
                        throw new InvalidComparisionException();
                    double d1 = ((Number) v1).doubleValue();
                    double d2 = ((Number) v2).doubleValue();
                    return d1 != d2;
                }
                throw new InvalidComparisionException();
            }
            if (condition.type == ComparatorType.NE) {
                if (t1 == ComparerType.NULL && t2 == ComparerType.NULL)
                    return true;
                if (t1 == ComparerType.NULL || t2 == ComparerType.NULL)
                    return false;
                if (t1 == t2) {
                    Comparable v1 = evalExpressionValue(condition.left, row);
                    Comparable v2 = evalExpressionValue(condition.right, row);
                    if (t1 == ComparerType.STRING)
                        return v1 == v2;
                    if (v1 == null || v2 == null)
                        throw new InvalidComparisionException();
                    double d1 = ((Number) v1).doubleValue();
                    double d2 = ((Number) v2).doubleValue();
                    return d1 == d2;
                }
                throw new InvalidComparisionException();
            }
            if (!comparisionTypeCheck(condition))
                throw new InvalidComparisionException();
            Comparable v1 = evalExpressionValue(condition.left, row);
            Comparable v2 = evalExpressionValue(condition.right, row);
            if (v1 == null || v2 == null)
                throw new InvalidComparisionException();
            if(t1 == ComparerType.STRING) {
                if (condition.type == ComparatorType.GT)
                    return v1.compareTo(v2) <= 0;
                if (condition.type == ComparatorType.GE)
                    return v1.compareTo(v2) < 0;
                if (condition.type == ComparatorType.LT)
                    return v1.compareTo(v2) >= 0;
                if (condition.type == ComparatorType.LE)
                    return v1.compareTo(v2) > 0;
                return true;
            }
            double d1 = ((Number) v1).doubleValue();
            double d2 = ((Number) v2).doubleValue();
            if (condition.type == ComparatorType.GT)
                return d1 <= d2;
            if (condition.type == ComparatorType.GE)
                return d1 < d2;
            if (condition.type == ComparatorType.LT)
                return d1 >= d2;
            if (condition.type == ComparatorType.LE)
                return d1 > d2;
            return true;
        }
    }

    private boolean comparisionTypeCheck(Condition whereCondition) {
        ComparerType t1 = evalExpressionType(whereCondition.left);
        ComparerType t2 = evalExpressionType(whereCondition.right);
        return t1 != null && t1 != ComparerType.NULL && t1 == t2;
    }

    private Comparable evalExpressionValue(Expression expression, Row row) {
        if (expression.terminal) {
            switch (expression.comparer.type) {
                case NUMBER:
                case STRING:
                case NULL:
                    return expression.comparer.value;
                case COLUMN:
                    return getColumnValue((String) expression.comparer.value, row);
                default:
                    return null;
            }
        } else {
            Comparable v1 = evalExpressionValue(expression.left, row);
            Comparable v2 = evalExpressionValue(expression.right, row);
            if (v1 == null || v2 == null)
                throw new InvalidExpressionException();
            double d1 = ((Number) v1).doubleValue();
            double d2 = ((Number) v2).doubleValue();
            switch (expression.operatorType) {
                case ADD:
                    return d1 + d2;
                case DIV:
                    return d1 / d2;
                case SUB:
                    return d1 - d2;
                case MUL:
                    return d1 * d2;
                default:
                    return null;
            }
        }
    }

    private ComparerType evalExpressionType(Expression expression) {
        if (expression.terminal) {
            switch (expression.comparer.type) {
                case STRING:
                    return ComparerType.STRING;
                case NUMBER:
                    return ComparerType.NUMBER;
                case COLUMN:
                    return getColumnType((String) expression.comparer.value);
                default:
                    return ComparerType.NULL;
            }
        } else {
            ComparerType t1 = evalExpressionType(expression.left);
            ComparerType t2 = evalExpressionType(expression.right);
            if (t1 == ComparerType.NUMBER && t2 == ComparerType.NUMBER)
                return ComparerType.NUMBER;
            throw new InvalidExpressionException();
        }
    }

    private ComparerType getColumnType(String columnName) {
        int i = getColumnIndex(columnName);
        if (i < 0)
            throw new ColumnNotFoundException(columnName);
        switch (columns.get(i).getType()) {
            case LONG:
            case FLOAT:
            case INT:
            case DOUBLE:
                return ComparerType.NUMBER;
            case STRING:
                return ComparerType.STRING;
        }
        throw new ColumnNotFoundException(columnName);
    }

    private Comparable getColumnValue(String columnName, Row row) {
        int i = getColumnIndex(columnName);
        if (i < 0)
            throw new ColumnNotFoundException(columnName);
        return row.getEntries().get(i).value;
    }

    private String[] splitColumnFullName(String info) {
        String[] tableInfo = info.split("\\.");
        if (tableInfo.length != 2)
            throw new ColumnNameFormatException();
        return tableInfo;
    }
}