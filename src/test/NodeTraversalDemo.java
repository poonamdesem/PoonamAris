package test;

import org.eclipse.jdt.astview.views.NodeProperty;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import GraphConceptsLibrary.CGraph.NodeTraversal;
import core.AbstractSyntaxTree;

public class NodeTraversalDemo {
	public static void main(String[] arg) {
		String filePath = "C:\\Users\\Rahman\\Documents\\Kuliah\\CS645\\E3.java";
		System.out.println("processing: " + filePath);

		AbstractSyntaxTree ast = new AbstractSyntaxTree(filePath);
		CompilationUnit compilationUnit = ast.getCompilationUnit();

		ASTNode root = compilationUnit.getRoot();
		System.out.println("root: " + root.toString());

		NodeTraversal nt = new NodeTraversal();
		Object[] kids = nt.getChildren(root);
		TypeDeclaration typeDec = null;
		for (int i = 0; i < kids.length; i++) {
			// System.out.println("i--> " + i + "\t" + kids[i].toString());
			if (kids[i] instanceof NodeProperty
					&& ((NodeProperty) kids[i]).getChildren().length != 0) {
				NodeProperty np = (NodeProperty) kids[i];
				Object[] npObjs = np.getChildren();
				for (int j = 0; j < npObjs.length; j++) {
					if (npObjs[j] instanceof TypeDeclaration) {
						typeDec = (TypeDeclaration) npObjs[j];
						break;
					}
				}
			}
		}

		Object[] typeKids = nt.getChildren(typeDec);
		NodeProperty bodyDeclarations = null;
		for (int i = 0; i < typeKids.length; i++) {
			if (typeKids[i] instanceof NodeProperty
					&& ((NodeProperty) typeKids[i]).getPropertyName().equals(
							"BODY_DECLARATIONS")) {
				bodyDeclarations = (NodeProperty) typeKids[i];
			}
		}
		
		Object[] decs = bodyDeclarations.getChildren();
		for(Object o : decs){
			System.out.println(o.getClass());
		}
	}
	
	public Object[] getBodyDeclarations(){
		return null;
	}
}
