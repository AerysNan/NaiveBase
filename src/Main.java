import index.BPlusTree;


public class Main {
    //demo
    public static void main(String[] args) throws Exception {

        BPlusTree<Integer, String> tree = new BPlusTree<>();

        for (int i = 0; i < 10; i++) {
            tree.put(i, String.valueOf(i));
        }
        System.out.println("Size: " + tree.size());
        System.out.println("Height: " + tree.height());
        System.out.println(tree.toString());

        for (int i = 0;i < 5;i++) {
            tree.remove(i);
        }
        System.out.println("Size: " + tree.size());
        System.out.println("Height: " + tree.height());
        System.out.println(tree.toString());

    }
}