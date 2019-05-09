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

    public void evaluate(String expression) {
        SQLLexer lexer = new SQLLexer(CharStreams.fromString(expression));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SQLParser parser = new SQLParser(tokens);
        ParseTree tree = parser.parse();
        SQLCustomVisitor visitor = new SQLCustomVisitor(manager);
        visitor.visit(tree);
    }
}
