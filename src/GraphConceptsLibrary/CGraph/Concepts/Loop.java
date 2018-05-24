package GraphConceptsLibrary.CGraph.Concepts;

import GraphConceptsLibrary.CGraph.GraphConcept;

public class Loop extends GraphConcept {

	private String type;
	private String conditions;

	public Loop() {
		super("Loop", false);
	}

	public Loop(String name) {
		super("Loop: " + name, false);
	}

	public Loop(String name, String value) {
		super("Loop: " + name, value, false);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public String toString() {
		String s = super.toString();
		s += "\tType: " + type + "\tCondition: " + conditions;
		return s;
	}
}
