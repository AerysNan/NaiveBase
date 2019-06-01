package query;

import type.ComparerType;
import type.NumericOpType;

public class Expression {
    public Expression left;
    public Expression right;
    public NumericOpType numericOpType;
    public Comparer comparer;
    public boolean terminal;

    public Expression(Comparer comparer) {
        this.terminal = true;
        this.comparer = comparer;
    }

    public Expression(Expression left, Expression right, NumericOpType numericOpType) {
        this.terminal = false;
        this.left = left;
        this.right = right;
        this.numericOpType = numericOpType;
    }

    boolean isConstExpression() {
        if (terminal)
            return comparer.type != ComparerType.COLUMN;
        return left.isConstExpression() && right.isConstExpression();
    }

    boolean isSimpleColumn() {
        return terminal && comparer.type == ComparerType.COLUMN;
    }

    Expression(Expression e) {
        this.left = e.left;
        this.right = e.right;
        this.numericOpType = e.numericOpType;
        this.comparer = e.comparer;
        this.terminal = e.terminal;
    }
}