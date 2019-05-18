package query;

import exception.ColumnNotFoundException;
import exception.InvalidComparisionException;
import schema.Column;

import java.util.ArrayList;

public abstract  class QueryTable {
    public boolean staticTypeCheck(WhereCondition whereCondition) {
        assert whereCondition.comparer.type != ComparerType.COLUMN;
        assert whereCondition.comparee.type != ComparerType.COLUMN;
        switch (whereCondition.comparer.type) {
            case NULL:
                if (whereCondition.comparee.type.equals(ComparerType.NULL)) {
                    return whereCondition.type.equals(ComparatorType.EQ) ||
                            whereCondition.type.equals(ComparatorType.LE) ||
                            whereCondition.type.equals(ComparatorType.GE);
                } else {
                    throw new InvalidComparisionException();
                }
            case STRING:
                if (whereCondition.comparee.type.equals(ComparerType.STRING)) {
                    int result = ((String) whereCondition.comparer.value).compareTo((String) whereCondition.comparee.value);
                    return comparatorTypeCheck(whereCondition.type, result);
                } else
                    throw new InvalidComparisionException();
            case NUMBER:
                if (whereCondition.comparee.type.equals(ComparerType.NUMBER)) {
                    Double comparer = (Double.parseDouble(String.valueOf(whereCondition.comparer.value)));
                    Double comparee = (Double.parseDouble(String.valueOf(whereCondition.comparee.value)));
                    int result = comparer.compareTo(comparee);
                    return comparatorTypeCheck(whereCondition.type, result);
                } else
                    throw new InvalidComparisionException();
        }
        return false;
    }

    public boolean comparatorTypeCheck(ComparatorType type, int result) {
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

    public int columnFind(ArrayList<Column> columns, String name) {
        int found = -1;
        for (int i = 0; i < columns.size(); i++) {
            if (name.equals(columns.get(i).getName())) {
                found = i;
            }
        }
        if (found == -1)
            throw new ColumnNotFoundException(name);
        return found;
    }
}