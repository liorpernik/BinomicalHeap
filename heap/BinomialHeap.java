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

    public BinomialHeap(HeapNode node) {
        this.last = node;
        this.size = 0;
        this.update_fields(node);
    }

    public BinomialHeap(HeapNode node, int rank){
        size = (int) Math.pow(2, rank);
        min = node;
        last = node;
        last.next = this.min;
    }

    private void update_fields(HeapNode node) {
        HeapNode min = node, start = node;
        this.size += 1;
        while(node.next != null && node.next != start){
            if(min.item.key > node.item.key){
                min = node;
            }
//            this.size += (int) (Math.pow(2, node.rank));
            node = node.next;
        }
        this.min = min;
//        this.last = node;
        this.last.next = start;
    }

    public BinomialHeap() {
        this.size = 0;
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
            }
            else {
                node.next = this.last.next;
//                this.last.next
                this.last.next = node;
                this.update_fields(node);
            }
        } else {
            this.last = node;
            this.update_fields(node);
        }
        return item;
    }

    /**
     * Delete the minimal item
     */
    public void deleteMin() {
        int curr = this.min.item.key, min = -Integer.MAX_VALUE;
        HeapNode i = this.min.next;
        do{
//            if (i.item.key < min) {
//
//                min = i.item.key;
////                newMin = i;
//            }
            i = i.next;
        }while (i.next.item.key != curr);

        HeapNode children = this.min.child.next;
        i.next = this.min.next;

        BinomialHeap childTree = new BinomialHeap(children);
        this.meld(childTree);


//        HeapNode newMin = null;
//
//        this.min = newMin;
        return;

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

    /**
     * Meld the heap with heap2
     */
    public void meld(BinomialHeap heap2) {


        if(heap2.size == 0) return;
        if(this.size == 0){
            update_fields(heap2.min);
            return;
        }
        this.size += heap2.size;
        BinomialHeap[] heaps = new BinomialHeap[Math.max(this.last.rank, heap2.last.rank) +2];

        HeapNode curr = this.last.next, start = curr, tmp = curr.next;
//        this.last.next = null;
         do{
             tmp = curr.next;
            heaps[curr.rank] = new BinomialHeap(curr, curr.rank);
            curr = tmp;
        }while (tmp != start);

        HeapNode heap2Node = heap2.last.next;
//        start = heap2.last;
        int rank = 0, heap2rank = heap2.numTrees();
        heap2.last.next = null;
        for (int i = 0; i < heap2rank; i++) {

            tmp = heap2Node;
            BinomialHeap heap2Child = new BinomialHeap(heap2Node);
            rank = heap2Child.min.rank;

            for (int j = rank; j < heaps.length && heaps[j] != null; j++) {
//                if(heaps[j] != null){
            //(rank < heaps.length  && heaps[rank] != null) {

                BinomialHeap tree = heaps[j];
                if (heap2Child.min.item.key < tree.min.item.key) {
                    meld2Roots(heap2Child.min, tree.min);
                }else{
                    meld2Roots(tree.min, heap2Child.min);
                    heap2Child = tree;
                }
//                heap2Child.size = (int)Math.pow(2, heap2Child.min.rank);
                heaps[j] = null;
                ;
//                }
                rank +=1;
            }
//            if (rank == heaps.length)
//                rank-=1;
            heaps[rank] = heap2Child;
            heap2Node = tmp.next;
        }//while (tmp != start);
        connectRoots(heaps);

		return;
    }


    private void meld2Roots(HeapNode first, HeapNode second){

        HeapNode tmp = second;
        second.parent = first;
        if (first.child != null) {
            tmp = first.child.next;
            first.child.next = second;
        }
        second.next = tmp;
        first.child = second;
        first.rank = first.rank +1;
    }

    private void connectRoots(BinomialHeap[] heaps){
        BinomialHeap[] newHeap = new BinomialHeap[heaps.length];
        int index = 0;
        for (BinomialHeap heap : heaps) {
            if (heap != null) {
                newHeap[index++] = heap;
            }
        }
        HeapNode newMin = newHeap[index - 1].min;
//        int size = 0;

        for (int i = 0; i < index - 1; i++) {
            if(newHeap[i].min.item.key < newMin.item.key){
                newMin = newHeap[i].min;
            }
            newHeap[i].min.next = newHeap[i + 1].min;
//            newHeap[i].min.rank = i;
//            size += (int)Math.pow(2, newHeap[i].min.rank);
        }
//        size += (int)Math.pow(2, newHeap[index-1].min.rank);

        this.last = newHeap[index - 1].min;
        this.min = newMin;
        this.last.next = newHeap[0].min;
//        this.size = size;
        return;
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
        int count = 1;
        for (HeapNode i = this.min.next; i.item.key != this.min.item.key; i = i.next) {
            count++;
        }
        return count;
    }

    public void print_r() {
        {
            int s;
            if(this.last.rank == 0){s = 1;}
            else if(this.last.rank == 1){s = 2;}
            else if(this.last.rank == 2){s = 3;}
            else {
                s = (int) Math.pow(2, this.last.rank - 1);
            }
            int[][][] answer = new int[this.numTrees()][s][s];
            BinomialHeap.HeapNode curr_tree = this.last.next;
            for (int k = 0; k < this.numTrees(); k++ ) {
                Set<HeapNode> dic = new HashSet<>();
                this.print_rec(curr_tree, 0, 0, dic, answer, k);
                curr_tree = curr_tree.next;
            }
            int x = 0;
            for (int i = 0; i < answer[x].length; i++) {
                for (int j = 0; j < answer[x].length; j++) {
                    if(answer[x][i][j] != 0) {
                        System.out.print(answer[x][i][j] + " ");
                    }
                    else{System.out.print("  ");}
                    if(x == answer.length-1 && j == answer[x].length-1) {break;}
                    if(j == answer[x].length-1) {
                        if (x + 1 < answer.length) {x += 1;}
                        j = -1;
                    }

                }
                System.out.println();
                x = 0;
            }
        }
    }
    public void print_rec(BinomialHeap.HeapNode first, int depth, int x, Set<BinomialHeap.HeapNode> dic, int[][][] answer, int num_in_row) {
        if (dic.contains(first)){return;}
        dic.add(first);
        answer[num_in_row][depth][x] = first.item.key;
        if(depth != 0) {
            print_rec(first.next, depth, x + 1, dic, answer, num_in_row);
        }
        if(first.child != null){
            print_rec(first.child.next,depth+1,x,dic,answer, num_in_row);
        }
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
