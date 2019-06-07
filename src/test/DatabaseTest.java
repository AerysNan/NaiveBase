package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import query.*;
import schema.Column;
import schema.Database;
import schema.Manager;
import schema.Table;
import type.ColumnType;
import type.ComparatorType;
import type.ComparerType;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class DatabaseTest {
    private Manager manager;
    private Database database;
    private int testNum;

    private Expression buildSimpleColumnExpression(String columnName) {
        return new Expression(new Comparer(ComparerType.COLUMN, columnName));
    }

    private Expression buildSimpleNumberExpression(String value) {
        return new Expression(new Comparer(ComparerType.NUMBER, value));
    }

    private Logic buildSimpleLogic(Condition condition) {
        return new Logic(condition);
    }

    private JointTable buildSimpleJoinTable(Condition onCondition) {
        ArrayList<Table> list = new ArrayList<>();
        list.add(database.getTable("grade"));
        list.add(database.getTable("grades"));
        return new JointTable(list, buildSimpleLogic(onCondition));
    }

    @Before
    public void setUp() {
        manager = new Manager();
        database = manager.getDatabase("admin");

        Column col1 = new Column("id", ColumnType.INT, 1, false, -1);
        Column col2 = new Column("name", ColumnType.STRING, 0, false, 10);
        Column col3 = new Column("score", ColumnType.DOUBLE, 0, false, -1);
        Column[] columns = new Column[]{col1, col2, col3};
        database.createTable("grade", columns);

        Column col1_ = new Column("id", ColumnType.INT, 1, false, -1);
        Column col2_ = new Column("nickname", ColumnType.STRING, 0, false, 10);
        Column[] columns_ = new Column[]{col1_, col2_};
        database.createTable("grades", columns_);

        testNum = 100;
        for (int i = 0; i < testNum; i++) {
            database.getTable("grade").insert(new String[]{String.valueOf(i), "'A'", String.valueOf(100 - i)}, null);
            database.getTable("grades").insert(new String[]{String.valueOf(i), "'B'"}, null);
        }
    }

    @Test
    public void testSimpleSelectStaticEmpty() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Expression left = buildSimpleNumberExpression("1");
        Expression right = buildSimpleNumberExpression("2");
        Condition condition = new Condition(left, right, ComparatorType.EQ);
        String result = database.select(null, tablesQueried, buildSimpleLogic(condition), false);
        assertEquals("Empty set.", result);
    }

    @Test
    public void testSimpleSelectNotStaticEmpty() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Expression left = buildSimpleColumnExpression("id");
        Expression right = buildSimpleNumberExpression("1000");
        Condition condition = new Condition(left, right, ComparatorType.GT);
        String result = database.select(null, tablesQueried, buildSimpleLogic(condition), false);
        assertEquals("Empty set.", result);
    }

    @Test
    public void testSimpleSelectStaticAll() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Expression left = buildSimpleNumberExpression("1");
        Expression right = buildSimpleNumberExpression("2");
        Condition condition = new Condition(left, right, ComparatorType.LE);
        String result = database.select(null, tablesQueried, buildSimpleLogic(condition), false);
        assertEquals(testNum + 5, result.split("\n").length);
    }

    @Test
    public void testSimpleSelectNotStaticAll() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Expression left = buildSimpleNumberExpression("-1000");
        Expression right = buildSimpleColumnExpression("score");
        Condition condition = new Condition(left, right, ComparatorType.LT);
        String result = database.select(null, tablesQueried, buildSimpleLogic(condition), false);
        assertEquals(testNum + 5, result.split("\n").length);
    }

    @Test
    public void testSimpleSelectColumnsCompareNearlyEmpty() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Expression left = buildSimpleColumnExpression("id");
        Expression right = buildSimpleColumnExpression("score");
        Condition condition = new Condition(left, right, ComparatorType.EQ);
        String result = database.select(null, tablesQueried, buildSimpleLogic(condition), false);
        assertEquals(1 + 5, result.split("\n").length);
    }

    @Test
    public void testSimpleSelectColumnsCompareNearlyAll() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Expression left = buildSimpleColumnExpression("id");
        Expression right = buildSimpleColumnExpression("score");
        Condition condition = new Condition(left, right, ComparatorType.NE);
        String result = database.select(null, tablesQueried, buildSimpleLogic(condition), false);
        assertEquals(testNum - 1 + 5, result.split("\n").length);
    }

    @Test
    public void testSimpleSelectPrimaryColumnCompareAll() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Expression left = buildSimpleColumnExpression("id");
        Expression right = buildSimpleNumberExpression("1000");
        Condition condition = new Condition(left, right, ComparatorType.LE);
        String result = database.select(null, tablesQueried, buildSimpleLogic(condition), false);
        assertEquals(testNum + 5, result.split("\n").length);
    }

    @Test
    public void testSimpleSelectPrimaryColumnCompareEmpty() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Expression left = buildSimpleColumnExpression("id");
        Expression right = buildSimpleNumberExpression("1000");
        Condition condition = new Condition(left, right, ComparatorType.GT);
        String result = database.select(null, tablesQueried, buildSimpleLogic(condition), false);
        assertEquals("Empty set.", result);
    }

    @Test
    public void testSimpleSelectNotPrimaryColumnAll() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Expression left = buildSimpleNumberExpression("1000");
        Expression right = buildSimpleColumnExpression("score");
        Condition condition = new Condition(left, right, ComparatorType.GT);
        String result = database.select(null, tablesQueried, buildSimpleLogic(condition), false);
        assertEquals(testNum + 5, result.split("\n").length);
    }

    @Test
    public void testSimpleSelectNotPrimaryColumnEmpty() {
        SimpleTable[] tablesQueried = new SimpleTable[1];
        tablesQueried[0] = new SimpleTable(database.getTable("grade"));
        Expression left = buildSimpleNumberExpression("1000");
        Expression right = buildSimpleColumnExpression("score");
        Condition condition = new Condition(left, right, ComparatorType.LT);
        String result = database.select(null, tablesQueried, buildSimpleLogic(condition), false);
        assertEquals("Empty set.", result);
    }

    @Test
    public void testJointSelectStaticEmpty() {
        Expression left = buildSimpleNumberExpression("1");
        Expression right = buildSimpleNumberExpression("2");
        Condition onCondition = new Condition(left, right, ComparatorType.EQ);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = buildSimpleJoinTable(onCondition);
        String result = database.select(null, tablesQueried, null, false);
        assertEquals("Empty set.", result);
    }

    @Test
    public void testJointSelectStaticAll() {
        Expression left = buildSimpleNumberExpression("1");
        Expression right = buildSimpleNumberExpression("2");
        Condition onCondition = new Condition(left, right, ComparatorType.LT);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = buildSimpleJoinTable(onCondition);
        String result = database.select(null, tablesQueried, null, false);
        assertEquals(testNum * testNum + 5, result.split("\n").length);
    }

    @Test
    public void testJointSelectNotStaticEmpty() {
        Expression left = buildSimpleColumnExpression("grade.id");
        Expression right = buildSimpleNumberExpression("1001");
        Condition onCondition = new Condition(left, right, ComparatorType.EQ);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = buildSimpleJoinTable(onCondition);
        String result = database.select(null, tablesQueried, null, false);
        assertEquals("Empty set.", result);
    }

    @Test
    public void testJointSelectNotStaticPartly() {
        Expression left = buildSimpleColumnExpression("grade.id");
        Expression right = buildSimpleNumberExpression("11");
        Condition onCondition = new Condition(left, right, ComparatorType.EQ);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = buildSimpleJoinTable(onCondition);
        String result = database.select(null, tablesQueried, null, false);
        assertEquals(testNum + 5, result.split("\n").length);
    }

    @Test
    public void testJointSelectNotStaticAll() {
        Expression left = buildSimpleColumnExpression("grade.id");
        Expression right = buildSimpleNumberExpression("10001");
        Condition onCondition = new Condition(left, right, ComparatorType.LT);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = buildSimpleJoinTable(onCondition);
        String result = database.select(null, tablesQueried, null, false);
        assertEquals(testNum * testNum + 5, result.split("\n").length);
    }

    @Test
    public void testJointSelectColumnsCompareNearlyEmpty() {
        Expression left = buildSimpleColumnExpression("grade.id");
        Expression right = buildSimpleColumnExpression("grades.id");
        Condition onCondition = new Condition(left, right, ComparatorType.EQ);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = buildSimpleJoinTable(onCondition);
        String result = database.select(null, tablesQueried, null, false);
        assertEquals(testNum + 5, result.split("\n").length);
    }

    @Test
    public void testJointSelectColumnsCompareNearlyAll() {
        Expression left = buildSimpleColumnExpression("grade.id");
        Expression right = buildSimpleColumnExpression("grades.id");
        Condition onCondition = new Condition(left, right, ComparatorType.NE);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = buildSimpleJoinTable(onCondition);
        String result = database.select(null, tablesQueried, null, false);
        assertEquals(testNum * (testNum - 1) + 5, result.split("\n").length);
    }

    @Test
    public void testJointSelectColumnCompareAll() {
        Expression left = buildSimpleColumnExpression("grade.score");
        Expression right = buildSimpleNumberExpression("1000");
        Condition onCondition = new Condition(left, right, ComparatorType.LT);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = buildSimpleJoinTable(onCondition);
        String result = database.select(null, tablesQueried, null, false);
        assertEquals(testNum * testNum + 5, result.split("\n").length);
    }

    @Test
    public void testJointSelectColumnComparePartly() {
        Expression left = buildSimpleColumnExpression("grade.score");
        Expression right = buildSimpleNumberExpression("1");
        Condition onCondition = new Condition(left, right, ComparatorType.EQ);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = buildSimpleJoinTable(onCondition);
        String result = database.select(null, tablesQueried, null, false);
        assertEquals(testNum + 5, result.split("\n").length);
    }

    @Test
    public void testJointSelectColumnCompareEmpty() {
        Expression left = buildSimpleColumnExpression("grade.score");
        Expression right = buildSimpleNumberExpression("1000");
        Condition onCondition = new Condition(left, right, ComparatorType.EQ);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = buildSimpleJoinTable(onCondition);
        String result = database.select(null, tablesQueried, null, false);
        assertEquals("Empty set.", result);
    }

    @Test
    public void testJointSelectColumnCompareWhere() {
        Expression l1 = buildSimpleColumnExpression("grade.id");
        Expression r1 = buildSimpleColumnExpression("grades.id");
        Condition onCondition = new Condition(l1, r1, ComparatorType.NE);
        Expression l2 = buildSimpleColumnExpression("grade.score");
        Expression r2 = buildSimpleNumberExpression("1");
        Condition whereCondition = new Condition(l2, r2, ComparatorType.EQ);
        JointTable[] tablesQueried = new JointTable[1];
        tablesQueried[0] = buildSimpleJoinTable(onCondition);
        String result = database.select(null, tablesQueried, buildSimpleLogic(whereCondition), false);
        assertEquals(testNum - 1 + 5, result.split("\n").length);
    }

    @After
    public void after() {
        database.deleteAllTable();
        manager.quit();
    }
}