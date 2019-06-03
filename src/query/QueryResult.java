package query;

import exception.AmbiguousColumnNameException;
import exception.ColumnNameFormatException;
import exception.ColumnNotFoundException;
import exception.TableNotExistsException;
import schema.Column;
import schema.Entry;
import schema.Row;
import schema.Table;

import java.util.ArrayList;
import java.util.List;
import selectFormat.*;

public class QueryResult {


    private class TableMetaInfo {
        String name;
        ArrayList<Column> columns;

        TableMetaInfo(String name, ArrayList<Column> columns) {
            this.name = name;
            this.columns = columns;
        }

        int columnFind(String name) {
            int found = -1;
            for (int i = 0; i < columns.size(); i++) {
                if (name.toLowerCase().equals(columns.get(i).getName().toLowerCase())) {
                    found = i;
                }
            }
            if (found == -1)
                throw new ColumnNotFoundException(name);
            return found;
        }
    }

    private ArrayList<TableMetaInfo> metaInfos;
    private ArrayList<Integer> index;
    private List<Cell> attr;

    public QueryResult(Table table, String[] selectProjects) {
        this.metaInfos = new ArrayList<TableMetaInfo>() {{
            add(new TableMetaInfo(table.tableName, table.columns));
        }};
        init(selectProjects);
    }

    public QueryResult(ArrayList<Table> tables, String[] selectProjects) {
        this.metaInfos = new ArrayList<TableMetaInfo>() {{
            for (Table table : tables)
                add(new TableMetaInfo(table.tableName, table.columns));
        }};
        init(selectProjects);
    }

    private void init(String[] selectProjects) {
        this.index = new ArrayList<>();
        this.attr = new ArrayList<>();
        if (selectProjects != null) {
            for (String selectProject : selectProjects) {
                this.index.add(getColumnIndex(selectProject));
                attr.add(new Cell(selectProject));
            }
        } else {
            int offset = 0;
            for (TableMetaInfo metaInfo : metaInfos) {
                for (int i = 0; i < metaInfo.columns.size(); i++) {
                    if (!"uid".equals(metaInfo.columns.get(i).getName())) {
                        index.add(i + offset);
                        attr.add(new Cell(metaInfo.columns.get(i).getName()));
                    }
                }
                offset += metaInfo.columns.size();
            }
        }
    }

    public List<Cell> getAttrs() {
        return this.attr;
    }

    public Row generateQueryRecord(Row row) {
        ArrayList<Entry> record = new ArrayList<>();
        for (Integer integer : index) record.add(row.getEntries().get(integer));
        return new Row(record.toArray(new Entry[index.size()]), 0);
    }


    private int getColumnIndex(String columnName) {
        int index = 0, found = 0, offset = 0;
        if (!columnName.contains(".")) {
            for (TableMetaInfo metaInfo : metaInfos) {
                for (int j = 0; j < metaInfo.columns.size(); j++) {
                    if (columnName.toLowerCase().equals(metaInfo.columns.get(j).getName().toLowerCase())) {
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
            for (TableMetaInfo metaInfo : metaInfos) {
                if (metaInfo.name.toLowerCase().equals(tableInfo[0].toLowerCase())) {
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