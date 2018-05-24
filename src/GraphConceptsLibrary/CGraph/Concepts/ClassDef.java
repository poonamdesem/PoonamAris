package GraphConceptsLibrary.CGraph.Concepts;

import GraphConceptsLibrary.CGraph.GraphConcept;

public class ClassDef extends GraphConcept {

	public ClassDef(String name, String value) {
		super("Class: " + name, value, false);
	}
}
