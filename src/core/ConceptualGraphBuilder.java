package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.astview.views.NodeProperty;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import GraphConceptsLibrary.CGraph.ConceptualGraph;
import GraphConceptsLibrary.CGraph.GraphConcept;
import GraphConceptsLibrary.CGraph.GraphEdge;
import GraphConceptsLibrary.CGraph.GraphEdgeFactory;
import GraphConceptsLibrary.CGraph.GraphMetrics;
import GraphConceptsLibrary.CGraph.NodeTraversal;
import GraphConceptsLibrary.CGraph.Concepts.AssignOp;
import GraphConceptsLibrary.CGraph.Concepts.BlockSyntax;
import GraphConceptsLibrary.CGraph.Concepts.ClassDef;
import GraphConceptsLibrary.CGraph.Concepts.CompareOp;
import GraphConceptsLibrary.CGraph.Concepts.Field;
import GraphConceptsLibrary.CGraph.Concepts.If;
import GraphConceptsLibrary.CGraph.Concepts.Loop;
import GraphConceptsLibrary.CGraph.Concepts.MathOp;
import GraphConceptsLibrary.CGraph.Concepts.Method;
import GraphConceptsLibrary.CGraph.Concepts.MethodCall;
import GraphConceptsLibrary.CGraph.Concepts.NullDef;
import GraphConceptsLibrary.CGraph.Concepts.StringDef;
import GraphConceptsLibrary.CGraph.Concepts.Switch;
import GraphConceptsLibrary.CGraph.Concepts.Try;
import GraphConceptsLibrary.CGraph.Concepts.Variable;
import GraphConceptsLibrary.CGraph.Relations.Condition;
import GraphConceptsLibrary.CGraph.Relations.Contains;
import GraphConceptsLibrary.CGraph.Relations.Parameter;
import GraphConceptsLibrary.CGraph.Relations.Returns;

public class ConceptualGraphBuilder {

	private static final Object GraphID = null;
	private ConceptualGraph graph;
	private NodeTraversal traversal;
	private AbstractSyntaxTree ast;
	private CompilationUnit cu;
	private String filePath;
	private GraphEdgeFactory edgeFactory;
	private BlockSyntax rootBlock;
	private ClassDef classDef; // class is a must for JDT
	private ASTNode classNode;
	private String globalScope = "";
	private LinkedHashSet<String> scopeSet;
	private AssignOp globalAssign = null;
	private Contains globalContains = null;
	private static String parentPath = "";
	private String fileName = "";
	private String dump = "";
	private String cgifFile = "";
	
	private NodeProperty classModifier; // hold "public"
	private NodeProperty className; // hold the name of the class
	private String mainMethod = "";
    static HashMap<String, ConceptualGraph> Graph = new HashMap<String, ConceptualGraph>();

	public ConceptualGraphBuilder() {

	}

	public ConceptualGraphBuilder(String filePath) {		
		this.filePath = filePath;
		this.ast = new AbstractSyntaxTree(filePath);
		this.cu = ast.getCompilationUnit();
		this.traversal = new NodeTraversal();
		this.scopeSet = new LinkedHashSet<String>();

		File file = new File(filePath);
		parentPath = file.getParent();
		fileName = file.getName().split(".java")[0] + ".txt";
		
		edgeFactory = new GraphEdgeFactory();
		graph = new ConceptualGraph(edgeFactory);

		ASTNode root = cu.getRoot();
		getMethod(root.toString().split("\n"));
		rootBlock = new BlockSyntax(ast.getFileName(), mainMethod);
		graph.addVertex(rootBlock);

		Object[] decs = getBodyDeclarations(root);
//System.out.println("decs====================="+decs.getClass()+"\n");
		if (className != null) {			
			// build the class concept
			classDef = new ClassDef(className.getChildren()[0].toString(),
					classNode.toString());
			Contains contains = new Contains(rootBlock.getName(),
					classDef.getName());
			graph.addVerticesAndEdge(contains, classDef);
			graph.addEdge(rootBlock, contains);
		}

		processGraphBuilding(decs);
	}

	/**
	 * Method to get the first body declarations in a class
	 * 
	 * @param root
	 *            - the root of the class in ASTNode
	 * @return an array of Objects that represents the declaration in the class.
	 */
	public Object[] getBodyDeclarations(ASTNode root) {
		Object[] kids = traversal.getChildren(root);
		TypeDeclaration typeDec = null;
		for (int i = 0; i < kids.length; i++) {
			if (kids[i] instanceof NodeProperty
					&& ((NodeProperty) kids[i]).getChildren().length != 0) {
				NodeProperty np = (NodeProperty) kids[i];
				Object[] npObjs = np.getChildren();
				for (int j = 0; j < npObjs.length; j++) {
					if (npObjs[j] instanceof TypeDeclaration) {
						typeDec = (TypeDeclaration) npObjs[j];
						classNode = typeDec;
						break;
					}
				}
			}
		}

		Object[] typeKids = traversal.getChildren(typeDec);
		NodeProperty bodyDeclarations = null;

		for (Object o : typeKids) {
			if (o instanceof NodeProperty
					&& ((NodeProperty) o).getPropertyName().equals("MODIFIERS"))
				classModifier = (NodeProperty) o;
			if (o instanceof NodeProperty
					&& ((NodeProperty) o).getPropertyName().equals("NAME"))
				className = (NodeProperty) o;
			if (o instanceof NodeProperty
					&& ((NodeProperty) o).getPropertyName().equals(
							"BODY_DECLARATIONS")) {
				bodyDeclarations = (NodeProperty) o;
			}
		}

		Object[] decs = bodyDeclarations.getChildren();
		return decs;
	}

	/**
	 * Method to start the building of conceptual graph
	 * 
	 * @param decs - the array representation for the method body
	 */
	private void processGraphBuilding(Object[] decs) {
		for (Object o : decs) {
			if (o instanceof MethodDeclaration) {
				// process the method conceptual graph building
				Method method = createMethodFromMethodDeclaration((MethodDeclaration) o);
				if (method != null) {
					// using classDef instead of rootBlock
					// all classDef replacing rootBlock
					Contains classContains = new Contains(classDef.getName(),
							method.getName());
					graph.addVerticesAndEdge(classContains, method);
					graph.addEdge(classDef, classContains);
				}
			} else if (o instanceof FieldDeclaration) {
				// process the field conceptual graph building
				Field field = createFieldFromFieldDeclaration(
						(FieldDeclaration) o, classDef);
				if (field != null) {
					Contains fieldContains = new Contains(classDef.getName(),
							field.getName());
					graph.addVerticesAndEdge(fieldContains, field);
					graph.addEdge(classDef, fieldContains);
				}
			}
		}
	}

	/**
	 * Method to create Field GraphConcept from field declaration
	 * 
	 * @param node - the FieldDeclaration node object
	 * @param parentNode - the parent of the node
	 * @return a Field object of GraphConcept
	 */
	private Field createFieldFromFieldDeclaration(FieldDeclaration node,
			GraphConcept parentNode) {

		String mod = "";
		List<IExtendedModifier> mods = node.modifiers();
		for (IExtendedModifier m : mods) {
			mod += m.toString() + " ";
		}
		mod = mod.trim();

		// assumption: every variable declaration only for one variable
		VariableDeclarationFragment v = (VariableDeclarationFragment) node
				.fragments().get(0);

		Field field = new Field(v.getName().toString(), node.toString());
		field.setModifiers(mod);
		field.setType(node.getType().toString());
		field.setScope(parentNode.getName());

		// process the variable initialization
		if (v.getInitializer() != null) {
			Expression initializeExpression = v.getInitializer();
			AssignOp assign = createAssignOpFromEquals(initializeExpression,
					parentNode.getName());

			if (assign != null) {
				assign.setLeftSide("Variable: " + v.getName().toString());
				Contains c1 = new Contains(assign.getName(), field.getName());
				graph.addVerticesAndEdge(c1, field);
				graph.addVerticesAndEdge(assign, c1);

				Contains c2 = new Contains(parentNode.getName(),
						assign.getName());
				graph.addVerticesAndEdge(c2, assign);
				graph.addVerticesAndEdge(parentNode, c2);
			}
		}

		return field;
	}
	
	/**
	 * Method to create AssignOp GraphConcept from equals assignment
	 * 
	 * @param equals - the Expression for equals assignment
	 * @param currentScope - the String representing the scope
	 * @return an AssignOp of GraphConcept
	 */
	private AssignOp createAssignOpFromEquals(Expression equals,
			String currentScope) {
		
		AssignOp assign = new AssignOp();
		assign.setValue(equals.toString());			

		List<Expression> childs = new ArrayList<Expression>();
		childs.add(equals);

		List<Expression> adds = new ArrayList<Expression>();
		for (Expression e : childs) {
			if (e instanceof ParenthesizedExpression
					|| e instanceof PrefixExpression
					|| e instanceof CastExpression) {
				adds.add(getSyntaxNodeFromParenthesis(e));
			}
		}
		childs.addAll(adds);

		for (Expression exp : childs) {
			if (exp instanceof InfixExpression || exp instanceof Assignment) {
				MathOp mathop = createMathOpFromBinaryExpression(exp,
						currentScope);

				assign.setRightSide(mathop.getName());
				Contains c = new Contains(assign.toString(), mathop.toString());				
				graph.addVerticesAndEdge(c, mathop);
				graph.addVerticesAndEdge(assign, c);
			} else if (exp instanceof BooleanLiteral
					|| exp instanceof CharacterLiteral
					|| exp instanceof NumberLiteral
					|| exp instanceof StringLiteral) {
				StringDef lit = null;
				if (exp instanceof BooleanLiteral)
					lit = new StringDef(((BooleanLiteral) exp).toString(),
							Boolean.toString(((BooleanLiteral) exp)
									.booleanValue()));
				else if (exp instanceof CharacterLiteral)
					lit = new StringDef(((CharacterLiteral) exp).toString(),
							((CharacterLiteral) exp).getEscapedValue());
				else if (exp instanceof NumberLiteral)
					lit = new StringDef(((NumberLiteral) exp).toString(),
							((NumberLiteral) exp).getToken());
				else if (exp instanceof StringLiteral)
					lit = new StringDef(((StringLiteral) exp).toString(),
							((StringLiteral) exp).getLiteralValue());
				assign.setRightSide(lit.getName());
				Contains c = new Contains(assign.toString(), lit.getName());
				globalAssign = assign;
				globalContains = c;
				graph.addVerticesAndEdge(c, lit);
				graph.addVerticesAndEdge(assign, c);
			} else if (exp instanceof SimpleName) {
				GraphConcept concept = searchIdentifierInScope(exp,
						currentScope);
				if (concept != null) {
					assign.setRightSide(concept.getName());
					Contains c = new Contains(assign.toString(),
							concept.getName());					
					graph.addVerticesAndEdge(c, concept);
					graph.addVerticesAndEdge(assign, c);
				} else {
					// define a null concept. an error has occured as no such
					// variable or field was previously defined
					NullDef nullDef = new NullDef();
					assign.setRightSide(nullDef.getName());
					Contains c = new Contains(assign.toString(),
							nullDef.getName());					
					graph.addVerticesAndEdge(c, nullDef);
					graph.addVerticesAndEdge(assign, c);
				}
			} else if (exp instanceof MethodInvocation) {
				MethodCall methodCall = createMethodCallFromInvocationExpression(
						(MethodInvocation) exp, currentScope);
				assign.setRightSide(methodCall.getName());
				Contains c = new Contains(assign.toString(),
						methodCall.getName());				
				graph.addVerticesAndEdge(c, methodCall);
				graph.addVerticesAndEdge(assign, c);
			}
		}

		return assign;
	}	
	
