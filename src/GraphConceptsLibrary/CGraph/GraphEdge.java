package GraphConceptsLibrary.CGraph;


public class GraphEdge {

	GraphConcept source, target;
	private String id;

	public GraphEdge(GraphConcept source, GraphConcept target) {
		this.source = source;
		this.target = target;
	}

	public void setID(String id) {
		this.id = id;
		// notify this id property has been changed (related to GUI)
	}

	public String getID() {
		return id;
	}

	public GraphConcept getSource() {
		return source;
	}

	public GraphConcept getTarget() {
		return target;
	}

	//@Override
	public String toString() {
		return "GraphEdge [source=" + source + ", target=" + target + ", id="
				+ id + "]";
	}
}
