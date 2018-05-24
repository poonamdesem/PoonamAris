package GraphConceptsLibrary.CGraph.Concepts;

public class AssignOp extends Action {

	private String leftSide;
	private String rightSide;

	public AssignOp() {
		super("Assign:*");
	}

	public String getLeftSide() {
		return leftSide;
	}

	public void setLeftSide(String leftSide) {
		this.leftSide = leftSide;
	}

	public String getRightSide() {
		return rightSide;
	}

	public void setRightSide(String rightSide) {
		this.rightSide = rightSide;
	}

	public String toString(){
		String s = super.toString() + "\t" + getValue();
		s += "\tLeftSide: " + leftSide + "\tRightSide: " + rightSide;
		return s;
	}
}
