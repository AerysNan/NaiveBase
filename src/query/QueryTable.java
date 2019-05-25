package query;

import exception.ColumnNotFoundException;
import exception.NullCompareNotNullException;
import exception.NumberCompareNotNumberException;
import exception.StringCompareNotStringException;
import schema.Column;

import java.util.ArrayList;

public abstract  class QueryTable {
    public boolean staticTypeCheck(WhereCondition whereCondition) {
        assert whereCondition.comparer.type != ComparerType.COLUMN;
        assert whereCondition.comparee.type != ComparerType.COLUMN;
        switch (whereCondition.comparer.type) {
            case NULL:
                if (whereCondition.comparee.type.equals(ComparerType.NULL)) {
                    if (whereCondition.type.equals(ComparatorType.EQ) ||
                            whereCondition.type.equals(ComparatorType.LE) ||
                            whereCondition.type.equals(ComparatorType.GE)) {
                        return true;
                    }
                    return false;
                } else {
                    throw new NullCompareNotNullException();
                }
            case STRING:
                if (whereCondition.comparee.type.equals(ComparerType.STRING)) {
                    int result = ((String) whereCondition.comparer.value).compareTo((String) whereCondition.comparee.value);
                    return comparatorTypeCheck(whereCondition.type, result);
                } else {
                    throw new StringCompareNotStringException();
                }
            case NUMBER:
                if (whereCondition.comparee.type.equals(ComparerType.NUMBER)) {
                    int result = ((Double) whereCondition.comparer.value).compareTo((Double) whereCondition.comparee.value);
                    return comparatorTypeCheck(whereCondition.type, result);
                } else {
                    throw new NumberCompareNotNumberException();
                }
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

    public int columnFind(ArrayList<Column> columns, String name){
        int  found = -1;
        for(int i = 0;i < columns.size();i++){
            if(name == columns.get(i).getName()){
                found = i;
            }
        }
        if(found == -1)
            throw  new ColumnNotFoundException(name);
        return found;
    }
}