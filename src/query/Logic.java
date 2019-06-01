package query;

import type.LogicalOpType;

public class Logic {
    public Logic left;
    public Logic right;
    public LogicalOpType logicalOpType;
    public Condition condition;
    public boolean terminal;

    public Logic(Logic left, Logic right, LogicalOpType logicalOpType) {
        this.terminal = false;
        this.left = left;
        this.right = right;
        this.logicalOpType = logicalOpType;
    }

    public Logic(Condition condition) {
        this.terminal = true;
        this.condition = condition;
    }
}