package GraphConceptsLibrary.CGraph.Concepts;

public class MethodCall extends Action {

	private String arguments;

	public MethodCall(String name) {
		super("MethodCall: " + name);
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public String toString() {
		String s = super.toString();
		s += "\tArguments: " + arguments;
		return s;
	}
}
