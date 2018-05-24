package GraphConceptsLibrary.CGraph.Concepts;

public class CompareOp extends Action {

	private String OP;
	private String leftSide;
	private String rightSide;

	public CompareOp(String oP) {
		super("CompareOp: " + oP);
		OP = oP;
	}

	public String getOP() {
		return OP;
	}

	public void setOP(String oP) {
		OP = oP;
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
		String s = super.toString();
		s += "\tLeftSide: " + leftSide + "\tRightSide: " + rightSide;
		return s;
	}
}
