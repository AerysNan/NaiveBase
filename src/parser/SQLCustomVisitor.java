package parser;

import exception.ColumnNotFoundException;
import exception.ValueFormatException;
import type.LiteralType;
import javafx.util.Pair;
import query.*;
import schema.Column;
import type.ConstraintType;
import schema.Manager;
import type.*;

import java.util.ArrayList;
import java.util.StringJoiner;

public class SQLCustomVisitor extends SQLBaseVisitor {
    private Manager manager;

    public SQLCustomVisitor(Manager manager) {
        super();
        this.manager = manager;
    }

    @Override
    public String visitParse(SQLParser.ParseContext ctx) {
        return visitSql_stmt_list(ctx.sql_stmt_list());
    }

    @Override
    public String visitSql_stmt_list(SQLParser.Sql_stmt_listContext ctx) {
        StringJoiner sj = new StringJoiner("\n");
        for (SQLParser.Sql_stmtContext subCtx : ctx.sql_stmt())
            sj.add(visitSql_stmt(subCtx));
        return sj.toString();
    }

    @Override
    public String visitSql_stmt(SQLParser.Sql_stmtContext ctx) {
        if (ctx.create_table_stmt() != null)
            return visitCreate_table_stmt(ctx.create_table_stmt());
        if (ctx.create_db_stmt() != null)
            return visitCreate_db_stmt(ctx.create_db_stmt());
        if (ctx.drop_db_stmt() != null)
            return visitDrop_db_stmt(ctx.drop_db_stmt());
        if (ctx.delete_stmt() != null)
            return visitDelete_stmt(ctx.delete_stmt());
        if (ctx.drop_table_stmt() != null)
            return visitDrop_table_stmt(ctx.drop_table_stmt());
        if (ctx.insert_stmt() != null)
            return visitInsert_stmt(ctx.insert_stmt());
        if (ctx.select_stmt() != null)
            return visitSelect_stmt(ctx.select_stmt());
        if (ctx.use_db_stmt() != null)
            return visitUse_db_stmt(ctx.use_db_stmt());
        if (ctx.show_db_stmt() != null)
            return visitShow_db_stmt(ctx.show_db_stmt());
        if (ctx.show_table_stmt() != null)
            return visitShow_table_stmt(ctx.show_table_stmt());
        if (ctx.quit_stmt() != null)
            return visitQuit_stmt(ctx.quit_stmt());
        if (ctx.update_stmt() != null)
            return visitUpdate_stmt(ctx.update_stmt());
        return null;
    }

