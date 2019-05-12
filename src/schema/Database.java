package schema;

import exception.DuplicateKeyException;
import exception.MultiplePrimaryKeyException;
import exception.TableAlreadyExistsException;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class Database {
    HashMap<String, Table> tables;
    String name;

    public Database(String name) {
        this.name = name;
        this.tables = new HashMap<>();
    }

    public Table createTable(String name, Column[] columns) throws IOException {
        if(tables.containsKey(name))
            throw new TableAlreadyExistsException(name);
        boolean hasPrimary = false;
        HashSet<String> nameSet = new HashSet<>();
        for (Column c : columns) {
            if (nameSet.contains(c.name))
                throw new DuplicateKeyException(name);
            nameSet.add(c.name);
            if (c.primary) {
                if (hasPrimary)
                    throw new MultiplePrimaryKeyException(name);
                hasPrimary = true;
            }
        }
        Table table;
        if (hasPrimary)
            table = new Table(name, columns);
        else {
            Column primaryColumn = new Column("uid", Type.LONG, true, true, -1);
            Column[] newColumns = new Column[columns.length + 1];
            newColumns[0] = primaryColumn;
            System.arraycopy(columns, 0, newColumns, 1, newColumns.length - 1);
            table = new Table(name, newColumns);
        }
        tables.put(name, table);
        return table;
    }

    public void quit() throws IOException {
        for (Table t : tables.values())
            t.commit();
    }
}