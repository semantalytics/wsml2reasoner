package org.wsml.reasoner.transformation.dl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Type;
import org.omwg.ontology.Value;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.AnonymousIdTranslator;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsml.reasoner.transformation.le.ImplicationReductionRules;
import org.wsml.reasoner.transformation.le.InverseImplicationReductionRules;
import org.wsml.reasoner.transformation.le.LogicalExpressionNormalizer;
import org.wsml.reasoner.transformation.le.MoleculeDecompositionRules;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsml.reasoner.transformation.le.OnePassReplacementNormalizer;
import org.wsmo.common.Entity;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.UnnumberedAnonymousID;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.common.exception.SynchronisationException;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

/**
 * A normalizer for WSML-DL. This normalizer eliminates anonymous id 
 * at WSMO elements and logical expressions and does some more normalizing 
 * steps on logical expressions:
 *  - equivalences and right-implications are replaced by left-implications
 *  - complex molecules are replaced by conjunctions of simple ones
 *
 * <pre>
 *  Created on July 3rd, 2006
 *  Committed by $Author: nathalie $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/transformation/dl/WSMLDLLogExprNormalizer.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.2 $ $Date: 2006-07-18 08:45:22 $
 */
public class WSMLDLLogExprNormalizer implements OntologyNormalizer {
	
	protected LogicalExpressionNormalizer leNormalizer;

    protected WsmoFactory wsmoFactory;
    
    protected LogicalExpressionFactory leFactory;

    protected AnonymousIdTranslator anonymousIdTranslator;
    
    @SuppressWarnings("unchecked")
	public WSMLDLLogExprNormalizer(WSMO4JManager wsmoManager) {
        // Normalization Rules
    	List<NormalizationRule> preOrderRules = new ArrayList<NormalizationRule>();
        List<NormalizationRule> postOrderRules = new ArrayList<NormalizationRule>();
        // the ImplicationReductionRules replace equivalences and right-implications 
        // in logical expressions by left-implications
        preOrderRules.addAll((List<NormalizationRule>) new ImplicationReductionRules(
        			wsmoManager));
        // the InverseImplicationReductionRules replace conjunctions on the left side 
        // and disjunctions on the right side of an inverse implication
        preOrderRules.addAll((List<NormalizationRule>) new 
        		InverseImplicationReductionRules(wsmoManager));
        // the MoleculeDecompositionRules replace complex molecules inside a logical 
        // expression by conjunctions of simple ones
        postOrderRules.addAll((List<NormalizationRule>) new MoleculeDecompositionRules(
        			wsmoManager));
        
        leNormalizer = new OnePassReplacementNormalizer(preOrderRules,
                postOrderRules, wsmoManager);
        wsmoFactory = wsmoManager.getWSMOFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        anonymousIdTranslator = new AnonymousIdTranslator(wsmoFactory);
    }
    