	/**
	 * Method to create a Method GraphConcept from a method declaration
	 * 
	 * @param node - the MethodDeclaration object
	 * @return a Method of GraphConcept
	 */
	private Method createMethodFromMethodDeclaration(MethodDeclaration node) {

		Method method = null;
		// if it is a method and not constructor
		if (!node.isConstructor()) {
			method = new Method(node.getName().toString(), node.toString());
			method.setReturnType(node.getReturnType2().toString());

			String t = node.modifiers().toString();
			t = t.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", "");
			method.setModifiers(t);
			
			t = node.parameters().toString();
			t = "(" + t.substring(1, t.length()-1) + ")";
			method.setParameterList(t);
			
			// assume only one method
			scopeSet.add(method.getName());
			
			graph.addVertex(method);

			// get the parameter
			List<SingleVariableDeclaration> pList = node.parameters();
			for (SingleVariableDeclaration var : pList) {
				Variable v = new Variable(var.getName().toString(),
						var.toString());
				v.setScope(method.getName());
				v.setType(var.getType().toString());
				
				if(!scopeSet.contains(method.getName()))
					scopeSet.add(method.getName());
				
				Parameter pr = new Parameter(method.toString(), v.getName());
				graph.addVerticesAndEdge(pr, v);
				graph.addVerticesAndEdge(method, pr);
			}

			// get the method implementation
			BlockSyntax methodBlock = null;
			Block block = node.getBody();
			if (block != null && block.statements().size() != 0) {
				methodBlock = createBlockFromBlockSyntax(block,
						method.getName());
			}

			if (methodBlock != null) {
				Contains containsMethod = new Contains(method.getName(),
						methodBlock.getName());
				graph.addVerticesAndEdge(containsMethod, methodBlock);
				graph.addVerticesAndEdge(method, containsMethod);
			}

			// get the return value
			if (!node.getReturnType2().equals("void")) {
				ReturnStatement rs = null;
				List<Statement> stats = node.getBody().statements();
				for (Statement s : stats) {
					if (s instanceof ReturnStatement)
						rs = (ReturnStatement) s;
				}

				if (rs != null) {
					GraphConcept value = createConceptFromExpressionSyntax(
							rs.getExpression(), method.getName());
					Returns ret = new Returns(method.getName(), value.toString());
					graph.addVerticesAndEdge(ret, value);
					graph.addVerticesAndEdge(method, ret);
				}
			}
		} else {
			// if it is a constructor declaration
			method = new Method(node.getName().toString(), node.toString());

			method.setModifiers(node.modifiers().toString());
			method.setParameterList(node.parameters().toString());

			graph.addVertex(method);

			// get the parameter
			List<SingleVariableDeclaration> pList = node.parameters();
			for (SingleVariableDeclaration var : pList) {
				Variable v = new Variable(var.getName().toString(),
						var.toString());
				v.setScope(method.getName());
				v.setType(var.getType().toString());
				
				if(!scopeSet.contains(method.getName()))
					scopeSet.add(method.getName());

				Parameter pr = new Parameter(method.toString(), v.getName());
				graph.addVerticesAndEdge(pr, v);
				graph.addVerticesAndEdge(method, pr);
			}

			// get the constructor implementation
			BlockSyntax methodBlock = null;
			Block block = node.getBody();
			if (block != null && block.statements().size() != 0) {
				methodBlock = createBlockFromBlockSyntax(block,
						method.getName());
			}

			if (methodBlock != null) {
				Contains containsMethod = new Contains(method.getName(),
						methodBlock.getName());
				graph.addVerticesAndEdge(containsMethod, methodBlock);
				graph.addVerticesAndEdge(method, containsMethod);
			}
		}
				
		return method;
	}
	
	/**
	 * Method to create a GraphCocept from an expression
	 * 
	 * @param exp - the Expression 
	 * @param currentScope - the String representing the scope
	 * @return a GraphConcept object
	 */
	private GraphConcept createConceptFromExpressionSyntax(Expression exp,
			String currentScope) {	
		
		if (exp instanceof BooleanLiteral) {
			StringDef lit = new StringDef(((BooleanLiteral) exp).toString(),
					Boolean.toString(((BooleanLiteral) exp).booleanValue()));
			return lit;
		} else if (exp instanceof CharacterLiteral) {
			StringDef lit = new StringDef(((CharacterLiteral) exp).toString(),
					((CharacterLiteral) exp).getEscapedValue());
			return lit;
		} else if (exp instanceof NumberLiteral) {
			StringDef lit = new StringDef(((NumberLiteral) exp).toString(),
					((NumberLiteral) exp).getToken());
			return lit;
		} else if (exp instanceof StringLiteral) {
			StringDef lit = new StringDef(((StringLiteral) exp).toString(),
					((StringLiteral) exp).getLiteralValue());
			return lit;
		} else if (exp instanceof InfixExpression || exp instanceof Assignment) {			
			MathOp mathop = createMathOpFromBinaryExpression(exp, currentScope);
			return mathop;
		} else if (exp instanceof MethodInvocation) {
			MethodCall methodCall = createMethodCallFromInvocationExpression(
					(MethodInvocation) exp, currentScope);
			return methodCall;
		} else if (exp instanceof PrefixExpression
				|| exp instanceof PostfixExpression) {
			MathOp mathop = null;
			Expression op = null;
			if (exp instanceof PrefixExpression) {
				mathop = new MathOp(((PrefixExpression) exp).getOperator()
						.toString());
				op = ((PrefixExpression) exp).getOperand();
			} else {
				mathop = new MathOp(((PostfixExpression) exp).getOperator()
						.toString());
				op = ((PostfixExpression) exp).getOperand();
			}					
			
			GraphConcept concept = searchIdentifierInScope(op, currentScope);
			if (concept != null) {				
				if(exp instanceof PrefixExpression)
					mathop.setRightSide(concept.getName());
				else
					mathop.setLeftSide(concept.getName());
				Contains contains = new Contains(mathop.toString(),
						concept.getName());
				graph.addVerticesAndEdge(contains, concept);
				graph.addVerticesAndEdge(mathop, contains);
			} else {
				NullDef nullDef = new NullDef();
				Contains contains = new Contains(mathop.toString(),
						nullDef.getName());
				graph.addVerticesAndEdge(contains, nullDef);
				graph.addVerticesAndEdge(mathop, contains);
			}

			return mathop;
		} else if (exp instanceof SimpleName) {
			GraphConcept concept = searchIdentifierInScope(exp, currentScope);
			return concept;
		}

		return new GraphConcept();
	}

	/**
	 * Method to get a GraphConcept which identifies a variable
	 * 
	 * @param id - the variable expression
	 * @param currentScope - the String representing the scope
	 * @return a GraphConcept identifying the variable
	 */
	private GraphConcept searchIdentifierInScope(Expression id,
			String currentScope) {
		Variable var = null;
		Field field = null;
		
		Set<GraphConcept> graphSet = graph.vertexSet();
		Iterator<GraphConcept> it;
		
		Iterator<String> scIt = scopeSet.iterator();
		outerloop:
		while(scIt.hasNext()){
			String scp = scIt.next();
			it = graphSet.iterator();
			while (it.hasNext()) {
				GraphConcept temp = it.next();
				if (temp instanceof Variable) {
					if (("Variable: " + id.toString()).equals(((Variable) temp)
							.getName())
							&& scp.equals(((Variable) temp).getScope())) {
						var = (Variable) temp;
						break outerloop;
					}
				}
			}
		}
		
		// find the identifier for this expression in the current scope
		if (var != null)
			return var;
		else {
			// find the identifier in the field level
			it = graphSet.iterator();
			while (it.hasNext()) {
				GraphConcept temp = it.next();
				if (temp instanceof Field) {
					if (("Field: " + id.toString()).equals(((Field) temp)
							.getName())
							&& currentScope.equals(((Field) temp).getScope())) {
						field = (Field) temp;
						break;
					}
				}
			}

			if (field != null)
				return field;
		}

		// if no identifier found
		return null;
	}
	
	/**
	 * Method to create a MethodCall GraphConcept from a method invocation
	 * 
	 * @param inv - the MethodInvocation object
	 * @param currentScope - the String representing the scope
	 * @return a MethodCall of GraphConcept
	 */
	private MethodCall createMethodCallFromInvocationExpression(
			MethodInvocation inv, String currentScope) {
		MethodCall method = null;

		Expression e = inv.getExpression();
		SimpleName name = inv.getName();
		String mCall = "";
		if (e != null) {
			if (e.toString().startsWith("this."))
				mCall = e.toString().substring(5) + ".";
			else
				mCall = e.toString() + ".";
		}

		mCall += name.toString() + "()";
		method = new MethodCall(mCall);
		method.setArguments(inv.arguments().toString());
		method.setValue(inv.toString());

		List<Expression> argList = inv.arguments();
		for (Expression exp : argList) {
			if (exp instanceof BooleanLiteral
					|| exp instanceof CharacterLiteral
					|| exp instanceof NumberLiteral
					|| exp instanceof StringLiteral) {
				StringDef lit = null;
				if (exp instanceof BooleanLiteral)
					lit = new StringDef(((BooleanLiteral) exp).toString(),
							Boolean.toString(((BooleanLiteral) exp)
									.booleanValue()));
				else if (exp instanceof CharacterLiteral)
					lit = new StringDef(((CharacterLiteral) exp).toString(),
							((CharacterLiteral) exp).getEscapedValue());
				else if (exp instanceof NumberLiteral)
					lit = new StringDef(((NumberLiteral) exp).toString(),
							((NumberLiteral) exp).getToken());
				else if (exp instanceof StringLiteral)
					lit = new StringDef(((StringLiteral) exp).toString(),
							((StringLiteral) exp).getLiteralValue());
				Parameter param = new Parameter(method.getName(), lit.getName());
				graph.addVerticesAndEdge(param, lit);
				graph.addVerticesAndEdge(method, param);
			} else if (exp instanceof InfixExpression
					|| exp instanceof Assignment) {
				MathOp mathop = createMathOpFromBinaryExpression(exp,
						currentScope);
				Parameter param = new Parameter(method.getName(),
						mathop.toString());
				graph.addVerticesAndEdge(param, mathop);
				graph.addVerticesAndEdge(method, param);
			} else if (exp instanceof SimpleName) {
				GraphConcept concept = searchIdentifierInScope(inv,
						currentScope);
				if (concept != null) {
					Parameter param = new Parameter(method.getName(),
							concept.getName());
					graph.addVerticesAndEdge(param, concept);
					graph.addVerticesAndEdge(method, method);
				} else {
					// define a null concept. an error has occured as no such
					// variable or field was previously defined
					NullDef nullDef = new NullDef();
					Parameter param = new Parameter(method.getName(),
							nullDef.getName());
					graph.addVerticesAndEdge(param, concept);
					graph.addVerticesAndEdge(method, param);
				}
			}
		}

		return method;
	}

