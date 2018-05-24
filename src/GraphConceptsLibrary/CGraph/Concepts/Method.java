package GraphConceptsLibrary.CGraph.Concepts;

import GraphConceptsLibrary.CGraph.GraphConcept;

public class Method extends GraphConcept {

	private String returnType;
	private String modifiers;
	private String parameterList;

	public Method(String name, String value) {
		super("Method: " + name, value, false);
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getModifiers() {
		return modifiers;
	}

	public void setModifiers(String modifiers) {
		this.modifiers = modifiers;
	}

	public String getParameterList() {
		return parameterList;
	}

	public void setParameterList(String parameterList) {
		this.parameterList = parameterList;
	}

	public String toString() {
		String s = super.toString();
		s += "\tModifiers: " + modifiers + "\tReturnType: " + returnType
				+ "\tParameterList: " + parameterList;
		return s;
	}
}
