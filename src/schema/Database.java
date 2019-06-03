package schema;

import exception.*;
import query.*;
import type.ColumnType;
import selectFormat.Cell;
import selectFormat.SelectFormat;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import static global.Global.*;

public class Database {
    private String dataBaseName;
    HashMap<String, Table> tables;

    public Database(String name) {
        this.dataBaseName = name;
        this.tables = new HashMap<>();
        recoverDatabase();
    }

    private void persistDatabase() {
        File path = new File(metadataPath);
        if (!path.exists())
            return;
        for (Table t : tables.values()) {
            persistTableColumn(t);
        }
    }

    private void persistTableColumn(Table table) {
        File file = new File(metadataPath + this.dataBaseName + "_" + table.tableName + ".dat");
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file, false);
        } catch (IOException e) {
            throw new InternalException("failed to open table metadata file.");
        }
        for (Column c : table.columns) {
            try {
                fileWriter.write(c.toString() + "\n");
            } catch (IOException e) {
                throw new InternalException("failed to write to table metadata file.");
            }
        }
        try {
            fileWriter.close();
        } catch (IOException e) {
            throw new InternalException("failed to close table metadata file.");
        }
    }

    public void createTable(String tableName, Column[] columns) {
        if (tables.containsKey(tableName))
            throw new TableAlreadyExistsException(tableName);
        int hasPrimary = 0;
        HashSet<String> nameSet = new HashSet<>();
        for (Column c : columns) {
            if (nameSet.contains(c.name))
                throw new DuplicateFieldException(tableName);
            nameSet.add(c.name);
            if (c.primary == 1) {
                if (hasPrimary > 0)
                    throw new MultiplePrimaryKeyException(tableName);
                hasPrimary = 1;
            } else if (c.primary == 2) {
                if (hasPrimary == 1)
                    throw new MultiplePrimaryKeyException(tableName);
                hasPrimary = 2;
            }
        }
        Table table;
        if (hasPrimary == 1)
            table = new Table(dataBaseName, tableName, columns);
        else {
            Column primaryColumn = new Column("uid", ColumnType.LONG, 1, true, -1);
            Column[] newColumns = new Column[columns.length + 1];
            newColumns[columns.length] = primaryColumn;
            System.arraycopy(columns, 0, newColumns, 0, newColumns.length - 1);
            table = new Table(dataBaseName, tableName, newColumns);
        }
        tables.put(tableName, table);
    }

    void deleteAllTable() {
        for (String name : tables.keySet())
            deleteTable(name);
        tables.clear();
    }

    void deleteTable(String name) {
        File file = new File(metadataPath + this.dataBaseName + "_" + name + ".dat");
        file.delete();
        tables.get(name).deleteAllPage();
    }

    private String buildCartesianProduct(QueryTable[] queryTables, QueryResult queryResult, boolean distinct) {
        int count = 0;
        HashSet<String> hashSet = new HashSet<>();
        List<List<Cell>> body = new ArrayList<>();
        LinkedList<Row> currentRows = new LinkedList<>();
        while (true) {
            if (currentRows.isEmpty()) {
                for (QueryTable queryTable : queryTables) {
                    if (!queryTable.hasNext())
                        return "No rows.";
                    currentRows.push(queryTable.next());
                }
                List<Cell> line = new ArrayList<>();
                for (Row row : currentRows) {
                    if (row == null)
                        return "No rows.";
                }
                Row row = queryResult.generateQueryRecord(QueryResult.combineRow(currentRows));
                for (Entry entry : row.getEntries()) {
                    line.add(new Cell(String.valueOf(entry.value)));
                }
                if (!distinct || (distinct && !hashSet.contains(row.toString()))) {
                    body.add(line);
                    count++;
                    if (distinct)
                        hashSet.add(row.toString());

                }
            } else {
                int index;
                for (index = queryTables.length - 1; index >= 0; index--) {
                    currentRows.pop();
                    if (!queryTables[index].hasNext())
                        queryTables[index].reset();
                    else break;
                }
                if (index < 0)
                    break;
                for (int i = index; i < queryTables.length; i++) {
                    if (!queryTables[i].hasNext())
                        break;
                    currentRows.push(queryTables[i].next());
                }
                List<Cell> line = new ArrayList<>();
                for (Row row : currentRows) {
                    if (row == null)
                        return "No rows.";
                }
                Row row = queryResult.generateQueryRecord(QueryResult.combineRow(currentRows));
                for (Entry entry : row.getEntries()) {
                    line.add(new Cell(String.valueOf(entry.value)));
                }
                if (!distinct || !hashSet.contains(row.toString())) {
                    body.add(line);
                    count++;
                    if (distinct)
                        hashSet.add(row.toString());
                }
            }
        }
        return new SelectFormat.ConsoleTableBuilder().addHeaders(queryResult.getAttrs()).addRows(body).build().getContent() + "\n" + count + " rows in set.";
    }

    public String select(String[] columnsProjected, QueryTable[] queryTables, Logic selectLogic, boolean distinct) {
        QueryResult queryResult = new QueryResult(queryTables, columnsProjected);
        for (QueryTable queryTable : queryTables)
            queryTable.setSelectLogic(selectLogic);
        return buildCartesianProduct(queryTables, queryResult, distinct);
    }

    private void recoverDatabase() {
        File path = new File(metadataPath);
        if (!path.exists())
            return;
        File[] files = path.listFiles();
        if (files == null)
            throw new InternalException("failed to get table metadata files.");
        for (File f : files) {
            String databaseName = f.getName().split("_")[0];
            if (!this.dataBaseName.equals(databaseName))
                continue;
            recoverTableColumn(f.getName());
        }
    }

    private void recoverTableColumn(String fileName) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(String.valueOf(Paths.get(metadataPath, fileName)));
        } catch (FileNotFoundException e) {
            throw new InternalException("failed to open table metadata file.");
        }
        BufferedReader reader = new BufferedReader(fileReader);
        String tableName = fileName.split("_")[1].replace(".dat", "");
        ArrayList<Column> colArrayList = new ArrayList<>();
        String strColumn;
        while (true) {
            try {
                if ((strColumn = reader.readLine()) == null)
                    break;
            } catch (IOException e) {
                throw new InternalException("failed to read from table metadata file.");
            }
            String[] columnAttr = strColumn.split(",");
            if (columnAttr.length != 5)
                throw new InternalException("table metadata file corrupted.");
            ColumnType type = ColumnType.INT;
            switch (columnAttr[1]) {
                case "INT":
                    type = ColumnType.INT;
                    break;
                case "LONG":
                    type = ColumnType.LONG;
                    break;
                case "FLOAT":
                    type = ColumnType.FLOAT;
                    break;
                case "DOUBLE":
                    type = ColumnType.DOUBLE;
                    break;
                case "STRING":
                    type = ColumnType.STRING;
                    break;
            }
            Column newColumn = new Column(columnAttr[0], type, Integer.parseInt(columnAttr[2]),
                    Boolean.parseBoolean(columnAttr[3]), Integer.parseInt(columnAttr[4]));
            colArrayList.add(newColumn);
        }
        try {
            reader.close();
        } catch (IOException e) {
            throw new InternalException("failed to close table metadata file.");
        }
        Column[] colList = colArrayList.toArray(new Column[0]);
        Table table = new Table(dataBaseName, tableName, colList);
        tables.put(tableName, table);
    }

    public Table getTable(String name) {
        if (!tables.containsKey(name))
            throw new TableNotExistsException(name);
        return tables.get(name);
    }

    void quit() {
        persistDatabase();
        for (Table t : tables.values())
            t.commit();
    }
}