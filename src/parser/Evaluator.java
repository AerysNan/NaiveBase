package parser;

import server.Context;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import schema.Manager;

public class Evaluator {
    private Manager manager;

    public Evaluator(Manager manager) {
        this.manager = manager;
    }

    public String evaluate(String statement, Context context) {
        SQLLexer lexer = new SQLLexer(CharStreams.fromString(statement));
        lexer.removeErrorListeners();
        lexer.addErrorListener(StrictErrorListener.instance);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SQLParser parser = new SQLParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(StrictErrorListener.instance);
        try {
            SQLCustomVisitor visitor = new SQLCustomVisitor(manager);
            return String.valueOf(visitor.visitParse(parser.parse(), context));
        } catch (Exception e) {
            return "Exception: illegal SQL statement! Error message: " + e.getMessage();
        }
    }
}