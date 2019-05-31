package global;

import exception.InvalidComparisionException;
import exception.InvalidExpressionException;
import type.ComparatorType;
import type.ComparerType;
import query.Condition;
import query.Expression;

public class Global {
    public static String adminDatabaseName = "admin";
    public static String persistFileName = "databases.dat";
    public static String metadataPath = "./meta/";
    public static String dataPath = "./data/";
    public static int maxPageNum = 256;
    public static int maxPageSize = 4096;
    public static int fanout = 129;

    public static Comparable evalConstExpressionValue(Expression expression) {
        if (expression.terminal) {
            if (expression.comparer.type == ComparerType.COLUMN)
                throw new InvalidExpressionException();
            return expression.comparer.value;
        } else {
            Comparable v1 = evalConstExpressionValue(expression.left);
            Comparable v2 = evalConstExpressionValue(expression.right);
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

    public static ComparerType evalConstExpressionType(Expression expression) {
        if (expression.terminal) {
            if (expression.comparer.type == ComparerType.COLUMN)
                throw new InvalidExpressionException();
            return expression.comparer.type;
        } else {
            ComparerType t1 = evalConstExpressionType(expression.left);
            ComparerType t2 = evalConstExpressionType(expression.right);
            if (t1 == ComparerType.NUMBER && t2 == ComparerType.NUMBER)
                return ComparerType.NUMBER;
            throw new InvalidExpressionException();
        }
    }


    public static boolean failedConstCondition(Condition condition) {
        if (condition == null) {
            return false;
        } else {
            ComparerType t1 = evalConstExpressionType(condition.left);
            ComparerType t2 = evalConstExpressionType(condition.right);
            if (condition.type == ComparatorType.EQ) {
                if (t1 == t2 || t1 == ComparerType.NULL || t2 == ComparerType.NULL)
                    return evalConstExpressionValue(condition.left) != evalConstExpressionValue(condition.right);
                throw new InvalidComparisionException();
            }
            if (condition.type == ComparatorType.NE) {
                if (t1 == t2 || t1 == ComparerType.NULL || t2 == ComparerType.NULL)
                    return evalConstExpressionValue(condition.left) == evalConstExpressionValue(condition.right);
                throw new InvalidComparisionException();
            }
            Comparable v1 = evalConstExpressionValue(condition.left);
            Comparable v2 = evalConstExpressionValue(condition.right);
            if (v1 == null || v2 == null)
                throw new InvalidComparisionException();
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

    }
}