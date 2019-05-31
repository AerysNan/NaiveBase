package query;

import type.ComparerType;
import type.OperatorType;

public class Expression {
    public Expression left;
    public Expression right;
    public OperatorType operatorType;
    public Comparer comparer;
    public boolean terminal;

    public Expression(Comparer comparer) {
        this.terminal = true;
        this.comparer = comparer;
    }

    public Expression(Expression left, Expression right, OperatorType operatorType) {
        this.terminal = false;
        this.left = left;
        this.right = right;
        this.operatorType = operatorType;
    }

    public boolean isConstExpression() {
        if (terminal)
            return comparer.type != ComparerType.COLUMN;
        return left.isConstExpression() && right.isConstExpression();
    }

    public boolean isSimpleColumn() {
        return terminal && comparer.type == ComparerType.COLUMN;
    }

    public Expression(Expression e) {
        this.left = e.left;
        this.right = e.right;
        this.operatorType = e.operatorType;
        this.comparer = e.comparer;
        this.terminal = e.terminal;
    }
}