	/**
	 * Method to create a MathOp GraphConcept from a mathematical binary expression
	 * @param exp - the binary expression
	 * @param currentScope - the String representing the scope
	 * @return a MathOp of GraphConcept
	 */
	private MathOp createMathOpFromBinaryExpression(Expression exp,
			String currentScope) {
		
		MathOp mathop = null;
		
		// if the expression is an InfixExpression
		if (exp instanceof InfixExpression) {
			InfixExpression inf = (InfixExpression) exp;
			Expression lhs = inf.getLeftOperand();
			Expression rhs = inf.getRightOperand();
			
			String op = inf.getOperator().toString();

			if (lhs instanceof PrefixExpression
					|| lhs instanceof ParenthesizedExpression
					|| lhs instanceof CastExpression) {
				lhs = getSyntaxNodeFromParenthesis(lhs);
			}

			if (rhs instanceof PrefixExpression
					|| rhs instanceof ParenthesizedExpression
					|| rhs instanceof CastExpression) {
				rhs = getSyntaxNodeFromParenthesis(rhs);
			}

			mathop = new MathOp(inf.getOperator().toString());
			mathop.setValue(lhs.toString() + " " + op + " " + rhs.toString());
			
			Contains cLeft = null;

			// process the left hand side
			if (lhs instanceof InfixExpression || lhs instanceof Assignment) {
				MathOp m = createMathOpFromBinaryExpression(lhs, currentScope);
				mathop.setLeftSide(m.getName());
				cLeft = new Contains(mathop.toString(), m.getName());				
				graph.addVerticesAndEdge(cLeft, m);
				graph.addVerticesAndEdge(mathop, cLeft);
			} else if (lhs instanceof BooleanLiteral
					|| lhs instanceof CharacterLiteral
					|| lhs instanceof NumberLiteral
					|| lhs instanceof StringLiteral) {
				StringDef lit = null;
				if (lhs instanceof BooleanLiteral)
					lit = new StringDef(((BooleanLiteral) lhs).toString(),
							Boolean.toString(((BooleanLiteral) lhs)
									.booleanValue()));
				else if (lhs instanceof CharacterLiteral)
					lit = new StringDef(((CharacterLiteral) lhs).toString(),
							((CharacterLiteral) lhs).getEscapedValue());
				else if (lhs instanceof NumberLiteral)
					lit = new StringDef(((NumberLiteral) lhs).toString(),
							((NumberLiteral) lhs).getToken());
				else if (lhs instanceof StringLiteral)
					lit = new StringDef(((StringLiteral) lhs).toString(),
							((StringLiteral) lhs).getLiteralValue());
				cLeft = new Contains(mathop.toString(), lit.getName());
				mathop.setLeftSide(lit.getName());
				graph.addVerticesAndEdge(cLeft, lit);
				graph.addVerticesAndEdge(mathop, cLeft);
			} else if (lhs instanceof SimpleName) {
				GraphConcept concept = searchIdentifierInScope(lhs,
						currentScope);
				if (concept != null) {
					mathop.setLeftSide(concept.getName());
					cLeft = new Contains(mathop.toString(),
							concept.getName());					
					graph.addVerticesAndEdge(cLeft, concept);
					graph.addVerticesAndEdge(mathop, cLeft);
				} else {
					// define a null concept. an error has occured as no such
					// variable or field was previously defined
					NullDef nullDef = new NullDef();
					mathop.setLeftSide(nullDef.getName());
					cLeft = new Contains(mathop.toString(),
							nullDef.getName());					
					graph.addVerticesAndEdge(cLeft, nullDef);
					graph.addVerticesAndEdge(mathop, cLeft);
				}
			} else if (lhs instanceof MethodInvocation) {
				MethodCall methodCall = createMethodCallFromInvocationExpression(
						(MethodInvocation) lhs, currentScope);
				mathop.setLeftSide(methodCall.getName());
				cLeft = new Contains(mathop.toString(),
						methodCall.getName());				
				graph.addVerticesAndEdge(cLeft, methodCall);
				graph.addVerticesAndEdge(mathop, cLeft);
			}
			
			// process the right hand side
			if (rhs instanceof InfixExpression || rhs instanceof Assignment) {
				MathOp m = createMathOpFromBinaryExpression(rhs, currentScope);
				mathop.setRightSide(m.getName());
				Contains c = new Contains(mathop.toString(), m.getName());				
				graph.addVerticesAndEdge(c, m);
				graph.addVerticesAndEdge(mathop, c);
			} else if (rhs instanceof BooleanLiteral
					|| rhs instanceof CharacterLiteral
					|| rhs instanceof NumberLiteral
					|| rhs instanceof StringLiteral) {
				StringDef lit = null;
				if (rhs instanceof BooleanLiteral)
					lit = new StringDef(((BooleanLiteral) rhs).toString(),
							Boolean.toString(((BooleanLiteral) rhs)
									.booleanValue()));
				else if (rhs instanceof CharacterLiteral)
					lit = new StringDef(((CharacterLiteral) rhs).toString(),
							((CharacterLiteral) rhs).getEscapedValue());
				else if (rhs instanceof NumberLiteral)
					lit = new StringDef(((NumberLiteral) rhs).toString(),
							((NumberLiteral) rhs).getToken());
				else if (rhs instanceof StringLiteral)
					lit = new StringDef(((StringLiteral) rhs).toString(),
							((StringLiteral) rhs).getLiteralValue());
				mathop.setRightSide(lit.getName());
				Contains c = new Contains(mathop.toString(), lit.getName());				
				graph.addVerticesAndEdge(c, lit);
				graph.addVerticesAndEdge(mathop, c);
			} else if (rhs instanceof SimpleName) {
				GraphConcept concept = searchIdentifierInScope(rhs,
						currentScope);
				if (concept != null) {
					mathop.setRightSide(concept.getName());
					Contains c = new Contains(mathop.toString(),
							concept.getName());					
					graph.addVerticesAndEdge(c, concept);
					graph.addVerticesAndEdge(mathop, c);
				} else {
					// define a null concept. an error has occured as no such
					// variable or field was previously defined
					NullDef nullDef = new NullDef();
					mathop.setRightSide(nullDef.getName());
					Contains c = new Contains(mathop.toString(),
							nullDef.getName());					
					graph.addVerticesAndEdge(c, nullDef);
					graph.addVerticesAndEdge(mathop, c);
				}
			} else if (rhs instanceof MethodInvocation) {
				MethodCall methodCall = createMethodCallFromInvocationExpression(
						(MethodInvocation) rhs, currentScope);
				mathop.setRightSide(methodCall.getName());
				Contains c = new Contains(mathop.toString(),
						methodCall.getName());				
				graph.addVerticesAndEdge(c, methodCall);
				graph.addVerticesAndEdge(mathop, c);
			}
			if(cLeft != null)
				cLeft.setInConcept(mathop.toString());
		} else if (exp instanceof Assignment) {
			// if the expression is an Assignment
			Assignment inf = (Assignment) exp;
			Expression lhs = inf.getLeftHandSide();
			Expression rhs = inf.getRightHandSide();
			if (lhs instanceof PrefixExpression
					|| lhs instanceof ParenthesizedExpression
					|| lhs instanceof CastExpression) {
				lhs = getSyntaxNodeFromParenthesis(lhs);
			}

			if (rhs instanceof PrefixExpression
					|| rhs instanceof ParenthesizedExpression
					|| rhs instanceof CastExpression) {
				rhs = getSyntaxNodeFromParenthesis(rhs);
			}
			
			mathop = new MathOp(inf.getOperator().toString());
			Assignment a = (Assignment) inf;			
			mathop.setValue(a.getLeftHandSide() + " " + a.getOperator() + " " + a.getRightHandSide());
			
			Contains cLeft = null;
			
			// process the left hand side
			if (lhs instanceof InfixExpression || lhs instanceof Assignment) {
				MathOp m = createMathOpFromBinaryExpression(lhs, currentScope);
				mathop.setLeftSide(m.getName());
				cLeft = new Contains(mathop.toString(), m.getName());				
				graph.addVerticesAndEdge(cLeft, m);
				graph.addVerticesAndEdge(mathop, cLeft);
			} else if (lhs instanceof BooleanLiteral
					|| lhs instanceof CharacterLiteral
					|| lhs instanceof NumberLiteral
					|| lhs instanceof StringLiteral) {
				StringDef lit = null;
				if (lhs instanceof BooleanLiteral)
					lit = new StringDef(((BooleanLiteral) lhs).toString(),
							Boolean.toString(((BooleanLiteral) lhs)
									.booleanValue()));
				else if (lhs instanceof CharacterLiteral)
					lit = new StringDef(((CharacterLiteral) lhs).toString(),
							((CharacterLiteral) lhs).getEscapedValue());
				else if (lhs instanceof NumberLiteral)
					lit = new StringDef(((NumberLiteral) lhs).toString(),
							((NumberLiteral) lhs).getToken());
				else if (lhs instanceof StringLiteral)
					lit = new StringDef(((StringLiteral) lhs).toString(),
							((StringLiteral) lhs).getLiteralValue());
				mathop.setLeftSide(lit.getName());
				cLeft = new Contains(mathop.toString(), lit.getName());				
				graph.addVerticesAndEdge(cLeft, lit);
				graph.addVerticesAndEdge(mathop, cLeft);
			} else if (lhs instanceof SimpleName) {
				GraphConcept concept = searchIdentifierInScope(lhs,
						currentScope);
				if (concept != null) {
					mathop.setLeftSide(concept.getName());
					cLeft = new Contains(mathop.toString(),
							concept.getName());					
					graph.addVerticesAndEdge(cLeft, concept);
					graph.addVerticesAndEdge(mathop, cLeft);
				} else {
					// define a null concept. an error has occured as no such
					// variable or field was previously defined
					NullDef nullDef = new NullDef();
					mathop.setLeftSide(nullDef.getName());
					cLeft = new Contains(mathop.toString(),
							nullDef.getName());					
					graph.addVerticesAndEdge(cLeft, nullDef);
					graph.addVerticesAndEdge(mathop, cLeft);
				}
			} else if (lhs instanceof MethodInvocation) {
				MethodCall methodCall = createMethodCallFromInvocationExpression(
						(MethodInvocation) lhs, currentScope);
				mathop.setLeftSide(methodCall.getName());
				cLeft = new Contains(mathop.toString(),
						methodCall.getName());				
				graph.addVerticesAndEdge(cLeft, methodCall);
				graph.addVerticesAndEdge(mathop, cLeft);
			}

			// process the right hand side
			if (rhs instanceof InfixExpression || rhs instanceof Assignment) {
				MathOp m = createMathOpFromBinaryExpression(rhs, currentScope);
				mathop.setRightSide(m.getName());
				Contains c = new Contains(mathop.toString(), m.getName());				
				graph.addVerticesAndEdge(c, m);
				graph.addVerticesAndEdge(mathop, c);
			} else if (rhs instanceof BooleanLiteral
					|| rhs instanceof CharacterLiteral
					|| rhs instanceof NumberLiteral
					|| rhs instanceof StringLiteral) {
				StringDef lit = null;
				if (rhs instanceof BooleanLiteral)
					lit = new StringDef(((BooleanLiteral) rhs).toString(),
							Boolean.toString(((BooleanLiteral) rhs)
									.booleanValue()));
				else if (rhs instanceof CharacterLiteral)
					lit = new StringDef(((CharacterLiteral) rhs).toString(),
							((CharacterLiteral) rhs).getEscapedValue());
				else if (rhs instanceof NumberLiteral)
					lit = new StringDef(((NumberLiteral) rhs).toString(),
							((NumberLiteral) rhs).getToken());
				else if (rhs instanceof StringLiteral)
					lit = new StringDef(((StringLiteral) rhs).toString(),
							((StringLiteral) rhs).getLiteralValue());
				mathop.setRightSide(lit.getName());
				Contains c = new Contains(mathop.toString(), lit.getName());				
				graph.addVerticesAndEdge(c, lit);
				graph.addVerticesAndEdge(mathop, c);
			} else if (rhs instanceof SimpleName) {
				GraphConcept concept = searchIdentifierInScope(rhs,
						currentScope);
				if (concept != null) {
					mathop.setRightSide(concept.getName());
					Contains c = new Contains(mathop.toString(),
							concept.getName());					
					graph.addVerticesAndEdge(c, concept);
					graph.addVerticesAndEdge(mathop, c);
				} else {
					// define a null concept. an error has occured as no such
					// variable or field was previously defined
					NullDef nullDef = new NullDef();
					mathop.setRightSide(nullDef.getName());
					Contains c = new Contains(mathop.toString(),
							nullDef.getName());					
					graph.addVerticesAndEdge(c, nullDef);
					graph.addVerticesAndEdge(mathop, c);
				}
			} else if (rhs instanceof MethodInvocation) {
				MethodCall methodCall = createMethodCallFromInvocationExpression(
						(MethodInvocation) rhs, currentScope);
				mathop.setRightSide(methodCall.getName());
				Contains c = new Contains(mathop.toString(),
						methodCall.getName());				
				graph.addVerticesAndEdge(c, methodCall);
				graph.addVerticesAndEdge(mathop, c);
			}
			if(cLeft != null)
				cLeft.setInConcept(mathop.toString());
		}

		return mathop;
	}

