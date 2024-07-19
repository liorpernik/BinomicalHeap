/*
 * Binomial Heap test program for Data Structures course.
 * To be compiled with BinomialHeap.java (Student file).
 *
 * @author  Oren Kishon
 *
 */

import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.RejectedExecutionException;

public class BinomialHeapTest {
    private static int[] createValues(int n) {
        int[] values = new int[n];
        int maxValue = n * 10;
        Random randomGenerator = new Random();

        for (int i = 0; i < n; ++i){
            while (true) {
                int j, randInt = randomGenerator.nextInt(maxValue);

                for (j = 0; j < i && randInt != values[j]; ++j);
                if (j < i) {
                    continue;
                }
                values[i] = randInt;
                break;
            }
        }

        return values;
    }

    private static String vals2str(int[] values) {
        int min = values[0];
        for (int val : values) {
            min = val < min ? val : min;
        }
        String s = "size=" + values.length + " min=" + min;


        if (values.length <= 20) {
            for (int val : values) {
                s += " " + val;
            }
        } else {
            for (int j = 0; j < 5; ++j) {
                s += " " + values[j];
            }
            s += " ...";
            for (int j = 0; j < 5; ++j) {
                s += " " + values[values.length - 6 + j];
            }
        }

        return s;
    }

    private static abstract class Test implements Runnable {
        public final String name;
        private boolean failed;
        private String error;
        private String eMessage;
        private String eTrace;

        public Test(String name) {
            this.name = name;
            this.failed = false;
            this.error = "";
            this.eMessage = "";
            this.eTrace = "";
//            System.out.println("Adding test: " + name);
        }

        @Override
        public void run() {
            try {
                test();
            } catch (Exception e) {
                setFailed(e);
            }
        }

        protected abstract void test();

        public void setFailed(String error) {
            failed = true;
            this.error = error;
//            System.out.println("Failed. error: " + error);
        }

        public void setFailed(Exception e) {
            setFailed("Java Exception");
            this.eMessage = e.getMessage();
            this.eTrace = "\n";
            for (StackTraceElement el : e.getStackTrace()) {
                if (!el.getClassName().contains("BinomialHeap")) {
                    continue;
                }
                this.eTrace += "\t" + el + "\n";
            }
//            System.out.println("Exception message: " + eMessage);
//            System.out.println("Stack trace:\n" + eTrace);
        }

        public boolean failed() {
            return failed;
        }

        public String toString() {
            return String.format("%s, Faild: %s, Error: %s, "+
                    "Exception: %s, Call stack: %s", name, 
                    failed ? "Y" : "N", error, eMessage, eTrace);
        }
    }

    static private class TestMeld1 extends Test {
        public TestMeld1() {
            super("Meld two empty heaps");
        }

        protected void test() {
            BinomialHeap heap1 = new BinomialHeap();
            BinomialHeap heap2 = new BinomialHeap();
            heap1.meld(heap2);
            if (!heap1.empty()) {
                setFailed("result not empty!");
            }
        }
    }

    static private class TestMeld2 extends Test {
        public TestMeld2() {
            super("Meld other empty heap");
        }

        protected void test() {
            BinomialHeap heap1 = new BinomialHeap();
            BinomialHeap heap2 = new BinomialHeap();
            heap1.insert(3,"");
            int size1 = heap1.size();
            heap1.meld(heap2);
            if (heap1.empty()) {
                setFailed("result empty!");
            }
            if (heap1.size() != size1 + heap2.size()) {
                setFailed("melded heap size ("+heap1.size()+
                        "!= heap1 ("+size1+") + heap2 ("+heap2.size()+")");
            }
            if (heap1.findMin().key != 3) {
                setFailed("findMin after meld failed");
            }
        }
    }

    static private class TestMeld3 extends Test {
        public TestMeld3() {
            super("Meld this empty heap with other nonempty");
        }

        protected void test() {
            BinomialHeap heap1 = new BinomialHeap();
            BinomialHeap heap2 = new BinomialHeap();
            heap2.insert(3,"");
            int size1 = heap1.size();
            heap1.meld(heap2);
            if (heap1.empty()) {
                setFailed("result empty!");
            }
            if (heap2.size() != size1 + heap2.size()) {
                setFailed("melded heap size ("+heap1.size()+
                        ") != heap1 ("+size1+") + heap2 ("+heap2.size()+
                        ")");
            }
            if (heap1.findMin().key != 3) {
                setFailed("findMin after meld failed");
            }
        }
    }

