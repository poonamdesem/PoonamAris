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

import org.jgrapht.graph.AbstractBaseGraph;

import GraphConceptsLibrary.CGraph.ConceptualGraph;
import GraphConceptsLibrary.CGraph.GraphConcept;
import GraphConceptsLibrary.CGraph.GraphEdge;

public class SubjectVerbObject {
		int graphId = 0;

	static HashMap<String, ConceptualGraph> Graph = new HashMap<String, ConceptualGraph>();
	String SvoDataCsv = "E:\\PoonamAris\\ArisJava\\bin\\svo_data.csv";	
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
		File file = new File(SvoDataCsv);
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
		      String ColumnNamesList = "ClassGraphNum,MethodGraphNumber,MethodName,Subject,Verb,Object";
		      bw.write(ColumnNamesList +"\n");
			bw.write(sb.toString());       
	        bw.flush();
	        bw.close();
	      }   		   	
	
	public void ToCSVOld(String className, Object object2, int methodID, StringBuilder sb, BufferedWriter bw) {
        String Relations[] = { "Condition", "Contains", "Parameter", "Returns", "Depends", "Defines" };
    	String subject=null;
    	String verb=null;
    	String object=null; 
		int MethodGraphNum = 1;
    	String MethodName1=className;   
    	String MethodGraphNumber=Integer.toString(methodID);
    	String CsvFolder = "E:\\PoonamAris\\ArisJava\\bin\\CsvFolder";
    	for(GraphConcept node : ((AbstractBaseGraph<GraphConcept, GraphEdge>) object2).vertexSet()){
    		 if(((ConceptualGraph) object2).getScopeSet().contains(node.getName())){           	  
	        	  MethodName1 =className+" "+node.getName();    
	        	  MethodGraphNumber =Integer.toString(methodID)+"_"+ Integer.toString(MethodGraphNum);
	        	  MethodGraphNum++;
	        	  String[] methodNameArr = node.getName().split(":");
	        	 int dot = className.lastIndexOf(".");
	  	    	File csvfile = new File(CsvFolder+"//"+className.substring(0,dot)+"_"+methodNameArr[1].trim()+".csv");
		        System.out.println("hi");
	  	    	if(!csvfile.exists()){
	  	    		try {
						csvfile.createNewFile();
						} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }

	           }
        	if(node.isRelation) {
            	String[] tokens = node.toString().split("\t");
            	if(tokens.length>=3) {
            		for(int i=0; i<tokens.length;i++) {
                        if(Arrays.asList(Relations).contains(tokens[i])){

                        }
                        if(Arrays.asList(Relations).contains(tokens[i])){
                    		verb = tokens[i];
                        }
                        if(tokens[i].startsWith("InConcept")){
                        	String[] tokensInConcept = tokens[i].split("InConcept:");
                    		subject = tokensInConcept[1];
                        }
                        if(tokens[i].startsWith("OutConcept")){
                        	String[] tokensOutConcept = tokens[i].split("OutConcept:");
                    		object = tokensOutConcept[1];
                        }
            		}
            		  sb.append(methodID);
                      sb.append(',');
                      sb.append(MethodGraphNumber);
                      sb.append(',');                      
            		  sb.append(MethodName1);
                      sb.append(',');
            		  sb.append(subject);
                      sb.append(',');
                      sb.append(verb);
                      sb.append(',');
                      sb.append(object);
                      sb.append("\n");  	
            	}
        	}

    	}
    	methodID++;
}
	public void ToCSV(String className, Object object2, int methodID, StringBuilder sb, BufferedWriter bw) {
        String Relations[] = { "Condition", "Contains", "Parameter", "Returns", "Depends", "Defines" };
    	String subject=null;
    	String verb=null;
    	String object=null; 
		int MethodGraphNum = 0;

    	String classMethodName=className;   
    	String  methodName=null;
    	String MethodGraphNumber=Integer.toString(methodID);
    	String CsvFolder = "E:\\PoonamAris\\ArisJava\\bin\\CsvFolder";
    	File csvfile = null;
    	BufferedWriter	cbw = null;
    	StringBuilder csb=null;
    	for(GraphConcept node : ((AbstractBaseGraph<GraphConcept, GraphEdge>) object2).vertexSet()){
    		 if(((ConceptualGraph) object2).getScopeSet().contains(node.getName())){           	  
    			 classMethodName =className+" "+node.getName();    
	        	  MethodGraphNumber =Integer.toString(methodID)+"_"+ Integer.toString(MethodGraphNum);
	        	  MethodGraphNum++;
	        	  String[] methodNameArr = node.getName().split(":");
	        	 int dot = className.lastIndexOf(".");
	  	    	 csvfile = new File(CsvFolder+"//"+className.substring(0,dot)+"_"+methodNameArr[1].trim()+".csv");
	  	    	if(!csvfile.exists()){
	  	    		try {
						csvfile.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
    		 }
    	} //end for loop of file creation
    	File[] files = new File(CsvFolder).listFiles();
   	 	int dot1 = className.lastIndexOf(".");

		for (File file : files) {
			 if (file.isFile() && file.getName().startsWith(className.substring(0,dot1)) ) {
			    	System.out.println("working for "+className);
			  	  String[] methodNameArr1 =null;

	        	 // String[] methodNameArr = node.getName().split(":");
	        	  //System.out.println(file.getName());
	                try {
	                	cbw = new BufferedWriter(new FileWriter(file));

	                	//cbw = new BufferedWriter(new FileWriter(csvfile.getAbsolutePath()));
     			        csb = new StringBuilder();
	                	for(GraphConcept node : ((AbstractBaseGraph<GraphConcept, GraphEdge>) object2).vertexSet()){
	               		 if(((ConceptualGraph) object2).getScopeSet().contains(node.getName())){ 
	               			classMethodName =className+" "+node.getName();    
	               			 methodName = node.getName();	               			 
	               			 MethodGraphNumber =Integer.toString(methodID)+"_"+ Integer.toString(MethodGraphNum);
	               			 MethodGraphNum++;
	               			 methodNameArr1 = node.getName().split(":");

	               		 }
	   	        	  int us = file.getName().indexOf("_");
	   	        	  String str =file.getName().substring(us+1);
	   	        	  int fname= str.lastIndexOf(".");	 
	   	        	/*  if(methodNameArr1!=null) {
	   	        		// System.out.println("MethodName1=="+methodNameArr1[1]);
		   	        	 //System.out.println("file=="+str.substring(0,fname));

		   	        	  if(methodNameArr1[1].trim().equals(str.substring(0,fname).trim())) {
			   	        	  System.out.println(methodNameArr1[1] + ".... "+str.substring(0,fname) );

		   	        	  }

	   	        	  }*/
	                  String contents = new String(Files.readAllBytes(Paths.get(CsvFolder+"//"+file.getName())));

	   	        	  //System.out.println("file=="+file + "size = "+contents);
	   	        	  if(node.isRelation && methodNameArr1!=null && methodNameArr1[1].trim().equals(str.substring(0,fname).trim()) ) 
	   	        	  {
		   	        	  //System.out.println(methodNameArr1[1] + ".... "+str.substring(0,fname) );

	   	        	 // if(node.isRelation) {
	     		            	String[] tokens = node.toString().split("\t");
	     		            	if(tokens.length>=3) {
	     		            		for(int i=0; i<tokens.length;i++) {
	     		                        if(Arrays.asList(Relations).contains(tokens[i])){

	     		                        }
	     		                        if(Arrays.asList(Relations).contains(tokens[i])){
	     		                    		verb = tokens[i];
	     		                        }
	     		                        if(tokens[i].startsWith("InConcept")){
	     		                        	String[] tokensInConcept = tokens[i].split("InConcept:");
	     		                    		subject = tokensInConcept[1];
	     		                        }
	     		                        if(tokens[i].startsWith("OutConcept")){
	     		                        	String[] tokensOutConcept = tokens[i].split("OutConcept:");
	     		                    		object = tokensOutConcept[1];
	     		                        }
	     		            		}
	     		    		      //  System.out.println("hello" +MethodGraphNumber);

	     		            		csb.append(methodID);
	     		            		csb.append(',');
	     		            		//csb.append(MethodGraphNumber);
	     		            		csb.append(graphId);
	     		            		csb.append(',');                      
	     		            		csb.append(classMethodName);
	     		            		csb.append(',');
	     		            		csb.append(subject);
	     		            		csb.append(',');
	     		            		csb.append(verb);
	     		            		csb.append(',');
	     		            		csb.append(object);
	     		            		csb.append("\n");  	    		            		
	    		            	}    		           
	     		           }
	   	        	  
	   	        	  
	                } // end for loop
		   	        //	System.out.println(csb.toString() );
	                	cbw.write(csb.toString());
	        			cbw.flush();
	        			cbw.close(); 
	        			graphId++;
	                	
	                }catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			 }
		}
    	
} 
	public static void main(String[] args) {
		SubjectVerbObject svo = new SubjectVerbObject();
		try {
			//workingcorpus
			//JavaCorpus
			//java_corpus
			svo.ConvertAll("E:\\PoonamAris\\ArisJava\\bin\\workingcorpus");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
