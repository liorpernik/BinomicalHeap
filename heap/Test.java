import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

public class Test {

    public static void main(String[] args) {
        long start = 0, end = 0;

        for (int j = 1; j < 6; j++) {
            BinomialHeap tree = new BinomialHeap();
            BinomialHeap.links = 0;
            int max = (int) Math.pow(3, 7);//;400;
            int[] arr = generateRandom(max);//(int)Math.pow(3, j+7)
            System.out.println("size: " + Math.pow(3, j + 7));
            start = System.nanoTime();
            //test1
//            for (int i = 1; i < max; i++) {
//                tree.insert(i, "");
//            }

//            test2(Math.pow(3, j+7))/2
            for (int i = 1; i < max; i++)//Math.pow(3, j+7)
                tree.insert(arr[i], "");
//            System.out.println("links:" + tree.links);
//            tree.printRanks();
            int[] sorted = Arrays.stream(arr).sorted().toArray();
            for (int i = 1; i <  max/2; i++) {//(Math.pow(3, j+7)/2)
                tree.deleteMin();
                System.out.println("min: " + tree.min.item.key);
                System.out.println("real min: " + sorted[i+1]);
            }

            //test3

//            for (int i = 1; i < Math.pow(3, j+7); i++)
//                tree.insert((int)Math.pow(3, j+7) - i, "");

//            int i =1;
//            while (tree.size > (int)Math.pow(2,5) -1){
//                tree.deleteMin();
//                System.out.println("min: " + tree.min.item.key);
//                System.out.println("real min: " + (i+1));
//                i++;
//            }

            end = System.nanoTime();
            System.out.println("time is: " + (end - start) / 1_000_000);
            System.out.println("links:" + BinomialHeap.links);
            System.out.println("num of tree: " + tree.numTrees());
            System.out.println("rank of deleted " + tree.deletedRanks);
            tree.printRanks();
//        }
        }
    }

        public static int[] generateRandom ( int n){
            int[] arr = new int[n];
            int x;
            Random random = new Random();
            for (int i = 1; i < n; i++) {
                do {
                    x = random.nextInt(n + 1);
                } while (Arrays.binarySearch(Arrays.stream(arr).sorted().toArray(),x) >= 0);
                arr[i] = x;
            }
            return arr;
        }
    }
