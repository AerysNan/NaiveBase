package parser;

import exception.ColumnNotFoundException;
import exception.ValueFormatException;
import global.LiteralType;
import javafx.util.Pair;
import query.*;
import schema.Column;
import schema.Constraint;
import schema.Manager;
import type.ColumnType;
import type.ComparatorType;
import type.ComparerType;
import type.OperatorType;

import java.util.StringJoiner;

public class SQLCustomVisitor extends SQLBaseVisitor {
    private Manager manager;

    public SQLCustomVisitor(Manager manager) {
        super();
        this.manager = manager;
    }

    @Override
    public String visitParse(SQLParser.ParseContext ctx) {
        return String.valueOf(visit(ctx.sql_stmt_list()));
    }

    @Override
    public String visitSql_stmt_list(SQLParser.Sql_stmt_listContext ctx) {
        StringJoiner sj = new StringJoiner("\n");
        for (SQLParser.Sql_stmtContext subCtx : ctx.sql_stmt())
            sj.add((String) visit(subCtx));
        return sj.toString();
    }

    @Override
    public String visitCreateTableStatement(SQLParser.CreateTableStatementContext ctx) {
        return String.valueOf(visit(ctx.create_table_stmt()));
    }

    @Override
    public String visitCreateDatabaseStatement(SQLParser.CreateDatabaseStatementContext ctx) {
        return String.valueOf(visit(ctx.create_db_stmt()));
    }

    @Override
    public String visitDropDatabaseStatement(SQLParser.DropDatabaseStatementContext ctx) {
        return String.valueOf(visit(ctx.drop_db_stmt()));
    }

    @Override
    public String visitDeleteStatement(SQLParser.DeleteStatementContext ctx) {
        return String.valueOf(visit(ctx.delete_stmt()));
    }

    @Override
    public String visitDropTableStatement(SQLParser.DropTableStatementContext ctx) {
        return String.valueOf(visit(ctx.drop_table_stmt()));
    }

    @Override
    public String visitInsertStatement(SQLParser.InsertStatementContext ctx) {
        return String.valueOf(visit(ctx.insert_stmt()));
    }

    @Override
    public String visitSelectStatement(SQLParser.SelectStatementContext ctx) {
        return String.valueOf(visit(ctx.select_stmt()));
    }

    @Override
    public String visitUseStatement(SQLParser.UseStatementContext ctx) {
        return String.valueOf(visit(ctx.use_db_stmt()));
    }

    @Override
    public String visitShowDatabaseStatement(SQLParser.ShowDatabaseStatementContext ctx) {
        return String.valueOf(visit(ctx.show_db_stmt()));
    }

    @Override
    public String visitShowTableStatement(SQLParser.ShowTableStatementContext ctx) {
        return String.valueOf(visit(ctx.show_table_stmt()));
    }

