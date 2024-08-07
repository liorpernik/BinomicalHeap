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
        double sum =  Math.pow(2, node.rank);
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
//        while (node.next != null && node != start.next) {
//            if (min.item.key > node.item.key) {
//                min = node;
//            }
//            node = node.next;
//        }
        min = findNewMin(node);
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

        if(this.min==null || key < this.min.item.key){
            this.min = node;
        }
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

//            do {
//                HeapNode nextChild = child.next;
//                if (child.item.key < newMin.item.key) {
//                    newMin = child;
//                }
//                child = nextChild;
//            } while (child != minNode.child);

            newMin = findNewMin(child);

            this.last = minNode.child;
            this.min = newMin;
            this.size -= 1;
            return;
        }

        boolean isMinLast = this.last == this.min;
        HeapNode current = this.min.next;
        newMin = this.min.next;
        HeapNode nextCurr = null;
        do {
            if (current.next == minNode) {

                current.next = minNode.next;
//                if(isMinLast) current =current.next;
            }
//            nextCurr = ;
            if (current.item.key < newMin.item.key) {
                newMin = current;
                newMin.next = current.next;
            }
            current = current.next;
        } while (current != this.min.next);

//        if (isMinLast) {
//            current.next = minNode.next;
//            this.last = current;
//        }
        this.size -=(int)Math.pow(2, minNode.rank);// size_of_ranks(current);

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
        }else
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
        if (heap2.size == 0) return;
        if (this.size == 0) {
            this.last = heap2.min;
            update_fields(heap2.min, 1);
            return;
        }
        this.size += heap2.size;
        HeapNode curr = this.last.next, melded = heap2.last.next, temp = melded, start = this.last.next, nextroot, prev = this.last, minChild;//thisstart=this.last.next;
        int heap2Trees=heap2.numTrees(),i=0;
        do {
            if(melded==temp && curr.next != melded)
                temp = melded.next;


            if (melded.rank < curr.rank) {
                HeapNode copy = new HeapNode(melded);
                prev.next = copy;
                copy.next = curr;

//                prev = prev.next;
                curr = copy;
            } else if (melded.rank > curr.rank) {
                if (curr == this.last) {
                    prev = curr;
                    nextroot = curr.next;
                    curr.next = melded;
                    curr = melded;
                    heap2.last.next = nextroot;
                    this.last = heap2.last;
                    if(heap2.min.item.key < this.min.item.key){
                        this.min = heap2.min;
                    }
//                    melded.next = nextroot;
                    break;
                }else {

                    prev = prev.next;
                    curr =curr.next;
//                    curr.next = curr.next.rank < melded.rank ?  : curr.nextmelded;
                }
                continue;

            }else if (melded.rank == curr.rank && melded != curr) {

                nextroot = curr.next;

                if (melded.item.key < curr.item.key) {

//                    curr.next = null;
//                    swapNodes(melded, curr);
//                    melded.addChild(curr);

//                    HeapNode copy = new HeapNode(melded);
//                    copy.next = nextroot;
//                    curr = copy;
                    curr.next = melded;
                    melded.next = nextroot;
                    nextroot = melded;
//                    nextroot = curr;
//                    curr = melded;
//                    prev = prev.next;

//                    curr.item.switchItems(melded.item);
                    //find melded childs min if < curr.key than switch
//                    if(melded.child != null){
//                        minChild = findNewMin(melded.child);
//                        if( minChild.item.key< melded.item.key){
//                            melded.item.switchItems(minChild.item);
//                        }
//                    }
                }
                else{

                    links++;
                    curr.addChild(melded);
                }


                if (curr != nextroot) {
                    if (curr.rank == nextroot.rank) {
                        prev.next = nextroot;
                        melded = curr; //melded.parent != null ? melded.parent : melded;
                        curr = nextroot;

                        continue;
                    }else
                        {
                            prev = curr;
                            prev.next = nextroot;
                            curr = nextroot;
                    }
                } }
            if (curr.item.key <= this.min.item.key)
                this.min = curr;
//          else if(melded.item.key <= this.min.item.key){
//                this.min = melded;
//            }

//        prev = prev.next;
            melded = temp;

//            temp = temp.next;
            i++;
    } while(i < heap2Trees);
