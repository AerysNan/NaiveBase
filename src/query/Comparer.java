package query;

public class Comparer {
    ComparerType type;
    Object value;

    public Comparer(ComparerType type, String value) {
        this.type = type;
        switch (type) {
            case NUMBER:
                this.value = Integer.parseInt(value);
                break;
            case STRING:
            case COLUMN:
                this.value = value;
                break;
            case NULL:
                this.value = null;
            default:
                this.value = null;
        }
    }

    public Comparer(Comparer comparer) {
        this.type = comparer.type;
        this.value = comparer.value;
    }
}