    static private class TestMeld4 extends Test {
        public TestMeld4() {
            super("Meld nonempty heap with other nonempty");
        }

        protected void test() {
            BinomialHeap heap1 = new BinomialHeap(); 
            BinomialHeap heap2 = new BinomialHeap();
            heap1.insert(5,"");
            heap1.insert(17,"");
            int size1 = heap1.size();
            heap2.insert(3,"");
            int size2 = heap2.size();

//            heap1.displayHeap();
//            heap2.displayHeap();

            heap1.meld(heap2);
            if (heap1.empty()) {
                setFailed("result empty!");
            }
            if (heap1.size() != size1 + size2) {
                setFailed("melded heap size ("+heap1.size()+
                        ") != heap1 ("+size1+") + heap2 ("+size2+")");
            }
            if (heap1.findMin().key != 3) {
                setFailed("findMin after meld failed");
            }
        }
    }

    static private class TestMeld5 extends Test {
        public TestMeld5() {
            super("Meld large heaps");
        }

        protected void test() {
            BinomialHeap heap1 = new BinomialHeap();
            BinomialHeap heap2 = new BinomialHeap();
            int[] vals1 = createValues(500);
            int[] vals2 = createValues(500);
            int min = vals1[0];
            for (int v : vals1) {
                heap1.insert(v, "");
                heap1.toString();
                min = v < min ? v : min;
            }
            for (int v : vals2) {
                heap2.insert(v,"");
                min = v < min ? v : min;
            }
            int size1 = heap1.size();
            int size2 = heap2.size();

            heap1.meld(heap2);
            if (heap1.empty()) {
                setFailed("result empty!");
            }
            if (heap1.size() != size1 + size2) {
                setFailed("melded heap size ("+heap1.size()+
                        ") != heap1 ("+size1+") + heap2 ("+size2+")");
            }
            if (heap1.findMin().key != min) {
                setFailed("findMin after meld failed");
            }
        }
    }

    static private class TestInsert extends Test {
        public TestInsert() {
            super("Check size each insert");
        }

        protected void test() {
            int[] vals = createValues(100);

            BinomialHeap heap1 = new BinomialHeap();
            for (int i = 0; i < vals.length; ++i) {
                if (heap1.size() != i) {
                    setFailed("size is "+i+" but size() says "+
                            heap1.size());
                    break;
                }
                heap1.insert(vals[i], "");
            }
        }
    }

    static private class TestFindMin1 extends Test {
        public TestFindMin1() {
            super("Check findMin each unsorted insert");
        }

        protected void test() {

            int[] vals = createValues(100);
            int min = vals[0];
            BinomialHeap heap1 = new BinomialHeap();

            for (int i = 0; i < vals.length; ++i) {
                min = vals[i] < min ? vals[i] : min;
                heap1.insert(vals[i],"");
                if (heap1.findMin().key != min) {
                    setFailed("min is "+min+
                            "but findMin() says "+
                            heap1.findMin());
                    break;
                }
            }
        }
    }

    static private class TestFindMin2 extends Test {
        public TestFindMin2() {
            super("Check findMin each sorted insert");
        }

        protected void test() {
            int[] vals = createValues(100);
            Arrays.sort(vals);
            BinomialHeap heap1 = new BinomialHeap();
            for (int i = vals.length - 1; i >= 0 ; --i) {
                heap1.insert(vals[i],"");
                if (heap1.findMin().key != vals[i]) {
                    setFailed("min is "+vals[i]+
                            "but findMin() says "+
                            heap1.findMin());
                    break;
                }
            }
        }
    }

    static private class TestDeleteMin extends Test {
        public TestDeleteMin() {
            super("Check findMin after each deleteMin");
        }

        protected void test() {

            int[] vals = createValues(100);
            BinomialHeap heap1 = new BinomialHeap();

            for (int v : vals) {
                heap1.insert(v,"");
            }
            Arrays.sort(vals);
            for (int v : vals) {
                if (heap1.findMin().key != v) {
                    setFailed("min is "+v+" but findMin() says "+
                            heap1.findMin());
                    break;
                }
                heap1.deleteMin();
            }
        }
    }

