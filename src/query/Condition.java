package query;

import type.ComparatorType;

public class Condition {
    public Expression left;
    public Expression right;
    public ComparatorType type;

    public Condition(Expression left, Expression right, ComparatorType type) {
        this.left = left;
        this.right = right;
        this.type = type;
    }
}
