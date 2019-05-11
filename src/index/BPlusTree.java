package index;

import storage.Page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public final class BPlusTree<K extends Comparable<K>, V> {

    private static final int M = 128;
    private Node root;
    private int height;
    private int size;

    public BPlusTree() {
        root = new LeafNode(0);
    }

    public int size() {
        return size;
    }

    public int height() {
        return root.getSize() == 0 ? height : height + 1;
    }

    public V get(K key) {
        if (key == null) throw new IllegalArgumentException("argument key to get() is null");
        return root.get(key);
    }

    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("argument key to put() is null");
        root.put(key, value);
    }

    public void remove(K key) {
        if (key == null) throw new IllegalArgumentException("argument key to remove() is null");
        root.remove(key);
    }

    public K containsKey(K key) {
        if (key == null) throw new IllegalArgumentException("argument key to containsKey() is null");
        return root.containsKey(key);
    }

//    TODO
//    ArrayList<V> getRange(K key1, K key2) {
//        return null;
//    }

    public String toString() {
        return toString(root, height, "") + "\n";
    }

    private String toString(Node node, int height, String indent) {
        StringBuilder s = new StringBuilder();
        if (height == 0) {
            LeafNode _node = (LeafNode) node;
            for (int j = 0; j < node.getSize(); j++) {
                s.append(indent).append(_node.keys.get(j)).append(" : ").append(_node.values.get(j)).append("\n");
            }
        } else {
            InternalNode _node = (InternalNode) node;
            for (int j = 0; j < node.getSize(); j++) {
                s.append(toString(_node.children.get(j), height - 1, indent + "   "));
                s.append(indent).append("(").append(_node.keys.get(j)).append(")\n");
            }
            s.append(toString(_node.children.get(node.getSize()), height - 1, indent + "   "));
        }
        return s.toString();
    }

    private abstract class Node {

        ArrayList<K> keys;
        int nodeSize;

        abstract V get(K key);

        abstract void put(K key, V value);

        abstract void remove(K key);

        abstract K containsKey(K key);

        abstract K getFirstLeafKey();

        abstract Node split();

        abstract void merge(Node sibling);

        int getSize() {
            return nodeSize;
        }

        boolean isOverFlow() {
            return nodeSize > M - 1;
        }

        boolean isUnderFlow() {
            return nodeSize < (M + 1) / 2 - 1;
        }

        int binarySearch(K key) {
            return Collections.binarySearch(keys.subList(0, nodeSize), key);
        }

        void checkRoot() {
            if (root.isOverFlow()) {
                Node newSiblingNode = split();
                InternalNode newRoot = new InternalNode(1);
                newRoot.keys.set(0, newSiblingNode.getFirstLeafKey());
                newRoot.children.set(0, this);
                newRoot.children.set(1, newSiblingNode);
                root = newRoot;
                height++;
            }
        }

        void keysAdd(int index, K key) {
            for (int i = nodeSize; i > index; i--) {
                keys.set(i, keys.get(i - 1));
            }
            keys.set(index, key);
            nodeSize++;
        }

        void keysRemove(int index) {
            for (int i = index; i < nodeSize - 1; i++) {
                keys.set(i, keys.get(i + 1));
            }
            nodeSize--;
        }

    }

    private final class InternalNode extends Node {

        ArrayList<Node> children;

        InternalNode(int size) {
            keys = new ArrayList<>(Collections.nCopies((int) (1.5 * M) + 1, null));
            children = new ArrayList<>((Collections.nCopies((int) (1.5 * M) + 2, null)));
            this.nodeSize = size;
        }

        void childrenAdd(int index, Node node) {
            for (int i = nodeSize + 1; i > index; i--) {
                children.set(i, children.get(i - 1));
            }
            children.set(index, node);
        }

        void childrenRemove(int index) {
            for (int i = index; i < nodeSize; i++) {
                children.set(i, children.get(i + 1));
            }
        }

        @Override
        K containsKey(K key) {
            return searchChild(key).containsKey(key);
        }

        @Override
        V get(K key) {
            return searchChild(key).get(key);
        }

        @Override
        void put(K key, V value) {
            Node child = searchChild(key);
            child.put(key, value);
            if (child.isOverFlow()) {
                Node newSiblingNode = child.split();
                insertChild(newSiblingNode.getFirstLeafKey(), newSiblingNode);
            }
            checkRoot();
        }

        @Override
        void remove(K key) {
            int index = binarySearch(key);
            int childIndex = index >= 0 ? index + 1 : -index - 1;
            Node child = children.get(childIndex);
            child.remove(key);
            if (child.isUnderFlow()) {
                Node childLeftSibling = getChildLeftSibling(key);
                Node childRightSibling = getChildRightSibling(key);
                Node left = childLeftSibling != null ? childLeftSibling : child;
                Node right = childLeftSibling != null ? child : childRightSibling;
                left.merge(right);
                if (index >= 0) {
                    childrenRemove(index + 1);
                    keysRemove(index);
                } else {
                    assert right != null;
                    deleteChild(right.getFirstLeafKey());
                }
                if (left.isOverFlow()) {
                    Node newSiblingNode = left.split();
                    insertChild(newSiblingNode.getFirstLeafKey(), newSiblingNode);
                }
                if (root.getSize() == 0) {
                    root = left;
                    height--;
                }
            } else {
                if (index >= 0) {
                    keys.set(index, children.get(index + 1).getFirstLeafKey());
                }
            }
        }

        @Override
        K getFirstLeafKey() {
            return children.get(0).getFirstLeafKey();
        }

        @Override
        Node split() {
            int from = getSize() / 2 + 1;
            int to = getSize();
            InternalNode newSiblingNode = new InternalNode(to - from);
            for (int i = 0; i < to - from; i++) {
                newSiblingNode.keys.set(i, keys.get(i + from));
                newSiblingNode.children.set(i, children.get(i + from));
            }
            newSiblingNode.children.set(to - from, children.get(to));
            this.nodeSize = this.nodeSize - to + from - 1;
            return newSiblingNode;
        }

        @Override
        void merge(Node sibling) {
            int index = getSize();
            InternalNode node = (InternalNode) sibling;
            int length = node.getSize();
            keys.set(index, node.getFirstLeafKey());
            for (int i = 0; i < length; i++) {
                keys.set(i + index + 1, node.keys.get(i));
                children.set(i + index + 1, node.children.get(i));
            }
            children.set(length + index + 1, node.children.get(length));
            nodeSize = index + length + 1;
        }

        Node searchChild(K key) {
            int index = binarySearch(key);
            return children.get(index >= 0 ? index + 1 : -index - 1);
        }

        void insertChild(K key, Node child) {
            int index = binarySearch(key);
            int childIndex = index >= 0 ? index + 1 : -index - 1;
            if (index >= 0) {
                children.set(childIndex, child);
            } else {
                childrenAdd(childIndex + 1, child);
                keysAdd(childIndex, key);
            }
        }

        void deleteChild(K key) {
            int index = binarySearch(key);
            if (index >= 0) {
                childrenRemove(index + 1);
                keysRemove(index);
            }
        }

        Node getChildLeftSibling(K key) {
            int index = binarySearch(key);
            int childIndex = index >= 0 ? index + 1 : -index - 1;
            if (childIndex > 0) {
                return children.get(childIndex - 1);
            }
            return null;
        }

        Node getChildRightSibling(K key) {
            int index = binarySearch(key);
            int childIndex = index >= 0 ? index + 1 : -index - 1;
            if (childIndex < getSize()) {
                return children.get(childIndex + 1);
            }
            return null;
        }
    }

    private final class LeafNode extends Node {

        ArrayList<V> values;
        LeafNode next;

        LeafNode(int size) {
            keys = new ArrayList<>(Collections.nCopies((int) (1.5 * M) + 1, null));
            values = new ArrayList<>(Collections.nCopies((int) (1.5 * M) + 1, null));
            this.nodeSize = size;
        }

        void valuesAdd(int index, V value) {
            for (int i = nodeSize; i > index; i--) {
                values.set(i, values.get(i - 1));
            }
            values.set(index, value);
        }

        void valuesRemove(int index) {
            for (int i = index; i < nodeSize - 1; i++) {
                values.set(i, values.get(i + 1));
            }
        }

        @Override
        K containsKey(K key) {
            int index = binarySearch(key);
            if (index >= 0) {
                return keys.get(index);
            } else {
                return null;
            }
        }

        @Override
        V get(K key) {
            int index = binarySearch(key);
            return index >= 0 ? values.get(index) : null;
        }

        @Override
        void put(K key, V value) {
            int index = binarySearch(key);
            int valueIndex = index >= 0 ? index : -index - 1;
            if (index >= 0) {
                values.set(valueIndex, value);
                //System.out.println("The key already exists!");
            } else {
                valuesAdd(valueIndex, value);
                keysAdd(valueIndex, key);
                size++;
            }
            checkRoot();
        }

        @Override
        void remove(K key) {
            int index = binarySearch(key);
            if (index >= 0) {
                valuesRemove(index);
                keysRemove(index);
                size--;
            } else {
                //System.out.println("The key doesn't exist!");
            }
        }

        @Override
        K getFirstLeafKey() {
            return keys.get(0);
        }

        @Override
        Node split() {
            int from = (getSize() + 1) / 2;
            int to = getSize();
            LeafNode newSiblingNode = new LeafNode(to - from);
            for (int i = 0; i < to - from; i++) {
                newSiblingNode.keys.set(i, keys.get(i + from));
                newSiblingNode.values.set(i, values.get(i + from));
                keys.set(i + from, null);
                values.set(i + from, null);
            }
            nodeSize = from;
            newSiblingNode.next = next;
            next = newSiblingNode;
            return newSiblingNode;
        }

        @Override
        void merge(Node sibling) {
            int index = getSize();
            LeafNode node = (LeafNode) sibling;
            int length = node.getSize();
            for (int i = 0; i < length; i++) {
                keys.set(i + index, node.keys.get(i));
                values.set(i + index, node.values.get(i));
            }
            nodeSize = index + length;
            next = node.next;
        }
    }
}