import java.util.HashSet;
import java.util.Set;

/**
 * BinomialHeap
 * <p>
 * An implementation of binomial heap over positive integers.
 */
public class BinomialHeap {
    public int size;
    public HeapNode last;
    public HeapNode min;
    public int links;
    public int deletedRanks;


    public BinomialHeap() {
        this.size = 0;
    }

    //    public BinomialHeap(HeapNode node) {
//         BinomialHeap(node,0);
//    }
    //Build Heap with Single root
    public BinomialHeap(HeapNode node) {
        this.size = (int) Math.pow(2, node.rank);
        node.parent = null;
//        links--;
        this.min = node;
        this.last = node;
        this.last.next = this.min;
    }

    //Used to build the children Heap in deleteMin
    public BinomialHeap(HeapNode node, int rank) {
        this.last = node;
        if (rank > 0)
            update_fields(node, size_of_ranks(node));
        else
            update_fields(node, 1);
    }

    private int size_of_ranks(HeapNode node) {
        int sum = (int) Math.pow(2, node.rank);
        HeapNode start = node.next;
        while (start != node) {
            sum += (int) Math.pow(2, start.rank);
            start = start.next;
        }
        return sum;
    }

    private void update_fields(HeapNode node, int size) {
        HeapNode min = node, start = size != 1 ? node.next : node;
        this.size += size;
        while (node.next != null && node != start.next) {
            if (min.item.key > node.item.key) {
                min = node;
            }
//            this.size += (int) (Math.pow(2, node.rank));
            node = node.next;
        }
        if (this.min == null || this.min.item.key > min.item.key)
            this.min = min;
//        this.last = node;
        this.last.next = start;
    }

    /**
     * pre: key > 0
     * <p>
     * Insert (key,info) into the heap and return the newly generated HeapItem.
     */
    public HeapItem insert(int key, String info) {
        HeapNode node = new HeapNode();
        HeapItem item = new HeapItem(node, key, info);
        node.item = item;
        node.rank = 0;

        if (this.last != null) {
            if (this.last.next.rank == 0) {
                BinomialHeap heap0 = new BinomialHeap(node);
                this.meld(heap0);
            } else {
                node.next = this.last.next;
//                this.last.next
                this.last.next = node;
                this.update_fields(node, 1);
//                this.links ++;
            }
        } else {
            this.last = node;
            this.update_fields(node, 1);
        }
        //this.links ++;
        return item;
    }

    /**
     * Delete the minimal item
     */
    public void deleteMin() {
        if (size == 1) {
            this.size = 0;
            this.last = null;
            this.min = null;
            return;
        }
        //links -= 1;
        this.deletedRanks += this.min.rank;
        HeapNode minNode = this.min;
        int trees = this.numTrees();
        HeapNode child = minNode.child;
        HeapNode newMin = child;
        if (trees == 1) {

            do {
                HeapNode nextChild = child.next;
                if (child.item.key < newMin.item.key) {
                    newMin = child;
                }
                child = nextChild;
            } while (child != minNode.child);

            this.last = minNode.child;
            this.min = newMin;
            this.size -= 1;
            return;
        }

        HeapNode current = this.last;
        newMin = this.last;
        HeapNode nextCurr = null;
        do {
            if (current.next == minNode) {

                current.next = minNode.next;
            }
            nextCurr = current.next;
            if (current.item.key < newMin.item.key) {
                newMin = current;
            }
            current = nextCurr;
        } while (current.next != this.last.next && current != this.last);


        this.size -= (int) Math.pow(2, this.min.rank);

        // If there are new roots, meld them into the current heap
        if (minNode.rank != 0) {
            BinomialHeap newHeap = new BinomialHeap(minNode.child, minNode.child.rank);
            this.meld(newHeap);
        } else {
            this.min = newMin;
        }

    }

    /**
     * Return the minimal HeapItem, null if empty.
     */
    public HeapItem findMin() {
        return this.empty() ? null : this.min.item;
    }

    /**
     * pre: 0<diff<item.key
     * <p>
     * Decrease the key of item by diff and fix the heap.
     */
    public void decreaseKey(HeapItem item, int diff) {

        item.key = item.key - diff;

        HeapNode node = item.node;
        while (node.parent != null && node.item.key < node.parent.item.key) {

            node.parent.item.switchItems(node.item);

            node = node.parent;
        }
    }

