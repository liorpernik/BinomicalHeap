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


    /**
        @params: node
        calculating size of tree by ranks of all roots
    */
    private int size_of_ranks(HeapNode node){
        int sum=(int)Math.pow(2,node.rank);
        HeapNode start=node.next;
        while(start!=node){
            // Add the size for the current node's rank to the sum
            sum+=(int)Math.pow(2,start.rank);
            start=start.next;
        }
        return sum;
    }

    /**
        update the fields of tree from given root of new tree
      */
    private void update_fields(HeapNode node,int size) {
        HeapNode min = node, start = size!=1 ? node.next: node;
        // Update the total size of the heap
        this.size+=size;


        while(node.next != null && node != start.next){
            // Update 'min' to the node with the smallest key
            if(min.item.key > node.item.key){
                min = node;
            }
            node = node.next;
        }

        // Update the minimum node if necessary
        if(this.min == null || this.min.item.key > min.item.key)
            this.min = min;

        this.last.next = start;
    }

    /**
     * pre: key > 0
     * <p>
     * Insert (key,info) into the heap and return the newly generated HeapItem.
     */
    public HeapItem insert(int key, String info) {

        HeapNode node = new HeapNode();
        // Create a new HeapItem instance and associate it with the newly created node
        HeapItem item = new HeapItem(node, key, info);
        node.item = item;
        node.rank = 0;

        // Check if the heap already contains nodes
        if (this.last != null) {
            // If current heap contains a binomial tree of rank 0
            // Create a new BinomialHeap containing the new node and meld it with the current heap
            if (this.last.next.rank == 0) {
                BinomialHeap heap0 = new BinomialHeap(node);
                this.meld(heap0);
            }
            else {
                node.next = this.last.next;
                this.last.next = node;
                // Update heap fields after the insertion -> this will be first root
                this.update_fields(node,1);
            }
        } else {
            // If the heap is empty, set the new node as the last node in the heap
            this.last = node;
            this.update_fields(node,1);
        }
        return item;
    }

    /**
     * Delete the minimal item
     */
    public void deleteMin() {

        // Handle the case where there is only one node in the heap
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

        // Case when there is only one tree in the heap
        if(trees == 1){
            // Update newMin if the current child has a smaller key
            do {
                HeapNode nextChild = child.next;
                if(child.item.key < newMin.item.key){
                    newMin = child;
                }
                child = nextChild;
            } while (child != minNode.child);

            // Update the last node and the min node
            this.last = minNode.child;
            this.min = newMin;
            this.size -= 1;
            return;
        }

        // Handle the case when there are multiple trees in the heap
        HeapNode current = this.last;
        newMin = this.last;
        HeapNode nextCurr = null;
        do {
            // delete min by bypassing it
            if (current.next == minNode) {
                current.next = minNode.next;
            }

            nextCurr = current.next;
            // find new minimum (next smallest key)
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
        }else { //set new min
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
        // Traverse up the tree, bubbling up the node if necessary
        while (node.parent != null && node.item.key < node.parent.item.key) {
            // Swap the items of the current node and its parent
            node.parent.item.switchItems(node.item);

            node = node.parent;
        }
        //change min if new item smaller than curr min
        if (node.item.key <  this.min.item.key){
            this.min = node;
        }
    }

    /**
     * Delete the item from the heap.
     */
    public void delete(HeapItem item) {
        //decrease item to -inf
        this.decreaseKey(item, Integer.MAX_VALUE);
        //delete min
        this.deleteMin();
	}

    /**
     * Meld the heap with heap2
     */
    public void meld(BinomialHeap heap2) {

        //if melding with empty
        if(heap2.size == 0) return;
        // If the current tree is empty, just copy the second heap's root
        if(this.size == 0){
            this.last = heap2.min;
            update_fields(heap2.min,1);
            return;
        }

        // Update the size of the current heap
        this.size += heap2.size;

        // Prepare an array to hold binomial heaps of different ranks
        BinomialHeap[] heaps = new BinomialHeap[Math.max(this.last.rank, heap2.last.rank) +2];

        HeapNode curr = this.last.next, start, tmp;
        boolean meld2 = false;
        if(heap2.numTrees() > this.numTrees()){
            curr = heap2.last.next;
            meld2 = true;
        }
        start = curr;

        // Traverse through the current tree's roots and store each tree according to rank in the array
         do{
             tmp = curr.next;
            heaps[curr.rank] = new BinomialHeap(curr);
            curr = tmp;
        }while (tmp != start);

        HeapNode heap2Node = meld2 ? this.last.next : heap2.last.next;

        int rank = 0, heap2roots = meld2 ? this.numTrees() : heap2.numTrees();
        // Traverse through the second heap's roots
        for (int i = 0; i < heap2roots; i++) {

            tmp = heap2Node.next;
            BinomialHeap heap2Child = new BinomialHeap(heap2Node);
            rank = heap2Child.min.rank;

            // Meld the current heap with each root of the second heap by ranks
            for (int j = rank; j < heaps.length && heaps[j] != null; j++) {


                BinomialHeap tree = heaps[j];
                if (heap2Child.min.item.key < tree.min.item.key) {
                    // meld it into the current tree
                    meld2Roots(heap2Child.min, tree.min);
                }else{
                    // meld the current tree into the heap2Child
                    meld2Roots(tree.min, heap2Child.min);
                    heap2Child = tree;
                }

                heaps[j] = null;

                rank +=1;
            }

            // Place the resulting heap back in the array at the appropriate rank
            heaps[rank] = heap2Child;
            heap2Node = tmp;
        }

        // Connect all the roots in the array to form the final melded tree
        connectRoots(heaps);

		return;
    }

    /**
     * params: two roots of trees to meld
     * meld second to first by setting second as first`s child
     * */
    private void meld2Roots(HeapNode first, HeapNode second){

        HeapNode tmp = second;

        second.parent = first;
        // If the first node already has children, update the pointers
        if (first.child != null) {
            tmp = first.child.next;
            first.child.next = second;
        }
        second.next = tmp;
        // Set the second node as the child of the first node
        first.child = second;

        first.rank = first.rank +1;

        // Traverse through the children of the first node to update their ranks
        HeapNode fchild=first.child.next;// child with rank 0
        for(HeapNode c=fchild;c.rank>0 &&c!=second;c=c.next)
            c.rank++;
    }

    private void connectRoots(BinomialHeap[] heaps) {
        // Create a new array to hold non-null binomial heaps
        BinomialHeap[] newHeap = new BinomialHeap[heaps.length];

        // Copy non-null heaps from the input array to the new array
        int index = 0;
        for (BinomialHeap heap : heaps) {
            if (heap != null) {
                newHeap[index++] = heap;
            }
        }

        // Initialize newMin with the minimum node from the last heap in newHeap
        HeapNode newMin = newHeap[index - 1].min;

        // Traverse through the newHeap to update the minimum and link the roots
        for (int i = 0; i < index - 1; i++) {
            // Update the minimum node if a smaller one is found
            if (newHeap[i].min.item.key < newMin.item.key) {
                newMin = newHeap[i].min;
            }

            //link roots of tree
            newHeap[i].min.next = newHeap[i + 1].min;
        }

        //set new last, min and size
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
        // Traverse the circular linked list of roots to find amount
        HeapNode start=this.last.next;
        do {
            count++;
            start=start.next;
        }while (start != this.last.next);

        return count;
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
            this.next = this;
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

        /**
         *
         * @param item
         * swap the content of 2 items (to preserve nodes structure)
         */
        public void switchItems(HeapItem item) {
            int tmpKey = this.key;
            String tmpInfo = this.info;

            // Swap the key and info of the current heap item with those of the provided item
            this.key = item.key;
            this.info = item.info;

            item.key = tmpKey;
            item.info = tmpInfo;
        }
    }
}
