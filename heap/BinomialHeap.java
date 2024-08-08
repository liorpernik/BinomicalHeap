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
    public static int links;
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
        double sum = Math.pow(2, node.rank);
        HeapNode start = node.next;
        while (start != node) {
            sum = sum + Math.pow(2, start.rank);
            start.parent = null;
            start = start.next;
        }
        return (int) sum;
    }

    private void update_fields(HeapNode node, int size) {
        HeapNode min = node, start = size != 1 ? node.next : node;
        this.size += size;
        min = findNewMin(start);
        if (this.min == null || this.min.item.key > min.item.key)
            this.min = min;
//        this.last = node;
        this.last.next = start;
    }

    private void clone(BinomialHeap heap2) {
        this.last = heap2.last;
        this.min = heap2.min;
        this.size = heap2.size;
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

        if (this.min == null || key < this.min.item.key) {
            this.min = node;
        }
        if (this.last != null) {
            if (this.last.next.rank == 0) {
                BinomialHeap heap0 = new BinomialHeap(node);
                this.meld(heap0);
            } else {
                node.next = this.last.next;
                this.last.next = node;
                this.update_fields(node, 1);
            }
        }
        else {
            this.last = node;
            this.update_fields(node, 1);
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
        this.deletedRanks += this.min.rank;
        HeapNode minNode = this.min;
        int trees = this.numTrees();
        HeapNode child = minNode.child;
        HeapNode newMin = child;
        if (trees == 1) {
            newMin = findNewMin(child);
            this.last = minNode.child;
            this.min = newMin;
            this.size -= 1;
            return;
        }
        HeapNode current = this.min.next;

        do {
            if (current.next == minNode) {
                current.next = minNode.next;
                if (this.last == minNode)
                    this.last = current;
            }
            if (current.item.key < newMin.item.key) {
                newMin = current;
                //newMin.next = current.next;
            }
            current = current.next;
        } while (current != this.min.next);

        this.size -= (int) Math.pow(2, minNode.rank);// size_of_ranks(current);

        // If there are new roots, meld them into the current heap
        if (minNode.rank != 0) {
            BinomialHeap newHeap = new BinomialHeap(minNode.child, minNode.child.rank);

            this.min = newMin.item.key < newHeap.min.item.key ? newMin : newHeap.min;
            if (newHeap.numTrees() < this.numTrees()) {
                this.meld(newHeap);
            } else {
                newHeap.meld(this);
                this.clone(newHeap);
            }
        } else
            this.min = newMin;


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
        this.union(heap2);
        int min = 0;
        HeapNode curr = this.last.next, prev = this.last, currNext = curr.next;
        while (curr != this.last) {
            if (curr.rank == currNext.rank) {
                if (currNext.next.rank == curr.rank && currNext.next != this.last.next) {
                    min = (int) Math.min(Math.min(curr.item.key, currNext.item.key), currNext.next.item.key);
                    //re-arranging the nodes
                    if (currNext.next.item.key == min) {
                        prev.next = currNext.next;
                        currNext.next = currNext.next.next;
                        prev.next.next = curr;//dd
                        curr = prev.next;
                    } else if (currNext.item.key == min) {
                        curr.next = currNext.next;
                        currNext.next = curr;
                        prev.next = currNext;
                        curr = currNext;
                    }
                } else {
                    if (curr.item.key <= currNext.item.key) {
                        if (currNext == this.last)
                            this.last = curr;
                        curr.next = currNext.next;
                        currNext.next=null;
                        curr.addChild(currNext);
                        currNext = curr.next;
                        continue;
                    } else {
                        prev.next = currNext;
                        curr.next=null;
                        currNext.addChild(curr);
                        curr = currNext;
                        currNext = curr.next;
                        continue;
                    }
                }
            }
            prev = curr;
            curr = curr.next;
            currNext = curr.next;
        }
        this.min=findNewMin(this.last);
    }

    public void union(BinomialHeap heap2) {

        if (heap2 == null || heap2.last == null) {
            return;
        }
        this.size+=heap2.size;
        // If either heap has only one root
        if (this.last == null) {
            this.last = heap2.last;
            this.min = heap2.min;
            return;
        }
        int size = heap2.numTrees(), i = 0;
        HeapNode r2 = heap2.last.next, curr = this.last.next, prev = this.last, temp, first;
        while (i < size) {
            if (r2.rank <= curr.rank) {
                prev.next = r2;
                temp = r2.next;
                r2.next = curr;
                curr =r2;
                r2 = temp;
                i++;
            } else if (r2.rank > curr.rank) {
                if (curr == this.last) {
                    temp = r2.next;
                    first = this.last.next;
                    curr.next = r2;
                    r2.next = first;
                    r2 = temp;
                    curr = curr.next;
                    this.last = curr;
                    i++;
                }
            }
            prev = prev.next;
            curr = curr.next;
        }
        if(curr.rank>this.last.rank)
            this.last = curr;
    }

    private HeapNode findNewMin(HeapNode root) {
        HeapNode start = root;
        HeapNode min = start;

        do {
            if (min.item.key >= root.item.key) {
                min = root;
            }
            root = root.next;
        } while (root.next != null && root != start);
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

//    public String toString() {
//        if (this.empty()) {
//            return "Heap is empty";
//        }
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("BinomialHeap\n");
//
//        BinomialHeap.HeapNode current = this.last;
//        do {
//            if (current != null) {
//                sb.append("Tree with root: ").append(current.item.key).append("\n");
//                appendTree(sb, current, "", true);
//                current = current.next;
//            }
//        } while (current != this.last && current.next != current);
//
//        return sb.toString();
//    }

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
        HeapNode node = this.last;
        int sum = 0;
        do {
            node = node.next;
            System.out.println(node.rank);
            sum += (int) Math.pow(2, node.rank);
        } while (node != last);
        System.out.println("curr size according to root ranks: " + sum);

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

        public HeapNode(HeapNode node) {
            this.rank = node.rank;
            this.item = node.item;
            this.item.node = this;
            this.child = node.child;
            this.parent = null;
        }

        public void addChild(HeapNode tree) {
            if (this.child == null) {
                this.child = tree;
                this.child.next = this.child;
            } else {
                HeapNode fChild = this.child.next;
                this.child.next = tree;
                tree.next = fChild;
                this.child = tree;
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
