package selectFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Header {

    public List<Cell> cells;

    public Header() {
        this.cells = new ArrayList<>();
    }

    public void addHead(Cell cell) {
        cells.add(cell);
    }

    public void addHeads(List<Cell> headers) {
        cells.addAll(headers);
    }

    public boolean isEmpty() {
        return cells == null || cells.isEmpty();
    }

    public List<String> print(int[] columnWidths, String horizontalSep, String verticalSep, String joinSep) {
        List<String> result = new ArrayList<>();
        if (!isEmpty()) {
            result.addAll(PrintUtil.printLineSep(columnWidths, horizontalSep, verticalSep, joinSep));
            result.addAll(PrintUtil.printRows(Collections.singletonList(cells), columnWidths, verticalSep));
        }
        return result;
    }
}