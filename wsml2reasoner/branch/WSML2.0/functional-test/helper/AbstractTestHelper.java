package helper;

import java.util.Map;
import java.util.Set;

import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.factory.ITermFactory;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.Literal;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public class AbstractTestHelper {

	protected static final WsmoFactory WF = org.wsmo.factory.Factory
			.createWsmoFactory(null);
	protected static final IBasicFactory BF = org.deri.iris.factory.Factory.BASIC;
	protected static final ITermFactory TF = org.deri.iris.factory.Factory.TERM;
	protected static final LogicalExpressionFactory LF = org.wsmo.factory.Factory
			.createLogicalExpressionFactory(null);


	public static Term createIRI(String iriName) {
		return WF.createIRI(iriName);
	}
	
	public static Literal createLiteral(boolean isPositive, String predicateUri, Term... terms) {
		Literal wsmlLiteral = new Literal(isPositive, predicateUri, terms);
		return wsmlLiteral;
	}
	
	public static Variable createVariable(String name){
		return LF.createVariable(name);
	}
	
	
	public static boolean checkIsIn(Set<Map<Variable, Term>> result,
			String varName, Term expected) {
		for (Map<Variable, Term> vBinding : result) {
			for (Variable var : vBinding.keySet()) {
				if (var.toString().equals(varName)) {
					if ((vBinding.get(var).equals(expected))) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static void printResult(Set<Map<Variable, Term>> result, String query) {
		// print out the results:
		System.out.println("The query '" + query
				+ "' has the following results: ");
		for (Map<Variable, Term> vBinding : result) {
			for (Variable var : vBinding.keySet()) {
				System.out.print(var + ": " + (vBinding.get(var)) + "\t ");
			}
			System.out.println();
		}
		System.out.println();
	}
}