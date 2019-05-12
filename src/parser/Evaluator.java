package parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import schema.Manager;
import java.io.IOException;

public class Evaluator {
    private Manager manager;

    public Evaluator() throws IOException {
        this.manager = new Manager();
    }

    public String evaluate(String expression) {
        SQLLexer lexer = new SQLLexer(CharStreams.fromString(expression));
        lexer.removeErrorListeners();
        lexer.addErrorListener(StrictErrorListener.instance);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SQLParser parser = new SQLParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(StrictErrorListener.instance);
        try {
            ParseTree tree = parser.parse();
            SQLCustomVisitor visitor = new SQLCustomVisitor(manager);
            return String.valueOf(visitor.visit(tree));
        } catch (Exception e) {
            return "Exception: illegal SQL statement! Error message: " + e.getMessage();
        }
    }
}