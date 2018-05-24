package test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
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

import core.AbstractSyntaxTree;
import GraphConceptsLibrary.CGraph.ConceptualGraph;
import GraphConceptsLibrary.CGraph.GraphConcept;
import GraphConceptsLibrary.CGraph.GraphEdgeFactory;
import GraphConceptsLibrary.CGraph.Concepts.BlockSyntax;
import GraphConceptsLibrary.CGraph.Concepts.Field;
import GraphConceptsLibrary.CGraph.Concepts.MathOp;
import GraphConceptsLibrary.CGraph.Concepts.Method;
import GraphConceptsLibrary.CGraph.Concepts.MethodCall;
import GraphConceptsLibrary.CGraph.Concepts.NullDef;
import GraphConceptsLibrary.CGraph.Concepts.StringDef;
import GraphConceptsLibrary.CGraph.Concepts.Variable;
import GraphConceptsLibrary.CGraph.Relations.Contains;
import GraphConceptsLibrary.CGraph.Relations.Parameter;
import GraphConceptsLibrary.CGraph.Relations.Returns;

public class Tester {
	public static void main(String[] args) throws Exception {
		GraphEdgeFactory edgeFactory = new GraphEdgeFactory();
		ConceptualGraph graph = new ConceptualGraph(edgeFactory);
		String filePath = "C:\\Users\\Rahman\\Documents\\Kuliah\\CS645\\E3.java";
		System.out.println("processing: " + filePath);

		AbstractSyntaxTree ast = new AbstractSyntaxTree(filePath);
		final CompilationUnit compilationUnit = ast.getCompilationUnit();

		ASTNode root = compilationUnit.getRoot();
//		System.out.println(root.toString());
		compilationUnit.accept(new VisitorHelper(graph));
		//need to visit the node one by one
	}
}

class VisitorHelper extends ASTVisitor {
	// SimpleDirectedGraph<GraphConcept, GraphEdge> graph;
	ConceptualGraph graph;

	// public VisitorHelper(SimpleDirectedGraph graph) {
	public VisitorHelper(ConceptualGraph graph) {
		this.graph = graph;
	}

	public boolean visit(MethodDeclaration node) {
		System.out.println("Visiting MethodDeclaration");

		Method method = null;
		if (!node.isConstructor()) {
			method = new Method(node.getName().toString(), node.toString());
			method.setModifiers(node.parameters().toString());
			method.setReturnType(node.getReturnType2().toString());
			method.setParameterList(node.modifiers().toString());

			graph.addVertex(method);

			// get the parameter
			List<SingleVariableDeclaration> pList = node.parameters();
			for (SingleVariableDeclaration var : pList) {
				Variable v = new Variable(var.getName().toString(),
						var.toString());
				v.setScope(method.getName());
				v.setType(var.getType().toString());

				Parameter pr = new Parameter(method.getName(), v.getName());
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
					Returns ret = new Returns(method.getName(), value.getName());
					graph.addVerticesAndEdge(ret, value);
					graph.addVerticesAndEdge(method, ret);
				}
			}
		} else {
			method = new Method(node.getName().toString(), node.toString());

			method.setModifiers(node.parameters().toString());
			method.setReturnType(node.getReturnType2().toString());
			method.setParameterList(node.modifiers().toString());

			graph.addVertex(method);

			// get the parameter
			List<SingleVariableDeclaration> pList = node.parameters();
			for (SingleVariableDeclaration var : pList) {
				Variable v = new Variable(var.getName().toString(),
						var.toString());
				v.setScope(method.getName());
				v.setType(var.getType().toString());

				Parameter pr = new Parameter(method.getName(), v.getName());
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

			}
		}

