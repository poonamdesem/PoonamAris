package GraphConceptsLibrary.CGraph.Concepts;

import GraphConceptsLibrary.CGraph.GraphConcept;

public class BlockSyntax extends GraphConcept {

	public BlockSyntax() {
		super("Block", false);
	}

	public BlockSyntax(String name, String value) {
		super("Block: " + name, value, false);
	}
	
	public String toString(){
		String s = super.toString();
		s += "\tValue: " + super.getValue();
		return s;
	}
}