    static private class TestEmpty extends Test {
        public TestEmpty() {
            super("Check empty/size after insert and deleteMin");
        }

        protected void test() {

            BinomialHeap heap1 = new BinomialHeap();
            int size = 0;

            for (int i = 10; i < 30; ++i) {
                if (!heap1.empty()) {
                    setFailed("empty but empty() returns false");
                    break;
                }
                if (heap1.size() != size) {
                    setFailed("size is "+size+
                            " but size() returns "+
                            heap1.size());
                    break;
                }
                for (int j = 0; j < i; ++j) {
                    heap1.insert(i,"");
                    ++size;
                    if (heap1.empty()) {
                        setFailed(
                                "not empty but empty() returns true");
                        break;
                    }
                    if (heap1.size() != size) {
                        setFailed("size is "+size+
                                " but size() returns "+
                                heap1.size());
                        break;
                    }
                }
                for (int j = 0; j < i; ++j) {
                    if (heap1.empty()) {
                        setFailed(
                                "not empty but empty() returns true");
                        break;
                    }
                    heap1.deleteMin();
                    --size;
                    if (heap1.size() != size) {
                        setFailed("size is "+size+
                                " but size() returns "+
                                heap1.size());
                        break;
                    }
                }
                if (!heap1.empty()) {
                    setFailed("empty but empty() returns false");
                    break;
                }
            }
        }
    }

//    static private class TestIsValid extends Test {
//        public TestIsValid() {
//            super("Check valid in after chain of operations");
//        }
//
//        protected void test() {
//
//            BinomialHeap heap1 = new BinomialHeap();
//            int nInsert = 0, nDelete = 0;
//
//            for (int i = 10; i < 30; ++i) {
//                if (!heap1.isValid()) {
//                    setFailed("isValid() returned false after "+
//                            nInsert+" inserts and "+nDelete+" deteles");
//                    break;
//                }
//                for (int j = 0; j < i; ++j) {
//                    heap1.insert(i);
//                    ++nInsert;
//                    if (!heap1.isValid()) {
//                        setFailed(
//                                "isValid() returned false after "+
//                                nInsert+ " inserts and "+nDelete+
//                                " deteles");
//                        break;
//                    }
//                }
//                for (int j = 0; j < i; ++j) {
//                    if (!heap1.isValid()) {
//                        setFailed(
//                                "isValid() returned false after "+
//                                nInsert+ " inserts and "+nDelete+
//                                " deteles");
//                        break;
//                    }
//                    heap1.deleteMin();
//                    ++nDelete;
//                }
//
//                if (!heap1.isValid()) {
//                    setFailed("isValid() returned false after "+
//                            nInsert+ " inserts and "+nDelete+" deteles");
//                    break;
//                }
//            }
//        }
//    }

//    static private class TestArrayToHeap1 extends Test {
//        public TestArrayToHeap1() {
//            super("arrayToHeap: Insert and check size");
//        }
//
//        protected void test() {
//
//            int[] vals = createValues(30);
//            int min = vals[0];
//            for (int v : vals) {
//                min = v < min ? v : min;
//            }
//            BinomialHeap heap1 = new BinomialHeap();
//
//            heap1.arrayToHeap(vals);
//            if (heap1.size() != vals.length) {
//                setFailed("Array size is "+vals.length+ " but size() returned "+
//                        heap1.size());
//            }
//            if (heap1.findMin() != min) {
//                setFailed("Array size is "+vals.length+ " but size() returned "+
//                        heap1.size());
//            }
//        }
//    }

//    static private class TestArrayToHeap2 extends Test {
//        public TestArrayToHeap2() {
//            super("arrayToHeap: Insert and check size after heap has elements");
//        }
//
//        protected void test() {
//
//            BinomialHeap heap1 = new BinomialHeap();
//            heap1.insert(1);
//            heap1.insert(2);
//            heap1.insert(3);
//            int[] vals = createValues(30);
//            int min = Integer.MAX_VALUE;
//            for (int i = 0; i < vals.length; ++i) {
//                vals[i] += 5;
//                min = vals[i] < min ? vals[i] : min;
//            }
//
//            heap1.arrayToHeap(vals);
//            if (heap1.size() != vals.length) {
//                setFailed("Array size is "+vals.length+ " but size() returned "+
//                        heap1.size());
//            }
//            if (heap1.findMin() != min) {
//                setFailed("Array minimum is "+min+ " but findMin() returned "+
//                        heap1.findMin());
//            }
//        }
//    }

