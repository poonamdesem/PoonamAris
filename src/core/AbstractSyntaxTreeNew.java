/*package core;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
public class AbstractSyntaxTree {
	AbstractSyntaxTree(String fileName){
		 loadASTFromSourceFile(fileName);
	}
private void loadASTFromSourceFile(String fileName) {	
		
		try {
			ASTParser parser = ASTParser.newParser(AST.JLS3);
		    ClassLoader classLoader = this.getClass().getClassLoader();
	        URL url = classLoader.getResource("Test.java");
			InputStream inputStream = classLoader.getResourceAsStream(fileName);
			System.out.println("input=="+inputStream +"url=="+ url);
	        if (inputStream!=null) {
	        	       	
				parser.setSource("public class A { int i = 9;  \n int j; \n ArrayList<Integer> al = new ArrayList<Integer>();j=1000; public void test(String arg1[], String arg2[]) {}}"
	               .toCharArray());

	            parser.setKind(ASTParser.K_COMPILATION_UNIT);
				CompilationUnit result = (CompilationUnit) parser.createAST(null);
				SingleVariableDeclaration node;
				List as = result.types();

	            System.out.println("vars=="+as  );
	        	
	        } 

		}catch(Exception ex){
			System.out.println("In Exception"+ex);
		}
		
	}
	public static void main(String[] args) {
		String fileName="Test.java";
		//IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		//IProject project = root.getProject("someJavaProject");
		//System.out.println("root=="+root +"Project=="+ project);
		//IJavaProject javaProject = JavaCore.create(project);
		//IType lwType = javaProject.findType("net.sourceforge.earticleast.app.Activator");
		//ICompilationUnit lwCompilationUnit = lwType.getCompilationUnit();
	
	  	AbstractSyntaxTree ast = new AbstractSyntaxTree(fileName);
		System.out.println("fgdg=="+ast);	
			//Tree = SyntaxTree.
	}

}*/
package core;
import java.io.File;
import java.util.Scanner;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

public class AbstractSyntaxTreeNew {
	private ASTNode tree;
	private String fileName;
	public AbstractSyntaxTreeNew() {
	}

	
	public void loadASTFromInputTextStringNew(String sourceString) {
		if (fileName == null)
			fileName = "Root";		
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
		parser.setSource(sourceString.toCharArray());
		parser.setResolveBindings(true);
		tree = parser.createAST(null);
		//compilationUnit = (CompilationUnit) parser.createAST(null);
		
		
	}

	public void setTrees(ASTNode tree) {
		this.tree = tree;
	}

	public ASTNode getTree() {
		return tree;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	
}