//     heap2.last.next = heap2.last;
//    this.last = this.last.rank > Math.max(melded.rank,prev.rank) ? this.last : melded.rank > prev.rank ? melded : prev;
//    start.next = this.last.next;
//    this.last.next = start;
//        start = prev;
//        prev = prev.next;
//        do {
//            if (prev.rank > this.last.rank) {
//                this.last = prev;
//            }
//            if(prev.rank < this.last.next.rank){
//                this.last.next = prev;
//            }
//            prev = prev.next;
//        } while (prev != start && prev.next != prev);

    }

private void clone(BinomialHeap heap2) {
    this.last = heap2.last;
    this.min = heap2.min;
    this.size = heap2.size;
}

private HeapNode findNewMin(HeapNode root){
    HeapNode min = root, start = size != 1 ? root.next : root;
    while (root.next != null && root != start.next) {
        if (min.item.key > root.item.key) {
            min = root;
        }
        root = root.next;
    }
    return min;
}

//
//    private void meld2Roots(HeapNode first, HeapNode second) {
//        links++;
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
    public void swapNodes(HeapNode node1, HeapNode node2){
        // Swap the parent pointers
//        HeapNode tempParent = node1.parent;
//        node1.parent = node2.parent;
//        node2.parent = tempParent;

        // Swap the child pointers
        HeapNode tempChild = node1.child;
        node1.child = node2.child;
        node2.child = tempChild;

        node1.item.switchItems(node2.item);
        // Swap the next pointers
//        HeapNode tempNext = node1.next;
//        node1.next = node2.next;
//        node2.next = tempNext;

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
        } while (current != this.last && current.next != current);

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

public void print_r() {
    {
        int s;
        if (this.last == null) {
            return;
        }
        if (this.last.rank == 0) {
            s = 1;
        } else if (this.last.rank == 1) {
            s = 2;
        } else if (this.last.rank == 2) {
            s = 3;
        } else {
            s = (int) Math.pow(2, this.last.rank - 1);
        }
        int[][][] answer = new int[this.numTrees()][s][s];
        BinomialHeap.HeapNode curr_tree = this.last.next;
        for (int k = 0; k < this.numTrees(); k++) {
            Set<HeapNode> dic = new HashSet<>();
            this.print_rec(curr_tree, 0, 0, dic, answer, k);
            curr_tree = curr_tree.next;
        }
        int x = 0;
        for (int i = 0; i < answer[x].length; i++) {
            for (int j = 0; j < answer[x].length; j++) {
                if (answer[x][i][j] != 0) {
                    System.out.print(answer[x][i][j] + " ");
                } else {
                    System.out.print("  ");
                }
                if (x == answer.length - 1 && j == answer[x].length - 1) {
                    break;
                }
                if (j == answer[x].length - 1) {
                    if (x + 1 < answer.length) {
                        x += 1;
                    }
                    j = -1;
                }

            }
            System.out.println();
            x = 0;
        }
    }
}

public void print_rec(BinomialHeap.HeapNode first, int depth, int x, Set<BinomialHeap.HeapNode> dic, int[][][] answer, int num_in_row) {
    if (dic.contains(first)) {
        return;
    }
    dic.add(first);
    answer[num_in_row][depth][x] = first.item.key;
    if (depth != 0) {
        print_rec(first.next, depth, x + 1, dic, answer, num_in_row);
    }
    if (first.child != null) {
        print_rec(first.child.next, depth + 1, x, dic, answer, num_in_row);
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

    public HeapNode(HeapNode node){
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