		return true;
	}
	

	private BlockSyntax createBlockFromBlockSyntax(Block block, String scope) {
		BlockSyntax methodBlock = new BlockSyntax();
		
		return null;
	}

	private GraphConcept createConceptFromExpressionSyntax(Expression exp,
			String currentScope) {
		System.out.println(ASTNode.nodeClassForType(exp.getNodeType()));
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
		} else if (exp instanceof SimpleName) {
			GraphConcept concept = searchIdentifierInScope(exp, currentScope);
			return concept;
		}

		return new GraphConcept();
	}

	private MathOp createMathOpFromBinaryExpression(Expression exp,
			String currentScope) {

		MathOp mathop = null;

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

			mathop = new MathOp(inf.getOperator().toString());
			mathop.setValue(inf.toString());

			if (lhs instanceof InfixExpression || lhs instanceof Assignment) {
				MathOp m = createMathOpFromBinaryExpression(lhs, currentScope);
				Contains c = new Contains(mathop.getName(), m.getName());
				graph.addVerticesAndEdge(c, m);
				graph.addVerticesAndEdge(mathop, c);
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
				Contains c = new Contains(mathop.getName(), lit.getName());
				graph.addVerticesAndEdge(c, lit);
				graph.addVerticesAndEdge(mathop, c);
			} else if (lhs instanceof SimpleName) {
				GraphConcept concept = searchIdentifierInScope(lhs,
						currentScope);
				if (concept != null) {
					Contains c = new Contains(mathop.getName(),
							concept.getName());
					graph.addVerticesAndEdge(c, concept);
					graph.addVerticesAndEdge(mathop, c);
				} else {
					// define a null concept. an error has occured as no such
					// variable or field was previously defined
					NullDef nullDef = new NullDef();
					Contains c = new Contains(mathop.getName(),
							nullDef.getName());
					graph.addVerticesAndEdge(c, nullDef);
					graph.addVerticesAndEdge(mathop, c);
				}
			} else if (lhs instanceof MethodInvocation) {
				MethodCall methodCall = createMethodCallFromInvocationExpression(
						(MethodInvocation) lhs, currentScope);
				Contains c = new Contains(mathop.getName(),
						methodCall.getName());
				graph.addVerticesAndEdge(c, methodCall);
				graph.addVerticesAndEdge(mathop, c);
			}

			if (rhs instanceof InfixExpression || rhs instanceof Assignment) {
				MathOp m = createMathOpFromBinaryExpression(rhs, currentScope);
				Contains c = new Contains(mathop.getName(), m.getName());
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
				Contains c = new Contains(mathop.getName(), lit.getName());
				graph.addVerticesAndEdge(c, lit);
				graph.addVerticesAndEdge(mathop, c);
			} else if (rhs instanceof SimpleName) {
				GraphConcept concept = searchIdentifierInScope(rhs,
						currentScope);
				if (concept != null) {
					Contains c = new Contains(mathop.getName(),
							concept.getName());
					graph.addVerticesAndEdge(c, concept);
					graph.addVerticesAndEdge(mathop, c);
				} else {
					// define a null concept. an error has occured as no such
					// variable or field was previously defined
					NullDef nullDef = new NullDef();
					Contains c = new Contains(mathop.getName(),
							nullDef.getName());
					graph.addVerticesAndEdge(c, nullDef);
					graph.addVerticesAndEdge(mathop, c);
				}
			} else if (rhs instanceof MethodInvocation) {
				MethodCall methodCall = createMethodCallFromInvocationExpression(
						(MethodInvocation) rhs, currentScope);
				Contains c = new Contains(mathop.getName(),
						methodCall.getName());
				graph.addVerticesAndEdge(c, methodCall);
				graph.addVerticesAndEdge(mathop, c);
			}
		} else if (exp instanceof Assignment) {
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
			mathop.setValue(inf.toString());

			if (lhs instanceof InfixExpression || lhs instanceof Assignment) {
				MathOp m = createMathOpFromBinaryExpression(lhs, currentScope);
				Contains c = new Contains(mathop.getName(), m.getName());
				graph.addVerticesAndEdge(c, m);
				graph.addVerticesAndEdge(mathop, c);
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
				Contains c = new Contains(mathop.getName(), lit.getName());
				graph.addVerticesAndEdge(c, lit);
				graph.addVerticesAndEdge(mathop, c);
			} else if (lhs instanceof SimpleName) {
				GraphConcept concept = searchIdentifierInScope(lhs,
						currentScope);
				if (concept != null) {
					Contains c = new Contains(mathop.getName(),
							concept.getName());
					graph.addVerticesAndEdge(c, concept);
					graph.addVerticesAndEdge(mathop, c);
				} else {
					// define a null concept. an error has occured as no such
					// variable or field was previously defined
					NullDef nullDef = new NullDef();
					Contains c = new Contains(mathop.getName(),
							nullDef.getName());
					graph.addVerticesAndEdge(c, nullDef);
					graph.addVerticesAndEdge(mathop, c);
				}
			} else if (lhs instanceof MethodInvocation) {
				MethodCall methodCall = createMethodCallFromInvocationExpression(
						(MethodInvocation) lhs, currentScope);
				Contains c = new Contains(mathop.getName(),
						methodCall.getName());
				graph.addVerticesAndEdge(c, methodCall);
				graph.addVerticesAndEdge(mathop, c);
			}

			if (rhs instanceof InfixExpression || rhs instanceof Assignment) {
				MathOp m = createMathOpFromBinaryExpression(rhs, currentScope);
				Contains c = new Contains(mathop.getName(), m.getName());
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
				Contains c = new Contains(mathop.getName(), lit.getName());
				graph.addVerticesAndEdge(c, lit);
				graph.addVerticesAndEdge(mathop, c);
			} else if (rhs instanceof SimpleName) {
				GraphConcept concept = searchIdentifierInScope(rhs,
						currentScope);
				if (concept != null) {
					Contains c = new Contains(mathop.getName(),
							concept.getName());
					graph.addVerticesAndEdge(c, concept);
					graph.addVerticesAndEdge(mathop, c);
				} else {
					// define a null concept. an error has occured as no such
					// variable or field was previously defined
					NullDef nullDef = new NullDef();
					Contains c = new Contains(mathop.getName(),
							nullDef.getName());
					graph.addVerticesAndEdge(c, nullDef);
					graph.addVerticesAndEdge(mathop, c);
				}
			} else if (rhs instanceof MethodInvocation) {
				MethodCall methodCall = createMethodCallFromInvocationExpression(
						(MethodInvocation) rhs, currentScope);
				Contains c = new Contains(mathop.getName(),
						methodCall.getName());
				graph.addVerticesAndEdge(c, methodCall);
				graph.addVerticesAndEdge(mathop, c);
			}
		}

		return mathop;
	}

	private GraphConcept searchIdentifierInScope(Expression id,
			String currentScope) {
		Variable var = null;
		Field field = null;

		Set<GraphConcept> graphSet = graph.vertexSet();
		Iterator<GraphConcept> it = graphSet.iterator();

		// find the identifier for this expression in the current scope
		while (it.hasNext()) {
			GraphConcept temp = it.next();
			System.out.println("temp: " + temp);
			if (temp instanceof Variable) {
				if (("Variable: " + id.toString()).equals(((Variable) temp)
						.getName())
						&& currentScope.equals(((Variable) temp).getScope())) {
					var = (Variable) temp;
					break;
				}
			}
		}

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

	private MethodCall createMethodCallFromInvocationExpression(
			MethodInvocation inv, String currentScope) {
		MethodCall method = null;

		Expression exp = inv.getExpression();
		SimpleName name = inv.getName();
		String mCall = "";
		if (exp != null) {
			if (exp.toString().startsWith("this."))
				mCall = exp.toString().substring(5) + ".";
			else
				mCall = exp.toString() + ".";
		}

		mCall += name.toString() + "()";
		method = new MethodCall(mCall);
		method.setArguments(inv.arguments().toString());
		method.setValue(inv.toString());

		List<Expression> argList = inv.arguments();
		for (Expression e : argList) {
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
						mathop.getName());
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

}
