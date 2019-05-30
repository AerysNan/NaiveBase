package global;

import exception.InvalidComparisionException;
import query.ComparatorType;
import query.ComparerType;
import query.Condition;

public class Global {
    public static String adminDatabaseName = "admin";
    public static String persistFileName = "databases.dat";
    public static String metadataPath = "./metadata/";
    public static String dataPath = "./data/";
    public static int maxPageNum = 256;
    public static int maxPageSize = 4096;
    public static int fanout = 129;

    public static boolean staticTypeCheck(Condition condition) {
        assert condition.comparer.type != ComparerType.COLUMN;
        assert condition.comparee.type != ComparerType.COLUMN;
        switch (condition.comparer.type) {
            case NULL:
                if (condition.comparee.type.equals(ComparerType.NULL)) {
                    return condition.type.equals(ComparatorType.EQ) ||
                            condition.type.equals(ComparatorType.LE) ||
                            condition.type.equals(ComparatorType.GE);
                } else
                    throw new InvalidComparisionException();
            case STRING:
                if (condition.comparee.type.equals(ComparerType.STRING)) {
                    int result = ((String) condition.comparer.value).compareTo((String) condition.comparee.value);
                    return comparatorTypeCheck(condition.type, result);
                } else
                    throw new InvalidComparisionException();
            case NUMBER:
                if (condition.comparee.type.equals(ComparerType.NUMBER)) {
                    Double comparer = (Double.parseDouble(String.valueOf(condition.comparer.value)));
                    Double comparee = (Double.parseDouble(String.valueOf(condition.comparee.value)));
                    int result = comparer.compareTo(comparee);
                    return comparatorTypeCheck(condition.type, result);
                } else
                    throw new InvalidComparisionException();
        }
        return false;
    }

    public static boolean comparatorTypeCheck(ComparatorType type, int result) {
        switch (type) {
            case NE:
                return result != 0;
            case EQ:
                return result == 0;
            case LT:
                return result < 0;
            case LE:
                return result <= 0;
            case GT:
                return result > 0;
            case GE:
                return result >= 0;
        }
        return false;
    }
}