package org.wsml.reasoner;

import java.util.Set;
import org.omwg.logicalexpression.AttributeValueMolecule;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.MembershipMolecule;
import org.omwg.logicalexpression.SubConceptMolecule;
import org.omwg.ontology.DataValue;
import org.semanticweb.owl.model.OWLDescription;
import org.wsml.reasoner.api.InternalReasonerException;
import org.wsml.reasoner.api.WSMLDLReasoner;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public class LPUtilities
{
//	public LPUtilities( WSMO4JManager wsmoManager )
//    {
//        this.wsmoManager = wsmoManager;
//        wsmoFactory = wsmoManager.getWSMOFactory();
//        leFactory = wsmoManager.getLogicalExpressionFactory();
//    }
//
//	/**
//	 * This method checks whether a given query (without variables) is true or 
//	 * false. E.g. check whether Mary[hasChild hasValue Jack] is true or not.
//	 * 
//	 * @return true or false
//	 */
//	public boolean executeGroundQuery(WSMLDLReasoner reasoner, LogicalExpression query) {
//		if (query.toString().contains("?")) {
//			throw new InternalReasonerException("A ground query may not contain " +
//					"variables!");
//		}
//		if (query instanceof AttributeValueMolecule) {
//			AttributeValueMolecule attr = (AttributeValueMolecule) query;
//			if (attr.getRightParameter() instanceof DataValue) {
//				return reasoner.instanceHasConstraintAttributeValue(
//						wsmoFactory.createInstance((IRI) attr.getLeftParameter()), 
//						(Identifier) attr.getAttribute(), 
//						(DataValue) attr.getRightParameter());
//			}
//			else {
//				return instanceHasInferingAttributeValue(
//						wsmoFactory.createInstance((IRI) attr.getLeftParameter()), 
//						(Identifier) attr.getAttribute(), 
//						wsmoFactory.createInstance((IRI) attr.getRightParameter()));
//			}
//		}
//		else if (query instanceof MembershipMolecule) {
//			MembershipMolecule attr = (MembershipMolecule) query;
//			return isMemberOf(wsmoFactory.createInstance(
//					(IRI) attr.getLeftParameter()), wsmoFactory.createConcept(
//							(IRI) attr.getRightParameter()));
//		}
//		else if (query instanceof SubConceptMolecule) {
//			SubConceptMolecule attr = (SubConceptMolecule) query;
//			return isSubConceptOf(wsmoFactory.createConcept(
//					(IRI) attr.getLeftParameter()), wsmoFactory.createConcept(
//							(IRI) attr.getRightParameter()));
//		}
//		else {
//			return entails(query);
//		}
//	}
//
//	/**
//	 * The method supports the following logical expressions as they are 
//	 * allowed in formulae in WSML-DL:
//	 * - Atom
//	 * - MembershipMolecule
//	 * - Conjunction
//	 * - Disjunction
//	 * - Negation
//	 * - UniversalQuantification
//	 * - ExistentialQuantification
//	 * 
//	 * @return true if the given expression is satisfiable, false otherwise
//	 * @throws InternalReasonerException if a logical expression different
//	 * 			than the ones mentionned above are given as input
//	 */
//	public boolean entails(LogicalExpression expression) {
//		OWLDescription des = transformLogicalExpression(expression);
//		try {
//			if (des != null) 
//				return builtInFacade.isConsistent(des);
//			else 
//				throw new InternalReasonerException("This logical expression" +
//						" is not supported for consistency check!");
//		} catch (Exception e) {
//			throw new InternalReasonerException(e);
//		} 
//	}
//
//	public boolean entails(
//			Set<LogicalExpression> expressions) {
//		for (LogicalExpression e : expressions) {
//			if (!entails(e))
//                return false;
//        }
//        return true;
//	}
//
//    private final WsmoFactory wsmoFactory;
//
//    private final LogicalExpressionFactory leFactory;
//
//    private final WSMO4JManager wsmoManager;
}
