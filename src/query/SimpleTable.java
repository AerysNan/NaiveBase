package query;

import exception.ColumnNotFoundException;
import exception.NumberCompareNotNumberException;
import exception.StringCompareNotStringException;
import schema.*;
import java.util.Iterator;
import java.util.ArrayList;

public class SimpleTable extends QueryTable implements Iterator<Row> {
    public Table table;
    private WhereCondition whereCondition;
    private Iterator<Row> iterator;

    public SimpleTable(Table table, WhereCondition whereCondition) {
        this.table = table;
        this.whereCondition = whereCondition;
        this.iterator = table.iterator();
    }

//    public ArrayList<Row> figure() {
//        ArrayList<Row> rowList = new ArrayList<>();
//        if (whereCondition == null) {
//            while (hasNext()) {
//                rowList.add(next());
//            }
//        } else {
//            if (!(whereCondition.comparer.type.equals(ComparerType.COLUMN) || whereCondition.comparee.type.equals(ComparerType.COLUMN))) {
//                boolean result = staticTypeCheck(whereCondition);
//                if (result) {
//                    while (hasNext()) {
//                        rowList.add(next());
//                    }
//                }
//            } else if (whereCondition.comparer.type.equals(ComparerType.COLUMN)&& whereCondition.comparee.type.equals(ComparerType.COLUMN)) {
//                int  foundComparer = columnFind(table.columns,(String)whereCondition.comparer.value);
//                int foundComparee = columnFind(table.columns,(String)whereCondition.comparee.value);
//                if(table.columns.get(foundComparer).getType().equals(ColumnType.STRING)&&
//                        !table.columns.get(foundComparee).getType().equals(ColumnType.STRING)){
//                    throw new StringCompareNotStringException();
//                }
//                if(!table.columns.get(foundComparer).getType().equals(ColumnType.STRING)&&
//                        table.columns.get(foundComparee).getType().equals(ColumnType.STRING)){
//                    throw  new NumberCompareNotNumberException();
//                }
//                while (hasNext()){
//                    Row row = next();
//                    int result;
//                    if(table.columns.get(foundComparer).getType().equals(ColumnType.STRING))
//                        result = ((String) whereCondition.comparer.value).compareTo((String) whereCondition.comparee.value);
//                    else
//                        result = ((Double) whereCondition.comparer.value).compareTo((Double) whereCondition.comparee.value);
//                    boolean right = comparatorTypeCheck(whereCondition.type,result);
//                    if(right){
//                        rowList.add(row);
//                    }
//                }
//            }else{
//                if(whereCondition.comparer.type.equals(ComparerType.COLUMN)){
//                    int index = columnFind(table.columns,(String)whereCondition.comparer.value);
//                    if(table.columns.get(index).isPrimary()){
//                        switch (whereCondition.type){
//                            case EQ:{
//                                Row row = table.index.get(new Entry(index,whereCondition.comparer.value));
//                                rowList.add(row);
//                                break;}
//                            case NE: {
//                                while (hasNext()) {
//                                    Row row = next();
//                                    if (row.getEntries().get(index).compareTo(new Entry(index, whereCondition.comparer.value)) != 0)
//                                        rowList.add(row);
//                                }
//                                break;
//                            }
//                            case GT: {
//                                while (hasNext()) {
//                                    Row row = next();
//                                    if (row.getEntries().get(index).compareTo(new Entry(index, whereCondition.comparer.value)) < 0)
//                                        rowList.add(row);
//                                }
//                                break;
//                            }
//                            case GE:{
//                                while (hasNext()) {
//                                    Row row = next();
//                                    if (row.getEntries().get(index).compareTo(new Entry(index, whereCondition.comparer.value)) <= 0)
//                                        rowList.add(row);
//                                }
//                                break;
//                            }
//                            case LT: {
//                                while (hasNext()) {
//                                    Row row = next();
//                                    if (row.getEntries().get(index).compareTo(new Entry(index, whereCondition.comparer.value)) > 0)
//                                        rowList.add(row);
//                                }
//                                break;
//                            }
//                            case LE:{
//                                while (hasNext()) {
//                                    Row row = next();
//                                    if (row.getEntries().get(index).compareTo(new Entry(index, whereCondition.comparer.value)) >= 0)
//                                        rowList.add(row);
//                                }
//                                break;
//                            }
//                        }
//                    }else
//                        switch (whereCondition.type){
//                            case EQ:
//                                //ArrayList rows = table.getBySecondaryIndex(table.columns.get(index),new Entry(index,whereCondition.comparee.value));
//                                //table.secondaryIndexList.get(new Column((String)whereCondition.comparee.value,table.columns.get(index).getType(),false,false,10));
//
//                        }
//                }
//
//
//            }
//
//
//        }
//        return rowList;
//    }




    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Row next() {
        return iterator.next();
    }
}