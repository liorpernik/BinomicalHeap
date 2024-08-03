import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

public class Test {

    public static void main(String[] args) {
        BinomialHeap tree = new BinomialHeap();
        long start = 0, end = 0;



        for (int j = 1; j < 6; j++) {
//            int[] arr = generateRandom((int)Math.pow(3, j+7));
//            System.out.println(Math.pow(3, j+7));
//            start = System.nanoTime();

            for (int i = 1; i < Math.pow(3, j+7); i++) {
//                tree.insert(arr[i], "");
                tree.insert((int)Math.pow(3, j+7) - i, "");
            }
            System.out.println("inert: " + tree.links);
//            for (int i = 0; i < (Math.pow(3, j+7))/2; i++) {
//                tree.deleteMin();
//            }
            while (tree.size > (int)Math.pow(2,5) -1){
                tree.deleteMin();
            }

            end  = System.nanoTime();
//            System.out.println("time is: " + (end - start)/ 1_000_000);
//            System.out.println("num of tree: " + tree.numTrees());
            System.out.println(tree.links);
            System.out.println("rank of deleted " + tree.deletedRanks);
        }

    }

    public static int[] generateRandom(int n) {
        int[] arr = new int[n];
        int x;
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            do{
                x = random.nextInt(n+1);
            }while (Arrays.binarySearch(arr,x) >= 0);
            arr[i] = x;
        }
        return arr;
    }
}