    /**
     * Delete the item from the heap.
     */
    public void delete(HeapItem item) {
        this.decreaseKey(item, Integer.MAX_VALUE);
        this.deleteMin();
    }

//    /**
//     * Meld the heap with heap2
//     */
//    public void meld(BinomialHeap heap2) {
//
//
//        if (heap2.size == 0) return;
//        if (this.size == 0) {
//            this.last = heap2.min;
//            update_fields(heap2.min, 1);
//            return;
//        }
//        this.size += heap2.size;
////        BinomialHeap[] heaps = new BinomialHeap[Math.max(this.last.rank, heap2.last.rank) + 2];
//        int maxRank = Math.max(this.last.rank, heap2.last.rank) + 2;
//        HeapNode curr = this.last.next, start2, start1,tmp = null, temp = null;
//
//        boolean meld2 = false;
//        if(heap2.numTrees() > this.numTrees()){
//            curr = heap2.last.next;
//            meld2 = true;
//        }
//        start1 = curr;
////        do {
////            tmp = curr.next;
////            heaps[curr.rank] = new BinomialHeap(curr);
////            curr = tmp;
////        } while (tmp != start);
//
//        HeapNode heap2Node = meld2 ? this.last.next : heap2.last.next;
//
//        int rank = 0, heap2roots = heap2.numTrees();
//
//        start2 = heap2Node;
////        do {
//
////            tmp = heap2Node.next;
////            BinomialHeap heap2Child = new BinomialHeap(heap2Node);
//
//            rank = heap2Node.rank;
//
//            while (rank < maxRank && curr.rank == heap2Node.rank && start2 != tmp) {//heaps[rank] != null
//
//                temp = curr.next;
//                tmp = heap2Node.next;
////                BinomialHeap tree = heaps[rank];
//                if (heap2Node.item.key < curr.item.key) {
//                    meld2Roots(heap2Node, curr);
//                    curr = temp;
//                } else {
//                    meld2Roots(curr, heap2Node);
//                    heap2Node = tmp;
//                }
////                this.links++;
////                heaps[rank] = null;
//
//                rank += 1;
//            }
//
////            heaps[rank] = heap2Child;
//            heap2Node = tmp;
////        } while (tmp != start && tmp.rank < maxRank);
//
//        while (temp != start1){
//            temp = curr.next;
//            if(curr.rank == temp.rank){
//                if (temp.item.key < curr.item.key) {
//                    meld2Roots(temp, curr);
////                    curr = temp;
//                } else {
//                    meld2Roots(curr, temp);
////                    heap2Node = tmp;
//                }
//            }
//            curr = temp;
//        }
//
//
////        connectRoots(heaps);
//
//        return;
//    }
//
//
//    private void meld2Roots(HeapNode first, HeapNode second) {
//        links++;
//        first.parent = null;
//        HeapNode tmp = second;
//        second.parent = first;
//        if (first.child != null) {
//            tmp = first.child.next;
//            first.child.next = second;
//        }
//        second.next = tmp;
//        first.child = second;
//        first.rank = first.rank + 1;
//        HeapNode fchild = first.child.next;// child with rank 0
//        for (HeapNode c = fchild; c.rank > 0 && c != second; c = c.next)
//            c.rank++;
//    }
//
//    private void connectRoots(BinomialHeap[] heaps) {
//        BinomialHeap[] newHeap = new BinomialHeap[heaps.length];
//        int index = 0;
//        for (BinomialHeap heap : heaps) {
//            if (heap != null) {
//                newHeap[index++] = heap;
//            }
//        }
//        HeapNode newMin = newHeap[index - 1].min;
//
//        for (int i = 0; i < index - 1; i++) {
//            if (newHeap[i].min.item.key < newMin.item.key) {
//                newMin = newHeap[i].min;
//            }
////            this.links++;
//            newHeap[i].min.next = newHeap[i + 1].min;
//        }
//        this.last = newHeap[index - 1].min;
//        this.min = newMin;
//        this.last.next = newHeap[0].min;
//    }


    // Meld two binomial heaps
    public BinomialHeap meld(BinomialHeap other) {
        BinomialHeap result = new BinomialHeap();
        this.last = meldTrees(this.last.next, other.last.next);

        return result;
    }