    static private class TestBinaryRep extends Test {
        public TestBinaryRep() {
            super("Binary representation");
        }

        private static int binaryArrayToNum(boolean[] array) {
            int num = 0;

            for (int i = 0; i < array.length; ++i) {
                if (array[i]) {
                    num += 1 << i;
                }
            }

            return num;
        }

        private static String bin2str(boolean[] arr) {
            String s = "";

            for (int i = arr.length - 1; i >= 0; --i) {
                s += arr[i] ? "1" : "0";
            }

            return s;
        }

        protected void test() {

            final boolean[][] binarySizes = {
                { true, true, false, false, false, true }, // 49
                { true, false, false, true, true, true }, // 39
                { true, true, false, false, true } // 25
            };

            for (boolean[] binaryArr : binarySizes) {
                BinomialHeap heap1 = new BinomialHeap();
                int size = binaryArrayToNum(binaryArr);
                int[] vals = createValues(size);

                for (int v : vals) {
                    heap1.insert(v,"");
                }

//                boolean[] res = heap1.binaryRep();
//                if (res.length != binaryArr.length) {
//                    setFailed("binary size length is "+binaryArr.length+
//                            "but result length is "+ res.length);
//                    break;
//                }
//                boolean unequal = false;
//
//                for (int i = 0; i < res.length; ++i) {
//                    if (res[i] != binaryArr[i]) {
//                        unequal = true;
//                        break;
//                    }
//                }
//                if (!unequal) {
//                    continue;
//                }
//
//                unequal = false;
//                boolean[] binaryArrayReverse = new boolean[binaryArr.length];
//                for (int i = 0; i < res.length; ++i) {
//                    binaryArrayReverse[binaryArrayReverse.length - 1 - i] =
//                        binaryArr[i];
//                }
//
//                for (int i = 0; i < res.length; ++i) {
//                    if (res[i] != binaryArrayReverse[i]) {
//                        unequal = true;
//                        break;
//                    }
//                }
//
//                if (unequal) {
//                    setFailed("size binary array is: "+bin2str(binaryArr)+
//                            " but result is "+ bin2str(res));
//                    break;
//                }
            }
        }
    }

//    static private class TestMinTreeRank extends Test {
//        public TestMinTreeRank() {
//            super("minTreeRank");
//        }
//
//        protected void test() {
//
//            int[] minRanks = { 0, 1, 4, 5 };
//            for (int minRank : minRanks) {
//                int size = 7 << minRank;
//                int[] vals = createValues(size);
//                BinomialHeap heap1 = new BinomialHeap();
//
//                for (int v : vals) {
//                    heap1.insert(v,"");
//                }
//                if (heap1.minTreeRank() != minRank) {
//                    setFailed("Min tree rank is "+minRank+" (Size="+size+
//                            ") but minTreeRank() returned "+
//                            heap1.minTreeRank());
//                    break;
//                }
//            }
//        }
//    }

    public static void main(String[] argv) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Test[] tests = {
                new TestMeld1(),
                new TestMeld2(),
                new TestMeld3(),
                new TestMeld4(),
                new TestMeld5(),
                new TestInsert(),
                new TestFindMin1(),
                new TestFindMin2(),
                new TestDeleteMin(),
                new TestEmpty(),
//                new TestIsValid(),
//                new TestArrayToHeap1(),
//                new TestArrayToHeap2(),
//                new TestBinaryRep(),
//                new TestMinTreeRank()
        };

        for (Test test : tests) {
            try {
                executor.submit(test).get(5, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                test.setFailed("Timed out. Infinite loop");
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
        executor.shutdown();

        int failed = 0, i = 0;
        for (Test test : tests) {
            if (test.failed()) {
                ++failed;
            }
            System.out.printf("Test %2d | " + test + "\n", ++i);
        }
        System.out.println("Failed "+failed+" Out of "+tests.length+" tests");
        System.out.printf("Grade:\n%.0f\n",
                100 * (1 - (float)failed/tests.length));
        System.exit(0);
    }


}
