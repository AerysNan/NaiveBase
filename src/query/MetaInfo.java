package query;

import exception.ColumnNotFoundException;
import schema.Column;

import java.util.ArrayList;

class MetaInfo {
    String tableName;
    ArrayList<Column> columns;

    MetaInfo(String tableName, ArrayList<Column> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    int columnFind(String name) {
        int found = -1;
        for (int i = 0; i < columns.size(); i++) {
            if (name.toLowerCase().equals(columns.get(i).getName()))
                found = i;
        }
        if (found == -1)
            throw new ColumnNotFoundException(name);
        return found;
    }
}

