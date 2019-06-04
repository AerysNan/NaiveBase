package query;

import exception.ColumnNotFoundException;
import exception.InvalidComparisionException;
import exception.InvalidExpressionException;
import schema.Row;
import type.ComparatorType;
import type.ComparerType;
import type.LogicalOpType;

import java.util.ArrayList;
import java.util.Iterator;

public class VirtualTable extends QueryTable {
    private String name;
    private View view;
    private Iterator<Row> iterator;

    public VirtualTable(String name, View view) {
        this.view = view;
        this.name = name;
        this.iterator = view.iterator();
        this.columns = view.columns;
    }

    @Override
    public void figure() {
        while (iterator.hasNext()) {
            Row row = iterator.next();
            if (failedLogic(selectLogic, row))
                continue;
            queue.add(row);
            break;
        }
    }

    @Override
    public void reset() {
        this.iterator = view.iterator();
    }

    @Override
    public ArrayList<MetaInfo> generateMeta() {
        return new ArrayList<>() {{
            add(new MetaInfo(name, columns));
        }};
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
        int i = columnFind(columns, columnName);
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
        int i = columnFind(columns, columnName);
        if (i < 0)
            throw new ColumnNotFoundException(columnName);
        return row.getEntries().get(i).value;
    }
}
