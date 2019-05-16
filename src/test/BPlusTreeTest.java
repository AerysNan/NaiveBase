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
        for (Integer key : keys) {
            assertEquals(tree.get(key), map.get(key));
        }
    }

    @Test
    public void testDelete(){
        for (Integer key : keys) {
            tree.remove(key);
            map.remove(key);
            assertEquals(tree.size(), map.size());
        }
    }
}