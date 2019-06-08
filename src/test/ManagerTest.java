package test;

import exception.RelationNotExistsException;
import exception.TableNameCollisionException;
import server.Context;
import global.Global;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import schema.*;
import type.ColumnType;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


//测试用户管理以及多主键
public class ManagerTest {
    private Manager manager;
    private Context context;
    private int userCount = 100;

    //添加一些用户
    @Before
    public void before() {
        manager = new Manager();
        context = new Context(Global.adminUserName, Global.adminDatabaseName);
        manager.createDatabase("test", context);
        manager.switchDatabase("test", context);
        for (int i = 0; i < userCount; i++)
            manager.createUser("User" + i, "password", context);

    }

    @Test
    public void testComposite() {
        int testNum = 50;
        Column col1 = new Column("first", ColumnType.INT, 2, false, -1);
        Column col2 = new Column("second", ColumnType.INT, 2, false, -1);
        Column col3 = new Column("score", ColumnType.DOUBLE, 0, false, -1);
        Column[] columns = new Column[]{col1, col2, col3};
        manager.createTable("test_composite", columns, context);
        for (int i = 0; i < testNum; i++)
            for (int j = 0; j < testNum; j++)
                manager.insert("test_composite", new String[]{
                        String.valueOf(i), String.valueOf(j), String.valueOf((double) (i * j))
                }, null, context);
        manager.quit();
        manager = new Manager();
        manager.switchDatabase("test", context);
        for (int i = 0; i < testNum; i++) {
            for (int j = 0; j < testNum; j++) {
                Entry fst = new Entry(i);
                Entry snd = new Entry(j);
                Entry[] e = new Entry[]{
                        new Entry(i),
                        new Entry(j),
                        new Entry((double) (i * j))
                };
                assertTrue(manager.get("test_composite", new Entry[]{fst, snd}, context).toString().contains(new Row(e, -1).toString()));//测试多主键的get结果是否符合预期
            }
        }
    }


    @Test
    public void testConcurrentCreateTable() throws InterruptedException {
        ArrayList<Thread> threads = new ArrayList<>();
        int testNum = userCount;
        AtomicInteger collision = new AtomicInteger(0);
        for (int i = 0; i < testNum; i++) {
            int finalI = i;
            Thread thread = new Thread(() -> {
                try {
                    manager.createTable("concurrentCreateTable", new Column[]{
                            new Column("id", ColumnType.INT, 1, true, -1)
                    }, new Context("User" + finalI, "test"));
                } catch (TableNameCollisionException e) {
                    collision.getAndIncrement();
                }
            });
            thread.start();
            threads.add(thread);
        }
        for (Thread thread : threads)
            thread.join();
        assertEquals(testNum - 1, collision.intValue());//通过并发的创建同一张表，捕捉不同用户创建相同表碰撞的异常，最后应该只有一张表创建成功不会抛出异常。
    }

    @Test
    public void testConcurrentDropTable() throws InterruptedException {
        int testNum = userCount;
        ArrayList<Thread> threads = new ArrayList<>();
        manager.createTable("concurrentDropTable", new Column[]{
                new Column("id", ColumnType.INT, 1, true, -1)
        }, new Context(Global.adminUserName, "test"));
        //为生成用户添加操作测试表的权限，用来之后删除表的并发测试
        for (int i = 0; i < userCount; i++)
            manager.addAuth("User" + i, "concurrentDropTable", 1 << Global.AUTH_DROP, new Context(Global.adminUserName, "test"));
        AtomicInteger collision = new AtomicInteger(0);
        for (int i = 0; i < testNum; i++) {
            int finalI = i;
            Thread thread = new Thread(() -> {
                try {
                    manager.deleteTable("concurrentDropTable", true, new Context("User" + finalI, "test"));
                } catch (RelationNotExistsException e) {
                    collision.getAndIncrement();
                }
            });
            thread.start();
            threads.add(thread);
        }
        for (Thread thread : threads)
            thread.join();
        assertEquals(testNum - 1, collision.intValue());//通过并发的删除之前创建的同一张表，捕捉不同用户并发删除表后表已不存在的异常，最后应该只有一位用户删除成功不会抛出异常。
    }

    @Test
    public void testGet() {
        int testNum = 2500;
        Column col1 = new Column("id", ColumnType.INT, 1, false, -1);
        Column col2 = new Column("name", ColumnType.STRING, 0, false, 10);
        Column col3 = new Column("score", ColumnType.DOUBLE, 0, false, -1);
        Column[] columns = new Column[]{col1, col2, col3};
        manager.createTable("test_get", columns, context);
        for (int i = 0; i < testNum; i++)
            manager.insert("test_get", new String[]{
                    String.valueOf(i), "'A'", String.valueOf(100 - i)
            }, null, context);
        manager.quit();
        manager = new Manager();
        manager.switchDatabase("test", context);
        for (int i = 0; i < testNum; i++) {
            Entry key = new Entry(i);
            Entry[] e = new Entry[]{
                    new Entry(i),
                    new Entry("A"),
                    new Entry((double) (100 - i))
            };
            assertEquals(new Row(e, -1).toString(), manager.get("test_get", new Entry[]{key}, context).toString());//测试单主键的get结果是否符合预期
        }
    }

    @After
    public void after() {
        for (int i = 0; i < userCount; i++)
            manager.dropUser("User" + i, true, context);
        manager.deleteDatabase("test", false, context);
        manager.quit();
    }
}