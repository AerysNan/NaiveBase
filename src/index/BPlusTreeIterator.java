package index;

import java.util.Iterator;
import java.util.Stack;

public class BPlusTreeIterator<K extends Comparable<K>, V> implements Iterator {
    private BPlusTree<K, V> tree;
    private Stack<BPlusTreeNode<K, V>> stack;
    private BPlusTreeNode<K,V> current;

    public BPlusTreeIterator(BPlusTree<K, V> tree) {
        this.tree = tree;
        this.stack = new Stack<>();
        this.current = tree.root;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Object next() {
        return null;
    }
}
