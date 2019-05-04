package schema;

public class Column {
    String name;
    Type type;
    boolean primary;

    public Column(String name, Type type, boolean primary) {
        this.name = name;
        this.type = type;
        this.primary = primary;
    }
}
