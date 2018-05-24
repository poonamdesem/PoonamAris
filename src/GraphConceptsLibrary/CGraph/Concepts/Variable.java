package GraphConceptsLibrary.CGraph.Concepts;

import GraphConceptsLibrary.CGraph.GraphConcept;

public class Variable extends GraphConcept {

	private String type;
	private String scope;

	public Variable(String name, String value) {
		super("Variable: " + name, value, false);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String toString() {
		String s = super.toString();
		s += "\tType: " + type + "\tScope: " + scope;
		return s;
	}
}
