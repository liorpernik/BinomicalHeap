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


    public BinomialHeap() {
        this.size = 0;
    }

//    public BinomialHeap(HeapNode node) {
//         BinomialHeap(node,0);
//    }
    //Build Heap with Single root
    public BinomialHeap(HeapNode node){
        this.size = (int) Math.pow(2, node.rank);
        node.parent = null;
        this.min = node;
        this.last = node;
        this.last.next = this.min;
    }
    //Used to build the children Heap in deleteMin
    public BinomialHeap(HeapNode node, int rank){
        this.last = node;
        if(rank>0)
            update_fields(node,size_of_ranks(node));
        else
            update_fields(node,1);
    }

    private int size_of_ranks(HeapNode node){
        int sum=(int)Math.pow(2,node.rank);
        HeapNode start=node.next;
        while(start!=node){
            sum+=(int)Math.pow(2,start.rank);
            start=start.next;
        }
        return sum;
    }
    private void update_fields(HeapNode node,int size) {
        HeapNode min = node, start = size!=1 ? node.next: node;
        this.size+=size;
        while(node.next != null && node != start.next){
            if(min.item.key > node.item.key){
                min = node;
            }
//            this.size += (int) (Math.pow(2, node.rank));
            node = node.next;
        }
        if(this.min == null || this.min.item.key > min.item.key)
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
            }
            else {
                node.next = this.last.next;
//                this.last.next
                this.last.next = node;
                this.update_fields(node,1);
            }
        } else {
            this.last = node;
            this.update_fields(node,1);
        }
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
        HeapNode minNode = this.min;
        int trees = this.numTrees();
        HeapNode child = minNode.child;
        HeapNode newMin = child;
        if(trees == 1){

            do {
                HeapNode nextChild = child.next;
                if(child.item.key < newMin.item.key){
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
            if(current.item.key < newMin.item.key){
                newMin = current;
            }
            current = nextCurr;
        } while (current.next != this.last.next && current != this.last);


        this.size -= (int) Math.pow(2, this.min.rank);

        // If there are new roots, meld them into the current heap
        if (minNode.rank != 0) {
            BinomialHeap newHeap = new BinomialHeap(minNode.child, minNode.child.rank);
            this.meld(newHeap);
        }else {
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

    /**
     * Meld the heap with heap2
     */
    public void meld(BinomialHeap heap2) {


        if(heap2.size == 0) return;
        if(this.size == 0){
            this.last = heap2.min;
            update_fields(heap2.min,1);
            return;
        }
        this.size += heap2.size;
        BinomialHeap[] heaps = new BinomialHeap[Math.max(this.last.rank, heap2.last.rank) +2];

        HeapNode curr = this.last.next, start = curr, tmp = curr.next;

         do{
             tmp = curr.next;
            heaps[curr.rank] = new BinomialHeap(curr);
            curr = tmp;
        }while (tmp != start);

        HeapNode heap2Node = heap2.last.next;

        int rank = 0, heap2roots = heap2.numTrees();

        for (int i = 0; i < heap2roots; i++) {

            tmp = heap2Node.next;
            BinomialHeap heap2Child = new BinomialHeap(heap2Node);
            rank = heap2Child.min.rank;

            for (int j = rank; j < heaps.length && heaps[j] != null; j++) {


                BinomialHeap tree = heaps[j];
                if (heap2Child.min.item.key < tree.min.item.key) {
                    meld2Roots(heap2Child.min, tree.min);
                }else{
                    meld2Roots(tree.min, heap2Child.min);
                    heap2Child = tree;
                }

                heaps[j] = null;

                rank +=1;
            }

            heaps[rank] = heap2Child;
            heap2Node = tmp;
        }

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
        HeapNode fchild=first.child.next;// child with rank 0
        for(HeapNode c=fchild;c.rank>0 &&c!=second;c=c.next)
            c.rank++;
    }

    private void connectRoots(BinomialHeap[] heaps) {
        BinomialHeap[] newHeap = new BinomialHeap[heaps.length];
        int index = 0;
        for (BinomialHeap heap : heaps) {
            if (heap != null) {
                newHeap[index++] = heap;
            }
        }
        HeapNode newMin = newHeap[index - 1].min;

        for (int i = 0; i < index - 1; i++) {
            if (newHeap[i].min.item.key < newMin.item.key) {
                newMin = newHeap[i].min;
            }
            newHeap[i].min.next = newHeap[i + 1].min;
        }
        this.last = newHeap[index - 1].min;
        this.min = newMin;
        this.last.next = newHeap[0].min;
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
        HeapNode start=this.last.next;
        do {
            count++;
            start=start.next;
        }while (start != this.last.next);

        return count;
    }

    public void print_r() {
        {
            int s;
            if(this.last == null){return;}
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
