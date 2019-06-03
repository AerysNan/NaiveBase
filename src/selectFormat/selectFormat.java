package selectFormat;

import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class selectFormat {

    private Header header;
    private Body body;
    String lineSep = "\n";
    String verticalSep = "|";
    String horizontalSep = "-";
    String joinSep = "+";
    int[] columnWidths;
    NullPolicy nullPolicy = NullPolicy.EMPTY_STRING;
    boolean restrict = false;

    private selectFormat() {
    }

    public void print() {
        System.out.println(getContent());
    }

    public String getContent() {
        return toString();
    }

    List<String> getLines() {
        List<String> lines = new ArrayList<>();
        if ((header != null && !header.isEmpty()) || (body != null && !body.isEmpty())) {
            lines.addAll(header.print(columnWidths, horizontalSep, verticalSep, joinSep));
            lines.addAll(body.print(columnWidths, horizontalSep, verticalSep, joinSep));
        }
        return lines;
    }

    @Override
    public String toString() {
        return StringUtils.join(getLines(), lineSep);
    }

    public static class ConsoleTableBuilder {

        selectFormat selectFormat = new selectFormat();

        public ConsoleTableBuilder() {
            selectFormat.header = new Header();
            selectFormat.body = new Body();
        }

        public ConsoleTableBuilder addHead(Cell cell) {
            selectFormat.header.addHead(cell);
            return this;
        }

        public ConsoleTableBuilder addRow(List<Cell> row) {
            selectFormat.body.addRow(row);
            return this;
        }

        public ConsoleTableBuilder addHeaders(List<Cell> headers) {
            selectFormat.header.addHeads(headers);
            return this;
        }

        public ConsoleTableBuilder addRows(List<List<Cell>> rows) {
            selectFormat.body.addRows(rows);
            return this;
        }

        public ConsoleTableBuilder lineSep(String lineSep) {
            selectFormat.lineSep = lineSep;
            return this;
        }

        public ConsoleTableBuilder verticalSep(String verticalSep) {
            selectFormat.verticalSep = verticalSep;
            return this;
        }

        public ConsoleTableBuilder horizontalSep(String horizontalSep) {
            selectFormat.horizontalSep = horizontalSep;
            return this;
        }

        public ConsoleTableBuilder joinSep(String joinSep) {
            selectFormat.joinSep = joinSep;
            return this;
        }

        public ConsoleTableBuilder nullPolicy(NullPolicy nullPolicy) {
            selectFormat.nullPolicy = nullPolicy;
            return this;
        }

        public ConsoleTableBuilder restrict(boolean restrict) {
            selectFormat.restrict = restrict;
            return this;
        }

        public selectFormat build() {
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
                        if (selectFormat.columnWidths[i] < length) {
                            selectFormat.columnWidths[i] = length;
                        }
                    }
                }
            }
            return selectFormat;
        }
    }
}