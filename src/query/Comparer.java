package query;

import type.ComparerType;

public class Comparer {
    public ComparerType type;
    public Comparable value;

    public Comparer(ComparerType type, String value) {
        this.type = type;
        switch (type) {
            case NUMBER:
                this.value = Double.parseDouble(value);
                break;
            case STRING:
            case COLUMN:
                this.value = value;
                break;
            default:
                this.value = null;
        }
    }

    public Comparer(Comparer comparer) {
        this.type = comparer.type;
        this.value = comparer.value;
    }
}