package query;

import exception.*;
import global.Global;
import schema.Entry;
import schema.Row;
import schema.Table;
import type.ColumnType;
import type.ComparatorType;
import type.ComparerType;
import type.LogicalOpType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class JointTable extends QueryTable implements Iterator<Row> {
    class QueryRow extends Row {
        QueryRow(LinkedList<Row> rows) {
            super(-1);
            this.entries = new ArrayList<>();
            for (int i = rows.size() - 1; i >= 0; i--)
                entries.addAll(rows.get(i).getEntries());
        }
        QueryRow(Row row1, Row row2) {
            super(-1);
            this.entries = new ArrayList<>();
            entries.addAll(row1.getEntries());
            entries.addAll(row2.getEntries());
        }
    }

    private ArrayList<Iterator<Row>> iterators;
    private ArrayList<Table> tables;
    private LinkedList<Row> currentRows;
    private Logic joinLogic;

    public JointTable(ArrayList<Table> tables, Logic joinLogic) {
        this.tables = tables;
        this.iterators = new ArrayList<>();
        this.currentRows = new LinkedList<>();
        this.joinLogic = joinLogic;
        this.buffer = new LinkedList<>();
        this.queue = new LinkedList<>();
        this.isFirst = true;
        this.columns = new ArrayList<>();
        for (Table t : tables) {
            this.columns.addAll(t.columns);
            this.iterators.add(t.iterator());
        }
    }

    @Override
    public void figure() {
        if (joinLogic.terminal && tables.size() == 2) {
            Condition joinCondition = joinLogic.condition;
            if (joinCondition.type.equals(ComparatorType.EQ) && ((joinCondition.left.isSimpleColumn() && joinCondition.right.isConstExpression() ||
                    (joinCondition.right.isSimpleColumn() && joinCondition.left.isConstExpression())))) {
                if (joinCondition.left.isSimpleColumn())
                    EQCompareFigure(joinCondition);
                else
                    EQCompareFigure(swapCondition(joinCondition));
                return;
            }
        }
        while (true) {
            QueryRow queryRow = buildQueryRow();
            if (queryRow == null)
                return;
            if (failedLogic(joinLogic, queryRow) || failedLogic(selectLogic, queryRow))
                continue;
            queue.add(queryRow);
            return;
        }
    }

    private QueryRow buildQueryRow() {
        if (currentRows.isEmpty()) {
            for (Iterator<Row> iter : iterators) {
                if (!iter.hasNext())
                    return null;
                currentRows.push(iter.next());
            }
            return new QueryRow(currentRows);
        } else {
            int index;
            for (index = iterators.size() - 1; index >= 0; index--) {
                currentRows.pop();
                if (!iterators.get(index).hasNext())
                    iterators.set(index, tables.get(index).iterator());
                else break;
            }
            if (index < 0)
                return null;
            for (int i = index; i < iterators.size(); i++) {
                if (!iterators.get(i).hasNext())
                    return null;
                currentRows.push(iterators.get(i).next());
            }
            return new QueryRow(currentRows);
        }
    }

    private void EQCompareFigure(Condition onCondition) {
        Table table1 = tables.get(0);
        Table table2 = tables.get(1);
        Iterator<Row> iterator1 = iterators.get(0);
        Iterator<Row> iterator2 = iterators.get(1);
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
                    if (failedLogic(selectLogic, newRow))
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
                    if (failedLogic(selectLogic, newRow))
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
                        if (failedLogic(selectLogic, newRow))
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
                        if (failedLogic(selectLogic, newRow))
                            continue;
                        queue.add(newRow);
                    }
                }
            }
        }
    }

    private int getColumnIndex(String columnName) {
        int index = 0;
        if (!columnName.contains(".")) {
            int found = 0;
            for (int i = 0; i < columns.size(); i++) {
                if (columnName.equals(columns.get(i).getName())) {
                    found++;
                    index = i;
                }
            }
            if (found < 1)
                throw new ColumnNotFoundException(columnName);
            if (found > 1)
                throw new AmbiguousColumnNameException(columnName);
        } else {
            String[] tableInfo = splitColumnFullName(columnName);
            int offset = 0;
            boolean found = false;
            for (Table table : tables) {
                if (tableInfo[0].equals(table.tableName)) {
                    index = columnFind(table.columns, tableInfo[1]) + offset;
                    found = true;
                    break;
                }
                offset += table.columns.size();
            }
            if (!found) {
                throw new TableNotExistsException(tableInfo[0]);
            }
        }
        return index;
    }

    private boolean failedLogic(Logic logic, Row row) {
        if (logic == null)
            return false;
        if (logic.terminal)
            return failedCondition(logic.condition, row);
        if (logic.logicalOpType == LogicalOpType.AND)
            return failedLogic(logic.left, row) || failedLogic(logic.right, row);
        return failedLogic(logic.left, row) && failedLogic(logic.right, row);
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
                        return v1.compareTo(v2) != 0;
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
                        return v1.compareTo(v2) == 0;
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
            if (t1 == ComparerType.STRING) {
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
            switch (expression.numericOpType) {
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

    public ArrayList<Table> getTables() {
        return tables;
    }
}