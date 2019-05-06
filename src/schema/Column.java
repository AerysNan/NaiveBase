package schema;

public class Column {
    String name;
    Type type;
    boolean primary;
    private boolean notNull;
    private int maxLength;

    public Column(String name, Type type, boolean primary, boolean notNull, int maxLength) {
        this.name = name;
        this.type = type;
        this.primary = primary;
        this.notNull = notNull;
        this.maxLength = maxLength;
    }

    public String getName() {
        return this.name;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public String toString() {
        return name + ',' + type + ',' + primary + ',' + notNull + ',' + maxLength;
    }
}