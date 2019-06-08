package test;

import index.BPlusTree;
import index.BPlusTreeIterator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

//将自己的B+树与HashMap做比较来进行功能测试
public class BPlusTreeTest {

    private BPlusTree<Integer, Integer> tree;
    private ArrayList<Integer> keys;
    private ArrayList<Integer> values;
    private HashMap<Integer, Integer> map;

    //添加一些随机的可能会覆盖旧值的数据
    @Before
    public void setUp() {
        tree = new BPlusTree<>();
        keys = new ArrayList<>();
        values = new ArrayList<>();
        map = new HashMap<>();
        HashSet<Integer> set = new HashSet<>();
        int size = 10000;
        for (int i = 0; i < size; i++) {
            double random = Math.random();
            set.add((int) (random * size));
        }
        for (Integer key : set) {
            int hashCode = key.hashCode();
            keys.add(key);
            values.add(hashCode);
            tree.put(key, hashCode);
            map.put(key, hashCode);
        }
    }


    @Test
    public void testGet() {
        for (Integer key : keys)
            assertEquals(map.get(key), tree.get(key));//比较所有的get结果是否一样
    }

    //比较所有的get结果是否一样
    @Test
    public void testRemove() {
        int size = keys.size();
        for (int i = 0; i < size; i += 2)
            tree.remove(keys.get(i));//remove掉一半的数据
        assertEquals(size / 2, tree.size());//比较size是否等于原来的一半
        for (int i = 1; i < size; i += 2)
            assertEquals(map.get(keys.get(i)), tree.get(keys.get(i)));//删除一半后再比较另一半的get结果是否一样
    }

    @Test
    public void testIterator() {
        BPlusTreeIterator<Integer, Integer> iterator = tree.iterator();
        int c = 0;
        while (iterator.hasNext()) {
            assertTrue(values.contains(iterator.next().getValue()));//比较是否包含测试值
            c++;
        }
        assertEquals(values.size(), c);//比较测试集大小
    }
}