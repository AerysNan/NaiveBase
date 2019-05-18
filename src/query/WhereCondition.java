package query;

public class WhereCondition {
    Comparer comparer;
    Comparer comparee;
    ComparatorType type;

    public WhereCondition(Comparer comparer, Comparer comparee, ComparatorType type) {
        this.comparer = comparer;
        this.comparee = comparee;
        this.type = type;
    }
}
