package GraphConceptsLibrary.CGraph.Concepts;

import GraphConceptsLibrary.CGraph.GraphConcept;

public class EnumDef extends GraphConcept {

	public EnumDef(String name, String value) {
		super("Enum: " + name, value, false);
	}
}