	/**
	 * Method to get the Expression written inside parenthesis
	 * 
	 * @param node - the Expression inside parenthesis
	 * @return the real Expression
	 */
	private Expression getSyntaxNodeFromParenthesis(Expression node) {
		Expression e = null;

		if (node instanceof CastExpression) {
			e = ((CastExpression) node).getExpression();
		} else if (node instanceof ParenthesizedExpression) {
			e = ((ParenthesizedExpression) node).getExpression();
		} else if (node instanceof PrefixExpression) {
			e = ((PrefixExpression) node).getOperand();
		}

		return e;
	}

	/**
	 * Method to create BlockSyntax GraphConcept from block declaration
	 * 
	 * @param block - the Block declaration object
	 * @param scope - the String representing the scope
	 * @return a BlockSyntax of GraphConcept
	 */
	private BlockSyntax createBlockFromBlockSyntax(Block block, String scope) {				
		BlockSyntax methodBlock = new BlockSyntax(scope, "");
		List<Statement> childStatement = block.statements();
		for (Statement s : childStatement) {
			if (s instanceof VariableDeclarationStatement) {
				VariableDeclarationFragment frag = (VariableDeclarationFragment) ((VariableDeclarationStatement) s)
						.fragments().get(0);
				String type = ((VariableDeclarationStatement) s).getType()
						.toString();
				GraphConcept concept = createConceptFromLocalDeclaration(frag,
						type, scope);			
				
				if (concept != null) {
					Contains c = new Contains(methodBlock.getName(),
							concept.toString());
					graph.addVerticesAndEdge(c, concept);
					graph.addVerticesAndEdge(methodBlock, c);
				}
			} else if (s instanceof ExpressionStatement) {
				Expression exp = ((ExpressionStatement) s).getExpression();
				if (exp instanceof InfixExpression) {
					MathOp mathop = createMathOpFromBinaryExpression(exp, scope);

					Contains c = new Contains(methodBlock.getName(),
							mathop.toString());
					graph.addVerticesAndEdge(c, mathop);
					graph.addVerticesAndEdge(methodBlock, c);
				} else if(exp instanceof Assignment){
					AssignOp assign = createAssignOpFromBinaryExpression(exp, scope);
					
					if (assign != null){
						Contains c2 = new Contains(methodBlock.getName(), assign.toString());
						graph.addVerticesAndEdge(c2, assign);
                        graph.addVerticesAndEdge(methodBlock, c2);
                    }
				}else if (exp instanceof PrefixExpression
						|| exp instanceof PostfixExpression) {
					MathOp mathop = null;
					Expression op = null;
					if (exp instanceof PrefixExpression) {
						mathop = new MathOp(((PrefixExpression) exp)
								.getOperator().toString());
						op = ((PrefixExpression) exp).getOperand();
					} else {
						mathop = new MathOp(((PostfixExpression) exp)
								.getOperator().toString());
						op = ((PostfixExpression) exp).getOperand();
					}

					GraphConcept concept = searchIdentifierInScope(op, scope);
					if (concept != null) {
						Contains contains = new Contains(mathop.getName(),
								concept.getName());
						graph.addVerticesAndEdge(contains, concept);
						graph.addVerticesAndEdge(mathop, contains);
						
						Contains contains2 = new Contains(
								methodBlock.getName(), mathop.getName());
						graph.addVerticesAndEdge(contains2, mathop);
						graph.addVerticesAndEdge(methodBlock, contains2);						
					}
				} else if (exp instanceof MethodInvocation) {
					MethodCall methodCall = createMethodCallFromInvocationExpression(
							(MethodInvocation) exp, scope);
					Contains contains = new Contains(methodBlock.getName(),
							methodCall.getName());
					graph.addVerticesAndEdge(contains, methodCall);
					graph.addVerticesAndEdge(methodBlock, contains);
				}
			} else if (s instanceof IfStatement) {
				GraphConcept ifExpr = createConceptFromIfExpression(
						(IfStatement) s, scope);
				if (ifExpr != null) {
					Contains contains = new Contains(methodBlock.getName(),
							ifExpr.getName());
					graph.addVerticesAndEdge(contains, ifExpr);
					graph.addVerticesAndEdge(methodBlock, contains);
				}
			} else if (s instanceof WhileStatement || s instanceof DoStatement
					|| s instanceof ForStatement
					|| s instanceof EnhancedForStatement) {
				Loop loop = null;
				if (s instanceof WhileStatement) {
					loop = createLoopConceptFromLoopExpression(
							(WhileStatement) s, scope);
				} else if (s instanceof DoStatement) {
					loop = createLoopConceptFromLoopExpression((DoStatement) s,
							scope);
				} else if (s instanceof ForStatement) {
					loop = createLoopConceptFromLoopExpression(
							(ForStatement) s, scope);
				} else if (s instanceof EnhancedForStatement) {
					loop = createLoopConceptFromLoopExpression(
							(EnhancedForStatement) s, scope);
				}

				if (loop != null) {
					// method block contains loop
					Contains contains = new Contains(methodBlock.getName(),
							loop.toString());
					graph.addVerticesAndEdge(contains, loop);
					graph.addVerticesAndEdge(methodBlock, contains);
				}
			} else if (s instanceof TryStatement) {
				Try tryStatement = createTryStatement((TryStatement) s, scope);
				if (tryStatement != null) {
					tryStatement.setName("TryCatchStatement: *");
					Contains contains = new Contains(methodBlock.getName(),
							tryStatement.getName());
					graph.addVerticesAndEdge(contains, tryStatement);
					graph.addVerticesAndEdge(methodBlock, contains);
				}
			} else if (s instanceof SwitchStatement) {
				Switch switchStatement = createSwitchStatement(
						(SwitchStatement) s, scope);

				if (switchStatement != null) {
					switchStatement.setName("Switch: *");
					Contains contains = new Contains(methodBlock.getName(),
							switchStatement.getName());
					graph.addVerticesAndEdge(contains, switchStatement);
					graph.addVerticesAndEdge(methodBlock, contains);
				}
			}else if(s instanceof ReturnStatement && !scope.startsWith("Method")){
				GraphConcept value = createConceptFromExpressionSyntax(
						((ReturnStatement) s).getExpression(), "Block: " + scope);
				Returns myreturn = new Returns("Block: " + scope, value.toString());
				myreturn.setValue(((ReturnStatement) s).getExpression()
						.toString());
				graph.addVerticesAndEdge(myreturn, value);
				graph.addVerticesAndEdge(methodBlock, myreturn);
			}
		}
		return methodBlock;
	}

	/**
	 * Method to create Switch GraphConcept from switch statement
	 * 
	 * @param switchStatement - the SwitchStatement object
	 * @param currentScope - the String representing the scope
	 * @return a Switch of GraphConcept
	 */
	private Switch createSwitchStatement(SwitchStatement switchStatement,
			String currentScope) {
		Switch sWitch = new Switch();

		Expression expr = switchStatement.getExpression();
		if (expr != null) {
			GraphConcept switchExpression = createConceptFromExpressionSyntax(
					expr, currentScope);
			if (switchExpression != null) {
				Condition condition = new Condition(sWitch.getName(),
						switchExpression.getName());
				graph.addVerticesAndEdge(condition, switchExpression);
				graph.addVerticesAndEdge(sWitch, condition);
			}
		}

		List<Statement> stats = switchStatement.statements();
		for (Statement s : stats) {
			if (s instanceof ExpressionStatement) {
				GraphConcept concept = createConceptFromStatementSyntax(
						(ExpressionStatement) s, currentScope);
				if (concept != null) {
					Contains contains = new Contains(sWitch.getName(),
							concept.toString());
					graph.addVerticesAndEdge(contains, concept);
					graph.addVerticesAndEdge(sWitch, contains);
				}
			}
		}

		return sWitch;
	}
	
