
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
        this.update_fields(node);
    }

    private void update_fields(HeapNode node) {
        this.min = node;
        this.last = node;
        this.last.next = this.min;
        this.size = (int) (Math.pow(2, node.rank));
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
        if (this.last != null) {
            if (this.last.next.rank == 0) {
                BinomialHeap heap0 = new BinomialHeap(node);
                this.meld(heap0);
            }
            else {
                node.next = this.last.next;
                this.last.next = node;
            }
        } else {
            this.update_fields(node);
        }
        return item;
    }

    /**
     * Delete the minimal item
     */
    public void deleteMin() {
        int curr = this.min.item.key, min = -Integer.MAX_VALUE;
        HeapNode newMin = null;
        for (HeapNode i = this.min.next; i.item.key != curr; i = i.next) {
            if (i.item.key < min) {

                min = i.item.key;
                newMin = i;
            }
        }
        HeapNode children = this.min.child.next, currNodes = this.min.next;
        this.min.child.next = null;

        BinomialHeap[] heaps = new BinomialHeap[this.last.rank + 1];

        while (currNodes != this.min) {
            heaps[currNodes.rank] = new BinomialHeap(currNodes);
            currNodes = currNodes.next;
        }

        while (children != null) {
            BinomialHeap minChild = new BinomialHeap(children);

            while (heaps[minChild.min.rank] != null) {
                minChild.meld(heaps[minChild.min.rank]);
            }
            heaps[minChild.min.rank] = minChild;
            children = children.next;
        }

        BinomialHeap[] newHeap = new BinomialHeap[heaps.length];
        int index = 0;
        for (BinomialHeap heap : heaps) {
            if (heap != null) {
                newHeap[index++] = heap;
            }
        }

        for (int i = 0; i < index - 1; i++) {
            newHeap[i].min.next = newHeap[i + 1].min;
        }
        this.last = newHeap[index - 1].min;
        this.min = newMin;
        this.last.next = this.min;
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
//		if(heap2.size == 0) return;
//		if(this.size == 0){
//			this.min = heap2.min;
//			this.last = heap2.last;
//			this.size = heap2.size;
//
////			this.min.next = this.last;
//			this.last.next = this.min;
//			return;
//		}
//
//		HeapNode node = heap2.min,tempRoot=node;
//		HeapNode curr= this.last.next,heapNRoot=curr;
//		while(curr.rank == node.rank){
//
//			if (node.item.key < curr.item.key){
//				curr.parent = node;
//				if(node.child != null){
//
//					node.child.next = curr;
//				}
////				tempRoot=node.next;
//				curr.next = node.child;
//				node.child = curr;
//				this.min = node;
//				this.last.next = this.min;
//
//			}else {
//				node.parent = curr;
//				if(curr.child != null)
//				{
//					curr.child.next = node;
//				}
////				tempRoot=curr.next;
//				node.next = curr.child;
//				curr.child = node;
//			}
////			node = tempRoot;
//			node.rank++;
//			this.size += heap2.size;
////			curr = heapNRoot.next;
////			node.next = curr;
////			heapNRoot = heapNRoot.next;
//		}
////
//		return;
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

    // Function to convert the binomial heap to a string representation
    @Override
    public String toString() {
        if (this.empty()) {
            return "Heap is empty.";
        }

        StringBuilder sb = new StringBuilder();
        HeapNode current = this.min;
        do {
            sb.append(printTree(current, 0));
            current = current.next;
        } while (current != this.min);

        return sb.toString();
    }

    private String printTree(HeapNode node, int rank) {
        StringBuilder sb = new StringBuilder();
        sb.append("    ".repeat(rank))
                .append(node.item.key);

        if (node.child != null) {
            HeapNode child = node.child;
            sb.append("----").append(printChildren(child, rank + 1));
        }

        return sb.toString() + "\n";
    }

    private String printChildren(HeapNode node, int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.item.key);
        HeapNode sibling = node.next;
        while (sibling != node) {
            sb.append("----").append(printTree(sibling, depth));
            sibling = sibling.next;
        }
        return sb.toString();
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