package query;

public class Condition {
    Comparer comparer;
    Comparer comparee;
    ComparatorType type;

    public Condition(Comparer comparer, Comparer comparee, ComparatorType type) {
        this.comparer = comparer;
        this.comparee = comparee;
        this.type = type;
    }
}
