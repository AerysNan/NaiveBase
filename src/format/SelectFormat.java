package format;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class SelectFormat {

    private Header header;
    private Body body;
    private int[] columnWidths;
    private NullPolicy nullPolicy = NullPolicy.EMPTY_STRING;
    private boolean restrict = false;

    private SelectFormat() {}

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

        SelectFormat selectFormat = new SelectFormat();

        public ConsoleTableBuilder() {
            selectFormat.header = new Header();
            selectFormat.body = new Body();
        }

        public ConsoleTableBuilder addHeaders(List<Cell> headers) {
            selectFormat.header.addHeads(headers);
            return this;
        }

        public ConsoleTableBuilder addRows(List<List<Cell>> rows) {
            selectFormat.body.addRows(rows);
            return this;
        }

        public SelectFormat build() {
            //compute max column widths
            if (!selectFormat.header.isEmpty() || !selectFormat.body.isEmpty()) {
                List<List<Cell>> allRows = new ArrayList<>();
                allRows.add(selectFormat.header.cells);
                allRows.addAll(selectFormat.body.rows);
                int maxColumn = allRows.stream().map(List::size).mapToInt(size -> size).max().getAsInt();
                int minColumn = allRows.stream().map(List::size).mapToInt(size -> size).min().getAsInt();
                if (maxColumn != minColumn && selectFormat.restrict) {
                    throw new IllegalArgumentException("number of columns for each row must be the same when strict mode used.");
                }
                selectFormat.columnWidths = new int[maxColumn];
                for (List<Cell> row : allRows) {
                    for (int i = 0; i < row.size(); i++) {
                        Cell cell = row.get(i);
                        if (cell == null || cell.getValue() == null) {
                            cell = selectFormat.nullPolicy.getCell(cell);
                            row.set(i, cell);
                        }
                        int length = StringPadUtil.strLength(cell.getValue());
                        if (selectFormat.columnWidths[i] < length)
                            selectFormat.columnWidths[i] = length;
                    }
                }
            }
            return selectFormat;
        }
    }
}