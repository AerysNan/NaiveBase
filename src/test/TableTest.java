package test;

import global.Global;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import schema.*;
import type.ColumnType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TableTest {
    private Manager manager;

    @Before
    public void before() {
        manager = new Manager();
        Global.maxPageNum = 8;
        Global.maxPageSize = 128;
        manager.createDatabase("test");
        manager.switchDatabase("test");
    }

    @Test
    public void testComposite() {
        int testNum = 50;
        Column col1 = new Column("first", ColumnType.INT, 2, false, -1);
        Column col2 = new Column("second", ColumnType.INT, 2, false, -1);
        Column col3 = new Column("score", ColumnType.DOUBLE, 0, false, -1);
        Column[] columns = new Column[]{col1, col2, col3};
        manager.createTable("test_composite", columns);
        for (int i = 0; i < testNum; i++)
            for (int j = 0; j < testNum; j++)
                manager.insert("test_composite", new String[]{String.valueOf(i), String.valueOf(j), String.valueOf((double) (i * j))}, null);
        manager.quit();
        manager = new Manager();
        manager.switchDatabase("test");
        for (int i = 0; i < testNum; i++) {
            for (int j = 0; j < testNum; j++) {
                Entry fst = new Entry(i);
                Entry snd = new Entry(j);
                Entry[] e = new Entry[]{
                        new Entry(i),
                        new Entry(j),
                        new Entry((double) (i * j))
                };
                assertTrue(manager.get("test_composite", new Entry[]{fst, snd}).toString().contains(new Row(e, -1).toString()));
            }
        }
    }

    @Test
    public void testGet() {
        int testNum = 2500;
        Column col1 = new Column("id", ColumnType.INT, 1, false, -1);
        Column col2 = new Column("name", ColumnType.STRING, 0, false, 10);
        Column col3 = new Column("score", ColumnType.DOUBLE, 0, false, -1);
        Column[] columns = new Column[]{col1, col2, col3};
        manager.createTable("test_get", columns);
        for (int i = 0; i < testNum; i++)
            manager.insert("test_get", new String[]{String.valueOf(i), "'A'", String.valueOf(100 - i)}, null);
        manager.quit();
        manager = new Manager();
        manager.switchDatabase("test");
        for (int i = 0; i < testNum; i++) {
            Entry key = new Entry(i);
            Entry[] e = new Entry[]{
                    new Entry(i),
                    new Entry("A"),
                    new Entry((double) (100 - i))
            };
            assertEquals(new Row(e, -1).toString(), manager.get("test_get", new Entry[]{key}).toString());
        }
    }

    @After
    public void after() {
        manager.deleteDatabaseIfExist("test");
        manager.quit();
    }
}