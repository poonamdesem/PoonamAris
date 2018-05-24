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
import GraphConceptsLibrary.CGraph.ConceptualGraphNew;
import GraphConceptsLibrary.CGraph.GraphConcept;
import GraphConceptsLibrary.CGraph.GraphEdge;

public class Neo4jProgram {
    static HashMap<String, ConceptualGraph> Graph = new HashMap<String, ConceptualGraph>();
    String nodeCsv = "E:\\PoonamAris\\ArisJava\\bin\\nodes.csv";
	String relationCsv = "E:\\PoonamAris\\ArisJava\\bin\\relation.csv";

	
	private void ConvertAll(String directory) {
		File[] files = new File(directory).listFiles();
		for (File file : files) {
		    if (file.isFile()) {
                try {
                	String contents = new String(Files.readAllBytes(Paths.get(directory+"//"+file.getName())));                	
                	                	
	               ConceptualGraphNew cg = ConceptualGraphNew.loadGraphFromStringNew(contents);
	               System.out.println("cg===="+cg);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }		  
		}
    		        
}
	
	public static void main(String[] arg) {
		
		Neo4jProgram neo4j = new Neo4jProgram();	
		neo4j.ConvertAll("E:\\PoonamAris\\ArisJava\\bin\\JavaCorpus");
				
	}

}
