
/**
 * BinomialHeap
 *
 * An implementation of binomial heap over positive integers.
 *
 */
public class BinomialHeap
{
	public int size;
	public HeapNode last;
	public HeapNode min;

	public BinomialHeap(HeapNode node){
		this.min = node;
		this.last = node;
		this.size = Math.pow(2,node.rank);
	}
	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapItem.
	 *
	 */
	public HeapItem insert(int key, String info) 
	{    
		HeapNode node = new HeapNode();
		HeapItem item = new HeapItem(node, key, info);
		node.item = item;
		BinomialHeap heap0 = new BinomialHeap(node);
		this.meld(node);

		return item;
	}

	/**
	 * 
	 * Delete the minimal item
	 *
	 */
	public void deleteMin()
	{
		int curr=this.min.item.key,min=Inf;
		for (HeapNode i = this.min.next; i.item.key!=curr  ; this.min.next) {
			if(i.item.key<min){

				min=i;
			}

		}
//		HeapNode children = this.min.child.next, currNodes = this.min.next;
//		this.min.child.next = null;
//		BinomialHeap nextMin = new BinomialHeap(currNodes), minChild = new BinomialHeap(children);
//		while(currNodes != this.min && minChild){
//			nextMin.meld(minChild);
//
//			nextMin.last.next = currNodes.next;
//
//			nextMin = new BinomialHeap(currNodes.next);
//			minChild = new BinomialHeap(children.next);
//
//			currNodes = currNodes.next;
//			children = children.next;
//		}

		this.min=min;
		return;

	}

	/**
	 * 
	 * Return the minimal HeapItem, null if empty.
	 *
	 */
	public HeapItem findMin()
	{

		return this.empty() ? null : this.min;
	} 

	/**
	 * 
	 * pre: 0<diff<item.key
	 * 
	 * Decrease the key of item by diff and fix the heap. 
	 * 
	 */
	public void decreaseKey(HeapItem item, int diff) 
	{    
		return;
	}

	/**
	 * 
	 * Delete the item from the heap.
	 *
	 */
	public void delete(HeapItem item) 
	{
		this.decreaseKey(item, Inf);
		this.deleteMin();

		return;
	}

	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(BinomialHeap heap2)
	{
		HeapNode node = heap2.min,tempRoot=node;
		HeapNode curr= this.last.next,heapNRoot=curr;
		while(curr.rank == node.rank){

			if (node.item.key < curr.item.key){
				curr.parent = node;
				node.child.next = curr;
				tempRoot=node.next;
				curr.next = node.child;
				node.child = curr;

			}else {
				node.parent = curr;
				curr.child.next = node;
				tempRoot=curr.next;
				node.next = curr.child;
				curr.child = node;
			}
			node = tempRoot;
			node.rank++;
			curr = heapNRoot.next;
			node.next = curr;
			heapNRoot = heapNRoot.next;
		}
		if(this.last.rank < node.rank){
			this.last = node;
		}
		return;
	}

	/**
	 * 
	 * Return the number of elements in the heap
	 *   
	 */
	public int size()
	{
		return this.size;
	}

	/**
	 * 
	 * The method returns true if and only if the heap
	 * is empty.
	 *   
	 */
	public boolean empty()
	{
		return this.size==0;
	}

	/**
	 * 
	 * Return the number of trees in the heap.
	 * 
	 */
	public int numTrees()
	{
		if(this.empty()){
			return 0;
		}
		int count=1;
		for (HeapNode i = this.min.next; i.item.key!=this.min.item.key  ; this.min.next) {
			count++;
		}
		return count;
	}

	/**
	 * Class implementing a node in a Binomial Heap.
	 *  
	 */
	public static class HeapNode{
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
	 *  
	 */
	public static class HeapItem{
		public HeapNode node;
		public int key;
		public String info;

		public HeapItem(HeapNode node, int key, String info){
			this.info = info;
			this.key = key;
			this.node = node;
		}
	}
}