    // Recursively meld trees with the same rank
    private HeapNode meldTrees(HeapNode h1, HeapNode h2) {

        if(h1 ==null || h2 == null){
            return null;
        }
        if (h1.rank < h2.rank) {
            h1.next = meldTrees(h1.next, h2);
            return h1;
        } else if (h1.rank > h2.rank) {
            h2.next = meldTrees(h1, h2.next);
            return h2;
        } else {
            HeapNode mergedTree = meldSameRank(h1, h2);
            mergedTree.next = meldTrees(h1.next, h2.next);
            return mergedTree;
        }
    }

    // Merge two trees of the same rank
    private HeapNode meldSameRank(HeapNode h1, HeapNode h2) {
        if (h1.item.key <= h2.item.key) {
            h2.next = h1.child;
            h1.child = h2;
            h2.parent = h1;
            h1.rank++;
            return h1;
        } else {
            h1.next = h2.child;
            h2.child = h1;
            h1.parent = h2;
            h2.rank++;
            return h2;
        }
    }

    // Find the minimum node in the circular list
    private HeapNode findMin(HeapNode node) {
        if (node == null) return null;
        HeapNode min = node;
        HeapNode current = node.next;
        while (current != node) {
            if (current.item.key < node.item.key) {
                min = current;
            }
            current = current.next;
        }
        return min;
    }


    /**
     * Return the number of elements in the heap
     */
    public int size() {
        return this.size;
    }

    /**
     * The method returns true if and only if the heap
     * is empty.
     */
    public boolean empty() {
        return this.size == 0;
    }

    /**
     * Return the number of trees in the heap.
     */
    public int numTrees() {
        if (this.empty()) {
            return 0;
        }
        int count = 0;
        HeapNode start = this.last.next;
        do {
            count++;
            start = start.next;
        } while (start != this.last.next);

        return count;
    }

    public String toString() {
        if (this.empty()) {
            return "Heap is empty";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("BinomialHeap\n");

        BinomialHeap.HeapNode current = this.last;
        do {
            if (current != null) {
                sb.append("Tree with root: ").append(current.item.key).append("\n");
                appendTree(sb, current, "", true);
                current = current.next;
            }
        } while (current != this.last);

        return sb.toString();
    }

    private void appendTree(StringBuilder sb, BinomialHeap.HeapNode node, String indent, boolean last) {
        if (node == null) return;

        sb.append(indent);
        if (last) {
            sb.append("└── ");
            indent += "    ";
        } else {
            sb.append("├── ");
            indent += "│   ";
        }
        sb.append(node.item.key).append("\n");

        if (node.child != null) {
            BinomialHeap.HeapNode child = node.child;
            do {
                appendTree(sb, child, indent, child.next == node.child);
                child = child.next;
            } while (child != node.child);
        }
    }

    public void printRanks() {
        HeapNode node=this.last;
        int sum=0;
        do{
            node=node.next;
            System.out.println(node.rank);
            sum+=(int)Math.pow(2,node.rank);
        }while(node!=last);
        System.out.println("curr size according to root ranks: "+sum);

    }

    /**
     * Class implementing a node in a Binomial Heap.
     */
    public static class HeapNode {
        public HeapItem item;
        public HeapNode child;
        public HeapNode next;
        public HeapNode parent;
        public int rank;

        public HeapNode() {
            this.rank = 0;
            this.next = null;
        }

//        public void addChild(HeapNode tree) {
//            if (this.child == null) {
//                this.child = tree;
//            } else {
//                HeapNode fChild=this.child.next;
//                this.child.next = tree;
//                tree.next=fChild;
//                tree.parent=this;
//            }
//            this.rank++;
//        }

        public void addChild(HeapNode tree) {
            if (this.child == null) {
                this.child = tree;
                tree.next = tree; // Circular list with one element
            } else {
                HeapNode fChild = this.child.next;
                this.child.next = tree;
                tree.next = fChild;
            }
            tree.parent = this;
            this.rank++;
        }

    }

    /**
     * Class implementing an item in a Binomial Heap.
     */
    public static class HeapItem {
        public HeapNode node;
        public int key;
        public String info;

        public HeapItem(HeapNode node, int key, String info) {
            this.info = info;
            this.key = key;
            this.node = node;
        }

        public void switchItems(HeapItem item) {
            int tmpKey = this.key;
            String tmpInfo = this.info;

            this.key = item.key;
            this.info = item.info;

            item.key = tmpKey;
            item.info = tmpInfo;
        }
    }
}
