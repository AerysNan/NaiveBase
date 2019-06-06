package parser;

import server.Context;
import exception.ColumnNotFoundException;
import exception.NotImplementedException;
import exception.ValueFormatException;
import global.Global;
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

    String visitParse(SQLParser.ParseContext ctx, Context context) {
        return visitSql_stmt_list(ctx.sql_stmt_list(), context);
    }

    private String visitSql_stmt_list(SQLParser.Sql_stmt_listContext ctx, Context context) {
        StringJoiner sj = new StringJoiner("\n\n");
        for (SQLParser.Sql_stmtContext subCtx : ctx.sql_stmt())
            sj.add(visitSql_stmt(subCtx, context));
        return sj.toString();
    }

    private String visitSql_stmt(SQLParser.Sql_stmtContext ctx, Context context) {
        if (ctx.create_table_stmt() != null)
            return visitCreate_table_stmt(ctx.create_table_stmt(), context);
        if (ctx.create_db_stmt() != null)
            return visitCreate_db_stmt(ctx.create_db_stmt(), context);
        if (ctx.create_user_stmt() != null)
            return visitCreate_user_stmt(ctx.create_user_stmt(), context);
        if (ctx.drop_user_stmt() != null)
            return visitDrop_user_stmt(ctx.drop_user_stmt(), context);
        if (ctx.drop_db_stmt() != null)
            return visitDrop_db_stmt(ctx.drop_db_stmt(), context);
        if (ctx.grant_stmt() != null)
            return visitGrant_stmt(ctx.grant_stmt(), context);
        if (ctx.create_view_stmt() != null)
            return visitCreate_view_stmt(ctx.create_view_stmt(), context);
        if (ctx.drop_view_stmt() != null)
            return visitDrop_view_stmt(ctx.drop_view_stmt(), context);
        if (ctx.revoke_stmt() != null)
            return visitRevoke_stmt(ctx.revoke_stmt(), context);
        if (ctx.delete_stmt() != null)
            return visitDelete_stmt(ctx.delete_stmt(), context);
        if (ctx.drop_table_stmt() != null)
            return visitDrop_table_stmt(ctx.drop_table_stmt(), context);
        if (ctx.insert_stmt() != null)
            return visitInsert_stmt(ctx.insert_stmt(), context);
        if (ctx.select_stmt() != null)
            return visitSelect_stmt(ctx.select_stmt(), context);
        if (ctx.use_db_stmt() != null)
            return visitUse_db_stmt(ctx.use_db_stmt(), context);
        if (ctx.update_stmt() != null)
            return visitUpdate_stmt(ctx.update_stmt(), context);
        if (ctx.show_table_stmt() != null)
            return visitShow_table_stmt(ctx.show_table_stmt());
        if (ctx.show_db_stmt() != null)
            return visitShow_db_stmt();
        if (ctx.quit_stmt() != null)
            return visitQuit_stmt();
        return null;
    }

    private String visitCreate_db_stmt(SQLParser.Create_db_stmtContext ctx, Context context) {
        String databaseName = ctx.database_name().getText();
        try {
            manager.createDatabase(databaseName.toLowerCase(), context);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Created database " + databaseName + ".";
    }

    private String visitCreate_user_stmt(SQLParser.Create_user_stmtContext ctx, Context context) {
        String username = ctx.user_name().getText().toLowerCase();
        String password = ctx.password().getText();
        password = password.substring(1, password.length() - 1).toLowerCase();
        try {
            manager.createUser(username, password, context);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Created user " + username + ".";
    }

    private String visitDrop_user_stmt(SQLParser.Drop_user_stmtContext ctx, Context context) {
        String username = ctx.user_name().getText().toLowerCase();
        boolean exists = ctx.K_IF() == null;
        try {
            manager.dropUser(username, exists, context);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Dropped user " + username + ".";
    }

    private String visitDrop_db_stmt(SQLParser.Drop_db_stmtContext ctx, Context context) {
        String name = ctx.database_name().getText();
        try {
            manager.deleteDatabase(name.toLowerCase(), ctx.K_IF() == null, context);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Dropped database " + name + ".";
    }

    private String visitCreate_view_stmt(SQLParser.Create_view_stmtContext ctx, Context context) {
        String viewName = ctx.view_name().getText().toLowerCase();
        SQLParser.Select_stmtContext subCtx = ctx.select_stmt();
        if (subCtx.K_DISTINCT() != null)
            throw new NotImplementedException("distinct");
        int columnCount = subCtx.result_column().size();
        String[] columnsProjected = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            String columnName = subCtx.result_column(i).getText().toLowerCase();
            if (columnName.equals("*")) {
                columnsProjected = null;
                break;
            }
            columnsProjected[i] = columnName;
        }
        int queryCount = subCtx.table_query().size();
        QueryTable[] queryTables = new QueryTable[queryCount];
        try {
            for (int i = 0; i < queryCount; i++)
                queryTables[i] = visitTable_query(subCtx.table_query(i), context);
        } catch (Exception e) {
            return e.getMessage();
        }
        Logic logic = null;
        if (subCtx.K_WHERE() != null)
            logic = visitMultiple_condition(subCtx.multiple_condition());
        try {
            manager.createView(viewName, columnsProjected, queryTables, logic, context);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Created view " + viewName + ".";
    }

    private String visitDrop_view_stmt(SQLParser.Drop_view_stmtContext ctx, Context context) {
        String viewName = ctx.view_name().getText().toLowerCase();
        boolean exists = ctx.K_IF() == null;
        try {
            manager.dropView(viewName, exists, context);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Dropped view " + viewName + ".";
    }

    private String visitGrant_stmt(SQLParser.Grant_stmtContext ctx, Context context) {
        int[] levels = new int[ctx.auth_level().size()];
        int totalLevel = 0;
        for (int i = 0; i < ctx.auth_level().size(); i++)
            levels[i] = visitAuth_level(ctx.auth_level(i));
        for (int level : levels)
            totalLevel |= (1 << level);
        String username = ctx.user_name().getText().toLowerCase();
        String tableName = ctx.table_name().getText().toLowerCase();
        try {
            manager.addAuth(username, tableName, totalLevel, context);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Granted user " + username + "'s authority on table " + tableName + ".";
    }

    private String visitRevoke_stmt(SQLParser.Revoke_stmtContext ctx, Context context) {
        int[] levels = new int[ctx.auth_level().size()];
        int totalLevel = 0;
        for (int i = 0; i < ctx.auth_level().size(); i++)
            levels[i] = visitAuth_level(ctx.auth_level(i));
        for (int level : levels)
            totalLevel |= (1 << level);
        String username = ctx.user_name().getText().toLowerCase();
        String tableName = ctx.table_name().getText().toLowerCase();
        try {
            manager.removeAuth(username, tableName, totalLevel, context);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Revoked user " + username + "'s authority on table " + tableName + ".";
    }

    private String visitCreate_table_stmt(SQLParser.Create_table_stmtContext ctx, Context context) {
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
                    if (c.getName().toLowerCase().equals(compositeNames[0].toLowerCase())) {
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
                        if (c.getName().toLowerCase().equals(compositeName.toLowerCase())) {
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
            manager.createTable(name.toLowerCase(), columns, context);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Created table " + name + ".";
    }

    private String visitUse_db_stmt(SQLParser.Use_db_stmtContext ctx, Context context) {
        String name = ctx.database_name().getText();
        try {
            manager.switchDatabase(name.toLowerCase(), context);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Switched to database " + name + ".";
    }

    private String visitDelete_stmt(SQLParser.Delete_stmtContext ctx, Context context) {
        String tableName = ctx.table_name().getText().toLowerCase();
        if (ctx.K_WHERE() == null) {
            try {
                return manager.delete(tableName, null, context);
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        Logic logic = visitMultiple_condition(ctx.multiple_condition());
        try {
            return manager.delete(tableName, logic, context);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String visitDrop_table_stmt(SQLParser.Drop_table_stmtContext ctx, Context context) {
        String name = ctx.table_name().getText();
        try {
            manager.deleteTable(name.toLowerCase(), ctx.K_IF() == null, context);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Dropped table " + name + ".";
    }

    private String visitShow_db_stmt() {
        return manager.showDatabases();
    }

    private String visitQuit_stmt() {
        try {
            manager.quit();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Quited.";

    }

    public String visitShow_table_stmt(SQLParser.Show_table_stmtContext ctx) {
        return manager.showTables(ctx.database_name().getText().toLowerCase());
    }

    private String visitInsert_stmt(SQLParser.Insert_stmtContext ctx, Context context) {
        String tableName = ctx.table_name().getText().toLowerCase();
        String[] columnNames = null;
        if (ctx.column_name() != null && ctx.column_name().size() != 0) {
            columnNames = new String[ctx.column_name().size()];
            for (int i = 0; i < ctx.column_name().size(); i++)
                columnNames[i] = ctx.column_name(i).getText().toLowerCase();
        }
        for (SQLParser.Value_entryContext subCtx : ctx.value_entry()) {
            String[] values = visitValue_entry(subCtx);
            try {
                manager.insert(tableName, values, columnNames, context);
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        return "Inserted " + ctx.value_entry().size() + " rows.";
    }

    public String[] visitValue_entry(SQLParser.Value_entryContext ctx) {
        String[] values = new String[ctx.literal_value().size()];
        for (int i = 0; i < ctx.literal_value().size(); i++)
            values[i] = ctx.literal_value(i).getText();
        return values;
    }

    private String visitSelect_stmt(SQLParser.Select_stmtContext ctx, Context context) {
        boolean distinct = false;
        if (ctx.K_DISTINCT() != null)
            distinct = true;
        int columnCount = ctx.result_column().size();
        String[] columnsProjected = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            String columnName = ctx.result_column(i).getText().toLowerCase();
            if (columnName.equals("*")) {
                columnsProjected = null;
                break;
            }
            columnsProjected[i] = columnName;
        }
        int queryCount = ctx.table_query().size();
        QueryTable[] queryTables = new QueryTable[queryCount];
        try {
            for (int i = 0; i < queryCount; i++)
                queryTables[i] = visitTable_query(ctx.table_query(i), context);
        } catch (Exception e) {
            return e.getMessage();
        }
        Logic logic = null;
        if (ctx.K_WHERE() != null)
            logic = visitMultiple_condition(ctx.multiple_condition());
        try {
            return manager.select(columnsProjected, queryTables, logic, distinct, context);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String visitUpdate_stmt(SQLParser.Update_stmtContext ctx, Context context) {
        String tableName = ctx.table_name().getText().toLowerCase();
        String columnName = ctx.column_name().getText().toLowerCase();
        Expression expression = visitExpression(ctx.expression());
        if (ctx.K_WHERE() == null) {
            try {
                return manager.update(tableName, columnName, expression, null, context);
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        Logic logic = visitMultiple_condition(ctx.multiple_condition());
        try {
            return manager.update(tableName, columnName, expression, logic, context);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

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
        String name = ctx.column_name().getText().toLowerCase();

        Pair<ColumnType, Integer> type = visitType_name(ctx.type_name());
        ColumnType columnType = type.getKey();
        int maxLength = type.getValue();
        return new Column(name, columnType, primary, notNull, maxLength);
    }

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

    public ConstraintType visitColumn_constraint(SQLParser.Column_constraintContext ctx) {
        if (ctx.K_PRIMARY() != null)
            return ConstraintType.PRIMARY;
        if (ctx.K_NULL() != null)
            return ConstraintType.NOTNULL;
        return null;
    }

    public Condition visitCondition(SQLParser.ConditionContext ctx) {
        Expression left = visitExpression(ctx.expression(0));
        Expression right = visitExpression(ctx.expression(1));
        ComparatorType type = visitComparator(ctx.comparator());
        return new Condition(left, right, type);
    }

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

    public Integer visitAuth_level(SQLParser.Auth_levelContext ctx) {
        if (ctx.K_DELETE() != null)
            return Global.AUTH_DELETE;
        if (ctx.K_INSERT() != null)
            return Global.AUTH_INSERT;
        if (ctx.K_SELECT() != null)
            return Global.AUTH_SELECT;
        if (ctx.K_DROP() != null)
            return Global.AUTH_DROP;
        return Global.AUTH_UPDATE;
    }

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

    public String[] visitTable_constraint(SQLParser.Table_constraintContext ctx) {
        int n = ctx.column_name().size();
        String[] compositeNames = new String[n];
        for (int i = 0; i < n; i++)
            compositeNames[i] = ctx.column_name(i).getText().toLowerCase();
        return compositeNames;
    }

    private QueryTable visitTable_query(SQLParser.Table_queryContext ctx, Context context) {
        if (ctx.K_JOIN().size() == 0)
            return manager.getSingleJointTable(ctx.table_name(0).getText().toLowerCase(), context);
        Logic logic = visitMultiple_condition(ctx.multiple_condition());
        ArrayList<String> tableNames = new ArrayList<>();
        for (SQLParser.Table_nameContext subCtx : ctx.table_name())
            tableNames.add(subCtx.getText().toLowerCase());
        return manager.getMultipleJointTable(tableNames, logic, context);
    }

    public LiteralType visitLiteral_value(SQLParser.Literal_valueContext ctx) {
        if (ctx.NUMERIC_LITERAL() != null)
            return LiteralType.NUMBER;
        if (ctx.STRING_LITERAL() != null)
            return LiteralType.STRING;
        if (ctx.K_NULL() != null)
            return LiteralType.NULL;
        return null;
    }

    public Logic visitMultiple_condition(SQLParser.Multiple_conditionContext ctx) {
        if (ctx.condition() != null)
            return new Logic(visitCondition(ctx.condition()));
        LogicalOpType logicalOpType;
        if (ctx.AND() != null)
            logicalOpType = LogicalOpType.AND;
        else
            logicalOpType = LogicalOpType.OR;
        return new Logic(visitMultiple_condition(ctx.multiple_condition(0)),
                visitMultiple_condition(ctx.multiple_condition(1)), logicalOpType);
    }
}