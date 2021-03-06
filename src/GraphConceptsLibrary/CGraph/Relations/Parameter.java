package GraphConceptsLibrary.CGraph.Relations;

import GraphConceptsLibrary.CGraph.GraphConcept;

public class Parameter extends GraphConcept {

	private String inConcept;
	private String outConcept;

	public Parameter() {
		super("Parameter", true);
	}

	public Parameter(String inConcept, String outConcept) {
		super("Parameter", true);
		this.inConcept = inConcept;
		this.outConcept = outConcept;
	}

	public String getInConcept() {
		return inConcept;
	}

	public void setInConcept(String inConcept) {
		this.inConcept = inConcept;
	}

	public String getOutConcept() {
		return outConcept;
	}

	public void setOutConcept(String outConcept) {
		this.outConcept = outConcept;
	}
	
	public String toString(){
		String s = super.toString();
		s += "\tInConcept: " + inConcept + "\tOutConcept: " + outConcept;		
		return s;
	}
}
