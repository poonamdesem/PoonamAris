package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

import org.eclipse.jdt.internal.ui.util.MainMethodSearchEngine;
import org.jgrapht.traverse.BreadthFirstIterator;

import GraphConceptsLibrary.CGraph.ConceptualGraph;
import GraphConceptsLibrary.CGraph.GraphConcept;
import GraphConceptsLibrary.CGraph.GraphEdge;
import GraphConceptsLibrary.CGraph.GraphEdgeFactory;
import GraphConceptsLibrary.CGraph.Concepts.AssignOp;
import GraphConceptsLibrary.CGraph.Concepts.BlockSyntax;
import GraphConceptsLibrary.CGraph.Concepts.ClassDef;
import GraphConceptsLibrary.CGraph.Concepts.CompareOp;
import GraphConceptsLibrary.CGraph.Concepts.If;
import GraphConceptsLibrary.CGraph.Concepts.Loop;
import GraphConceptsLibrary.CGraph.Concepts.MathOp;
import GraphConceptsLibrary.CGraph.Concepts.Method;
import GraphConceptsLibrary.CGraph.Concepts.StringDef;
import GraphConceptsLibrary.CGraph.Concepts.Variable;
import GraphConceptsLibrary.CGraph.Relations.Condition;
import GraphConceptsLibrary.CGraph.Relations.Contains;
import GraphConceptsLibrary.CGraph.Relations.Parameter;
import GraphConceptsLibrary.CGraph.Relations.Returns;
import core.ConceptualGraphBuilder;

public class RebuildFromCGIF {
	private LinkedHashSet<String> conceptSet;	
	private GraphEdgeFactory edgeFactory;	
	private ConceptualGraph graph;
	private BlockSyntax root;
	
	public RebuildFromCGIF(){
		 conceptSet = new LinkedHashSet<String>();
		 edgeFactory = new GraphEdgeFactory();
		 graph = new ConceptualGraph(edgeFactory);
	}
	
	public static void main(String[] arg) throws Exception{
		String filePath = "E:\\\\PoonamAris\\\\ArisJava\\\\bin\\\\Count.java";
		ConceptualGraphBuilder builder = new ConceptualGraphBuilder(filePath);
		
		builder.dumpToFile();
		RebuildFromCGIF reb = new RebuildFromCGIF();
		reb.rebuild();
		reb.printBreadthFirst();
		reb.getCSV();
	}
	
