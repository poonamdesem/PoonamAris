package GraphConceptsLibrary.CGraph;

import java.util.ArrayList;
import java.util.Iterator;

public class GraphMetrics {
	 public static double AverageDegree(ConceptualGraph graph)
     {
		 System.out.println(graph.edgeSet().size() + " "+ graph.getVertex().size());
       //  return (double)(2 * graph.EdgeCount) / (double)graph.VertexCount;
		 return (double)(2 * graph.edgeSet().size()) / (double)graph.getVertex().size();
     }
	 public static double ClusteringCoefficient(ConceptualGraph graph)
     {
        // calculateVerticesClusteringCoefficient(graph);
         //A graph’s average clustering coefficient is the average clustering coefficient over all the nodes.
         double sum = 0;
 		for(GraphConcept v : graph.getVertex())
             sum += v.clusteringCoefficient;
         return sum / graph.getVertex().size();     
 		
     }
	 /*private static void calculateVerticesClusteringCoefficient(ConceptualGraph graph) {
		 for (GraphConcept v : graph.getVertex())
         {
             Object v;
			Object a;
			var neighbours = graph.incomingEdgesOf((GraphConcept) v).Select(a => a.source).Union(graph.OutEdges(v).Select(a => a.Target));
             int count = 0;

             if (neighbours != null && neighbours.Count() >= 2)
             {
                 // count the number of existing edges between the neighbours of v
                 foreach (var n in neighbours)
                 {
                     var currentNeighbours = graph.InEdges(n).Select(a => a.Source).Union(graph.OutEdges(n).Select(a => a.Target));

                     foreach (var other in neighbours)
                     {
                         if (n != other)
                         {
                             if (currentNeighbours.Contains(other))
                                 count++;
                         }
                     }
                 }

                 v.ClusteringCoefficient = (2 * count) / (neighbours.Count() * (neighbours.Count() - 1));

             }
             else
                 v.ClusteringCoefficient = 0;


         }		
	}*/

     private static double QuadraticError = 0.001;
     private static double dampingFactor = 0.85;
     private static int IterationLimit = 50;
     
     private static double getConvergence(ConceptualGraph graph)
     {
         double sum = 0.0;
  		for(GraphConcept v : graph.getVertex())
         {
             sum += v.nodeRank;
         }
         //   Console.WriteLine("Sum = " + sum);

         return sum;
     }
     private static void normalizeValues(ConceptualGraph graph)
     {
         double min = 1.0;
         double max = 0.0;

         //calculate min and max
   		for(GraphConcept v : graph.getVertex())
         {
             if (v.nodeRank < min)
                 min = v.nodeRank;
             else
                 if (v.nodeRank > max)
                     max = v.nodeRank;
         }

         // normalize values
   		for(GraphConcept v : graph.getVertex())
         {
             v.nodeRank = (int) ((double)(v.nodeRank - min) / (double)(max - min));
         }

     }
     private static void normalizeValuesToOne(ConceptualGraph graph)
     {
         double sum = 0.0;
    	for(GraphConcept v : graph.getVertex())
         {
             sum += v.nodeRank;
         }

         // normalize values
    	for(GraphConcept v : graph.getVertex())
         {
             v.nodeRank = (int) (v.nodeRank / sum);
         }

     }

	 public static void assignInitialNodeRanks(ConceptualGraph graph)
     {
         //foreach (var v in graph.Vertices)
          //   v.NodeRank = (double)((double)1 / (double)graph.Vertices.Count());
		for(GraphConcept v : graph.getVertex()){
			v.nodeRank = (int)((double)1 / (double)graph.getVertex().size());
			//System.out.println("node rank ="+v.nodeRank);
		}
	 }
	  public static void calculateActualNodeRanks(ConceptualGraph graph)
      {
          assignInitialNodeRanks(graph);

          int iteration = 0;
          double last = Double.MAX_VALUE;
          while ((double)(last - getConvergence(graph)) > QuadraticError && iteration < IterationLimit)
          {
              last = getConvergence(graph);
        	 // System.out.println("vertex==="+graph.getVertex());

      		for(GraphConcept u : graph.getVertex()){
              {
                  //NR(u) = SUM (NR(v) / OutDegree(v)) // foreach v in IN(u)
                  double sum = 0.0;
// Need to work on below commented lines
            	  GraphEdge a = null;
                  //var IN = graph.InEdges(u).Select(a => a.Source);
            	  boolean INCheck = graph.incomingEdgesOf(u).iterator().hasNext();
            	  if (INCheck!=false) {
            	//  System.out.println("in==="+graph.incomingEdgesOf(u).iterator().next().getSource());
            	  GraphConcept IN =  graph.incomingEdgesOf(u).iterator().next().getSource();
                	 sum += (IN.nodeRank / graph.outgoingEdgesOf((GraphConcept) IN).size());
            	  }
				/*for (GraphConcept v :IN)
                {
               	 sum += (v.nodeRank / graph.outgoingEdgesOf((GraphConcept) v).size());
				}*/
                 

                  u.nodeRank = (int) ((((double)1 - dampingFactor) / (double)graph.getVertex().size()) + dampingFactor * sum);
                  //     u.NodeRank =  sum;
              }

              normalizeValues(graph);
              
              //normalizeValuesToOne(graph);

              // normalize values
            //  for(GraphConcept v : graph.getVertex()){              {
                //  v.nodeRank = (((double)1 - dampingFactor) / (double)graph.getVertex().size()) + dampingFactor * v.nodeRank;
             // }

              iteration++;
          }

          // the values have converged or the iteration limit has been exceeded
          graph.ConvergeValue = getConvergence(graph);
      }
    }
	
}
