package selectFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrintUtil {

    public static List<String> printLineSep(int[] columnWidths, String horizontalSep, String verticalSep, String joinSep) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < columnWidths.length; i++) {
            String l = String.join("", Collections.nCopies(columnWidths[i] +
                    StringPadUtil.strLength(verticalSep) + 1, horizontalSep));
            line.append(joinSep).append(l).append(i == columnWidths.length - 1 ? joinSep : "");
        }
        return Collections.singletonList(line.toString());
    }

    public static List<String> printRows(List<List<Cell>> rows, int[] columnWidths, String verticalSep) {
        List<String> result = new ArrayList<>();
        for (List<Cell> row : rows) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < row.size(); i++) {
                Cell cell = row.get(i);
                if (cell == null) {
                    cell = new Cell("");
                }
                //add v-sep after last column
                String verStrTemp = i == row.size() - 1 ? verticalSep : "";
                Align align = cell.getAlign();
                switch (align) {
                    case LEFT:
                        sb.append(String.format("%s %s %s", verticalSep, StringPadUtil.rightPad(cell.getValue(), columnWidths[i]), verStrTemp));
                        break;
                    case RIGHT:
                        sb.append(String.format("%s %s %s", verticalSep, StringPadUtil.leftPad(cell.getValue(), columnWidths[i]), verStrTemp));
                        break;
                    case CENTER:
                        sb.append(String.format("%s %s %s", verticalSep, StringPadUtil.center(cell.getValue(), columnWidths[i]), verStrTemp));
                        break;
                    default:
                        throw new IllegalArgumentException("wrong align : " + align.name());
                }
            }
            result.add(sb.toString());
        }
        return result;
    }
}