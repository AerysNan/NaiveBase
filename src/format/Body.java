package format;

import java.util.ArrayList;
import java.util.List;

public class Body {
    public List<List<Cell>> rows;

    Body() {
        rows = new ArrayList<>();
    }

    void addRows(List<List<Cell>> rows) {
        this.rows.addAll(rows);
    }

    boolean isEmpty() {
        return rows == null || rows.isEmpty();
    }

    List<String> print(int[] columnWidths, String horizontalSep, String verticalSep, String joinSep) {
        List<String> result = new ArrayList<>();
        if (!isEmpty()) {
            result.addAll(PrintUtil.printLineSep(columnWidths, horizontalSep, verticalSep, joinSep));
            result.addAll(PrintUtil.printRows(rows, columnWidths, verticalSep));
            result.addAll(PrintUtil.printLineSep(columnWidths, horizontalSep, verticalSep, joinSep));
        }
        return result;
    }
}