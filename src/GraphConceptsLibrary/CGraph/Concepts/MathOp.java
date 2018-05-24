package GraphConceptsLibrary.CGraph.Concepts;

public class MathOp extends Action {

	private String OP;
	private String leftSide;
	private String rightSide;

	public MathOp(String op) {
		super("MathOp: " + op);
		OP = op;
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
		String s = super.toString() + "\t" + getValue();
		s += "\tLeftSide: " + leftSide + "\tRightSide: " + rightSide;
		return s;
	}

}
