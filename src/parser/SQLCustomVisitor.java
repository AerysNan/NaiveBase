package parser;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import schema.Manager;

public class SQLCustomVisitor extends SQLBaseVisitor {
    private Manager manager;

    public SQLCustomVisitor(Manager manager) {
        super();
        this.manager = manager;
    }

    @Override
    public String visitParse(SQLParser.ParseContext ctx) {
        if (ctx.sql_stmt_list() != null)
            return String.valueOf(visit(ctx.sql_stmt_list()));
        else
            return String.valueOf(visit(ctx.error()));
    }

    @Override
    public String visitSql_stmt_list(SQLParser.Sql_stmt_listContext ctx) {
        StringBuilder sb = new StringBuilder();
        for (SQLParser.Sql_stmtContext subCtx : ctx.sql_stmt())
            sb.append(visit(subCtx)).append(' ');
        return sb.toString();
    }

    @Override
    public String visitCreateTableStatement(SQLParser.CreateTableStatementContext ctx) {
        return null;
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
    public Object visitDropTableStatement(SQLParser.DropTableStatementContext ctx) {
        return null;
    }

    @Override
    public Object visitInsertStatement(SQLParser.InsertStatementContext ctx) {
        return null;
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
        return manager.showDatabases();
    }

    @Override
    public Object visitShowTableStatement(SQLParser.ShowTableStatementContext ctx) {
        return null;
    }

    @Override
    public String visitQuitStatement(SQLParser.QuitStatementContext ctx) {
        manager.quit();
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
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        return "Created database " + name + ".";
    }

    @Override
    public Object visitDrop_db_stmt(SQLParser.Drop_db_stmtContext ctx) {
        String name = ctx.database_name().getText();
        try {
            if (ctx.K_IF() != null && ctx.K_EXISTS() != null)
                manager.deleteDatabaseIfExist(ctx.database_name().getText());
            else
                manager.deleteDatabase(ctx.database_name().getText());
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        return "Dropped database " + name + ".";
    }

    @Override
    public Object visitCreate_table_stmt(SQLParser.Create_table_stmtContext ctx) {
        return null;
    }

    @Override
    public String visitUse_db_stmt(SQLParser.Use_db_stmtContext ctx) {
        String name = ctx.database_name().getText();
        try {
            manager.switchDatabase(ctx.database_name().getText());
        } catch (RuntimeException e) {
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
        return null;
    }

    @Override
    public Object visitShow_db_stmt(SQLParser.Show_db_stmtContext ctx) {
        return null;
    }

    @Override
    public Object visitQuit_stmt(SQLParser.Quit_stmtContext ctx) {
        return null;
    }

    @Override
    public Object visitShow_table_stmt(SQLParser.Show_table_stmtContext ctx) {
        return null;
    }

    @Override
    public Object visitInsert_stmt(SQLParser.Insert_stmtContext ctx) {
        return null;
    }

    @Override
    public Object visitValue_entry(SQLParser.Value_entryContext ctx) {
        return null;
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
    public Object visitColumn_def(SQLParser.Column_defContext ctx) {
        return null;
    }

    @Override
    public Object visitTypeInt(SQLParser.TypeIntContext ctx) {
        return null;
    }

    @Override
    public Object visitTypeLong(SQLParser.TypeLongContext ctx) {
        return null;
    }

    @Override
    public Object visitTypeFloat(SQLParser.TypeFloatContext ctx) {
        return null;
    }

    @Override
    public Object visitTypeDouble(SQLParser.TypeDoubleContext ctx) {
        return null;
    }

    @Override
    public Object visitTypeString(SQLParser.TypeStringContext ctx) {
        return null;
    }

    @Override
    public Object visitPrimaryKeyConstraint(SQLParser.PrimaryKeyConstraintContext ctx) {
        return null;
    }

    @Override
    public Object visitNotNullConstraint(SQLParser.NotNullConstraintContext ctx) {
        return null;
    }

    @Override
    public Object visitExpr(SQLParser.ExprContext ctx) {
        return null;
    }

    @Override
    public Object visitTable_constraint(SQLParser.Table_constraintContext ctx) {
        return null;
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