	/**
	 * Method to create a GraphConcept from a statement
	 * 
	 * @param stat - the Statement object
	 * @param currentScope - the String representing the scope
	 * @return a GraphConcept
	 */
	private GraphConcept createConceptFromStatementSyntax(Statement stat,
			String currentScope) {
		if (stat instanceof VariableDeclarationStatement) {
			VariableDeclarationFragment frag = (VariableDeclarationFragment) ((VariableDeclarationStatement) stat)
					.fragments().get(0);
			String type = ((VariableDeclarationStatement) stat).getType()
					.toString();
			return createConceptFromLocalDeclaration(frag, type, currentScope);
		} else if (stat instanceof ExpressionStatement) {
			Expression exp = ((ExpressionStatement) stat).getExpression();
			if (exp instanceof InfixExpression || exp instanceof Assignment) {
				return createAssignOpFromBinaryExpression(exp, currentScope);
			} else if (exp instanceof PrefixExpression
					|| exp instanceof PostfixExpression) {
				MathOp mathop = null;
				Expression op = null;
				if (exp instanceof PrefixExpression) {
					mathop = new MathOp(((PrefixExpression) exp).getOperator()
							.toString());
					op = ((PrefixExpression) exp).getOperand();
				} else {
					mathop = new MathOp(((PostfixExpression) exp).getOperator()
							.toString());
					op = ((PostfixExpression) exp).getOperand();
				}

				GraphConcept concept = searchIdentifierInScope(op, currentScope);
				if (concept != null) {
					Contains contains = new Contains(mathop.getName(),
							concept.getName());
					graph.addVerticesAndEdge(contains, concept);
					graph.addVerticesAndEdge(mathop, contains);
				}
				return mathop;
			} else if (exp instanceof MethodInvocation) {
				return createMethodCallFromInvocationExpression(
						(MethodInvocation) exp, currentScope);
			}
		} else if (stat instanceof IfStatement) {
			return createConceptFromIfExpression((IfStatement) stat,
					currentScope);
		} else if (stat instanceof WhileStatement) {
			return createLoopConceptFromLoopExpression((WhileStatement) stat,
					currentScope);
		} else if (stat instanceof DoStatement) {
			return createLoopConceptFromLoopExpression((DoStatement) stat,
					currentScope);
		} else if (stat instanceof ForStatement) {
			return createLoopConceptFromLoopExpression((ForStatement) stat,
					currentScope);
		} else if (stat instanceof EnhancedForStatement) {
			return createLoopConceptFromLoopExpression(
					(EnhancedForStatement) stat, currentScope);
		} else if (stat instanceof TryStatement) {
			return createTryStatement((TryStatement) stat, currentScope);
		} else if (stat instanceof SwitchStatement) {
			return createSwitchStatement((SwitchStatement) stat, currentScope);
		} else if (stat instanceof ReturnStatement) {
			GraphConcept value = createConceptFromExpressionSyntax(
					((ReturnStatement) stat).getExpression(), currentScope);
			Returns myreturn = new Returns(currentScope, value.toString());
			myreturn.setValue(((ReturnStatement) stat).getExpression()
					.toString());
			graph.addVerticesAndEdge(myreturn, value);
			return myreturn;
		}
		return null;
	}

	/**
	 * Method to create an AssignOp GraphConcept from a binary expression
	 * 
	 * @param exp - the binary Expression
	 * @param currentScope - the String representing the scope
	 * @return an AssignOp of GraphConcept
	 */
	private AssignOp createAssignOpFromBinaryExpression(Expression exp,
			String currentScope) {
		AssignOp assign = new AssignOp();		
		Assignment as = (Assignment) exp;
		String asop = as.getOperator().toString();
		String ls = as.getLeftHandSide().toString();
		String rs = as.getRightHandSide().toString();
		assign.setValue(ls + " " + asop + " " + rs);

		List<Expression> childs = new ArrayList<Expression>();
		childs.add(exp);

		List<Expression> adds = new ArrayList<Expression>();
		for (Expression e : childs) {
			if (e instanceof ParenthesizedExpression
					|| e instanceof PrefixExpression
					|| e instanceof CastExpression) {
				adds.add(getSyntaxNodeFromParenthesis(e));
			}
			// skip the MemberAccessExpressionSyntax??
		}
		childs.addAll(adds);

		for (Expression e : childs) {
			if (e instanceof InfixExpression || e instanceof Assignment) {
				if(e instanceof InfixExpression){
					MathOp mathop = createMathOpFromBinaryExpression(e,
							currentScope);

					Contains c = new Contains(assign.toString(), mathop.getName());
					graph.addVerticesAndEdge(c, mathop);
					graph.addVerticesAndEdge(assign, c);
				}
				
			} else if (e instanceof BooleanLiteral
					|| e instanceof CharacterLiteral
					|| e instanceof NumberLiteral || e instanceof StringLiteral) {
				StringDef lit = null;
				if (e instanceof BooleanLiteral)
					lit = new StringDef(((BooleanLiteral) e).toString(),
							Boolean.toString(((BooleanLiteral) e)
									.booleanValue()));
				else if (e instanceof CharacterLiteral)
					lit = new StringDef(((CharacterLiteral) e).toString(),
							((CharacterLiteral) e).getEscapedValue());
				else if (e instanceof NumberLiteral)
					lit = new StringDef(((NumberLiteral) e).toString(),
							((NumberLiteral) e).getToken());
				else if (e instanceof StringLiteral)
					lit = new StringDef(((StringLiteral) e).toString(),
							((StringLiteral) e).getLiteralValue());
				Contains c = new Contains(assign.toString(), lit.getName());
				graph.addVerticesAndEdge(c, lit);
				graph.addVerticesAndEdge(assign, c);
			} else if (e instanceof SimpleName) {
				GraphConcept concept = searchIdentifierInScope(e, currentScope);
				if (concept != null) {
					Contains c = new Contains(assign.toString(),
							concept.getName());
					graph.addVerticesAndEdge(c, concept);
					graph.addVerticesAndEdge(assign, c);
				} else {
					// define a null concept. an error has occured as no such
					// variable or field was previously defined
					NullDef nullDef = new NullDef();
					Contains c = new Contains(assign.getName(),
							nullDef.getName());
					graph.addVerticesAndEdge(c, nullDef);
					graph.addVerticesAndEdge(assign, c);
				}
			} else if (e instanceof MethodInvocation) {
				MethodCall methodCall = createMethodCallFromInvocationExpression(
						(MethodInvocation) e, currentScope);
				Contains c = new Contains(assign.toString(),
						methodCall.getName());
				graph.addVerticesAndEdge(c, methodCall);
				graph.addVerticesAndEdge(assign, c);
			}
		}

		return assign;
	}
	
	/**
	 * Method to create Try GraphConcept from try declaration
	 * 
	 * @param tryStatementSyntax - the TryStatement object
	 * @param currentScope - the String representing the scope
	 * @return a Try of GraphConcept
	 */
	private Try createTryStatement(TryStatement tryStatementSyntax,
			String currentScope) {

		Try tryStatement = new Try();
		Block block = tryStatementSyntax.getBody();
		if (block != null) {
			BlockSyntax tryBlock = createBlockFromBlockSyntax(block,
					currentScope);
			if (tryBlock != null) {
				Contains contains = new Contains(tryStatement.getName(),
						tryBlock.getName());
				graph.addVerticesAndEdge(contains, tryBlock);
				graph.addVerticesAndEdge(tryStatement, contains);
			}
		}

		List<CatchClause> catchs = tryStatementSyntax.catchClauses();
		for (CatchClause c : catchs) {
			BlockSyntax catchBlock = createBlockFromBlockSyntax(c.getBody(),
					currentScope);
			if (catchBlock != null) {
				Contains contains = new Contains(tryStatement.getName(),
						catchBlock.getName());
				graph.addVerticesAndEdge(contains, catchBlock);
				graph.addVerticesAndEdge(tryStatement, contains);
			}
		}

		return tryStatement;
	}

	/**
	 * Method to create Loop GraphConcept from loop declaration
	 * 
	 * @param loopStatement - the Statement for the loop declaration
	 * @param currentScope - the String representing the scope
	 * @return a Loop of GraphConcept
	 */
	private Loop createLoopConceptFromLoopExpression(Statement loopStatement,
			String currentScope) {
		String loopExp = loopStatement.toString().substring(0,
				loopStatement.toString().indexOf(")") + 1);		
		
		Statement stmt = null;
		String loopIdent = "";
		if (loopStatement instanceof ForStatement){
			stmt = (ForStatement) loopStatement;
			loopIdent = "Loop: For";
		}else if (loopStatement instanceof WhileStatement){
			stmt = (WhileStatement) loopStatement;
			loopIdent = "Loop: While";
		}else if (loopStatement instanceof DoStatement){
			stmt = (DoStatement) loopStatement;
			loopIdent = "Loop: DoWhile";
		}else if (loopStatement instanceof EnhancedForStatement){
			stmt = (EnhancedForStatement) loopStatement;
			loopIdent = "Loop: Foreach";
		}

		
		Loop loop = new Loop(loopIdent.substring(6), loopExp);

		// the condition is an Expression from getExpression
		Expression exp = null;
		if (stmt instanceof ForStatement)
			exp = ((ForStatement) stmt).getExpression();
		else if (stmt instanceof WhileStatement)
			exp = ((WhileStatement) stmt).getExpression();
		else if (stmt instanceof DoStatement)
			exp = ((DoStatement) stmt).getExpression();
		
		// initialization part in for loop
		if (stmt instanceof ForStatement) {
			loop.setConditions(exp.toString());
			List<VariableDeclarationExpression> inits = ((ForStatement) stmt)
					.initializers();
			for (VariableDeclarationExpression v : inits) {
				List<VariableDeclarationFragment> frags = v.fragments();
				for (VariableDeclarationFragment f : frags) {

					String tes = v.getType().toString();
					
					GraphConcept concept = createConceptFromLocalDeclaration(f,
							v.getType().toString(), currentScope);
					
					if (concept != null) {
						Contains contains = new Contains(loop.toString(),
								concept.toString());
						graph.addVerticesAndEdge(contains, concept);
						graph.addVerticesAndEdge(loop, contains);
					}
				}
			}
		}							
		
		
		// get the condition of the loop
		if (exp != null) {
			loop.setConditions(exp.toString());
			
			CompareOp compare = createCompareOpFromBinaryExpression(exp,
					currentScope);

			if (compare != null) {				
				Condition condition = new Condition(loop.toString(),
						compare.toString());
				graph.addVerticesAndEdge(condition, compare);
				graph.addVerticesAndEdge(loop, condition);
			}
		}	
		
		// get the updater part from a For loop
		if(stmt instanceof ForStatement){
			List<Expression> upds = ((ForStatement) stmt).updaters();
			for(Expression e : upds){
				GraphConcept concept = createConceptFromExpressionSyntax(e, currentScope);
				if (concept != null) {
					Contains contains = new Contains(loop.toString(),
							concept.toString());
					graph.addVerticesAndEdge(contains, concept);
					graph.addVerticesAndEdge(loop, contains);
				}
			}
		}

		Statement bodyStatement = null;
		if (stmt instanceof ForStatement)
			bodyStatement = ((ForStatement) stmt).getBody();
		else if (stmt instanceof WhileStatement)
			bodyStatement = ((WhileStatement) stmt).getBody();
		else if (stmt instanceof DoStatement)
			bodyStatement = ((DoStatement) stmt).getBody();
		else if (stmt instanceof EnhancedForStatement)
			bodyStatement = ((EnhancedForStatement) stmt).getBody();
		
		// create the block of the loop body
		if (bodyStatement instanceof Block) {
			String li = loopIdent + "\tType: " + loop.getType() + "\tCondition: " + loop.getConditions();
			BlockSyntax loopblock = createBlockFromBlockSyntax(
					(Block) bodyStatement, li);
			Contains contains = new Contains(li,
					loopblock.getName());
			graph.addVerticesAndEdge(contains, loopblock);
			graph.addVerticesAndEdge(loop, contains);
		} else {
			// create the loop body
			GraphConcept concept = createConceptFromExpressionSyntax(
					(ExpressionStatement) bodyStatement, currentScope);
			if (concept != null) {
				Contains contains = new Contains(loopIdent,
						concept.getName());
				graph.addVerticesAndEdge(contains, concept);
				graph.addVerticesAndEdge(loop, contains);
			}
		}
		
		return loop;
	}

