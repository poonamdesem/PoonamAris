package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.AbstractBaseGraph;

import GraphConceptsLibrary.CGraph.ConceptualGraph;
import GraphConceptsLibrary.CGraph.GraphConcept;
import GraphConceptsLibrary.CGraph.GraphEdge;

public class VectoData {
    int GraphNum = 0;

	static HashMap<String, ConceptualGraph> Graph = new HashMap<String, ConceptualGraph>();
	String vectorDataCsv = "E:\\PoonamAris\\ArisJava\\bin\\vector_data.csv";
	private void ConvertAll(String directory) throws IOException {
        String methodName="";
		File[] files = new File(directory).listFiles();
		for (File file : files) {
		    if (file.isFile()) {
                try {
                	
                String contents = new String(Files.readAllBytes(Paths.get(directory+"//"+file.getName())));
	            //  System.out.println(contents);
                ConceptualGraph cg = ConceptualGraph.loadGraphFromString(contents);
	               Iterator itr = cg.scopeSet.iterator();
	               while (itr.hasNext()){
	                  methodName =itr.next().toString();
		            }	               
	               Graph.put(file.getName(), cg);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }		  
		}
		File file = new File(vectorDataCsv);
	    if(!file.exists()){
	      file.createNewFile();
	    }
	      BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
	        StringBuilder sb = new StringBuilder();
	        Set set = Graph.entrySet();
			 // Get an iterator
		      Iterator i = set.iterator();
	         int methodID=1;
		      // Display elements
		      while(i.hasNext()) {
		         Map.Entry me = (Map.Entry)i.next();
		        // System.out.print("key=="+me.getKey() + ": ");
		        // System.out.println("value=="+me.getValue());
		         ToCSV((String) me.getKey(), me.getValue(), methodID++,sb,bw);
		      }
		      String ColumnNamesList = "GraphNum,NoRelations,NoConcepts,NoUniqueRealtions,NoConnectedComponents,NoNodesinLargestConnectedComponent,Condition,Contains,Parameter,Depends,Returns,Sum";
		      bw.write(ColumnNamesList +"\n");
			bw.write(sb.toString());       
	        bw.flush();
	        bw.close();
	      }   	
	   	
	
	public void ToCSV(String MethodName, Object object2, int methodID, StringBuilder sb, BufferedWriter bw) {
        String Relations[] = { "Condition", "Contains", "Parameter", "Returns", "Depends", "Defines" };
        int conceptCount=0;
        int relationCount=0;
        int relatedNodeCount=0;
        int NodesinCount=0;


        int containsCount = 0,conditionCount = 0,parameterCount = 0,returnsCount=0,dependsCount = 0,definesCount = 0;
		for(GraphConcept node : ((AbstractBaseGraph<GraphConcept, GraphEdge>) object2).vertexSet()){
            //Type, Name, MethodName, MethodID/GraphID, NodeID
			//NodeID++;
			if(node.isRelation) {
				relatedNodeCount++;
			}
			//System.out.println("Number of connected sets are " + ins.connectedSets().size());

          String NodeType = "Noun";
           if(Arrays.asList(Relations).contains(node.getName())){  
        	   relationCount++;
   			//System.out.println(node.getName());
        	   if(node.getName()=="Condition") {
        		   conditionCount++;
        	   }
        	   if(node.getName()=="Contains") {
        		   containsCount++;
        	   }
        	   if(node.getName()=="Parameter") {
        		   parameterCount++;
        	   }
        	   if(node.getName()=="Returns") {
        		   returnsCount++;
        	   }
        	   if(node.getName()=="Depends") {
        		   dependsCount++;
        	   }
        	   if(node.getName()=="Defines") {
        		   definesCount++;
        	   }
               // NodeType = "Verb";
                
            }
           else {
        	   conceptCount++;
           }
           if(((ConceptualGraph) object2).getScopeSet().contains(node.getName())){           	  
        	   MethodName =node.getName();    	   
           }

		} 	
		GraphNum++;
		System.out.println("method name== "+MethodName +" "+GraphNum);

	int sum=relationCount+conceptCount+relatedNodeCount+relatedNodeCount+NodesinCount+conditionCount+containsCount+parameterCount+dependsCount+returnsCount;
		String sumString=Integer.toString(relationCount)+Integer.toString(conceptCount)+Integer.toString(relatedNodeCount)+Integer.toString(relatedNodeCount)+Integer.toString(NodesinCount)+Integer.toString(conditionCount)+Integer.toString(containsCount)+Integer.toString(parameterCount)+Integer.toString(dependsCount)+Integer.toString(returnsCount);
		sb.append(GraphNum);
        sb.append(',');
        sb.append(relationCount);
        sb.append(',');
        sb.append(conceptCount);
        sb.append(',');
        sb.append(relatedNodeCount);
        sb.append(',');
        sb.append(relatedNodeCount);
        sb.append(',');
        sb.append(NodesinCount);
        sb.append(',');
        sb.append(conditionCount);
        sb.append(',');
        sb.append(containsCount);
        sb.append(',');
        sb.append(parameterCount);
        sb.append(',');
        sb.append(dependsCount);
        sb.append(',');
        sb.append(returnsCount);
        sb.append(',');
        sb.append(sum);
        sb.append(',');
        sb.append(sumString);
        sb.append('\n');
		//ConnectivityInspector ins = new ConnectivityInspector((ConceptualGraph) object2);
		//System.out.println("Number of connected sets are " + ins.connectedSets().size());


}
	public static void main(String[] args) throws IOException {
		VectoData vd = new VectoData();	
		//JavaCorpus
		//java_corpus
		vd.ConvertAll("E:\\PoonamAris\\ArisJava\\bin\\java_corpus");
	}

}
