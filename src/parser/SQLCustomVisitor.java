package parser;

import javafx.util.Pair;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import schema.Column;
import schema.Constraint;
import schema.Manager;
import schema.Type;

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
        //TODO: return string can only be one line, must fix.
        StringBuilder sb = new StringBuilder();
        for (SQLParser.Sql_stmtContext subCtx : ctx.sql_stmt())
            sb.append(visit(subCtx)).append(' ');
        return sb.toString();
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
    public Object visitDeleteStatement(SQLParser.DeleteStatementContext ctx) {
        return null;
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
    public Object visitSaveStatement(SQLParser.SaveStatementContext ctx) {
        return null;
    }

    @Override
    public Object visitSelectStatement(SQLParser.SelectStatementContext ctx) {
        return null;
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
    public Object visitUpdateStatement(SQLParser.UpdateStatementContext ctx) {
        return null;
    }

    @Override
    public Object visitCreate_db_stmt(SQLParser.Create_db_stmtContext ctx) {
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
            String primaryName = String.valueOf(visit(ctx.table_constraint()));
            for (Column c : columns)
                if (c.getName().equals(primaryName))
                    c.setPrimary(true);
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
    public Object visitDelete_stmt(SQLParser.Delete_stmtContext ctx) {
        return null;
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
        String[] values = new String[ctx.expr().size()];
        for (int i = 0; i < ctx.expr().size(); i++)
            values[i] = ctx.expr(i).getText();
        return values;
    }

    @Override
    public Object visitSave_stmt(SQLParser.Save_stmtContext ctx) {
        return null;
    }

    @Override
    public Object visitSelect_stmt(SQLParser.Select_stmtContext ctx) {
        return null;
    }

    @Override
    public Object visitSelect_core(SQLParser.Select_coreContext ctx) {
        return null;
    }

    @Override
    public Object visitUpdate_stmt(SQLParser.Update_stmtContext ctx) {
        return null;
    }

    @Override
    public Column visitColumn_def(SQLParser.Column_defContext ctx) {
        boolean notNull = false;
        boolean primary = false;
        for (SQLParser.Column_constraintContext subCtx : ctx.column_constraint()) {
            Constraint constraint = (Constraint) visit(subCtx);
            if (constraint.equals(Constraint.PRIMARY))
                primary = true;
            else if (constraint.equals(Constraint.NOTNULL))
                notNull = true;
            notNull = notNull || primary;
        }
        String name = ctx.column_name().getText();
        Pair<Type, Integer> type = (Pair<Type, Integer>) visit(ctx.type_name());
        Type columnType = type.getKey();
        int maxLength = type.getValue();
        return new Column(name, columnType, primary, notNull, maxLength);
    }

    @Override
    public Pair<Type, Integer> visitTypeInt(SQLParser.TypeIntContext ctx) {
        return new Pair<>(Type.INT, -1);
    }

    @Override
    public Pair<Type, Integer> visitTypeLong(SQLParser.TypeLongContext ctx) {
        return new Pair<>(Type.LONG, -1);
    }

    @Override
    public Pair<Type, Integer> visitTypeFloat(SQLParser.TypeFloatContext ctx) {
        return new Pair<>(Type.FLOAT, -1);
    }

    @Override
    public Pair<Type, Integer> visitTypeDouble(SQLParser.TypeDoubleContext ctx) {
        return new Pair<>(Type.DOUBLE, -1);
    }

    @Override
    public Pair<Type, Integer> visitTypeString(SQLParser.TypeStringContext ctx) {
        return new Pair<>(Type.STRING, Integer.valueOf(ctx.INTEGER().getText()));
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
    public Object visitExpr(SQLParser.ExprContext ctx) {
        return null;
    }

    @Override
    public String visitTable_constraint(SQLParser.Table_constraintContext ctx) {
        return ctx.column_name().getText();
    }

    @Override
    public Object visitResult_column(SQLParser.Result_columnContext ctx) {
        return null;
    }

    @Override
    public Object visitTable_query(SQLParser.Table_queryContext ctx) {
        return null;
    }

    @Override
    public Object visitLiteral_value(SQLParser.Literal_valueContext ctx) {
        return null;
    }

    @Override
    public Object visitDatabase_name(SQLParser.Database_nameContext ctx) {
        return null;
    }

    @Override
    public Object visitTable_name(SQLParser.Table_nameContext ctx) {
        return null;
    }

    @Override
    public Object visitColumn_full_name(SQLParser.Column_full_nameContext ctx) {
        return null;
    }

    @Override
    public Object visitColumn_name(SQLParser.Column_nameContext ctx) {
        return null;
    }

    @Override
    public Object visitAny_name(SQLParser.Any_nameContext ctx) {
        return null;
    }

    @Override
    public Object visitChildren(RuleNode ruleNode) {
        return null;
    }

    @Override
    public Object visitTerminal(TerminalNode terminalNode) {
        return null;
    }

    @Override
    public Object visitErrorNode(ErrorNode errorNode) {
        return null;
    }
}