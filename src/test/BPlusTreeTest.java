package test;

import index.BPlusTree;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class BPlusTreeTest {

    private BPlusTree<Integer, String> tree;
    private ArrayList<Integer> keys;
    private HashMap<Integer, String> map;

    @Before
    public void setUp() {
        tree = new BPlusTree<>();
        keys = new ArrayList<>();
        map = new HashMap<>();
        HashSet<Integer> set = new HashSet<>();
        int size = 10000;
        for (int i = 0; i < size; i++) {
            double random = Math.random();
            set.add((int) (random * size));
        }
        for (Integer key : set) {
            keys.add(key);
            tree.put(key, String.valueOf(key));
            map.put(key, String.valueOf(key));
        }
    }

    @Test
    public void testGet() {
        for (Integer key : keys)
            assertEquals(map.get(key), tree.get(key));
    }

    @Test
    public void testRemove() {
        int size = keys.size();
        for (int i = 0; i < size; i += 2)
            tree.remove(keys.get(i));
        assertEquals(size / 2, tree.size());
        for (int i = 1; i < size; i += 2)
            assertEquals(map.get(keys.get(i)), tree.get(keys.get(i)));
    }
}