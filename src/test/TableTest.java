package test;

import org.junit.Test; 
import org.junit.Before;
import schema.*;


import static org.junit.Assert.assertEquals;


public class TableTest {
        Manager db;
        int maxPageNum = 8;
        int maxPageSize = 128;
        int testNum = 1000;

        @Before
        public void before() throws Exception {
                db = new Manager();
                db.setPageAttr(maxPageNum, maxPageSize);
                Column col1 = new Column("id", Type.INT, true);
                Column col2 = new Column("name", Type.STRING, false);
                Column col3 = new Column("score", Type.DOUBLE, false);
                Column[] columns = new Column[]{col1, col2, col3};
                Database txy = db.createDatabase("txy");
                Table table = txy.createTable("grade", columns);
                for (int i = 0; i < testNum; i++) {
                        Entry[] e = new Entry[]{
                                new Entry(0, i, table),
                                new Entry(1, "A", table),
                                new Entry(2, i, table)
                        };
                        table.insert(e);
                }
                db.quit();
        }

        @Test
        public void testRecoverTable() throws Exception {
                db = new Manager();
                Table table = db.useDatabase("txy").useTable("grade");
                assertEquals(table.pagesSize(), maxPageNum);
                assertEquals(table.getIndex().size(), testNum);
        }


        @Test
        public void testGet() throws Exception {
                db = new Manager();
                Table table = db.useDatabase("txy").useTable("grade");
                for (int i = 0; i < testNum; i++) {
                        assertEquals(table.pagesSize(), maxPageNum);
                        Entry a = new Entry(0, i, table);
                        Entry[] e = new Entry[]{
                                new Entry(0, i, table),
                                new Entry(1, "A", table),
                                new Entry(2, i, table)
                        };
                        assertEquals(new Row(e).toString(), table.get(a).toString());
                }
        }

}



