package format;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class PrintFormat {

    private Header header;
    private Body body;
    private int[] columnWidths;
    private NullPolicy nullPolicy = NullPolicy.EMPTY_STRING;
    private boolean restrict = false;

    private PrintFormat() {}

    public String getContent() {
        return toString();
    }

    private List<String> getLines() {
        List<String> lines = new ArrayList<>();
        if ((header != null && !header.isEmpty()) || (body != null && !body.isEmpty())) {
            String verticalSep = "|";
            String horizontalSep = "-";
            String joinSep = "+";
            lines.addAll(header.print(columnWidths, horizontalSep, verticalSep, joinSep));
            lines.addAll(body.print(columnWidths, horizontalSep, verticalSep, joinSep));
        }
        return lines;
    }

    @Override
    public String toString() {
        List<String> list = getLines();
        String lineSep = "\n";
        StringJoiner sj = new StringJoiner(lineSep);
        for (String s : list)
            sj.add(s);
        return sj.toString();
    }

    public static class ConsoleTableBuilder {

        PrintFormat printFormat = new PrintFormat();

        public ConsoleTableBuilder() {
            printFormat.header = new Header();
            printFormat.body = new Body();
        }

        public ConsoleTableBuilder addHeaders(List<Cell> headers) {
            printFormat.header.addHeads(headers);
            return this;
        }

        public ConsoleTableBuilder addRows(List<List<Cell>> rows) {
            printFormat.body.addRows(rows);
            return this;
        }

        public PrintFormat build() {
            //compute max column widths
            if (!printFormat.header.isEmpty() || !printFormat.body.isEmpty()) {
                List<List<Cell>> allRows = new ArrayList<>();
                allRows.add(printFormat.header.cells);
                allRows.addAll(printFormat.body.rows);
                int maxColumn = allRows.stream().map(List::size).mapToInt(size -> size).max().getAsInt();
                int minColumn = allRows.stream().map(List::size).mapToInt(size -> size).min().getAsInt();
                if (maxColumn != minColumn && printFormat.restrict) {
                    throw new IllegalArgumentException("number of columns for each row must be the same when strict mode used.");
                }
                printFormat.columnWidths = new int[maxColumn];
                for (List<Cell> row : allRows) {
                    for (int i = 0; i < row.size(); i++) {
                        Cell cell = row.get(i);
                        if (cell == null || cell.getValue() == null) {
                            cell = printFormat.nullPolicy.getCell(cell);
                            row.set(i, cell);
                        }
                        int length = StringPadUtil.strLength(cell.getValue());
                        if (printFormat.columnWidths[i] < length)
                            printFormat.columnWidths[i] = length;
                    }
                }
            }
            return printFormat;
        }
    }
}