package GraphConceptsLibrary.CGraph.Concepts;

import GraphConceptsLibrary.CGraph.GraphConcept;

public class Field extends GraphConcept {

	private String modifiers;
	private String type;
	private String scope;

	public Field(String name, String value) {
		super("Field: " + name, value, false);
	}

	public String getModifiers() {
		return modifiers;
	}

	public void setModifiers(String modifiers) {
		this.modifiers = modifiers;
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
		s += "\tModifiers: " + modifiers + "\tType: " + type + "\tScope: "
				+ scope;
		return s;
	}
}
