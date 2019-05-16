package test;

import global.Global;
import org.junit.*;
import schema.*;


import static org.junit.Assert.assertEquals;


public class TableTest {
    private Manager manager;
    private int testNum = 1000;

    @Before
    public void beforeClass() {
        manager = new Manager();
        Global.maxPageNum = 8;
        Global.maxPageSize = 128;
        Column col1 = new Column("id", Type.INT, true, false, -1);
        Column col2 = new Column("name", Type.STRING, false, false, 10);
        Column col3 = new Column("score", Type.DOUBLE, false, false, -1);
        Column[] columns = new Column[]{col1, col2, col3};
        manager.createDatabase("test");
        manager.switchDatabase("test");
        manager.createTable("grade", columns);
        for (int i = 0; i < testNum; i++)
            manager.insert("grade", new String[]{String.valueOf(i), "'A'", String.valueOf(100 - i)}, null);
        manager.quit();
    }

    @Test
    public void testGet() {
        manager = new Manager();
        manager.switchDatabase("test");
        for (int i = 0; i < testNum; i++) {
            Entry key = new Entry(0, i);
            Entry[] e = new Entry[]{
                    new Entry(0, i),
                    new Entry(1, "A"),
                    new Entry(2, (double) (100 - i))
            };
            assertEquals(new Row(e, -1).toString(), manager.get("grade", key).toString());
        }
    }

    @After
    public void after() {
        manager.deleteDatabaseIfExist("test");
        manager.quit();
    }
}