    private void getCSV() {
    	BreadthFirstIterator<GraphConcept, GraphEdge> it = new BreadthFirstIterator<GraphConcept, GraphEdge>(graph, root);
        String Relations[] = { "Condition", "Contains", "Parameter", "Returns", "Depends", "Defines" };
		String NodeType = "";
		String nodeCsv = "E:\\PoonamAris\\ArisJava\\bin\\nodesCGIF.csv";
		try {
			File file = new File(nodeCsv);
	    	File relationfile = new File(nodeCsv);
	        if(!file.exists()){
	              file.createNewFile();
	        }
	        BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
	        StringBuilder sb = new StringBuilder();
	        int NodeID = 0;
	         int methodID=1;
            while(it.hasNext()){
        		GraphConcept g = it.next();
        			// System.out.println("value=="+graph+"\n");
        	           if(Arrays.asList(Relations).contains(g.getName())){          
        	                NodeType = "Verb";
        	            }else {
        	            	 NodeType = "Noun";
        	            }
        	           NodeID++;
        	           sb.append(NodeType);
        	           sb.append(',');
        	           sb.append(g.getName());
        	           sb.append(',');
        	           sb.append(root.getName());
        	           sb.append(',');
        	           sb.append(methodID);
        	           sb.append(',');
        	           sb.append(NodeID);
        	           sb.append('\n');    			

        	}
            bw.write(sb.toString());
            bw.flush();
  	      bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
   	}

	private void printBreadthFirst(){
    	BreadthFirstIterator<GraphConcept, GraphEdge> it = new BreadthFirstIterator<GraphConcept, GraphEdge>(graph, root);
    	while(it.hasNext()){
    		GraphConcept g = it.next();
    		if(!g.isRelation()){
    			System.out.println("[" + g + "]");
    		}else{
    			System.out.println("(" + g + ")");
    		}    
    	
    	} 	
    	
    }
	
	private String[] getBlockData(ArrayList<String> block){
		String[] blocks = new String[2];
		
		String[] firstLine = block.get(0).split("\t");
		blocks[0] = firstLine[0].trim().substring(8);
		
		StringBuilder sb = new StringBuilder();
		sb.append(firstLine[1].trim().substring(7) + "\n");
		
		for(int i = 1; i < block.size()-1; i++){
			sb.append(block.get(i) + "\n");
		}
		
		blocks[1] = sb.toString();
		return blocks;
	}
	
	private void rebuild() throws Exception{
		String fileName = "E:\\PoonamAris\\ArisJava\\bin\\CGIF_Count.txt";
		Scanner sc = new Scanner(new File(fileName));
				
		StringBuilder sb = new StringBuilder();
		ArrayList<String> lines = new ArrayList<String>();
		ArrayList<String> conceptList = new ArrayList<String>();
		ArrayList<String> relationList = new ArrayList<String>();
		
		while(sc.hasNext()){
			//special case for the root			
			String line = sc.nextLine();
			lines.add(line);
			if(line.endsWith("]")){
				break;
			}
		}
		
		String[] blockData = getBlockData(lines);
		
		root = new BlockSyntax(blockData[0], blockData[1]);
		
		System.out.println("BLOCKDATA => " + blockData[1]);
		
		graph.addVertex(root);			
		
		//System.out.println("ROOT => " + root);
//		System.out.println("ROOT.TOSTRING => " + root.toString());
		printBreadthFirst();		
		
		//build the value		
				
		while(sc.hasNext()){
			String line = sc.nextLine();
			if(line.startsWith("[")){
				conceptList.add(line);
			}
			else{
				String t = line;
				if(!line.contains("OutConcept")){
					while(sc.hasNext()){
						String nLine = sc.nextLine();
						t += nLine;
						
						if(nLine.contains("OutConcept"))
							break;
					}
				}
				relationList.add(t);
			}
		}
		
		for(String s : conceptList){
			handleConcept(s);
		}
		
//		printConcept();
//		System.exit(0);
		
//		System.out.println("=========");		
		
		String firstRel = relationList.get(0);
//		System.out.println("FIRSTREL => " + firstRel);
		int inIdx = firstRel.indexOf("InConcept");
		int outIdx = firstRel.indexOf("OutConcept");
		
		String inconcept = firstRel.substring(inIdx, outIdx).trim().substring(11);
		String outconcept = firstRel.substring(outIdx, firstRel.length()-1).trim().substring(12);		
		
//		System.out.println("Inconcept\t" + inconcept);
//		System.out.println("Outconcept\t" + outconcept);
		
//		System.exit(0);
		
		GraphConcept outGraph = handleMethod(outconcept);
		Contains c = new Contains(inconcept, outconcept);
		graph.addVerticesAndEdge(c, outGraph);
		graph.addVerticesAndEdge(root, c);
		
		
		
		for(int i = 1; i < relationList.size(); i++){
//			System.out.println("HANDLING: " + relationList.get(i));
			handleRelation(relationList.get(i));
//			dumpGraphConcept();
//			getGraphConcept(null);
		}
		
//		for(String s : relationList){
//			handleRelation(s);
//		}
		
		
	}
	
	private GraphConcept getGraphConcept(GraphConcept g){
		GraphConcept ret = null;
		Set<GraphConcept> set = graph.vertexSet();
		for(GraphConcept gc : set){
			if(g.getName().equals(gc.getName())){
				if(g instanceof ClassDef){
					ret = gc;
					break;
				}else if(g instanceof Method){
					String mod = ((Method) gc).getModifiers();
					String rt = ((Method) gc).getReturnType();
					String par = ((Method) gc).getParameterList();
					
					if(mod.equals(((Method) g).getModifiers()) && rt.equals(((Method) g).getReturnType()) && par.equals(((Method) g).getParameterList())){
						ret = gc;
						break;
					}					
				}else if(g instanceof BlockSyntax){
					ret = gc;
				}else if(g instanceof AssignOp){
					String val = ((AssignOp) gc).getValue();
					String left = ((AssignOp) gc).getLeftSide();
					String right = ((AssignOp) gc).getRightSide();
					
					if(val.equals(((AssignOp) g).getValue()) && left.equals(((AssignOp) g).getLeftSide()) && right.equals(((AssignOp) g).getRightSide())){
						ret = gc;
						break;
					}
				}else if(g instanceof CompareOp){
					String op = ((CompareOp) gc).getOP();
					String left = ((CompareOp) gc).getLeftSide();
					String right = ((CompareOp) gc).getRightSide();					
					
					if(op.equals(((CompareOp) g).getOP()) && left.equals(((CompareOp) g).getLeftSide()) && right.equals(((CompareOp) g).getRightSide())){
//						System.out.println("TRUE");
						ret = gc;
						break;
					}
//					CompareOp: <	LeftSide: Variable: n	RightSide: Variable: k)
				}else if(g instanceof Loop){
					String name = ((Loop) gc).getName();
					String conditions = ((Loop) gc).getConditions();
//					(Contains	InConcept: Loop: For	Type: null	Condition: n < k	OutConcept: Assign:*	LeftSide: Variable: n	RightSide: String: 0)
//					Loop -> Loop: For	Type: null	Condition: n < k
					if(name.equals(g.getName()) && conditions.equals(((Loop) g).getConditions())){
						ret = gc;
						break;
					}
				}else if(g instanceof MathOp){
//					MathOp: +=	LeftSide: Variable: sum	RightSide: Variable: n
//					System.out.println("MATHOP --> " + g);
					String op = ((MathOp) gc).getOP();
					String left = ((MathOp) gc).getLeftSide();
					String right = ((MathOp) gc).getRightSide();
					
					if(op.equals(((MathOp) g).getOP()) && left.equals(((MathOp) g).getLeftSide()) && right.equals(((MathOp) g).getRightSide())){
						ret = gc;
						break;
					}
				}
			}
//			System.out.println("RET ==> " + ret);
//			System.out.println("Class => " + gc.getClass());
		}
		
		return ret;
	}
	
	private void dumpGraphConcept(){
		Set<GraphConcept> set = graph.vertexSet();
		for(GraphConcept g : set){
//			System.out.println("GRAPH CONCEPT ==> " + g);
		}						
	}
	
	private void printConcept(){
		Iterator<String> it = conceptSet.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}
	
	private void handleConcept(String concept){		
		conceptSet.add(concept.substring(1, concept.length()-1));
	}
	
	private void handleRelation(String relation){
//		(Contains	InConcept: Block: Tes.java	OutConcept: Class: Tes)
//		(Parameter	InConcept: Method: sum	OutConcept: Variable: k)
//		(Contains	InConcept: Method: sum	OutConcept: Block: Method: sum)
//		(Returns	InConcept: Method: sum	OutConcept: Variable: s)
		String rel = relation.substring(1);
		if(rel.startsWith("Contains")){
			//build contains
//			System.out.println("CONTAINS BUILT");
			buildContains(relation);
//			System.out.println();
		}else if(rel.startsWith("Parameter")){
			//build Parameter
//			System.out.println("PARAMETER BUILT");
			buildParameter(relation);
//			System.out.println();
		}else if(rel.startsWith("Returns")){
			//build Returns
//			System.out.println("RETURN BUILT");
			buildReturn(relation);
//			System.out.println();
		}else if(rel.startsWith("Condition")){
			//build Condition
//			System.out.println("CONDITION BUILT");
			buildCondition(relation);
//			System.out.println();
		}

	}
	
	private void buildCondition(String condition){
//		(Condition	InConcept : Loop: While	OutConcept : CompareOp: <=	LeftSide: Variable: low	RightSide: Variable: high)
//		(Condition	InConcept : If: midVal < key	OutConcept : CompareOp: <	LeftSide: Variable: midVal	RightSide: Variable: key)
//		(Condition	InConcept : If: key < midVal	OutConcept : CompareOp: <	LeftSide: Variable: key	RightSide: Variable: midVal)
//		(Condition	InConcept : Loop: While	OutConcept : CompareOp: <	LeftSide: Variable: k	RightSide: Variable: x)
//		(Condition	InConcept : Loop: For	OutConcept : CompareOp: <	LeftSide: Variable: n	RightSide: Variable: k)
		
		int inIdx = condition.indexOf("InConcept");
		int outIdx = condition.indexOf("OutConcept");
		
		String inconcept = condition.substring(inIdx, outIdx).trim().substring(11);
		String outconcept = condition.substring(outIdx, condition.length()-1).trim().substring(12);
		
		GraphConcept inGraph = null;
		GraphConcept outGraph = null;
		
//		System.out.println("Condition Inconcept\t" + inconcept);
//		System.out.println("Condition Outconcept\t" + outconcept);
		
		if(inconcept.startsWith("Loop")){
			inGraph = handleLoop(inconcept);
		}else if(inconcept.startsWith("If")){
			inGraph = handleIf(inconcept);
		}
		
//		System.out.println("INGRAPH => " + inGraph);
				
		if(outconcept.startsWith("CompareOp")){
			outGraph = handleCompare(outconcept);
		}
		
		Condition c = new Condition(inconcept, outconcept);
		graph.addVerticesAndEdge(c, outGraph);
		graph.addVerticesAndEdge(inGraph, c);
		
//		System.out.println("OUTGRAPH => " + outGraph);		
//		System.out.println();
	}
	
	private void buildReturn(String returns){
//		(Returns	InConcept: Block: If: x < y	OutConcept: Variable: x)
//		(Returns	InConcept: If: x == y	OutConcept: Variable: y)
//		(Returns	InConcept: Block: If: x == y	OutConcept: Variable: c)
//		(Returns	InConcept: Method: BinarySearch	OutConcept: MathOp: -)
//		(Returns	InConcept: Block: Else: If: key < midVal	OutConcept: Variable: mid)
//		(Returns	InConcept: Method: sum	OutConcept: Variable: add)
//		(Returns	InConcept: Method: sum	OutConcept: Variable: sum)
		int inIdx = returns.indexOf("InConcept");
		int outIdx = returns.indexOf("OutConcept");
		
		String inconcept = returns.substring(inIdx, outIdx).trim().substring(11);
		String outconcept = returns.substring(outIdx, returns.length()-1).trim().substring(12);
		
		GraphConcept inGraph = null;
		GraphConcept outGraph = null;
		
//		System.out.println("Return Inconcept\t" + inconcept);
//		System.out.println("Return Outconcept\t" + outconcept);
		
		if(inconcept.startsWith("Method")){
			inGraph = handleMethod(inconcept);
		}else if(inconcept.startsWith("If")){
			inGraph = handleIf(inconcept);
		}else if(inconcept.startsWith("Block")){
			inGraph = handleBlock(inconcept);
		}		
		
//		System.out.println("INGRAPH => " + inGraph);
		
		if(outconcept.startsWith("Variable")){
			outGraph = handleVariable(outconcept);
		}else if(outconcept.startsWith("MathOp")){
			outGraph = handleMath(outconcept);
		}
		
		Returns r = new Returns(inconcept, outconcept);
		graph.addVerticesAndEdge(r, outGraph);
		graph.addVerticesAndEdge(inGraph, r);
		
//		System.out.println("OUTGRAPH => " + outGraph);
//		System.out.println();
	}
	
	private void buildParameter(String parameter){
//		(Parameter	InConcept: Method: sum	OutConcept: Variable: x)
//		(Parameter	InConcept: Method: sum	OutConcept: Variable: k)
		int inIdx = parameter.indexOf("InConcept");
		int outIdx = parameter.indexOf("OutConcept");
		
		String inconcept = parameter.substring(inIdx, outIdx).trim().substring(11);
		String outconcept = parameter.substring(outIdx, parameter.length()-1).trim().substring(12);
		
		GraphConcept inGraph = null;
		GraphConcept outGraph = null;
		
//		System.out.println("Param Inconcept\t" + inconcept);
//		System.out.println("Param Outconcept\t" + outconcept);
		
		//there should be only one... (what about methodcall?)
		if(inconcept.startsWith("Method")){
			inGraph = handleMethod(inconcept);
		}
		
//		System.out.println("INGRAPH => " + inGraph);
		
		if(outconcept.startsWith("Variable")){
			outGraph = handleVariable(outconcept);
		}
		
		Parameter p = new Parameter(inconcept, outconcept);
		graph.addVerticesAndEdge(p, outGraph);
		graph.addVerticesAndEdge(inGraph, p);
		
//		System.out.println("OUTGRAPH => " + outGraph);
//		System.out.println();
	}
	
	private void buildContains(String contains){
		int inIdx = contains.indexOf("InConcept");
		int outIdx = contains.indexOf("OutConcept");
		
		String inconcept = contains.substring(inIdx, outIdx).trim().substring(11);
		String outconcept = contains.substring(outIdx, contains.length()-1).trim().substring(12);		
		
//		System.out.println("Inconcept\t" + inconcept);
//		System.out.println("Outconcept\t" + outconcept);			
//		(Contains	InConcept: Loop: For	Type: null	Condition: null	OutConcept: Assign:*	LeftSide: Variable: n	RightSide: String: 0)
		GraphConcept inGraph = null;
		GraphConcept outGraph = null;
		
		if(inconcept.startsWith("Block")){
			inGraph = handleBlock(inconcept);
		}else if(inconcept.startsWith("Class")){
			inGraph = handleClass(inconcept);
		}else if(inconcept.startsWith("Method")){
			inGraph = handleMethod(inconcept);
		}else if(inconcept.startsWith("Loop")){
			inGraph = handleLoop(inconcept);
		}else if(inconcept.startsWith("Variable")){
			inGraph = handleVariable(inconcept);
		}else if(inconcept.startsWith("String")){
			inGraph = handleString(inconcept);
		}else if(inconcept.startsWith("Assign")){
//			System.out.println("\n==HANDLE ASSIGN==\n");
			inGraph = handleAssign(inconcept);
		}else if(inconcept.startsWith("CompareOp")){
			inGraph = handleCompare(inconcept);
		}else if(inconcept.startsWith("MathOp")){
			inGraph = handleMath(inconcept);
		}else if(inconcept.startsWith("If")){
			inGraph = handleIf(inconcept);
		}
		
//		System.out.println("INGRAPH => " + inGraph);
		
		if(outconcept.startsWith("Block")){
			outGraph = handleBlock(outconcept);
		}else if(outconcept.startsWith("Class")){
			outGraph = handleClass(outconcept);
		}else if(outconcept.startsWith("Method")){
			outGraph = handleMethod(outconcept);
		}else if(outconcept.startsWith("Loop")){
			outGraph = handleLoop(outconcept);
		}else if(outconcept.startsWith("Variable")){
			outGraph = handleVariable(outconcept);
		}else if(outconcept.startsWith("String")){
			outGraph = handleString(outconcept);
		}else if(outconcept.startsWith("Assign")){
			outGraph = handleAssign(outconcept);
		}else if(outconcept.startsWith("CompareOp")){
			outGraph = handleCompare(outconcept);
		}else if(outconcept.startsWith("MathOp")){
			outGraph = handleMath(outconcept);
		}else if(outconcept.startsWith("If")){
			outGraph = handleIf(outconcept);
		}
		
//		System.out.println("INGRAPH => " + inGraph);
//		System.out.println("OUTGRAPH => " + outGraph);
		Contains c = new Contains(inconcept, outconcept);
		graph.addVerticesAndEdge(inGraph, c);
		graph.addVerticesAndEdge(c, outGraph);

//		printBreadthFirst();
//		System.out.println("OUTGRAPH => " + outGraph);
//		System.out.println();
	}
	
	private If handleIf(String concept){
		//do the lazy thing
		return new If(concept.substring(4));
	}
	
	private MathOp handleMath(String concept){
//		System.out.println("MATHCONCEPT -> " + concept);
		MathOp math;
		
		int lIdx = concept.indexOf("LeftSide:");
		int rIdx = concept.indexOf("RightSide:");
		
		String mName = (lIdx == -1) ? concept : concept.substring(0, lIdx).trim();
		String mLeft = (lIdx == -1 && rIdx == -1) ? "null" : concept.substring(lIdx+10, rIdx).trim();
		String mRight = (rIdx == -1) ? "null" : concept.substring(rIdx+11).trim();
		
		Iterator<String> it = conceptSet.iterator();
		while(it.hasNext()){
			String t = it.next();
			if(t.startsWith(mName)){
				int l2Idx = t.indexOf("LeftSide:");
				int r2Idx = t.indexOf("RightSide:");
				
				String tLeft = t.substring(l2Idx+10, r2Idx).trim();
				String tRight = t.substring(r2Idx+11).trim();
				
				if((!tLeft.equals("null") || tLeft.equals(mLeft)) && (!tRight.equals("") || tRight.equals(mRight))){
					mName = mName.substring(8);
					break;
				}
			}			
		}
		math = new MathOp(mName);
		math.setLeftSide(mLeft);
		math.setRightSide(mRight);
		
		MathOp fromGraph = (MathOp) getGraphConcept(math);
		return fromGraph == null ? math : fromGraph;
		
//		return math;
	}
	
	private CompareOp handleCompare(String concept){		
		CompareOp comp;
		
		int lIdx = concept.indexOf("LeftSide:");
		int rIdx = concept.indexOf("RightSide:");
		
		String cName = concept.substring(0, lIdx).trim();
		String cLeft = concept.substring(lIdx+10, rIdx).trim();
		String cRight = concept.substring(rIdx+11).trim();
		
		Iterator<String> it = conceptSet.iterator();
		while(it.hasNext()){
			String t = it.next();
			if(t.startsWith(cName)){
				int l2Idx = t.indexOf("LeftSide:");
				int r2Idx = t.indexOf("RightSide:");
				
				String tLeft = t.substring(l2Idx+10, r2Idx).trim();
				String tRight = t.substring(r2Idx+11).trim();
				
				if((!tLeft.equals("null") || tLeft.equals(cLeft)) && (!tRight.equals("") || tRight.equals(cRight))){
					cName = cName.substring(11);
					break;
				}
			}			
		}
		comp = new CompareOp(cName);
		comp.setLeftSide(cLeft);
		comp.setRightSide(cRight);
		
		CompareOp fromGraph = (CompareOp) getGraphConcept(comp);
		return fromGraph == null ? comp : fromGraph;		
		
//		return comp;
	}
	
	private AssignOp handleAssign(String concept){		
		int lIdx = concept.indexOf("LeftSide");
		int rIdx = concept.indexOf("RightSide");
//		[Assign:*	A[j]=v	LeftSide: null	RightSide: null]
		String[] st = concept.split("\t");
		
		AssignOp as = new AssignOp();
		as.setValue(st[1]);
		if(lIdx != -1 && rIdx != -1){
			as.setLeftSide(concept.substring(lIdx+10, rIdx));
			as.setRightSide(concept.substring(rIdx+11));
		}
		
		AssignOp fromGraph = (AssignOp) getGraphConcept(as);
		return fromGraph == null ? as : fromGraph;
	}
	
	private StringDef handleString(String concept){
		//do the lazy thing				
		String name = concept.substring(8).trim();
		StringDef sDef = new StringDef(name, "");
		
		return sDef;
	}
	
	private Variable handleVariable(String concept){
		Variable var;
		
		Iterator<String> it = conceptSet.iterator();
		String vName = "";
		String vType = "";
		String vScope = "";
		
		while(it.hasNext()){
			String t = it.next();
			if(t.startsWith(concept)){
				int tIdx = t.indexOf("Type:");
				int sIdx = t.indexOf("Scope:");
				
				vName = t.substring(10, tIdx).trim();
				vType = t.substring(tIdx+6, sIdx).trim();
				vScope = t.substring(sIdx+7).trim();

				break;
			}
		}
		var = new Variable(vName, "");
		var.setType(vType);
		var.setScope(vScope);
		
		return var;
	}
	
	private Loop handleLoop(String concept){
		Loop loop;
		
		int tIdx = concept.indexOf("Type:");
		int cIdx = concept.indexOf("Condition:");
		
		String lName = concept.substring(0, tIdx).trim();
		String lType = concept.substring(tIdx+6, cIdx).trim();
		String lCondition = concept.substring(cIdx+11).trim();
		
		Iterator<String> it = conceptSet.iterator();
		while(it.hasNext()){
			String t = it.next();
			if(t.startsWith(lName)){
				int t2Idx = t.indexOf("Type:");
				int c2Idx = t.indexOf("Condition:");
				
				String tType = t.substring(t2Idx+6, c2Idx).trim();
				String tCondition = t.substring(c2Idx+11).trim();
				
				if(lType.equals(tType) && lCondition.equals(tCondition)){
					lName = concept.substring(6, tIdx).trim();
					break;
				}
			}			
		}
		loop = new Loop(lName, "");
		loop.setType(lType);
		loop.setConditions(lCondition);
		
		Loop fromGraph = (Loop) getGraphConcept(loop);
		return fromGraph == null ? loop : fromGraph;		
//		return loop;
	}
	
	private Method handleMethod(String concept){
		Method method;
		Iterator<String> it = conceptSet.iterator();
		String mName = "";
		String value = "";
		String ret = "";
		String mod = "";
		String param = "";
		
		while(it.hasNext()){
			String t = it.next();						
			if(t.startsWith(concept)){
				int modIdx = t.indexOf("Modifiers:");
				int retIdx = t.indexOf("ReturnType:");
				int paramIdx = t.indexOf("ParameterList:");
				
				mName = t.substring(8, modIdx).trim();
				mod = t.substring(modIdx+11, retIdx).trim();
				ret = t.substring(retIdx+12, paramIdx).trim();
				param = t.substring(paramIdx+15).trim();
				
				break;
			}			
		}
		method = new Method(mName, value);
		method.setModifiers(mod);
		method.setReturnType(ret);
		method.setParameterList(param);
		
//		System.out.println("METHOD ==> " + method);
		
		Method m = (Method) getGraphConcept(method);
		return m == null ? method : m;
	}
	
	private ClassDef handleClass(String concept){
		ClassDef clDef;
		
		Iterator<String> it = conceptSet.iterator();
		String cName = "";
		String value = "";
		
		while(it.hasNext()){
			String t = it.next();
			if(t.startsWith(concept)){
				int vIdx = t.indexOf("Value:");
				if(vIdx != -1){
					cName = t.substring(7, vIdx).trim();
					value = t.substring(vIdx+7).trim();
				}else{
					cName = t.substring(7);
				}
				break;
			}
		}
		clDef = new ClassDef(cName, value);
		
//		System.out.println("CLASS ==> " + clDef);
		
//		return clDef;		
		ClassDef fromGraph = (ClassDef) getGraphConcept(clDef);
		return fromGraph == null ? clDef : fromGraph;
	}
	
	private BlockSyntax handleBlock(String concept){
		BlockSyntax block;
		Iterator<String> it = conceptSet.iterator();
		String bName = "";
		String value = "";
		while(it.hasNext()){
			String t = it.next();
//			System.out.println("CONCEPT IT => " + t);			
			if(t.startsWith(concept)){				
				int vIdx = t.indexOf("Value:");
				if(vIdx != -1){
					bName = t.substring(7, vIdx).trim();
					value = t.substring(vIdx+7).trim();
				}else{
					bName = t.substring(7);
				}
				break;
			}			
		}
		block = new BlockSyntax(bName, value);
		
//		System.out.println("BLOCK CREATED ==> " + block);
		
		BlockSyntax b = (BlockSyntax) getGraphConcept(block);
		
		return b == null? block : b;
	}
}
