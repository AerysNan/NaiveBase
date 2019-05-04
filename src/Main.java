import schema.*;

import java.io.File;


public class Main {
    public static void main(String[] args) throws Exception {
        File file = new File("./data");
        if(!file.exists())
            file.mkdir();
        Database db = new Database();
        Column col1 = new Column("id", Type.INT, true);
        Column col2 = new Column("name", Type.STRING, false);
        Column col3 = new Column("score", Type.DOUBLE, false);
        Column[] columns = new Column[]{col1, col2, col3};
        Table table = db.createTable("grade", columns);
        Entry[] e1 = new Entry[]{
                new Entry(0, 1, table),
                new Entry(1, "A", table),
                new Entry(2, 47.56, table)
        };
        Entry[] e2 = new Entry[]{
                new Entry(0, 2, table),
                new Entry(1, "B", table),
                new Entry(2, 56.43, table)
        };
        Entry[] e3 = new Entry[]{
                new Entry(0, 3, table),
                new Entry(1, "C", table),
                new Entry(2, 76.51, table)
        };
        table.insert(e1);
        table.insert(e2);
        table.insert(e3);
        db.quit();
    }
}