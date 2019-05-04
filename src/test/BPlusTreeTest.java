package test;

import index.BPlusTree;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class BPlusTreeTest {

    private BPlusTree<Integer, String> tree;
    private ArrayList<Integer> keys;
    private HashMap<Integer, String> map;

    @Before
    public void setUp() {
        tree = new BPlusTree<>();
        keys = new ArrayList<>();
        map = new HashMap<>();
        int size = 10000;
        for (int i = 0; i < size; i++) {
            double random = Math.random();
            int key = (int) (random * size);
            keys.add(key);
            tree.put(key, String.valueOf(key));
            map.put(key, String.valueOf(key));
        }
    }

    @Test
    public void put() {
        for (Integer key : keys) {
            assertEquals(tree.get(key), map.get(key));
        }
    }

    @Test
    public void remove() {
        for (Integer key : keys) {
            tree.remove(key);
            map.remove(key);
            assertEquals(tree.size(), map.size());
            assertEquals(tree.get(key), map.get(key));
        }
    }
}