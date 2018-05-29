package test;
import GraphConceptsLibrary.CGraph.GraphConcept;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class HelloJGraphT {

	public static void main(String[] arg) {
		UndirectedGraph<String, DefaultEdge> stringGraph = createStringGraph();
		System.out.println(stringGraph.toString());

		DirectedGraph<URL, DefaultEdge> urlGraph = createURLGraph();
		System.out.println(urlGraph.toString());
		
		ConnectivityInspector ins = new ConnectivityInspector(stringGraph);
		ConnectivityInspector insurl = new ConnectivityInspector(urlGraph);


		System.out.println("Number of vertices " + stringGraph.vertexSet().size() + "<>" + stringGraph.edgeSet().size());
		System.out.println(" Graph Connected ? " + insurl.isGraphConnected());
		
		System.out.println("Number of vertices " + urlGraph.vertexSet().size() + "<>" + urlGraph.edgeSet().size());
		System.out.println(" Graph Connected ? " + insurl.isGraphConnected());
		
		System.out.println("Number of connected sets are " + ins.connectedSets().size());
		System.out.println("Number of connected sets are " + insurl.connectedSets().size());

		Iterator <Set<GraphConcept>> csIterator = ins.connectedSets().iterator();
		

	}

	private static DirectedGraph<URL, DefaultEdge> createURLGraph() {
		DirectedGraph<URL, DefaultEdge> g = new DefaultDirectedGraph<URL, DefaultEdge>(
				DefaultEdge.class);

		try {
			URL amazon = new URL("http://www.amazon.com");
			URL yahoo = new URL("http://www.yahoo.com");
			URL ebay = new URL("http://www.ebay.com");

			g.addVertex(amazon);
			g.addVertex(yahoo);
			g.addVertex(ebay);
			g.addEdge(yahoo, amazon);
			g.addEdge(yahoo, ebay);
			//g.addEdge(amazon, ebay);

			g.addVertex(amazon);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return g;
	}

	private static UndirectedGraph<String, DefaultEdge> createStringGraph() {
		UndirectedGraph<String, DefaultEdge> g = new SimpleGraph<String, DefaultEdge>(
				DefaultEdge.class);
		
		String v1 = "v1";
		String v2 = "v2";
		String v3 = "v3";
		String v4 = "v4";
		
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		
		g.addEdge(v1, v2);
		g.addEdge(v2, v3);
		g.addEdge(v3, v4);
		g.addEdge(v4, v1);
		
		return g;
	}
}