	/**
	 * Method to create a GraphConcept from If declaration
	 * 
	 * @param ifExpression - the IfStatement object
	 * @param currentScope - the String representing the scope
	 * @return a GraphObject for If declaration
	 */
	private GraphConcept createConceptFromIfExpression(
			IfStatement ifExpression, String currentScope) {
		
		If ifExp = new If(ifExpression.getExpression().toString());
		CompareOp compare = createCompareOpFromBinaryExpression(
				ifExpression.getExpression(), currentScope);
		
		// create the if condition
		if (compare != null) {
			Condition condition = new Condition(ifExp.getName(),
					compare.toString());
			graph.addVerticesAndEdge(condition, compare);
			graph.addVerticesAndEdge(ifExp, condition);
		}

		Statement thenStatement = ifExpression.getThenStatement();
		// Then part contains block
		if (thenStatement instanceof Block) {
			BlockSyntax thenBlock = createBlockFromBlockSyntax(
					(Block) thenStatement, ifExp.getName());
			Contains contains = new Contains(ifExp.getName(),
					thenBlock.getName());
			graph.addVerticesAndEdge(contains, thenBlock);
			graph.addVerticesAndEdge(ifExp, contains);
		}else if(thenStatement instanceof ReturnStatement){
			// Then contains Return
			GraphConcept value = createConceptFromExpressionSyntax(
					((ReturnStatement) thenStatement).getExpression(), currentScope);
			Returns myreturn = new Returns(ifExp.getName(), value.toString());
			myreturn.setValue(((ReturnStatement) thenStatement).getExpression()
					.toString());
			graph.addVerticesAndEdge(myreturn, value);
			graph.addVerticesAndEdge(ifExp, myreturn);
		}else {
			// create the Then part
			GraphConcept concept = createConceptFromExpressionSyntax(
					(ExpressionStatement) thenStatement, currentScope);

			if (concept != null) {
				Contains contains = new Contains(ifExp.getName(),
						concept.getName());
				graph.addVerticesAndEdge(contains, concept);
				graph.addVerticesAndEdge(ifExp, contains);
			}
		}

		Statement elseStatement = ifExpression.getElseStatement();
		// create the Else part
		if (elseStatement != null) {
			// Else contains block
			if (elseStatement instanceof Block) {
				BlockSyntax elseBlock = createBlockFromBlockSyntax(
						(Block) elseStatement, "Else: " + ifExp.getName());
				Contains contains = new Contains(ifExp.getName(),
						elseBlock.getName());
				graph.addVerticesAndEdge(contains, elseBlock);
				graph.addVerticesAndEdge(ifExp, contains);
			}else if(elseStatement instanceof ReturnStatement){
				// Else contains Return
				GraphConcept value = createConceptFromExpressionSyntax(
						((ReturnStatement) elseStatement).getExpression(), currentScope);
				Returns myreturn = new Returns(ifExp.getName(), value.toString());
				myreturn.setValue(((ReturnStatement) elseStatement).getExpression()
						.toString());
				graph.addVerticesAndEdge(myreturn, value);
				graph.addVerticesAndEdge(ifExp, myreturn);
			}else if(elseStatement instanceof IfStatement){
				// Else contains if
				GraphConcept ifConcept = createConceptFromIfExpression((IfStatement)elseStatement, currentScope);
				if (ifConcept != null) {
					Contains contains = new Contains(ifExp.getName(),
							ifConcept.getName());
					graph.addVerticesAndEdge(contains, ifConcept);
					graph.addVerticesAndEdge(ifExp, contains);
				}
			}else {
				// else statement only has a single statement
				GraphConcept concept = createConceptFromExpressionSyntax(
						(ExpressionStatement) elseStatement, currentScope);

				if (concept != null) {
					Contains contains = new Contains(ifExp.getName(),
							concept.getName());
					graph.addVerticesAndEdge(contains, concept);
					graph.addVerticesAndEdge(ifExp, contains);
				}
			}
		}

		return ifExp;
	}

	/**
	 * Method to create a GraphConcept from an ExpressionStatement
	 * 
	 * @param expression - the ExpressionStatement object
	 * @param currentScope - the String representing the scope
	 * @return a GraphObjec
	 */
	private GraphConcept createConceptFromExpressionSyntax(
			ExpressionStatement expression, String currentScope) {
		Expression exp = expression.getExpression();

		if (exp instanceof InfixExpression || exp instanceof Assignment) {
			MathOp mathop = createMathOpFromBinaryExpression(exp, currentScope);
			return mathop;
		} else if (exp instanceof MethodInvocation) {
			MethodCall methodCall = createMethodCallFromInvocationExpression(
					(MethodInvocation) exp, currentScope);
			return methodCall;
		} else if (exp instanceof PrefixExpression
				|| exp instanceof PostfixExpression) {
			MathOp mathop = null;
			Expression op = null;
			if (exp instanceof PrefixExpression) {
				mathop = new MathOp(((PrefixExpression) exp).getOperator()
						.toString());
				op = ((PrefixExpression) exp).getOperand();
			} else {
				mathop = new MathOp(((PostfixExpression) exp).getOperator()
						.toString());
				op = ((PostfixExpression) exp).getOperand();
			}

			GraphConcept concept = searchIdentifierInScope(op, currentScope);
			if (concept != null) {
				Contains contains = new Contains(mathop.getName(),
						concept.getName());
				graph.addVerticesAndEdge(contains, concept);
				graph.addVerticesAndEdge(mathop, contains);
			} else {
				NullDef nullDef = new NullDef();
				Contains contains = new Contains(mathop.getName(),
						nullDef.getName());
				graph.addVerticesAndEdge(contains, nullDef);
				graph.addVerticesAndEdge(mathop, contains);
			}

			return mathop;
		}
		return null;
	}

