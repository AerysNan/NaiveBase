package schema;

import type.ColumnType;

public class Column implements Comparable<Column> {
    String name;
    ColumnType type;
    int primary;
    boolean notNull;
    int maxLength;

    public Column(String name, ColumnType type, int primary, boolean notNull, int maxLength) {
        this.name = name;
        this.type = type;
        this.primary = primary;
        this.notNull = notNull;
        this.maxLength = maxLength;
    }

    public String getName() {
        return this.name;
    }

    public ColumnType getType() {
        return this.type;
    }

    public void setPrimary(int primary) {
        this.primary = primary;
    }

    public int getPrimary() {
        return this.primary;
    }

    @Override
    public int compareTo(Column e) {
        return name.compareTo(e.name);
    }

    public String toString() {
        return name + ',' + type + ',' + primary + ',' + notNull + ',' + maxLength;
    }
}