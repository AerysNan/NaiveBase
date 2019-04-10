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

    //TODO
    ArrayList<V>  getRange(K key1, K key2){
        return null;
    }

    public String toString(){
        return toString(root,height,"") + "\n";
    }

    private String toString(Node node,int height,String indent){
        StringBuilder s = new StringBuilder();
        if(height == 0){
            LeafNode _node = (LeafNode)node;
            for(int j = 0;j < node.getSize();j++){
                s.append(indent).append(_node.keys.get(j)).append(" : ").append(_node.values.get(j)).append("\n");
            }
        }else{
            InternalNode _node = (InternalNode)node;
            for(int j = 0;j < node.getSize();j++){
                if(j > 0) s.append(indent).append("(").append(_node.keys.get(j)).append(")\n");
                s.append(toString(_node.children.get(j),height - 1,indent + "   "));
            }
            s.append(toString(_node.children.get(node.getSize()),height - 1,indent + "   "));
        }
        return s.toString();
    }

    private abstract class Node {

        ArrayList<K> keys;

        abstract V get(K key);
        abstract void put(K key,V value);
        abstract void remove(K key);
        abstract K getFirstLeafKey();
        abstract Node split();
        abstract void merge(Node sibling);

        int getSize(){
            return keys.size();
        }

        boolean isOverFlow(){
            return getSize() > M - 1;
        }

        boolean isUnderFlow(){
            return getSize() < (M + 1) / 2 - 1;
        }

        int binarySearch(K key){
            return Collections.binarySearch(keys,key);
        }

        void checkRoot(){
            if(root.isOverFlow()) {
                Node newSiblingNode = split();
                InternalNode newRoot = new InternalNode();
                newRoot.keys.add(newSiblingNode.getFirstLeafKey());
                newRoot.children.add(this);
                newRoot.children.add(newSiblingNode);
                root = newRoot;
                height++;
            }
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
            return searchChild(key).get(key);
        }

        @Override
        void put(K key,V value){
            Node child = searchChild(key);
            child.put(key,value);
            if(child.isOverFlow()){
                Node newSiblingNode = child.split();
                insertChild(newSiblingNode.getFirstLeafKey(),newSiblingNode);
            }
            checkRoot();
        }

        @Override
        void remove(K key){
            Node child = searchChild(key);
            child.remove(key);
            if(child.isUnderFlow()){
                Node childLeftSibling = getChildLeftSibling(key);
                Node childRightSibling = getChildRightSibling(key);
                Node left = childLeftSibling != null ? childLeftSibling : child;
                Node right = childLeftSibling != null ? child : childRightSibling;
                left.merge(right);
                deleteChild(right.getFirstLeafKey());
                if(left.isOverFlow()){
                    Node newSiblingNode = left.split();
                    insertChild(newSiblingNode.getFirstLeafKey(),newSiblingNode);
                }
                if(root.getSize() == 0){
                    root = left;
                    height--;
                }
            }
        }

        @Override
        K getFirstLeafKey(){
            return children.get(0).getFirstLeafKey();
        }

        @Override
        Node split(){
            int from = getSize() / 2 + 1;
            int to = getSize();
            InternalNode newSiblingNode = new InternalNode();
            newSiblingNode.keys.addAll(keys.subList(from,to));
            newSiblingNode.children.addAll(children.subList(from,to + 1));
            keys.subList(from - 1, to).clear();
            children.subList(from, to + 1).clear();
            return newSiblingNode;
        }

        @Override
        void merge(Node sibling){
            InternalNode node = (InternalNode) sibling;
            keys.add(node.getFirstLeafKey());
            keys.addAll(node.keys);
            children.addAll(node.children);
        }

        Node searchChild(K key){
            int index = binarySearch(key);
            return children.get(index >= 0 ? index + 1 : -index - 1);
        }

        void insertChild(K key,Node child){
            int index = binarySearch(key);
            int childIndex = index >= 0 ? index + 1 : -index - 1;
            if (index >= 0){
                children.set(childIndex,child);
            }else{
                keys.add(childIndex,key);
                children.add(childIndex + 1,child);
            }
        }

        void deleteChild(K key){
            int index = binarySearch(key);
            if(index >= 0){
                keys.remove(index);
                children.remove(index + 1);
            }
        }

        Node getChildLeftSibling(K key){
            int index = binarySearch(key);
            int childIndex = index >= 0 ? index + 1 : -index - 1;
            if(childIndex > 0){
                return children.get(childIndex - 1);
            }
            return null;
        }

        Node getChildRightSibling(K key){
            int index = binarySearch(key);
            int childIndex = index >= 0 ? index + 1 : -index - 1;
            if(childIndex < getSize()){
                return children.get(childIndex + 1);
            }
            return null;
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
            int index = binarySearch(key);
            return index >= 0 ? values.get(index) : null;
        }

        @Override
        void put(K key,V value){
            int index =  binarySearch(key);
            int valueIndex = index >= 0 ? index: - index - 1;
            if(index >= 0) {
                values.set(valueIndex,value);
                //System.out.println("The key already exists!");
            } else {
                keys.add(valueIndex,key);
                values.add(valueIndex,value);
                size++;
            }
            checkRoot();
        }

        @Override
        void remove(K key){
            int index = binarySearch(key);
            if(index >= 0){
                keys.remove(index);
                values.remove(index);
                size--;
            }else{
                //System.out.println("The key doesn't exist!");
            }
        }

        @Override
        K getFirstLeafKey(){
            return keys.get(0);
        }

        @Override
        Node split(){
            int from = (getSize() + 1) / 2 ;
            int to = getSize();
            LeafNode newSiblingNode = new LeafNode();
            newSiblingNode.keys.addAll(keys.subList(from,to));
            newSiblingNode.values.addAll(values.subList(from,to));
            keys.subList(from, to).clear();
            values.subList(from, to).clear();
            newSiblingNode.next = next;
            next = newSiblingNode;
            return newSiblingNode;
        }

        @Override
        void merge(Node sibling){
            LeafNode node = (LeafNode)sibling;
            keys.addAll(node.keys);
            values.addAll(node.values);
            next = node.next;
        }
    }
}

