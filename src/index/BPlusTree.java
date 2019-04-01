package index;
import java.util.ArrayList;
import java.util.Collections;

class Entry<K extends Comparable<K>, V> implements Comparable<Entry<K, V>>{
    K key;
    V val;
    boolean hook;
    Node<K, V> child;

    Entry(K key, V val, Node<K, V> child) {
        this.key = key;
        this.val = val;
        this.child = child;
        this.hook = false;
    }

    @Override
    public int compareTo(Entry<K, V> entry) {
        if(hook) return -1;
        if(entry.hook) return 1;
        return key.compareTo(entry.key);
    }
}

class Node<K extends Comparable<K>, V> {
    int size;
    ArrayList<Entry<K, V>> subNodes;

    Node(int size) {
        subNodes = new ArrayList<>(BPlusTree.M);
        while (subNodes.size() < BPlusTree.M)
            subNodes.add(null);
        this.size = size;
    }
}

public class BPlusTree<K extends Comparable<K>, V> {
    static final int M = 4;
    private Node<K, V> root;
    private int height;
    private int size;

    public BPlusTree() {
        root = new Node<>(1);
        Entry<K, V> entry = new Entry<>(null, null, null);
        entry.hook = true;
        root.subNodes.set(0, entry);
    }

    public int size() {
        return size;
    }

    public int height() {
        return height;
    }

    public V get(K key) {
        if (key == null) throw new IllegalArgumentException("key shouldn't be null");
        return search(root, key, height);
    }

    private V search(Node<K, V> node, K key, int height) {
        ArrayList<Entry<K, V>> subNodes = node.subNodes;
        int index = Collections.binarySearch(subNodes.subList(0, node.size), new Entry<>(key, null, null));
        if (height == 0) {
            if (index >= 0)
                return subNodes.get(index).val;
        } else {
            index = index >= 0 ? index : (-index - 2);
            return search(subNodes.get(index).child, key, height - 1);
        }
        return null;
    }

    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("argument key to put() is null");
        Node<K, V> newSplitNode = insert(root, key, value, height);
        if (newSplitNode == null) return;

        // need to split root
        Node<K, V> newRootNode = new Node<>(2);
        newRootNode.subNodes.set(0, new Entry<>(root.subNodes.get(0).key, null, root));
        if (root.subNodes.get(0).hook)
            newRootNode.subNodes.get(0).hook = true;
        newRootNode.subNodes.set(1, new Entry<>(newSplitNode.subNodes.get(0).key, null, newSplitNode));
        root = newRootNode;
        height++;
    }

    private Node<K, V> insert(Node<K, V> node, K key, V value, int height) {
        Entry<K, V> newEntry = new Entry<>(key, value, null);
        int index = Collections.binarySearch(node.subNodes.subList(0, node.size), newEntry);
        // external node
        if (height == 0) {
            if (index >= 0) {
                node.subNodes.set(index, newEntry);
                return null;
            } else
                index = -index - 1;
            size++;
        }

        // internal node
        else {
            index = index >= 0 ? index : (-index - 2);
            Node<K, V> u = insert(node.subNodes.get(index++).child, key, value, height - 1);
            if (u == null) return null;
            newEntry.key = u.subNodes.get(0).key;
            newEntry.child = u;
        }
        for (int i = node.size; i > index; i--)
            node.subNodes.set(i, node.subNodes.get(i - 1));
        node.subNodes.set(index, newEntry);
        node.size++;
        if (node.size < M) return null;
        else return split(node);
    }

    // split node in half
    private Node<K, V> split(Node<K, V> node) {
        Node<K, V> newNode = new Node<>(M / 2);
        node.size = M / 2;
        for (int j = 0; j < M / 2; j++)
            newNode.subNodes.set(j, node.subNodes.get(M / 2 + j));
        return newNode;
    }

    public String toString() {
        return toString(root, height, "") + "\n";
    }

    private String toString(Node<K, V> node, int height, String indent) {
        StringBuilder s = new StringBuilder();
        ArrayList<Entry<K, V>> children = node.subNodes;

        if (height == 0) {
            for (int j = 0; j < node.size; j++) {
                s.append(indent).append(children.get(j).key).append(" ").append(children.get(j).val).append("\n");
            }
        } else {
            for (int j = 0; j < node.size; j++) {
                if (j > 0) s.append(indent).append("(").append(children.get(j).key).append(")\n");
                s.append(toString(children.get(j).child, height - 1, indent + "     "));
            }
        }
        return s.toString();
    }
}