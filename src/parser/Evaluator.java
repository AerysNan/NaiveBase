package parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import schema.Session;

public class Evaluator {
    private Session session;

    public Evaluator(Session session) {
        this.session = session;
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
            SQLCustomVisitor visitor = new SQLCustomVisitor(session);
            return String.valueOf(visitor.visit(tree));
        } catch (Exception e) {
            return "Exception: illegal SQL statement! Error message: " + e.getMessage();
        }
    }
}