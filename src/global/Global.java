package global;

import exception.InternalException;
import exception.InvalidComparisionException;
import exception.InvalidExpressionException;
import type.ComparatorType;
import type.ComparerType;
import query.Condition;
import query.Expression;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Global {
    public static final String adminDatabaseName = "admin";
    public static final String persistFileName = "databases.dat";
    public static final String metadataPath = "./meta/";
    public static final String dataPath = "./data/";
    public static final String authTableName = "auth";
    public static final String adminUserName = "admin";
    public static final String userTableName = "users";
    public static final String adminPassWordFileName = "admin.conf";
    public static int maxNameLength = 40;
    public static int maxPageNum = 256;
    public static int maxPageSize = 4096;
    public static int fanout = 129;

    public static final int AUTH_DELETE = 0;
    public static final int AUTH_SELECT = 1;
    public static final int AUTH_UPDATE = 2;
    public static final int AUTH_INSERT = 3;
    public static final int AUTH_DROP = 4;
    public static final int AUTH_MAX = 31;

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

    public static String toLiteral(String s) {
        return "'" + s + "'";
    }

    public static String encrypt(String dataStr) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(dataStr.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = m.digest();
            StringBuilder result = new StringBuilder();
            for (byte b : bytes)
                result.append(Integer.toHexString((0x000000FF & b) | 0xFFFFFF00).substring(6));
            return result.toString();
        } catch (Exception e) {
            throw new InternalException("failed to encode string.");
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