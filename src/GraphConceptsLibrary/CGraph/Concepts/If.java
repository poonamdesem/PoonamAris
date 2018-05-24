package GraphConceptsLibrary.CGraph.Concepts;

import GraphConceptsLibrary.CGraph.GraphConcept;

public class If extends GraphConcept {

	public If() {
		super("If", false);
	}

	public If(String name) {
		super("If: " + name, false);
	}

	public If(String name, String value) {
		super("If: " + name, value, false);
	}	
}
