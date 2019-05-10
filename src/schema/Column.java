package schema;

import java.io.Serializable;

public class Column implements Serializable {
    private static final long serialVersionUID = -5809782578272943999L;
    String name;
    Type type;
    boolean primary;

    public Column(String name, Type type, boolean primary) {
        this.name = name;
        this.type = type;
        this.primary = primary;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(',').append(type).append(',').append(primary);
        return sb.toString();
    }
}
