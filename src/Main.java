import index.BPlusTree;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception {
        BPlusTree<Integer, String> tree = new BPlusTree<>();
        ArrayList<Integer> keys = new ArrayList<>();
        HashMap<Integer, String> map = new HashMap<>();
        int size = 10000;

        //test put
        for (int i = 0; i < size; i++) {
            double random = Math.random();
            int key = (int) (random * size);
            keys.add(key);
            tree.put(key, String.valueOf(random * size));
            map.put(key, String.valueOf(random * size));
        }
        System.out.println("Size: " + tree.size());
        System.out.println("Height: " + tree.height());

        System.out.println(tree.toString());
        for (Integer key : keys) {
            if (!tree.get(key).equals(map.get(key))) {
                throw new Exception("error!");
            }
        }

        //test remove
        for (Integer key:keys) {
            tree.remove(key);
            map.remove(key);
            if(tree.size() != map.size()){
                throw new Exception("error!");
            }
        }
        for (Integer key : keys) {
            if (tree.get(key) != null && !tree.get(key).equals(map.get(key))) {
                System.out.println(tree.get(key) + " : " + map.get(key));
                throw new Exception("error!");
            } else {
                if (map.get(key) != null && !map.get(key).equals(tree.get(key))) {
                    System.out.println(tree.get(key) + " : " + map.get(key));
                    throw new Exception("error!");
                }
            }
        }
        System.out.println("Size: " + tree.size());
        System.out.println("Height: " + tree.height());
        System.out.println(tree.toString());

        System.out.println("Test Passed!\n");
    }
}