import java.util.LinkedList;

class FPTree{
	Double value;
	int count;
	boolean hasSibling;
	boolean isLeaf;
	LinkedList<FPTree> parent = new LinkedList<FPTree>();
	LinkedList<FPTree> children = new LinkedList<FPTree>();
	
	public FPTree(Double value){
		this.value = value;
		this.count = 1;
	}
}