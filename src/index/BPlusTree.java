package index;
import java.util.ArrayList;
import java.util.Collections;

public class BPlusTree<K extends Comparable<K>, V> {

    private static final int M = 4;
    private Node root;
    private int height;
    private int size;

    public BPlusTree() {
        root = new LeafNode();
    }

    public int size() {
        return size;
    }

    public int height() {
        return height;
    }

    public V get(K key) {
        if (key == null) throw new IllegalArgumentException("argument key to get() is null");
        return root.get(key);
    }

    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("argument key to put() is null");
        root.put(key,value);
    }

    public void remove(K key){
        if (key == null) throw new IllegalArgumentException("argument key to remove() is null");
        root.remove(key);
    }

    private abstract class Node {
        ArrayList<K> keys;

        abstract V get(K key);
        abstract void put(K key,V value);
        abstract void remove(K key);
        abstract K getFirstLeafKey();
        abstract Node split();
        abstract void merge(Node sibling);
        abstract boolean isOverflow();
        abstract boolean isUnderflow();
        int getKeyNum(){
            return keys.size();
        }
    }

    private class InternalNode extends Node{
        ArrayList<Node> children;

        InternalNode(){
            keys = new ArrayList<K>();
            children = new ArrayList<Node>();
        }

        @Override
        V get(K key){
            return childSearch(key).get(key);
        }

        @Override
        void put(K key,V value){
            Node child = childSearch(key);
            child.put(key,value);
            if(child.isOverflow()){
                Node newSiblingNode = child.split();
                childInsert(newSiblingNode.getFirstLeafKey(),newSiblingNode);
            }
            if(root.isOverflow()) {
                Node newSiblingNode = split();
                InternalNode newRoot = new InternalNode();
                newRoot.keys.add(newSiblingNode.getFirstLeafKey());
                newRoot.children.add(this);
                newRoot.children.add(newSiblingNode);
                root = newRoot;
                height++;
            }
        }

        @Override
        void remove(K key){
            return;
        }

        @Override
        K getFirstLeafKey(){
            return children.get(0).getFirstLeafKey();
        }

        @Override
        Node split(){
            int from = getKeyNum() / 2 + 1;
            int to = getKeyNum();
            InternalNode newSiblingNode = new InternalNode();
            newSiblingNode.keys.addAll(keys.subList(from,to));
            newSiblingNode.children.addAll(children.subList(from,to + 1));
            keys.subList(from - 1, to).clear();
            children.subList(from, to + 1).clear();
            return newSiblingNode;
        }

        @Override
        void merge(Node sibling){
            return;
        }

        @Override
        boolean isOverflow(){
            return children.size() > M;
        }

        @Override
        boolean isUnderflow(){
            return children.size() < (M + 1) / 2;
        }

        Node childSearch(K key){
            return children.get(indexSearch(key));
        }

        int indexSearch(K key){
            int index = Collections.binarySearch(keys,key);
            return index >= 0 ? index + 1 : -index - 1;
        }

        void childInsert(K key,Node child){
            int index = Collections.binarySearch(keys,key);
            if (index >= 0){
                children.set(index + 1,child);
            }else{
                index = -index - 1;
                keys.add(index,key);
                children.add(index + 1,child);
            }
        }

    }

    private class LeafNode extends Node{
        ArrayList<V> values;
        LeafNode next;

        LeafNode() {
            keys = new ArrayList<K>();
            values = new ArrayList<V>();
        }

        @Override
        V get(K key){
            int index = indexSearch(key);
            return index >= 0 ? values.get(index) : null;
        }

        @Override
        void put(K key,V value){
            int index =  indexSearch(key);
            if(index >= 0) {
                values.set(index,value);
            } else {
                index= -index - 1;
                keys.add(index,key);
                values.add(index,value);
                size++;
            }
            if(root.isOverflow()) {
                Node newSiblingNode = split();
                InternalNode newRoot = new InternalNode();
                newRoot.keys.add(newSiblingNode.getFirstLeafKey());
                newRoot.children.add(this);
                newRoot.children.add(newSiblingNode);
                root = newRoot;
                height++;
            }

            return;
        }

        @Override
        void remove(K key){
            return;
        }

        @Override
        K getFirstLeafKey(){
            return keys.get(0);
        }

        @Override
        Node split(){
            int from = (getKeyNum() + 1) / 2 ;
            int to = getKeyNum();
            LeafNode newSiblingNode = new LeafNode();
            newSiblingNode.keys.addAll(keys.subList(from,to));
            newSiblingNode.values.addAll(values.subList(from,to));
            keys.subList(from, to).clear();
            values.subList(from, to).clear();
            return newSiblingNode;
        }

        @Override
        void merge(Node sibling){
            return;
        }

        @Override
        boolean isOverflow(){
            return values.size() > M - 1;
        }

        @Override
        boolean isUnderflow(){
            return values.size() < M / 2;
        }

        int indexSearch(K key){
            return Collections.binarySearch(keys,key);
        }
    }
}

