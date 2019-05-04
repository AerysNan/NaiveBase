package schema;

import java.io.IOException;
import java.util.HashMap;

public class Database {
    HashMap<String, Table> tables;
    String currentTable;

    public Database() {
        this.tables = new HashMap<>();
    }

    public Table createTable(String name, Column[] columns) throws IOException {
        Table table = new Table(name, columns);
        tables.put(name, table);
        return table;
    }

    public void quit() throws IOException {
        for (Table t : tables.values())
            t.commit();
    }

    public void switchTable(String name) {
        currentTable = name;
    }
}