	/**
	 * Method to create CompareOp of GraphConcept from binary expression
	 * 
	 * @param exp - the binary expression
	 * @param currentScope - the String representing the scope
	 * @return a CompareOp of GraphConcept
	 */
	private CompareOp createCompareOpFromBinaryExpression(Expression exp,
			String currentScope) {
		
		CompareOp compareOp = null;

		// if the expression is an InfixExpression
		if (exp instanceof InfixExpression) {				
			InfixExpression inf = (InfixExpression) exp;
			Expression lhs = inf.getLeftOperand();
			Expression rhs = inf.getRightOperand();

			if (lhs instanceof PrefixExpression
					|| lhs instanceof ParenthesizedExpression
					|| lhs instanceof CastExpression) {
				lhs = getSyntaxNodeFromParenthesis(lhs);
			}

			if (rhs instanceof PrefixExpression
					|| rhs instanceof ParenthesizedExpression
					|| rhs instanceof CastExpression) {
				rhs = getSyntaxNodeFromParenthesis(rhs);
			}

			compareOp = new CompareOp(inf.getOperator().toString());
			compareOp.setValue(inf.toString());

			Contains cLeft = null;
			
			// process the left hand side
			if (lhs instanceof InfixExpression || lhs instanceof Assignment) {
				MathOp m = createMathOpFromBinaryExpression(lhs, currentScope);
				compareOp.setLeftSide(m.getName());
				cLeft = new Contains(compareOp.toString(), m.getName());				
				graph.addVerticesAndEdge(cLeft, m);
				graph.addVerticesAndEdge(compareOp, cLeft);
			} else if (lhs instanceof BooleanLiteral
					|| lhs instanceof CharacterLiteral
					|| lhs instanceof NumberLiteral
					|| lhs instanceof StringLiteral) {
				StringDef lit = null;
				if (lhs instanceof BooleanLiteral)
					lit = new StringDef(((BooleanLiteral) lhs).toString(),
							Boolean.toString(((BooleanLiteral) lhs)
									.booleanValue()));
				else if (lhs instanceof CharacterLiteral)
					lit = new StringDef(((CharacterLiteral) lhs).toString(),
							((CharacterLiteral) lhs).getEscapedValue());
				else if (lhs instanceof NumberLiteral)
					lit = new StringDef(((NumberLiteral) lhs).toString(),
							((NumberLiteral) lhs).getToken());
				else if (lhs instanceof StringLiteral)
					lit = new StringDef(((StringLiteral) lhs).toString(),
							((StringLiteral) lhs).getLiteralValue());
				compareOp.setLeftSide(lit.getName());
				cLeft = new Contains(compareOp.toString(), lit.getName());				
				graph.addVerticesAndEdge(cLeft, lit);
				graph.addVerticesAndEdge(compareOp, cLeft);
			} else if (lhs instanceof SimpleName) {
				GraphConcept concept = searchIdentifierInScope(lhs,
						currentScope);				
				if (concept != null) {
					compareOp.setLeftSide(concept.getName());
					cLeft = new Contains(compareOp.toString(),
							concept.getName());					
					graph.addVerticesAndEdge(cLeft, concept);
					graph.addVerticesAndEdge(compareOp, cLeft);
				} else {
					// define a null concept. an error has occured as no such
					// variable or field was previously defined
					NullDef nullDef = new NullDef();
					compareOp.setLeftSide(nullDef.getName());
					cLeft = new Contains(compareOp.toString(),
							nullDef.getName());					
					graph.addVerticesAndEdge(cLeft, nullDef);
					graph.addVerticesAndEdge(compareOp, cLeft);
				}
			} else if (lhs instanceof MethodInvocation) {
				MethodCall methodCall = createMethodCallFromInvocationExpression(
						(MethodInvocation) lhs, currentScope);
				compareOp.setLeftSide(methodCall.getName());
				cLeft = new Contains(compareOp.toString(),
						methodCall.getName());				
				graph.addVerticesAndEdge(cLeft, methodCall);
				graph.addVerticesAndEdge(compareOp, cLeft);
			}

			// process the right hand side
			if (rhs instanceof InfixExpression || rhs instanceof Assignment) {
				MathOp m = createMathOpFromBinaryExpression(rhs, currentScope);
				compareOp.setRightSide(m.getName());
				Contains c = new Contains(compareOp.toString(), m.getName());				
				graph.addVerticesAndEdge(c, m);
				graph.addVerticesAndEdge(compareOp, c);
			} else if (rhs instanceof BooleanLiteral
					|| rhs instanceof CharacterLiteral
					|| rhs instanceof NumberLiteral
					|| rhs instanceof StringLiteral) {
				StringDef lit = null;
				if (rhs instanceof BooleanLiteral)
					lit = new StringDef(((BooleanLiteral) rhs).toString(),
							Boolean.toString(((BooleanLiteral) rhs)
									.booleanValue()));
				else if (rhs instanceof CharacterLiteral)
					lit = new StringDef(((CharacterLiteral) rhs).toString(),
							((CharacterLiteral) rhs).getEscapedValue());
				else if (rhs instanceof NumberLiteral)
					lit = new StringDef(((NumberLiteral) rhs).toString(),
							((NumberLiteral) rhs).getToken());
				else if (rhs instanceof StringLiteral)
					lit = new StringDef(((StringLiteral) rhs).toString(),
							((StringLiteral) rhs).getLiteralValue());
				compareOp.setRightSide(lit.getName());
				Contains c = new Contains(compareOp.toString(), lit.getName());				
				graph.addVerticesAndEdge(c, lit);
				graph.addVerticesAndEdge(compareOp, c);
			} else if (rhs instanceof SimpleName) {
				GraphConcept concept = searchIdentifierInScope(rhs,
						currentScope);
				if (concept != null) {
					compareOp.setRightSide(concept.getName());
					Contains c = new Contains(compareOp.toString(),
							concept.getName());					
					graph.addVerticesAndEdge(c, concept);
					graph.addVerticesAndEdge(compareOp, c);
				} else {
					// define a null concept. an error has occured as no such
					// variable or field was previously defined
					NullDef nullDef = new NullDef();
					compareOp.setRightSide(nullDef.getName());
					Contains c = new Contains(compareOp.toString(),
							nullDef.getName());					
					graph.addVerticesAndEdge(c, nullDef);
					graph.addVerticesAndEdge(compareOp, c);
				}
			} else if (rhs instanceof MethodInvocation) {
				MethodCall methodCall = createMethodCallFromInvocationExpression(
						(MethodInvocation) rhs, currentScope);
				compareOp.setRightSide(methodCall.getName());
				Contains c = new Contains(compareOp.toString(),
						methodCall.getName());				
				graph.addVerticesAndEdge(c, methodCall);
				graph.addVerticesAndEdge(compareOp, c);
			}
			if(cLeft != null)
				cLeft.setInConcept(compareOp.toString());
		} else if (exp instanceof Assignment) {
			// if the expression is an Assignment
			Assignment inf = (Assignment) exp;
			Expression lhs = inf.getLeftHandSide();
			Expression rhs = inf.getRightHandSide();

			if (lhs instanceof PrefixExpression
					|| lhs instanceof ParenthesizedExpression
					|| lhs instanceof CastExpression) {
				lhs = getSyntaxNodeFromParenthesis(lhs);
			}

			if (rhs instanceof PrefixExpression
					|| rhs instanceof ParenthesizedExpression
					|| rhs instanceof CastExpression) {
				rhs = getSyntaxNodeFromParenthesis(rhs);
			}

			compareOp = new CompareOp(inf.getOperator().toString());
			compareOp.setValue(inf.toString());
			
			Contains cLeft = null;

			// process the left hand side
			if (lhs instanceof InfixExpression || lhs instanceof Assignment) {
				MathOp m = createMathOpFromBinaryExpression(lhs, currentScope);
				compareOp.setLeftSide(m.getName());
				cLeft = new Contains(compareOp.toString(), m.getName());				
				graph.addVerticesAndEdge(cLeft, m);
				graph.addVerticesAndEdge(compareOp, cLeft);
			} else if (lhs instanceof BooleanLiteral
					|| lhs instanceof CharacterLiteral
					|| lhs instanceof NumberLiteral
					|| lhs instanceof StringLiteral) {
				StringDef lit = null;
				if (lhs instanceof BooleanLiteral)
					lit = new StringDef(((BooleanLiteral) lhs).toString(),
							Boolean.toString(((BooleanLiteral) lhs)
									.booleanValue()));
				else if (lhs instanceof CharacterLiteral)
					lit = new StringDef(((CharacterLiteral) lhs).toString(),
							((CharacterLiteral) lhs).getEscapedValue());
				else if (lhs instanceof NumberLiteral)
					lit = new StringDef(((NumberLiteral) lhs).toString(),
							((NumberLiteral) lhs).getToken());
				else if (lhs instanceof StringLiteral)
					lit = new StringDef(((StringLiteral) lhs).toString(),
							((StringLiteral) lhs).getLiteralValue());
				compareOp.setLeftSide(lit.getName());
				cLeft = new Contains(compareOp.toString(), lit.getName());				
				graph.addVerticesAndEdge(cLeft, lit);
				graph.addVerticesAndEdge(compareOp, cLeft);
			} else if (lhs instanceof SimpleName) {
				GraphConcept concept = searchIdentifierInScope(lhs,
						currentScope);
				if (concept != null) {
					compareOp.setLeftSide(concept.getName());
					cLeft = new Contains(compareOp.toString(),
							concept.getName());					
					graph.addVerticesAndEdge(cLeft, concept);
					graph.addVerticesAndEdge(compareOp, cLeft);
				} else {
					// define a null concept. an error has occured as no such
					// variable or field was previously defined
					NullDef nullDef = new NullDef();
					compareOp.setLeftSide(nullDef.getName());
					cLeft = new Contains(compareOp.toString(),
							nullDef.getName());					
					graph.addVerticesAndEdge(cLeft, nullDef);
					graph.addVerticesAndEdge(compareOp, cLeft);
				}
			} else if (lhs instanceof MethodInvocation) {
				MethodCall methodCall = createMethodCallFromInvocationExpression(
						(MethodInvocation) lhs, currentScope);
				compareOp.setLeftSide(methodCall.getName());
				cLeft = new Contains(compareOp.toString(),
						methodCall.getName());				
				graph.addVerticesAndEdge(cLeft, methodCall);
				graph.addVerticesAndEdge(compareOp, cLeft);
			}

			// process the right hand side
			if (rhs instanceof InfixExpression || rhs instanceof Assignment) {
				MathOp m = createMathOpFromBinaryExpression(rhs, currentScope);
				compareOp.setRightSide(m.getName());
				Contains c = new Contains(compareOp.toString(), m.getName());				
				graph.addVerticesAndEdge(c, m);
				graph.addVerticesAndEdge(compareOp, c);
			} else if (rhs instanceof BooleanLiteral
					|| rhs instanceof CharacterLiteral
					|| rhs instanceof NumberLiteral
					|| rhs instanceof StringLiteral) {
				StringDef lit = null;
				if (rhs instanceof BooleanLiteral)
					lit = new StringDef(((BooleanLiteral) rhs).toString(),
							Boolean.toString(((BooleanLiteral) rhs)
									.booleanValue()));
				else if (rhs instanceof CharacterLiteral)
					lit = new StringDef(((CharacterLiteral) rhs).toString(),
							((CharacterLiteral) rhs).getEscapedValue());
				else if (rhs instanceof NumberLiteral)
					lit = new StringDef(((NumberLiteral) rhs).toString(),
							((NumberLiteral) rhs).getToken());
				else if (rhs instanceof StringLiteral)
					lit = new StringDef(((StringLiteral) rhs).toString(),
							((StringLiteral) rhs).getLiteralValue());
				compareOp.setRightSide(lit.getName());
				Contains c = new Contains(compareOp.toString(), lit.getName());				
				graph.addVerticesAndEdge(c, lit);
				graph.addVerticesAndEdge(compareOp, c);
			} else if (rhs instanceof SimpleName) {
				GraphConcept concept = searchIdentifierInScope(rhs,
						currentScope);
				if (concept != null) {
					compareOp.setRightSide(concept.getName());
					Contains c = new Contains(compareOp.toString(),
							concept.getName());					
					graph.addVerticesAndEdge(c, concept);
					graph.addVerticesAndEdge(compareOp, c);
				} else {
					// define a null concept. an error has occured as no such
					// variable or field was previously defined
					NullDef nullDef = new NullDef();
					compareOp.setRightSide(nullDef.getName());
					Contains c = new Contains(compareOp.toString(),
							nullDef.getName());					
					graph.addVerticesAndEdge(c, nullDef);
					graph.addVerticesAndEdge(compareOp, c);
				}
			} else if (rhs instanceof MethodInvocation) {
				MethodCall methodCall = createMethodCallFromInvocationExpression(
						(MethodInvocation) rhs, currentScope);
				compareOp.setRightSide(methodCall.getName());
				Contains c = new Contains(compareOp.toString(),
						methodCall.getName());				
				graph.addVerticesAndEdge(c, methodCall);
				graph.addVerticesAndEdge(compareOp, c);
			}
			if(cLeft != null)
				cLeft.setInConcept(compareOp.toString());
		}

		return compareOp;
	}
	
	/**
	 * Method to create a local variable declaration
	 * 
	 * @param frag - the VariableDeclarationFragment object
	 * @param type - the String representing the type of the variable
	 * @param currentScope - the String representing the scope
	 * @return the GraphConcept for the variable declaration
	 */
	private GraphConcept createConceptFromLocalDeclaration(
			VariableDeclarationFragment frag, String type, String currentScope) {
		// assuming only one variable in every declaration
		Variable variable = new Variable(frag.getName().toString(), frag.toString());
		variable.setScope(currentScope);
		variable.setType(type);
		
		if(!scopeSet.contains(currentScope))
			scopeSet.add(currentScope);

		Expression init = frag.getInitializer();

		if (init == null) {
			return variable;
		} else {
			AssignOp assign = createAssignOpFromEquals(init, currentScope);
			if (assign != null) {
				assign.setLeftSide(variable.getName());
				Contains c = new Contains(assign.toString(), variable.getName());
				
				if(globalAssign != null)
					globalContains.setInConcept(globalAssign.toString());
				graph.addVerticesAndEdge(c, variable);
				graph.addVerticesAndEdge(assign, c);
				
				return assign;
			}
		}
		return null;
	}

