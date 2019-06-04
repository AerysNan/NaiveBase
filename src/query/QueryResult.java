package query;

import exception.AmbiguousColumnNameException;
import exception.ColumnNameFormatException;
import exception.ColumnNotFoundException;
import exception.TableNotExistsException;
import global.Global;
import schema.Entry;
import schema.Row;
import format.Cell;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class QueryResult {

    private ArrayList<MetaInfo> metaInfoInfos;
    private ArrayList<Integer> index;
    private List<Cell> attrs;

    public QueryResult(QueryTable[] queryTables, String[] selectProjects) {
        this.metaInfoInfos = new ArrayList<>() {{
            for (QueryTable queryTable : queryTables)
                addAll(queryTable.generateMeta());
        }};
        init(selectProjects);
    }

    private void init(String[] selectProjects) {
        this.index = new ArrayList<>();
        this.attrs = new ArrayList<>();
        if (selectProjects != null) {
            for (String selectProject : selectProjects) {
                this.index.add(getColumnIndex(selectProject));
                this.attrs.add(new Cell(selectProject));
            }
        } else {
            int offset = 0;
            for (MetaInfo metaInfo : metaInfoInfos) {
                for (int i = 0; i < metaInfo.columns.size(); i++) {
                    if (!Global.uniqueIDName.equals(metaInfo.columns.get(i).getName())) {
                        index.add(i + offset);
                        this.attrs.add(new Cell(metaInfo.columns.get(i).getName()));
                    }
                }
                offset += metaInfo.columns.size();
            }
        }
    }

    public List<Cell> getAttrs() {
        return attrs;
    }


    public static Row combineRow(LinkedList<Row> rows) {
        Row result = new Row(-1);
        for (int i = rows.size() - 1; i >= 0; i--)
            result.appendEntries(rows.get(i).getEntries());
        return result;
    }

    public Row generateQueryRecord(Row row) {
        ArrayList<Entry> record = new ArrayList<>();
        for (Integer integer : index) record.add(row.getEntries().get(integer));
        return new Row(record.toArray(new Entry[index.size()]), -1);
    }


    private int getColumnIndex(String columnName) {
        int index = 0, found = 0, offset = 0;
        if (!columnName.contains(".")) {
            for (MetaInfo metaInfo : metaInfoInfos) {
                for (int j = 0; j < metaInfo.columns.size(); j++) {
                    if (columnName.equals(metaInfo.columns.get(j).getName())) {
                        found++;
                        index = j + offset;
                    }
                }
                offset += metaInfo.columns.size();
            }
            if (found < 1)
                throw new ColumnNotFoundException(columnName);
            if (found > 1)
                throw new AmbiguousColumnNameException(columnName);
        } else {
            String[] tableInfo = splitColumnFullName(columnName);
            for (MetaInfo metaInfo : metaInfoInfos) {
                if (metaInfo.tableName.equals(tableInfo[0])) {
                    found++;
                    index = metaInfo.columnFind(tableInfo[1]) + offset;
                }
                offset += metaInfo.columns.size();
            }
            if (found == 0)
                throw new TableNotExistsException(tableInfo[0]);
        }
        return index;
    }

    private String[] splitColumnFullName(String info) {
        String[] tableInfo = info.split("\\.");
        if (tableInfo.length != 2)
            throw new ColumnNameFormatException();
        return tableInfo;
    }
}