import index.BPlusTree;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception {
        BPlusTree<Integer, String> tree = new BPlusTree<>();

        ArrayList<Integer> keys = new ArrayList<>();
        HashMap<Integer, String> map = new HashMap<>();
        int size = 200000;
        for (int i = 0; i < size; i++) {
            double random = Math.random();
            int key = (int) (random * size);
            keys.add(key);
            tree.put(key, String.valueOf(random * size));
            map.put(key, String.valueOf(random * size));
        }
        System.out.println("Size: " + tree.size());
        System.out.println("Height:" + tree.height());

        for (Integer key : keys)
            if (!tree.get(key).equals(map.get(key)))
                throw new Exception("error!");
        //System.out.println(tree.toString());
    }
}