    @Override
    public String visitCreate_db_stmt(SQLParser.Create_db_stmtContext ctx) {
        String databaseName = ctx.database_name().getText();
        try {
            manager.createDatabase(databaseName);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Created database " + databaseName + ".";
    }

    @Override
    public String visitDrop_db_stmt(SQLParser.Drop_db_stmtContext ctx) {
        String name = ctx.database_name().getText();
        try {
            if (ctx.K_IF() != null && ctx.K_EXISTS() != null)
                manager.deleteDatabaseIfExist(ctx.database_name().getText());
            else
                manager.deleteDatabase(ctx.database_name().getText());
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Dropped database " + name + ".";
    }

    @Override
    public String visitCreate_table_stmt(SQLParser.Create_table_stmtContext ctx) {
        String name = ctx.table_name().getText();
        int n = ctx.column_def().size();
        Column[] columns = new Column[n];
        int i = 0;
        for (SQLParser.Column_defContext subCtx : ctx.column_def())
            columns[i++] = visitColumn_def(subCtx);
        if (ctx.table_constraint() != null) {
            String[] compositeNames = visitTable_constraint(ctx.table_constraint());
            if (compositeNames.length == 1) {
                boolean found = false;
                for (Column c : columns) {
                    if (c.getName().equals(compositeNames[0])) {
                        c.setPrimary(1);
                        found = true;
                    }
                }
                if (!found)
                    throw new ColumnNotFoundException(compositeNames[0]);
            } else if (compositeNames.length > 1) {
                for (String compositeName : compositeNames) {
                    boolean found = false;
                    for (Column c : columns) {
                        if (c.getName().equals(compositeName)) {
                            c.setPrimary(2);
                            found = true;
                        }
                    }
                    if (!found)
                        throw new ColumnNotFoundException(compositeName);
                }
            }
        }
        try {
            manager.createTable(name, columns);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Created table " + name + ".";
    }

    @Override
    public String visitUse_db_stmt(SQLParser.Use_db_stmtContext ctx) {
        String name = ctx.database_name().getText();
        try {
            manager.switchDatabase(ctx.database_name().getText());
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Switched to database " + name + ".";
    }

    @Override
    public String visitDelete_stmt(SQLParser.Delete_stmtContext ctx) {
        String tableName = ctx.table_name().getText();
        if (ctx.K_WHERE() == null)
            return manager.delete(tableName, null);
        Logic logic = visitMultiple_condition(ctx.multiple_condition());
        try {
            return manager.delete(tableName, logic);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String visitDrop_table_stmt(SQLParser.Drop_table_stmtContext ctx) {
        String name = ctx.table_name().getText();
        try {
            if (ctx.K_IF() != null && ctx.K_EXISTS() != null)
                manager.deleteTableIfExist(ctx.table_name().getText());
            else
                manager.deleteTable(ctx.table_name().getText());
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Dropped table " + name + ".";
    }

    @Override
    public String visitShow_db_stmt(SQLParser.Show_db_stmtContext ctx) {
        return manager.showDatabases();
    }

    @Override
    public String visitQuit_stmt(SQLParser.Quit_stmtContext ctx) {
        try {
            manager.quit();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Quited.";

    }

    @Override
    public String visitShow_table_stmt(SQLParser.Show_table_stmtContext ctx) {
        return manager.showTables(ctx.database_name().getText());
    }

    @Override
    public String visitInsert_stmt(SQLParser.Insert_stmtContext ctx) {
        String tableName = ctx.table_name().getText();
        String[] columnNames = null;
        if (ctx.column_name() != null && ctx.column_name().size() != 0) {
            columnNames = new String[ctx.column_name().size()];
            for (int i = 0; i < ctx.column_name().size(); i++)
                columnNames[i] = ctx.column_name(i).getText();
        }
        for (SQLParser.Value_entryContext subCtx : ctx.value_entry()) {
            String[] values = visitValue_entry(subCtx);
            try {
                manager.insert(tableName, values, columnNames);
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        return "Inserted " + ctx.value_entry().size() + " rows.";
    }

    @Override
    public String[] visitValue_entry(SQLParser.Value_entryContext ctx) {
        String[] values = new String[ctx.literal_value().size()];
        for (int i = 0; i < ctx.literal_value().size(); i++)
            values[i] = ctx.literal_value(i).getText();
        return values;
    }

    @Override
    public String visitSelect_stmt(SQLParser.Select_stmtContext ctx) {
        // TODO: order by
        try {
            return visitSelect_core(ctx.select_core());
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String visitSelect_core(SQLParser.Select_coreContext ctx) {
        // TODO: distinct / all
        int columnCount = ctx.result_column().size();
        String[] columnsProjected = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            String columnName = ctx.result_column(i).getText();
            if (columnName.equals("*")) {
                columnsProjected = null;
                break;
            }
            columnsProjected[i] = columnName;
        }
        int queryCount = ctx.table_query().size();
        QueryTable[] queryTables = new QueryTable[queryCount];
        for (int i = 0; i < queryCount; i++)
            queryTables[i] = visitTable_query(ctx.table_query(i));
        Logic logic = null;
        if (ctx.K_WHERE() != null)
            logic = visitMultiple_condition(ctx.multiple_condition());
        try {
            return manager.select(columnsProjected, queryTables, logic);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String visitUpdate_stmt(SQLParser.Update_stmtContext ctx) {
        String tableName = ctx.table_name().getText();
        String columnName = ctx.column_name().getText();
        Expression expression = visitExpression(ctx.expression());
        if (ctx.K_WHERE() == null)
            return manager.update(tableName, columnName, expression, null);
        Logic logic = visitMultiple_condition(ctx.multiple_condition());
        try {
            return manager.update(tableName, columnName, expression, logic);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public Column visitColumn_def(SQLParser.Column_defContext ctx) {
        boolean notNull = false;
        int primary = 0;
        for (SQLParser.Column_constraintContext subCtx : ctx.column_constraint()) {
            ConstraintType constraintType = visitColumn_constraint(subCtx);
            if (constraintType.equals(ConstraintType.PRIMARY))
                primary = 1;
            else if (constraintType.equals(ConstraintType.NOTNULL))
                notNull = true;
            notNull = notNull || (primary > 0);
        }
        String name = ctx.column_name().getText();

        Pair<ColumnType, Integer> type = visitType_name(ctx.type_name());
        ColumnType columnType = type.getKey();
        int maxLength = type.getValue();
        return new Column(name, columnType, primary, notNull, maxLength);
    }


    @Override
    public Pair<ColumnType, Integer> visitType_name(SQLParser.Type_nameContext ctx) {
        if (ctx.T_INT() != null)
            return new Pair<>(ColumnType.INT, -1);
        if (ctx.T_LONG() != null)
            return new Pair<>(ColumnType.LONG, -1);
        if (ctx.T_FLOAT() != null)
            return new Pair<>(ColumnType.FLOAT, -1);
        if (ctx.T_DOUBLE() != null)
            return new Pair<>(ColumnType.DOUBLE, -1);
        if (ctx.T_STRING() != null) {
            try {
                return new Pair<>(ColumnType.STRING, Integer.parseInt(ctx.NUMERIC_LITERAL().getText()));
            } catch (Exception e) {
                throw new ValueFormatException();
            }
        }
        return null;
    }

    @Override
    public ConstraintType visitColumn_constraint(SQLParser.Column_constraintContext ctx) {
        if (ctx.K_PRIMARY() != null)
            return ConstraintType.PRIMARY;
        if (ctx.K_NULL() != null)
            return ConstraintType.NOTNULL;
        return null;
    }

    @Override
    public Condition visitCondition(SQLParser.ConditionContext ctx) {
        Expression left = visitExpression(ctx.expression(0));
        Expression right = visitExpression(ctx.expression(1));
        ComparatorType type = visitComparator(ctx.comparator());
        return new Condition(left, right, type);
    }

    @Override
    public ComparatorType visitComparator(SQLParser.ComparatorContext ctx) {
        if (ctx.EQ() != null)
            return ComparatorType.EQ;
        if (ctx.NE() != null)
            return ComparatorType.NE;
        if (ctx.GT() != null)
            return ComparatorType.GT;
        if (ctx.LT() != null)
            return ComparatorType.LT;
        if (ctx.GE() != null)
            return ComparatorType.GE;
        if (ctx.LE() != null)
            return ComparatorType.LE;
        return null;
    }

    @Override
    public Expression visitExpression(SQLParser.ExpressionContext ctx) {
        if (ctx.comparer() != null)
            return new Expression(visitComparer(ctx.comparer()));
        Expression left = visitExpression(ctx.expression(0));
        Expression right = visitExpression(ctx.expression(1));
        NumericOpType operatorType = null;
        if (ctx.ADD() != null)
            operatorType = NumericOpType.ADD;
        if (ctx.SUB() != null)
            operatorType = NumericOpType.SUB;
        if (ctx.MUL() != null)
            operatorType = NumericOpType.MUL;
        if (ctx.DIV() != null)
            operatorType = NumericOpType.DIV;
        return new Expression(left, right, operatorType);
    }

    @Override
    public Comparer visitComparer(SQLParser.ComparerContext ctx) {
        if (ctx.column_full_name() != null)
            return new Comparer(ComparerType.COLUMN, ctx.column_full_name().getText());
        LiteralType type = visitLiteral_value(ctx.literal_value());
        String text = ctx.literal_value().getText();
        switch (type) {
            case NUMBER:
                return new Comparer(ComparerType.NUMBER, text);
            case STRING:
                return new Comparer(ComparerType.STRING, text.substring(1, text.length() - 1));
            case NULL:
                return new Comparer(ComparerType.NULL, null);
            default:
                return null;
        }
    }

    @Override
    public String[] visitTable_constraint(SQLParser.Table_constraintContext ctx) {
        int n = ctx.column_name().size();
        String[] compositeNames = new String[n];
        for (int i = 0; i < n; i++)
            compositeNames[i] = ctx.column_name(i).getText();
        return compositeNames;
    }

    @Override
    public QueryTable visitTable_query(SQLParser.Table_queryContext ctx) {
        if (ctx.K_JOIN().size() == 0)
            return manager.getSingleJointTable(ctx.table_name(0).getText());
        Logic logic = visitMultiple_condition(ctx.multiple_condition());
        ArrayList<String> tableNames = new ArrayList<>();
        for (SQLParser.Table_nameContext subCtx : ctx.table_name())
            tableNames.add(subCtx.getText());
        return manager.getMultipleJointTable(tableNames, logic);
    }

    @Override
    public LiteralType visitLiteral_value(SQLParser.Literal_valueContext ctx) {
        if (ctx.NUMERIC_LITERAL() != null)
            return LiteralType.NUMBER;
        if (ctx.STRING_LITERAL() != null)
            return LiteralType.STRING;
        if (ctx.K_NULL() != null)
            return LiteralType.NULL;
        return null;
    }

    @Override
    public Logic visitMultiple_condition(SQLParser.Multiple_conditionContext ctx) {
        if (ctx.condition() != null)
            return new Logic(visitCondition(ctx.condition()));
        LogicalOpType logicalOpType;
        if (ctx.AND() != null)
            logicalOpType = LogicalOpType.AND;
        else logicalOpType = LogicalOpType.OR;
        return new Logic(visitMultiple_condition(ctx.multiple_condition(0)), visitMultiple_condition(ctx.multiple_condition(1)), logicalOpType);
    }
}