    /**
     * @see OntologyNormalizer#normalize(Ontology)
     */
	@SuppressWarnings("unchecked")
	public Ontology normalize(Ontology ontology) {
		try {		
			// normalizing concepts (replace unnumbered anonymous identifiers)
			ontology = normalizeConcepts(ontology);
			// normalizing instances (replace unnumbered anonymous identifiers)
			ontology = normalizeInstances(ontology);
		} catch (SynchronisationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidModelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// gather logical expressions from normalized axioms       
        Set<Axiom> axioms = (Set<Axiom>) ontology.listAxioms();       
        Set<LogicalExpression> resultExp = normalizeLogExpr(axioms);
        
        // create new ontology containing the resulting logical expressions:
        Ontology resultOnt = wsmoFactory.createOntology((IRI) ontology.getIdentifier());
        
        // delete old axioms (wsmo4j statics :( ) and add a new one containing the 
        // normalized logical expressions
        for (Axiom a : (Set<Axiom>)resultOnt.listAxioms()){
            try {
                a.setOntology(null);
            } catch (InvalidModelException e) {
                e.printStackTrace();
            }
        }
        Axiom axiom = wsmoFactory.createAxiom((IRI) 
        		anonymousIdTranslator.translate(wsmoFactory.createAnonymousID()));
        for (LogicalExpression expression : resultExp) {
            axiom.addDefinition(expression);
        }
        try {
            resultOnt.addAxiom(axiom);
        } catch (InvalidModelException e) {
            e.printStackTrace();
        }
        return resultOnt;
	}
	
	/*
	 * If a concept has an unnumbered anonymous identifier, it is replaced by 
	 * an IRI containing a randomly created number. A new concept with this new 
	 * identifier is created and added to the ontology, while the old one is 
	 * removed from it. The new concept receives eventual subconcepts, 
	 * superconcepts, instances, nonfunctionalproperties or attributes from the 
	 * old concept.
	 * If a concept has a superconcept with an unnumbered anonymous identifier, 
	 * this superconcept's identifier is also replaced.
	 */
	@SuppressWarnings("unchecked")
	private Ontology normalizeConcepts(Ontology ontology) throws SynchronisationException, InvalidModelException {
		Set<Concept> concepts = (Set<Concept>) ontology.listConcepts();
		for (Concept concept : concepts) {
			if (concept.getIdentifier() instanceof UnnumberedAnonymousID) {
				Identifier id = (Identifier)anonymousIdTranslator.translate(
						concept.getIdentifier());
				Concept newConcept = wsmoFactory.createConcept(id);
				// the superConcept to which the old concept refers to is transfered to the new 
				// concept
				if (concept.listSuperConcepts().size() > 0) {
					Iterator<Concept> it = concept.listSuperConcepts().iterator();
					while (it.hasNext()) {
						Concept superConcept = it.next();
						if (superConcept.getIdentifier() instanceof UnnumberedAnonymousID) {
							Identifier id2 = (Identifier)anonymousIdTranslator.translate(
									superConcept.getIdentifier());
							superConcept = wsmoFactory.createConcept(id2);
						}
						newConcept.addSuperConcept(superConcept);
					}	
				}
				// the attribute to which the old concept refers to is transfered to the new concept
				if (concept.listAttributes().size() > 0) {
					Iterator<Attribute> it = concept.listAttributes().iterator();
					while (it.hasNext()) {
						Attribute attribute = it.next();
						Attribute newAttribute = null;
						newAttribute = newConcept.createAttribute(attribute.getIdentifier());
						if (attribute.listNFPValues().size() > 0) {
							Set<Entry> nfpsSet = attribute.listNFPValues().entrySet();
							newAttribute = (Attribute) transferNFPs(nfpsSet, newAttribute);
						}
						if (attribute.listTypes().size() > 0) {
							Set<Type> types = attribute.listTypes();
							Iterator<Type> it4 = types.iterator();
							while (it4.hasNext()) {
								Type type = it4.next();
								newAttribute.addType(type);
							}
						}
					}
				}
				if (concept.listNFPValues().size() > 0) {
					Map nfps = concept.listNFPValues();
					Set<Entry> nfpsSet = nfps.entrySet();
					newConcept = (Concept) transferNFPs(nfpsSet, newConcept); 
				}
				ontology.addConcept(newConcept);
				ontology.removeConcept(concept);			
			}
			else {
				// the superConcept to which the old concept refers to is transfered to the new 
				// concept
				if (concept.listSuperConcepts().size() > 0) {
					Iterator<Concept> it = concept.listSuperConcepts().iterator();
					while (it.hasNext()) {
						Concept superConcept = it.next();
						if (superConcept.getIdentifier() instanceof UnnumberedAnonymousID) {
							concept.removeSuperConcept(superConcept);
							Identifier id = (Identifier)anonymousIdTranslator.translate(
									superConcept.getIdentifier());
							superConcept = wsmoFactory.createConcept(id);
							concept.addSuperConcept(superConcept);
						}
					}	
				}
			}
		}
		return ontology;
	}
	
	/*
	 * If an instance has an unnumbered anonymous identifier, it is replaced by 
	 * an IRI containing a randomly created number. A new instance with this new 
	 * identifier is created and added to the ontology, while the old one is 
	 * removed from it.
	 * If an instance is member of an concept with an unnumberd anonymous identifier, 
	 * this concept's identifier is also replaced.
	 */
	@SuppressWarnings("unchecked")
	private Ontology normalizeInstances(Ontology ontology) throws SynchronisationException, InvalidModelException {
		Set<Instance> instances = (Set<Instance>) ontology.listInstances();
		Iterator<Instance> it = instances.iterator();
		while (it.hasNext()) {
			Instance instance = it.next();
			Instance newInstance = null;
			if (instance.getIdentifier() instanceof UnnumberedAnonymousID) {
				Identifier id = (Identifier) anonymousIdTranslator.translate(
						instance.getIdentifier());
				newInstance = wsmoFactory.createInstance(id);
				ontology.removeInstance(instance);
				ontology.addInstance(newInstance);

			}
			else {
				// replace concept's unnumbered anonymous identifiers
				if (instance.listConcepts().size() > 0) {
					Iterator<Concept> it2 = instance.listConcepts().iterator();
					while (it2.hasNext()) {
						Concept concept = it2.next();
						if (concept.getIdentifier() instanceof UnnumberedAnonymousID) {
							Identifier id = (Identifier) anonymousIdTranslator.translate(
									concept.getIdentifier());
							Concept newConcept = wsmoFactory.createConcept(id);
							instance.removeConcept(concept);
							instance.addConcept(newConcept);		
						}
					}
				}
			}
		}
		return ontology;
	}
	
	/*
	 * Method to normalize logical expressions. Anonymous identifiers are
	 * replaced, and the preorder and postorder rules defined at the constructor 
	 * are applied.
	 */
	@SuppressWarnings("unchecked")
	private Set<LogicalExpression> normalizeLogExpr(Set<Axiom> axioms) {
		Set<LogicalExpression> expressions = new HashSet<LogicalExpression>();
		for (Axiom axiom : axioms) {
            expressions.addAll((Collection<LogicalExpression>) axiom.listDefinitions());
        }

        // iteratively normalize logical expressions:
        Set<LogicalExpression> resultExp = new HashSet<LogicalExpression>();
        for (LogicalExpression expression : expressions) {
            anonymousIdTranslator.setScope(expression);
            resultExp.add(leNormalizer.normalize(expression));
        }
        return resultExp;
	}
	
	/*
	 * Method to transfer non functional properties from one element to another.
	 */
	private Entity transferNFPs(Set<Entry> nfpsSet, Entity entity) 
			throws SynchronisationException, InvalidModelException {
		Iterator<Entry> itNfp = nfpsSet.iterator();
		while (itNfp.hasNext()) {
			Entry mapEntry = itNfp.next();
			if (mapEntry.getValue() instanceof Identifier) {	
				entity.addNFPValue((IRI) mapEntry.getKey(), 
						(Identifier) mapEntry.getValue());
			}
			else if (mapEntry.getValue() instanceof Value) {
				entity.addNFPValue((IRI) mapEntry.getKey(), 
					(Value) mapEntry.getValue());
			}
			else if (mapEntry.getValue() instanceof Set) {
				Set values = (Set) mapEntry.getValue();
				Iterator itVal = values.iterator();
				while (itVal.hasNext()) {
					Object obj = itVal.next();
					if (obj instanceof Value) {	
						entity.addNFPValue((IRI) mapEntry.getKey(), (Value) obj);
					}
					else if (obj instanceof Identifier) {
						entity.addNFPValue((IRI) mapEntry.getKey(), (Identifier) obj);
					}
				}
			}
		}
		return entity;
	}
	
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/07/18 08:21:01  nathalie
 * adding wsml dl reasoner interface,
 * transformation from wsml dl to owl-dl
 *
 *
 */
