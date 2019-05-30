package query;

public class Condition {
    public Comparer comparer;
    public Comparer comparee;
    public ComparatorType type;

    public Condition(Comparer comparer, Comparer comparee, ComparatorType type) {
        this.comparer = comparer;
        this.comparee = comparee;
        this.type = type;
    }
}
