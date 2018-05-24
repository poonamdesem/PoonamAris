package GraphConceptsLibrary.CGraph;

import GraphConceptsLibrary.CGraph.Concepts.AssignOp;
import GraphConceptsLibrary.CGraph.Concepts.BlockSyntax;
import GraphConceptsLibrary.CGraph.Concepts.ClassDef;
import GraphConceptsLibrary.CGraph.Concepts.CompareOp;
import GraphConceptsLibrary.CGraph.Concepts.Field;
import GraphConceptsLibrary.CGraph.Concepts.If;
import GraphConceptsLibrary.CGraph.Concepts.LogicalOp;
import GraphConceptsLibrary.CGraph.Concepts.Loop;
import GraphConceptsLibrary.CGraph.Concepts.MathOp;
import GraphConceptsLibrary.CGraph.Concepts.StringDef;
import GraphConceptsLibrary.CGraph.Concepts.Try;
import GraphConceptsLibrary.CGraph.Concepts.Variable;
import GraphConceptsLibrary.CGraph.Relations.Condition;
import GraphConceptsLibrary.CGraph.Relations.Contains;
import GraphConceptsLibrary.CGraph.Relations.Defines;
import GraphConceptsLibrary.CGraph.Relations.Depends;
import GraphConceptsLibrary.CGraph.Relations.Parameter;
import GraphConceptsLibrary.CGraph.Relations.Returns;

public class GraphConcept implements Comparable {

	public boolean isRelation;
	public String name;
	public String value;
	public double clusteringCoefficient;
	public int nodeRank;

	public GraphConcept() {
		this.value = "";
	}

	public GraphConcept(boolean isRelation) {
		this.isRelation = isRelation;
		this.value = "";
	}

	public GraphConcept(String name, boolean isRelation) {
		this.name = name;
		this.isRelation = isRelation;
		this.value = "";
	}

	public GraphConcept(String name, String value, boolean isRelation) {
		this.name = name;
		this.value = value;
		this.isRelation = isRelation;
	}

	public boolean isRelation() {
		return isRelation;
	}

	public void setRelation(boolean isRelation) {
		this.isRelation = isRelation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public double getClusteringCoefficient() {
		return clusteringCoefficient;
	}

	public void setClusteringCoefficient(double clusteringCoefficient) {
		this.clusteringCoefficient = clusteringCoefficient;
	}

	public double getNodeRank() {
		return nodeRank;
	}

	public void setNodeRank(int nodeRank) {
		this.nodeRank = nodeRank;
	}

	public double getShortNodeRank() {
		// return nodeRank in 5 decimal rounding
		return Math.round(nodeRank * 100000.0) / 100000.0;
	}

	public int compareTo(Object o) {
		if (((GraphConcept) o).name.equals(name))
			return 0;
		return 1;
	}

	public String toString() {
		return name;
	}

	public boolean isClassDef() {
		return this instanceof ClassDef;
	}

	public boolean isBlock() {
		return this instanceof BlockSyntax;
	}

	public boolean isLoop() {
		return this instanceof Loop;
	}

	public boolean isCompareOp() {
		return this instanceof CompareOp;
	}

	public boolean isAssignOp() {
		return this instanceof AssignOp;
	}

	public boolean isField() {
		return this instanceof Field;
	}

	public boolean isIf() {
		return this instanceof If;
	}

	public boolean isLogicalOp() {
		return this instanceof LogicalOp;
	}

	public boolean isMathOp() {
		return this instanceof MathOp;
	}

	public boolean isStringDef() {
		return this instanceof StringDef;
	}

	public boolean isVariable() {
		return this instanceof Variable;
	}

	public boolean getCondition() {
		return this instanceof Condition;
	}

	public boolean getContains() {
		return this instanceof Contains;
	}

	public boolean getDefines() {
		return this instanceof Defines;
	}

	public boolean getDepends() {
		return this instanceof Depends;
	}

	public boolean isParameter() {
		return this instanceof Parameter;
	}

	public boolean isReturns() {
		return this instanceof Returns;
	}

	public boolean isTryStatement() {
		return this instanceof Try;
	}

	
}
