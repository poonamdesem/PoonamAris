package GraphConceptsLibrary.CGraph;

import org.jgrapht.EdgeFactory;

public class GraphEdgeFactory implements EdgeFactory<GraphConcept, GraphEdge> {

	@Override
	public GraphEdge createEdge(GraphConcept g1, GraphConcept g2) {
		GraphEdge edge = new GraphEdge(g1, g2);
		return edge;
	}

}
