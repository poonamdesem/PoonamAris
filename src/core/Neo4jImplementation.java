package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jgrapht.graph.AbstractBaseGraph;

import GraphConceptsLibrary.CGraph.ConceptualGraph;
import GraphConceptsLibrary.CGraph.GraphConcept;
import GraphConceptsLibrary.CGraph.GraphEdge;
import au.com.bytecode.opencsv.CSVWriter;

public class Neo4jImplementation {
    static HashMap<String, ConceptualGraph> Graph = new HashMap<String, ConceptualGraph>();
    String nodeCsv = "E:\\PoonamAris\\ArisJava\\bin\\nodes.csv";
	String relationCsv = "E:\\PoonamAris\\ArisJava\\bin\\relation.csv";
	String NodeRelationCsv = "E:\\PoonamAris\\ArisJava\\bin\\NodeRelationCsv.csv";
	private void ConvertAll(String directory) {
        String methodName="";

		File[] files = new File(directory).listFiles();
		for (File file : files) {
		    if (file.isFile()) {
                try {
                	String contents = new String(Files.readAllBytes(Paths.get(directory+"//"+file.getName())));
	               ConceptualGraph cg = ConceptualGraph.loadGraphFromString(contents);
	               Iterator itr = cg.scopeSet.iterator();
		           // System.out.println(cg.getScopeSet());           	
	               while (itr.hasNext()){
	                  methodName =itr.next().toString();
		             //  System.out.println("methodName....==="+methodName);
		              //Graph.put(methodName,cg ); // generate graph as a whole for each methods
	               }
	              // Graph.put(methodName,cg );
	              // System.out.println("methodName....==="+methodName);
	             //  String methodName1= cg.mainMethod.substring(0,cg.mainMethod.indexOf("{")).replace(",", " ");
	             //  System.out.println("methodName1....==="+methodName1);
	               
	               Graph.put(file.getName(), cg);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }		  
		}
    	
    	//edgeFactory = new GraphEdgeFactory();
		//graph = new ConceptualGraph(edgeFactory);
    	try{
	    	File file = new File(nodeCsv);
	    	File relationfile = new File(relationCsv);
	    	File noderelationfile = new File(NodeRelationCsv);

	        if(!file.exists()){
	              file.createNewFile();
	        }
	        if(!relationfile.exists()){
	        	relationfile.createNewFile();
	        }
	        if(!noderelationfile.exists()){
	        	noderelationfile.createNewFile();
	        }
	        BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
	        BufferedWriter bwr = new BufferedWriter(new FileWriter(relationfile.getAbsolutePath()));
	        BufferedWriter bwnr = new BufferedWriter(new FileWriter(noderelationfile.getAbsolutePath()));


	        StringBuilder sb = new StringBuilder();
	        StringBuilder sbr = new StringBuilder();
	        StringBuilder sbnr = new StringBuilder();

	        
	        Set set = Graph.entrySet();
			 // Get an iterator
		      Iterator i = set.iterator();
	         int methodID=1;
		      // Display elements
		      while(i.hasNext()) {
		         Map.Entry me = (Map.Entry)i.next();
		        // System.out.print("key=="+me.getKey() + ": ");
		        // System.out.println("value=="+me.getValue());
		         ToCSV((String) me.getKey(), me.getValue(), methodID++,sb,sbr,bw,bwr,bwnr,sbnr);
		       //  ToNeo4j((String) me.getKey(), me.getValue());

		      }
		      try {
					bw.write(sb.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      try {
					bwr.write(sbr.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    
		      try {
		    	  String ColumnNamesList = "s,v,o";
		           //sbnr.append(ColumnNamesList +"\n");
				//	bwnr.write(ColumnNamesList+"\n"+sbnr.toString());
					bwnr.write(sbnr.toString());

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	        
	        bw.flush();
	        bw.close();
	        bwr.flush();
	        bwr.close();
	        bwnr.flush();
	        bwnr.close();
    	}
	    catch (Exception e) {
	    	e.printStackTrace();
	    } 	
    	JoinCsv jcsv = new JoinCsv();
    	try {
			jcsv.JoinCsvFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}
	
	 public static List<Object> union(List<Object> cols, List<Object> cols2) {
	        final ArrayList result = new ArrayList(cols);
	        result.addAll(cols2);
	        return result;
	    }
  


	private static List<Object> readFile(String fileName) throws IOException
    {
       List<Object> values = new ArrayList<Object>();
       BufferedReader br = new BufferedReader(new  FileReader(fileName));
       
	   String line = null;
	   int i = 0;
	   while ((line = br.readLine()) != null) {
	    	values.add(line);
			System.out.println("linesep==="+line);
	        i++;
	    }
       
	    br.close();
	    return values;
    }


	public static void ToNeo4j(String MethodName, Object object) {
    	 // Create Nodes.
        String Relations[] = { "Condition", "Contains", "Parameter", "Returns", "Depends", "Defines" };
    	for(GraphConcept node : ((AbstractBaseGraph<GraphConcept, GraphEdge>) object).vertexSet()) {
            if(!Arrays.asList(Relations).contains(node.getName())){       
                //String type = node.GetType().ToString().Split('.').Last();
               // System.out.println(node.getName()+" value== "+node.getValue());
            }    		
           // node.nodeRank=;

    	}
    	for(GraphEdge edge : ((AbstractBaseGraph<GraphConcept, GraphEdge>) object).edgeSet()) {
           /* System.out.println("Source===="+edge.getSource());
            System.out.println("Source Id===="+edge.getSource().hashCode());
            System.out.println("Target===="+edge.getTarget().getName());
            System.out.println("Target Id===="+edge.getTarget().hashCode());*/
            int sourceId = edge.getSource().hashCode();
            int targetId = edge.getTarget().hashCode();
          

           // if (Arrays.asList(Relations).contains(edge.getTarget().getName()))
          //  {
            	List<GraphConcept> sourceTargets = new ArrayList<GraphConcept>(); 
            	//Set<GraphEdge> node = ((AbstractBaseGraph<GraphConcept, GraphEdge>) object).edgeSet();
            	for(GraphEdge node : ((AbstractBaseGraph<GraphConcept, GraphEdge>) object).edgeSet()) {
            		if(node.getSource()==edge.getTarget()) {
                       // System.out.println("Target===="+node.getTarget().getName());
                        sourceTargets.add(node.getTarget());
                    }else {
                      //  System.out.println("no");

                    }
            	}

            	  
            	

            //}
          

        }
        /*foreach (GraphConcept node in cg.Vertices)
        {
            if (!Relations.Contains(node.Name))
            {
                string type = node.GetType().ToString().Split('.').Last();
                client.Cypher
                .Create("(node:" + type + " {data})")
                .WithParam("data", new Node { NodeID = node.GetHashCode(), Name = node.Name })
                .ExecuteWithoutResults();
            }
        }*/
    }

	public void ToCSV(String MethodName, Object object2, int methodID, StringBuilder sb, StringBuilder sbr, BufferedWriter bw, BufferedWriter bwr, BufferedWriter bwnr, StringBuilder sbnr) {
        String Relations[] = { "Condition", "Contains", "Parameter", "Returns", "Depends", "Defines" };
        int NodeID = 0;
		/*for(GraphConcept node : ((AbstractBaseGraph<GraphConcept, GraphEdge>) object2).vertexSet()){
            //Type, Name, MethodName, MethodID/GraphID, NodeID
			//NodeID++;
			//System.out.println(NodeID);
          String NodeType = "Noun";
		// System.out.println("value=="+graph+"\n");
           if(Arrays.asList(Relations).contains(node.getName())){          
                NodeType = "Verb";
            }
           if(((ConceptualGraph) object2).getScopeSet().contains(node.getName())){           	  
        	   MethodName =node.getName();    	   
           }
           
            NodeID++;
           sb.append(NodeType);
           sb.append(',');
           sb.append(node.getName());
           sb.append(',');
           sb.append(MethodName);
           sb.append(',');
           sb.append(methodID);
           sb.append(',');
           sb.append(NodeID);
           sb.append('\n');
		} */
		
		// writing into node-relation file
		int IntNodeId = 0;
        int IntNodeIdGVLabelc=0;
        int IntNodeIdGVLabelr=0;
		String MethodName1 = "";
		 MethodName1 = appendDQ(MethodName);

		for(GraphConcept node : ((AbstractBaseGraph<GraphConcept, GraphEdge>) object2).vertexSet()){
			//{"Prpnoun":false,"IntNodeId":10,"GVLabel":"c10","Word":"[unknown]-2","Graphid":2,"nodeOccurrence":""} 
			 if(((ConceptualGraph) object2).getScopeSet().contains(node.getName())){           	  
	        	  MethodName1 =MethodName+" "+node.getName();    	
	 			 MethodName1 = appendDQ(MethodName1);
	           }
	   	 String writestr = "";
           String strPnoun="Prpnoun";
           strPnoun = appendDQ(strPnoun);
           String strIntNodeId="IntNodeId";
           strIntNodeId = appendDQ(strIntNodeId);
           String strGVLabel="GVLabel";
           strGVLabel = appendDQ(strGVLabel);
           String strWord="Word";
           strWord = appendDQ(strWord);
           String strGraphid="Graphid";
           strGraphid = appendDQ(strGraphid);
           String strnodeOccurrence="nodeOccurrence";
           strnodeOccurrence = appendDQ(strnodeOccurrence);
           String strSectionType="SectionType";
           strSectionType = appendDQ(strSectionType);
           String strDocuid="Docuid";
           strDocuid = appendDQ(strDocuid);
           String strRhetCategory="RhetCategory";
           strRhetCategory = appendDQ(strRhetCategory);
           String strBlank="";
           strBlank = appendDQ(strBlank);
           String strGVLabelType="";
           String strWordVal="";
           if(Arrays.asList(Relations).contains(node.getName())){          
               strGVLabelType="r"+IntNodeIdGVLabelr;
               strGVLabelType = appendDQ(strGVLabelType.toString());
               strWordVal=node.getName();
               strWordVal = appendDQ(strWordVal);
              //  writestr = "\"{"+appendDQ(strPnoun)+":"+Prpnoun+","+appendDQ(strIntNodeId)+":"+IntNodeId+"}\"";
                writestr = "\"{"+appendDQ(strWord)+":"+appendDQ(strWordVal)+","
         						+appendDQ(strDocuid)+":"+appendDQ(MethodName1)+","
                        		+appendDQ(strGraphid)+":"+methodID+","
                        		+appendDQ(strIntNodeId)+":"+IntNodeId+","
                        		+appendDQ(strGVLabel)+":"+appendDQ(strGVLabelType)+","
                				+appendDQ(strSectionType)+":"+0+","
                				+appendDQ(strnodeOccurrence)+":"+appendDQ(strBlank)+","
                        		+appendDQ(strRhetCategory)+":"+0
                		+"}\"";
                IntNodeIdGVLabelr++;

           }else {
           	 strGVLabelType="c"+IntNodeIdGVLabelc;
           	 strGVLabelType = appendDQ(strGVLabelType.toString());
             strWordVal=node.getName();
             strWordVal = appendDQ(strWordVal);
             writestr = "\"{"+appendDQ(strWord)+":"+appendDQ(strWordVal)+","
     				+appendDQ(strDocuid)+":"+appendDQ(MethodName1)+","
             		+appendDQ(strGraphid)+":"+methodID+","
             		+appendDQ(strIntNodeId)+":"+IntNodeId+","
            		+appendDQ(strGVLabel)+":"+appendDQ(strGVLabelType)+","
     				+appendDQ(strSectionType)+":"+0+","
     				+appendDQ(strnodeOccurrence)+":"+appendDQ(strBlank)+","
             		+appendDQ(strPnoun)+":"+"false"


     		+"}\"";
             IntNodeIdGVLabelc++;
           }
           node.nodeRank=IntNodeId;
           IntNodeId++;
           sbnr.append(writestr);        
           sbnr.append('\n');
		}	
    	for(GraphEdge edge : ((AbstractBaseGraph<GraphConcept, GraphEdge>) object2).edgeSet()) {
    		System.out.println("graphId= "+methodID+"Source Id=  "+edge.getSource().nodeRank+" Traget id= "+edge.getTarget().nodeRank );
    		System.out.println("graphId= "+methodID+"Source Id=  "+edge.getSource().getName()+" Traget id= "+edge.getTarget().getName() );
    		/*sbnr.append(",");
    		sbnr.append(methodID);
    		sbnr.append(",");
    		sbnr.append(edge.getSource().nodeRank);
    		sbnr.append(",");
    		sbnr.append(edge.getTarget().nodeRank);   	
    		sbnr.append('\n');*/
    		sbr.append(methodID);
    		sbr.append(",");
    		sbr.append(edge.getSource().nodeRank);
    		sbr.append(",");
    		sbr.append(edge.getTarget().nodeRank);   	
    		sbr.append('\n');
    	}
			        
}
	private static String appendDQ(String str) {
	    return "\"" + str + "\"";
	}
	@SuppressWarnings("resource")
	public static void main(String[] arg) throws IOException {
		
		Neo4jImplementation neo4j = new Neo4jImplementation();	
		neo4j.ConvertAll("E:\\PoonamAris\\ArisJava\\bin\\JavaCorpus");
	 
	}

}
