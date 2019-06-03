package format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Header {
    List<Cell> cells;

    Header() {
        this.cells = new ArrayList<>();
    }

    void addHeads(List<Cell> headers) {
        cells.addAll(headers);
    }

    boolean isEmpty() {
        return cells == null || cells.isEmpty();
    }

    List<String> print(int[] columnWidths, String horizontalSep, String verticalSep, String joinSep) {
        List<String> result = new ArrayList<>();
        if (!isEmpty()) {
            result.addAll(PrintUtil.printLineSep(columnWidths, horizontalSep, verticalSep, joinSep));
            result.addAll(PrintUtil.printRows(Collections.singletonList(cells), columnWidths, verticalSep));
        }
        return result;
    }
}