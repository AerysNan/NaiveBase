package test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import query.*;
import schema.*;


public class DatabaseTest {
    Database database;
    int testNum;

    @Before
    public void setUp() {
        database = new Database("admin");
        Column col1 = new Column("id", Type.INT, 1, false, -1);
        Column col2 = new Column("name", Type.STRING, 0, false, 10);
        Column col3 = new Column("score", Type.DOUBLE, 0, false, -1);
        Column[] columns = new Column[]{col1, col2, col3};
        database.createTable("grade", columns);
        Column col1_ = new Column("id", Type.INT, 1, false, -1);
        Column col2_ = new Column("nickname", Type.STRING, 0, false, 10);
        Column[] columns_ = new Column[]{col1_, col2_};
        database.createTable("grades", columns_);
        testNum = 100;
        for (int i = 0; i < testNum; i++) {
            database.getTable("grade").insert(new String[]{String.valueOf(i), "'A'", String.valueOf(100 - i)}, null);
            database.getTable("grades").insert(new String[]{String.valueOf(i), "'B"}, null);
        }
    }

    @Test
    public void testSimpleSelectStaticEmpty() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Comparer comparer = new Comparer(ComparerType.NUMBER, String.valueOf(1));
        Comparer comparee = new Comparer(ComparerType.NUMBER, String.valueOf(2));
        Condition condition = new Condition(comparer, comparee, ComparatorType.EQ);
        String result = database.select(null, tablesQueried, condition);
        assertEquals(result, "--EMPTY--");
    }

    @Test
    public void testSimpleSelectNotStaticEmpty() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Comparer comparer = new Comparer(ComparerType.COLUMN, "id");
        Comparer comparee = new Comparer(ComparerType.NUMBER, String.valueOf(1000));
        Condition condition = new Condition(comparer, comparee, ComparatorType.GT);
        String result = database.select(null, tablesQueried, condition);
        assertEquals(result, "--EMPTY--");
    }

    @Test
    public void testSimpleSelectStaticAll() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Comparer comparer = new Comparer(ComparerType.NUMBER, String.valueOf(1));
        Comparer comparee = new Comparer(ComparerType.NUMBER, String.valueOf(2));
        Condition condition = new Condition(comparer, comparee, ComparatorType.LE);
        String result = database.select(null, tablesQueried, condition);
        assertEquals(result.split("\n").length, testNum);
    }

    @Test
    public void testSimpleSelectNotStaticAll() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Comparer comparer = new Comparer(ComparerType.NUMBER, String.valueOf(-1000));
        Comparer comparee = new Comparer(ComparerType.COLUMN, "score");
        Condition condition = new Condition(comparer, comparee, ComparatorType.LT);
        String result = database.select(null, tablesQueried, condition);
        assertEquals(result.split("\n").length, testNum);
    }

    @Test
    public void testSimpleSelectColumnsCompareNearlyEmpty() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Comparer comparer = new Comparer(ComparerType.COLUMN, "id");
        Comparer comparee = new Comparer(ComparerType.COLUMN, "score");
        Condition condition = new Condition(comparer, comparee, ComparatorType.EQ);
        String result = database.select(null, tablesQueried, condition);
        assertEquals(result.split("\n").length, 1);
    }

    @Test
    public void testSimpleSelectColumnsCompareNearlyAll() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Comparer comparer = new Comparer(ComparerType.COLUMN, "id");
        Comparer comparee = new Comparer(ComparerType.COLUMN, "score");
        Condition condition = new Condition(comparer, comparee, ComparatorType.NE);
        String result = database.select(null, tablesQueried, condition);
        assertEquals(result.split("\n").length, testNum - 1);
    }

    @Test
    public void testSimpleSelectPrimaryColumnCompareAll() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Comparer comparer = new Comparer(ComparerType.COLUMN, "id");
        Comparer comparee = new Comparer(ComparerType.NUMBER, String.valueOf(1000));
        Condition condition = new Condition(comparer, comparee, ComparatorType.LE);
        String result = database.select(null, tablesQueried, condition);
        assertEquals(result.split("\n").length, testNum);
    }

    @Test
    public void testSimpleSelectPrimaryColumnCompareEmpty() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Comparer comparer = new Comparer(ComparerType.COLUMN, "id");
        Comparer comparee = new Comparer(ComparerType.NUMBER, String.valueOf(1000));
        Condition condition = new Condition(comparer, comparee, ComparatorType.GT);
        String result = database.select(null, tablesQueried, condition);
        assertEquals(result, "--EMPTY--");
    }

    @Test
    public void testSimpleSelectNotPrimaryColumnAll() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Comparer comparer = new Comparer(ComparerType.NUMBER, String.valueOf(1000));
        Comparer comparee = new Comparer(ComparerType.COLUMN, "score");
        Condition condition = new Condition(comparer, comparee, ComparatorType.GT);
        String result = database.select(null, tablesQueried, condition);
        assertEquals(result.split("\n").length, testNum);
    }

    @Test
    public void testSimpleSelectNotPrimaryColumnEmpty() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Comparer comparer = new Comparer(ComparerType.NUMBER, String.valueOf(1000));
        Comparer comparee = new Comparer(ComparerType.COLUMN, "score");
        Condition condition = new Condition(comparer, comparee, ComparatorType.LT);
        String result = database.select(null, tablesQueried, condition);
        assertEquals(result, "--EMPTY--");
    }

    @Test
    public void testJointSelectStaticEmpty() {
        Comparer comparer = new Comparer(ComparerType.NUMBER, String.valueOf(1));
        Comparer comparee = new Comparer(ComparerType.NUMBER, String.valueOf(2));
        Condition onCondition = new Condition(comparer, comparee, ComparatorType.EQ);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = new JointTable(database.getTable("grade"), database.getTable("grades"), onCondition);
        String result = database.select(null, tablesQueried, null);
        assertEquals(result, "--EMPTY--");
    }

    @Test
    public void testJointSelectStaticAll() {
        Comparer comparer = new Comparer(ComparerType.NUMBER, String.valueOf(1));
        Comparer comparee = new Comparer(ComparerType.NUMBER, String.valueOf(2));
        Condition onCondition = new Condition(comparer, comparee, ComparatorType.LT);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = new JointTable(database.getTable("grade"), database.getTable("grades"), onCondition);
        String result = database.select(null, tablesQueried, null);
        assertEquals(result.split("\n").length, testNum * testNum);
    }

    @Test
    public void testJointSelectNotStaticEmpty() {
        Comparer comparer = new Comparer(ComparerType.COLUMN, "grade.id");
        Comparer comparee = new Comparer(ComparerType.NUMBER, String.valueOf(1001));
        Condition onCondition = new Condition(comparer, comparee, ComparatorType.EQ);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = new JointTable(database.getTable("grade"), database.getTable("grades"), onCondition);
        String result = database.select(null, tablesQueried, null);
        assertEquals(result, "--EMPTY--");
    }

    @Test
    public void testJointSelectNotStaticPartly() {
        Comparer comparer = new Comparer(ComparerType.COLUMN, "grade.id");
        Comparer comparee = new Comparer(ComparerType.NUMBER, String.valueOf(11));
        Condition onCondition = new Condition(comparer, comparee, ComparatorType.EQ);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = new JointTable(database.getTable("grade"), database.getTable("grades"), onCondition);
        String result = database.select(null, tablesQueried, null);
        assertEquals(result.split("\n").length, testNum);
    }

    @Test
    public void testJointSelectNotStaticAll() {
        Comparer comparee = new Comparer(ComparerType.COLUMN, "grade.id");
        Comparer comparer = new Comparer(ComparerType.NUMBER, String.valueOf(10001));
        Condition onCondition = new Condition(comparer, comparee, ComparatorType.GT);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = new JointTable(database.getTable("grade"), database.getTable("grades"), onCondition);
        String result = database.select(null, tablesQueried, null);
        assertEquals(result.split("\n").length, testNum * testNum);
    }

    @Test
    public void testJointSelectColumnsCompareNearlyEmpty() {
        Comparer comparee = new Comparer(ComparerType.COLUMN, "grade.id");
        Comparer comparer = new Comparer(ComparerType.COLUMN, "grades.id");
        Condition onCondition = new Condition(comparer, comparee, ComparatorType.EQ);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = new JointTable(database.getTable("grade"), database.getTable("grades"), onCondition);
        String result = database.select(null, tablesQueried, null);
        assertEquals(result.split("\n").length, testNum);
    }

    @Test
    public void testJointSelectColumnsCompareNearlyAll() {
        Comparer comparee = new Comparer(ComparerType.COLUMN, "grade.id");
        Comparer comparer = new Comparer(ComparerType.COLUMN, "grades.id");
        Condition onCondition = new Condition(comparer, comparee, ComparatorType.NE);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = new JointTable(database.getTable("grade"), database.getTable("grades"), onCondition);
        String result = database.select(null, tablesQueried, null);
        String[] tmp = result.split("\n");
        assertEquals(result.split("\n").length, testNum * (testNum - 1));
    }

    @Test
    public void testJointSelectColumnCompareAll() {
        Comparer comparer = new Comparer(ComparerType.COLUMN, "grade.score");
        Comparer comparee = new Comparer(ComparerType.NUMBER, String.valueOf(1000));
        Condition onCondition = new Condition(comparer, comparee, ComparatorType.LT);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = new JointTable(database.getTable("grade"), database.getTable("grades"), onCondition);
        String result = database.select(null, tablesQueried, null);
        assertEquals(result.split("\n").length, testNum * testNum);
    }

    @Test
    public void testJointSelectColumnComparePartly() {
        Comparer comparee = new Comparer(ComparerType.COLUMN, "grade.score");
        Comparer comparer = new Comparer(ComparerType.NUMBER, String.valueOf(1));
        Condition onCondition = new Condition(comparer, comparee, ComparatorType.EQ);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = new JointTable(database.getTable("grade"), database.getTable("grades"), onCondition);
        String result = database.select(null, tablesQueried, null);
        assertEquals(result.split("\n").length, testNum);
    }

    @Test
    public void testJointSelectColumnCompareEmpty() {
        Comparer comparee = new Comparer(ComparerType.COLUMN, "grade.score");
        Comparer comparer = new Comparer(ComparerType.NUMBER, String.valueOf(1000));
        Condition onCondition = new Condition(comparer, comparee, ComparatorType.EQ);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = new JointTable(database.getTable("grade"), database.getTable("grades"), onCondition);
        String result = database.select(null, tablesQueried, null);
        assertEquals(result, "--EMPTY--");
    }

    @Test
    public void testJointSelectColumnCompareWhere() {
        Comparer comparee = new Comparer(ComparerType.COLUMN, "grade.id");
        Comparer comparer = new Comparer(ComparerType.COLUMN, "grades.id");
        Condition onCondition = new Condition(comparer, comparee, ComparatorType.NE);
        Comparer comparer_ = new Comparer(ComparerType.COLUMN, "grade.score");
        Comparer comparee_ = new Comparer(ComparerType.NUMBER, String.valueOf(1));
        Condition whereCondition = new Condition(comparer_, comparee_, ComparatorType.EQ);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = new JointTable(database.getTable("grade"), database.getTable("grades"), onCondition);
        String result = database.select(null, tablesQueried, whereCondition);
        assertEquals(result.split("\n").length, testNum - 1);
    }
}