    @Override
    public String visitQuitStatement(SQLParser.QuitStatementContext ctx) {
        try {
            manager.quit();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Quited.";
    }

    @Override
    public String visitUpdateStatement(SQLParser.UpdateStatementContext ctx) {
        return String.valueOf(visit(ctx.update_stmt()));
    }

    @Override
    public String visitCreate_db_stmt(SQLParser.Create_db_stmtContext ctx) {
        String name = ctx.database_name().getText();
        try {
            manager.createDatabase(name);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Created database " + name + ".";
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
            columns[i++] = (Column) visit(subCtx);
        if (ctx.table_constraint() != null) {
            String[] compositeNames = (String[]) visit(ctx.table_constraint());
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
        Condition condition = (Condition) visit(ctx.condition());
        try {
            return manager.delete(tableName, condition);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public Object visitDrop_table_stmt(SQLParser.Drop_table_stmtContext ctx) {
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
    public Object visitQuit_stmt(SQLParser.Quit_stmtContext ctx) {
        return null;
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
            String[] values = (String[]) visit(subCtx);
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
        return String.valueOf(visit(ctx.select_core()));
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
        QueryTable[] tablesQueried = new QueryTable[queryCount];
        for (int i = 0; i < columnCount; i++)
            tablesQueried[i] = (QueryTable) visit(ctx.table_query(i));
        Condition whereCondition = null;
        if (ctx.K_WHERE() != null)
            whereCondition = (Condition) visit(ctx.condition());
        try {
            return manager.select(columnsProjected, tablesQueried, whereCondition);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String visitUpdate_stmt(SQLParser.Update_stmtContext ctx) {
        String tableName = ctx.table_name().getText();
        String columnName = ctx.column_name().getText();
        Expression expression = (Expression) visit(ctx.expression());
        if (ctx.K_WHERE() == null)
            return manager.update(tableName, columnName, expression, null);
        Condition condition = (Condition) visit(ctx.condition());
        try {
            return manager.update(tableName, columnName, expression, condition);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public Column visitColumn_def(SQLParser.Column_defContext ctx) {
        boolean notNull = false;
        int primary = 0;
        for (SQLParser.Column_constraintContext subCtx : ctx.column_constraint()) {
            Constraint constraint = (Constraint) visit(subCtx);
            if (constraint.equals(Constraint.PRIMARY))
                primary = 1;
            else if (constraint.equals(Constraint.NOTNULL))
                notNull = true;
            notNull = notNull || (primary > 0);
        }
        String name = ctx.column_name().getText();

        Pair<ColumnType, Integer> type = (Pair<ColumnType, Integer>) visit(ctx.type_name());
        ColumnType columnType = type.getKey();
        int maxLength = type.getValue();
        return new Column(name, columnType, primary, notNull, maxLength);
    }

    @Override
    public Pair<ColumnType, Integer> visitTypeInt(SQLParser.TypeIntContext ctx) {
        return new Pair<>(ColumnType.INT, -1);
    }

    @Override
    public Pair<ColumnType, Integer> visitTypeLong(SQLParser.TypeLongContext ctx) {
        return new Pair<>(ColumnType.LONG, -1);
    }

    @Override
    public Pair<ColumnType, Integer> visitTypeFloat(SQLParser.TypeFloatContext ctx) {
        return new Pair<>(ColumnType.FLOAT, -1);
    }

    @Override
    public Pair<ColumnType, Integer> visitTypeDouble(SQLParser.TypeDoubleContext ctx) {
        return new Pair<>(ColumnType.DOUBLE, -1);
    }

    @Override
    public Pair<ColumnType, Integer> visitTypeString(SQLParser.TypeStringContext ctx) {
        try {
            int a = Integer.parseInt(ctx.NUMERIC_LITERAL().getText());
            return new Pair<>(ColumnType.STRING, a);
        } catch (Exception e) {
            throw new ValueFormatException();
        }
    }

    @Override
    public Constraint visitPrimaryKeyConstraint(SQLParser.PrimaryKeyConstraintContext ctx) {
        return Constraint.PRIMARY;
    }

    @Override
    public Constraint visitNotNullConstraint(SQLParser.NotNullConstraintContext ctx) {
        return Constraint.NOTNULL;
    }

    @Override
    public Condition visitCondition(SQLParser.ConditionContext ctx) {
        Expression left = (Expression) visit(ctx.expression(0));
        Expression right = (Expression) visit(ctx.expression(1));
        ComparatorType type = (ComparatorType) visit(ctx.comparator());
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
            return new Expression((Comparer) visit(ctx.comparer()));
        if (ctx.ADD() != null)
            return new Expression((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)), OperatorType.ADD);
        if (ctx.SUB() != null)
            return new Expression((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)), OperatorType.SUB);
        if (ctx.MUL() != null)
            return new Expression((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)), OperatorType.MUL);
        if (ctx.DIV() != null)
            return new Expression((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)), OperatorType.DIV);
        return (Expression) visit(ctx.expression(0));
    }

    @Override
    public Comparer visitComparer(SQLParser.ComparerContext ctx) {
        if (ctx.column_full_name() != null)
            return new Comparer(ComparerType.COLUMN, ctx.column_full_name().getText());
        LiteralType type = (LiteralType) visit(ctx.literal_value());
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
        if (ctx.K_JOIN() == null)
            return manager.getSingleJointTable(ctx.table_name(0).getText());
        Condition whereCondition = (Condition) visit(ctx.condition());
        return manager.getMultipleJointTable(ctx.table_name(0).getText(), ctx.table_name(1).getText(), whereCondition);
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
}