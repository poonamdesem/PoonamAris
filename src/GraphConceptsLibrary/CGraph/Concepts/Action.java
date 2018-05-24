package GraphConceptsLibrary.CGraph.Concepts;

import GraphConceptsLibrary.CGraph.GraphConcept;

public class Action extends GraphConcept {

	public Action() {
		super("", false);
	}

	public Action(String name) {
		super(name, false);
	}

	public Action(String name, String value) {
		super(name, value, false);
	}
}
