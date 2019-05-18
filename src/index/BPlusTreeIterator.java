package index;

import java.util.Iterator;
import java.util.LinkedList;

public class BPlusTreeIterator<K extends Comparable<K>, V> implements Iterator<V> {
    private LinkedList<BPlusTreeNode<K, V>> queue;
    private LinkedList<V> buffer;

    BPlusTreeIterator(BPlusTree<K, V> tree) {
        queue = new LinkedList<>();
        buffer = new LinkedList<>();
        queue.add(tree.root);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty() || !buffer.isEmpty();
    }

    @Override
    public V next() {
        if (buffer.isEmpty()) {
            while (true) {
                BPlusTreeNode<K, V> node = queue.poll();
                if (node instanceof BPlusTreeLeafNode) {
                    for (int i = 0; i < node.size(); i++)
                        buffer.add(((BPlusTreeLeafNode<K, V>) node).values.get(i));
                    break;
                } else if (node instanceof BPlusTreeInternalNode)
                    for (int i = 0; i <= node.size(); i++)
                        queue.add(((BPlusTreeInternalNode<K, V>) node).children.get(i));
            }
        }
        return buffer.poll();
    }
}