	/**
	 * Method to get the set of vertex in the graph
	 * 
	 * @return the vertex set
	 */
    public Set<GraphConcept> getVertex(){
        return graph.vertexSet();
    }	    

    /**
     * Method to print the conceptual graph in breadth first manner
     */
    public void printBreadthFirst(){
    	BreadthFirstIterator<GraphConcept, GraphEdge> it = new BreadthFirstIterator<GraphConcept, GraphEdge>(graph, rootBlock);
    	while(it.hasNext()){
    		GraphConcept g = it.next();
    		if(!g.isRelation()){
    			System.out.println("[" + g + "]");
    		}else{
    			System.out.println("(" + g + ")");
    		}    		
    	}
    }
    
    /**
     * Method to dump the conceptual graph as the Conceptual Graph Interchange Format (CGIF)
     * 
     */
    public void dumpToFile(){
//    	String filename = "C:\\Users\\Rahman\\workspace\\Misc\\src\\ast\\CGIF-Dump.txt";
    	
    	String cgif = parentPath + "\\CGIF_" + fileName;
    	
    	BreadthFirstIterator<GraphConcept, GraphEdge> it = new BreadthFirstIterator<GraphConcept, GraphEdge>(graph, rootBlock);
    	
    	try{
	    	File file = new File(cgif);
	        
	        if(!file.exists()){
	                file.createNewFile();
	        }
	        
	        BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
	        StringBuilder sb = new StringBuilder();
	        int i = 0;
	        String s = "";
	        String blockString = "";
	        while(it.hasNext()){
	    		GraphConcept g = it.next();
	    		
	    		if(i == 0){
	    			blockString = g.toString();
	    		}
	    		
	    		if(i == 1 || i == 2){
	    			++i;
	    			continue;
	    		}
	    		if(i == 3){
	    			//modify 	inconcept to what we have in i == 0
	    			String[] t = g.toString().split("\t");
	    			t[1] = "InConcept: " + blockString;
	    			String merged = t[0] + "\t" + t[1] + "\t" + t[2];	    			
	    			bw.write("(" + merged + ")");
	    			bw.newLine();
	    			sb.append("(" + merged + ")\n");
	    			++i;
	    			continue;
	    		}
	    		
	    		if(!g.isRelation()){
	    			s += "[" + g + "]\n";
	    			bw.write("[" + g + "]");
	    			sb.append("[" + g + "]\n");
	    			bw.newLine();
	    		}else{
	    			s += "(" + g + ")\n";
	    			bw.write("(" + g + ")");
	    			sb.append("(" + g + ")\n");
	    			bw.newLine();
	    		}    			    	
	    		++i;
	    	}               
	        	        
	        dump = sb.toString();
	        cgifFile = cgif;
	        
	        bw.flush();
	        bw.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public String getDump(){
    	return dump;
    }
    
    public String getCGIF(){
    	return cgifFile;
    }
    
    /**
     * Method to get the method declaration and its body
     * 
     * @param line - String array for the code representation
     */
    public void getMethod(String[] line){    	
        StringBuilder sb = new StringBuilder();
        int brackCount = 0;
        for (int i = 0; i < line.length; i++){
            if (line[i].contains("class")){
                if (line[i].endsWith("{") && brackCount == 0){
                    brackCount = 1;
                }
                continue;
            }                

            if (!line[i].trim().equals("") && !line[i].trim().endsWith("}") && brackCount != 0){
                if (line[i].contains("{"))
                    brackCount++;
                sb.append(line[i] + "\n");
            }

            if (line[i].contains("}")){
                sb.append(line[i] + "\n");
                brackCount--;

                if (brackCount == 1)
                    break;
            }
        }

        mainMethod = sb.toString();
    }
    
	public static void main(String[] arg) {
		/*String filePath = "E:\\PoonamAris\\ArisJava\\bin\\Test.java";
		ConceptualGraphBuilder builder = new ConceptualGraphBuilder(filePath);	
		builder.ToCSVTest();*/

		ConceptualGraphBuilder builder = new ConceptualGraphBuilder();	
		builder.ConvertAll("E:\\PoonamAris\\ArisJava\\bin\\JavaCorpus");
		
		//builder.dumpToFile();
		
		//builder.printBreadthFirst();
		
	}

	private void ToCSVTest() {
		String nodeCsv = "E:\\PoonamAris\\ArisJava\\bin\\nodes1.csv";
    	String relationCsv = "E:\\PoonamAris\\ArisJava\\bin\\relation1.csv";
    	edgeFactory = new GraphEdgeFactory();
		graph = new ConceptualGraph(edgeFactory);
		String contents = null;
		try {
			contents = new String(Files.readAllBytes(Paths.get("E:\\PoonamAris\\ArisJava\\bin\\Test.java")));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        ConceptualGraph cg = ConceptualGraph.loadGraphFromString(contents);
    	try{
	    	File file = new File(nodeCsv);
	    	File relationfile = new File(relationCsv);

	        if(!file.exists()){
	              file.createNewFile();
	        }
	        if(!relationfile.exists()){
	        	relationfile.createNewFile();
	        }
	        BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
	        BufferedWriter bwr = new BufferedWriter(new FileWriter(relationfile.getAbsolutePath()));

	        StringBuilder sb = new StringBuilder();
	        StringBuilder sbr = new StringBuilder();	
	        String Relations[] = { "Condition", "Contains", "Parameter", "Returns", "Depends", "Defines" };
            int NodeID = 0;
            int MethodID =0;
             System.out.println("vertex set=="+(cg.getVertex()));
             Object[] nodes= cg.getVertex().toArray();
             System.out.println("vertex set node=="+nodes.length);
             System.out.println("Edge set node=="+cg.edgeSet().size());
             // Graph Metrics
             GraphMetrics gm =new GraphMetrics();
             System.out.println("Average degree=="+gm.AverageDegree(cg));
            // gm.assignInitialNodeRanks(cg);
            // System.out.println("assignInitialNodeRanks=="+gm.n);

             
             int j=0;
 			for(GraphConcept node : cg.getVertex()){
 				j++;
 	          //   System.out.println("vertex set node=="+node+" j=="+j);

 			}
			for(GraphConcept node : cg.getVertex()){
				
	            //Type, Name, MethodName, MethodID/GraphID, NodeID				
                String NodeType = "Noun";
			// System.out.println("value=="+graph+"\n");

	           if(Arrays.asList(Relations).contains(node.getName())){          
	                NodeType = "Verb";
	            }
	            NodeID++;
	           sb.append(NodeType);
	           sb.append(',');
	           sb.append(node.getName());
	           sb.append(',');
	         //  sb.append(MethodName);
	          // sb.append(',');
	           sb.append(MethodID);
	           sb.append(',');
	           sb.append(NodeID);
	           sb.append('\n');
			}
	           try {
				bw.write(sb.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	            //file.WriteLine($"{NodeType},{node.Name.Replace(',', ' ')},{methodName.Replace(',', ' ')},{GraphID},{NodeID}");
	           // node.NodeRank = NodeID;
	        
			for(GraphEdge edge : cg.edgeSet()){
				 sbr.append(MethodID);
		         sbr.append(',');
		         sbr.append(edge.getSource().getNodeRank());
		         sbr.append(',');
		         sbr.append(edge.getTarget().getNodeRank());
		         sbr.append('\n');
		         try {
					bwr.write(sbr.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
   }
	        
	        bw.flush();
	        bw.close();
	        bwr.flush();
	        bwr.close();
    	}
	    catch (Exception e) {
	    	e.printStackTrace();
	    }      
	}

	private void ConvertAll(String directory) {
		File[] files = new File(directory).listFiles();
		for (File file : files) {
		    if (file.isFile()) {
                try {
                	String contents = new String(Files.readAllBytes(Paths.get(directory+"//"+file.getName())));
	               ConceptualGraph cg = ConceptualGraph.loadGraphFromString(contents);
	               String methodName= cg.mainMethod.substring(0,cg.mainMethod.indexOf("{")).replace(",", " ");
	              // System.out.println("cg....==="+methodName);
	               Graph.put(methodName, cg);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }		  
		}
    	String nodeCsv = "E:\\PoonamAris\\ArisJava\\bin\\nodes.csv";
    	String relationCsv = "E:\\PoonamAris\\ArisJava\\bin\\relation.csv";
    	//edgeFactory = new GraphEdgeFactory();
		//graph = new ConceptualGraph(edgeFactory);
    	try{
	    	File file = new File(nodeCsv);
	    	File relationfile = new File(relationCsv);

	        if(!file.exists()){
	              file.createNewFile();
	        }
	        if(!relationfile.exists()){
	        	relationfile.createNewFile();
	        }
	        BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
	        BufferedWriter bwr = new BufferedWriter(new FileWriter(relationfile.getAbsolutePath()));

	        StringBuilder sb = new StringBuilder();
	        StringBuilder sbr = new StringBuilder();
	        
	        Set set = Graph.entrySet();
			 // Get an iterator
		      Iterator i = set.iterator();
	         int methodID=1;
		      // Display elements
		      while(i.hasNext()) {
		         Map.Entry me = (Map.Entry)i.next();
		        // System.out.print("key=="+me.getKey() + ": ");
		        // System.out.println("value=="+me.getValue());
		         ToCSV((String) me.getKey(), me.getValue(), methodID++,sb,sbr,bw,bwr);
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
	        
	        bw.flush();
	        bw.close();
	        bwr.flush();
	        bwr.close();
    	}
	    catch (Exception e) {
	    	e.printStackTrace();
	    } 	      
	}
public void ToCSV(String MethodName, Object object2, int methodID, StringBuilder sb, StringBuilder sbr, BufferedWriter bw, BufferedWriter bwr) {
	        String Relations[] = { "Condition", "Contains", "Parameter", "Returns", "Depends", "Defines" };
            int NodeID = 0;
			for(GraphConcept node : ((AbstractBaseGraph<GraphConcept, GraphEdge>) object2).vertexSet()){
	            //Type, Name, MethodName, MethodID/GraphID, NodeID
				//NodeID++;
				//System.out.println(NodeID);
	          String NodeType = "Noun";
			// System.out.println("value=="+graph+"\n");
	           if(Arrays.asList(Relations).contains(node.getName())){          
	                NodeType = "Verb";
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
			}
	          
	            //file.WriteLine($"{NodeType},{node.Name.Replace(',', ' ')},{methodName.Replace(',', ' ')},{GraphID},{NodeID}");
	           // node.NodeRank = NodeID;
	        
			for(GraphEdge edge : ((AbstractBaseGraph<GraphConcept, GraphEdge>) object2).edgeSet()){
				// System.out.println("Edges set=="+edge.getSource().getNodeRank());

				 sbr.append(methodID);
		         sbr.append(',');
		         sbr.append(edge.getSource().getNodeRank());
		         sbr.append(',');
		         sbr.append(edge.getTarget().getNodeRank());
		         sbr.append('\n');
			}
		        
    }
}
