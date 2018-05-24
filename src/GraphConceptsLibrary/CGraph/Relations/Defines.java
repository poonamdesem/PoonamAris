package GraphConceptsLibrary.CGraph.Relations;

import GraphConceptsLibrary.CGraph.GraphConcept;

public class Defines extends GraphConcept {

	public String inConcept;
	public String outConcept;

	public Defines() {
		super("Defines", true);
	}

	public Defines(String inConcept, String outConcept) {
		super("Defines", true);
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
