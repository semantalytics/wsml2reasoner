/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package org.wsml.reasoner.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.FOLReasonerFacade;
import org.wsml.reasoner.api.WSMLFOLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.builtin.spass.SpassFacade;
import org.wsml.reasoner.builtin.tptp.TPTPFacade;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.MoleculeNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsml.reasoner.transformation.le.FOLMoleculeDecompositionRules;
import org.wsml.reasoner.transformation.le.LogicalExpressionNormalizer;
import org.wsml.reasoner.transformation.le.OnePassReplacementNormalizer;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

/**
 * A prototypical implementation of a reasoner for WSML FOL
 * 
 * At present the implementation only supports the following reasoning tasks: -
 * Query answering Ontology registration
 * 
 * @author Holger Lausen, DERI Innsbruck
 */
public class FOLBasedWSMLReasoner implements WSMLFOLReasoner {

    protected FOLReasonerFacade builtInFacade = null;
    protected WsmoFactory wsmoFactory;
    protected LogicalExpressionFactory leFactory;
    protected WSMO4JManager wsmoManager;

    public FOLBasedWSMLReasoner(
            WSMLReasonerFactory.BuiltInReasoner builtInType,
            WSMO4JManager wsmoManager,
            String uri) {
        this.wsmoManager = wsmoManager;
        switch (builtInType) {
        case TPTP:
            builtInFacade = new TPTPFacade(wsmoManager,uri);
            break;
        case SPASS:
            builtInFacade = new SpassFacade(wsmoManager,uri);
            break;
        default:
            throw new UnsupportedOperationException("Reasoning with "
                    + builtInType.toString() + " is not supported in FOL!");
        }
        wsmoFactory = this.wsmoManager.getWSMOFactory();
        leFactory = this.wsmoManager.getLogicalExpressionFactory();
    }
    
    /**
     * 
     */
    public List<EntailmentType> checkEntailment(IRI ontologyID, List<LogicalExpression> conjectures) {
        
        LogicalExpressionNormalizer moleculeNormalizer = new OnePassReplacementNormalizer(
                new FOLMoleculeDecompositionRules(wsmoManager), wsmoManager);

        List<LogicalExpression> newconjectures = new ArrayList<LogicalExpression>();
        for (LogicalExpression le: conjectures){
//            System.out.println("Q before molecule normalization: " + le);
            le = moleculeNormalizer.normalize(le);
            newconjectures.add(le);
//            System.out.println("Q after molecule normalization: " + le);
        }
        
        
        
        
        return builtInFacade.checkEntailment(ontologyID.toString(), newconjectures);
    }

    /* (non-Javadoc)
     * @see org.wsml.reasoner.api.WSMLFOLReasoner#checkEntailment(org.wsmo.common.IRI, org.omwg.logicalexpression.LogicalExpression)
     */
    public EntailmentType checkEntailment(IRI ontologyID, LogicalExpression conjectures) {
        List<LogicalExpression> l = new ArrayList<LogicalExpression>();
        l.add(conjectures);
        List<EntailmentType> result = checkEntailment(ontologyID, l);
        return result.get(0);
    }

    /* (non-Javadoc)
     * @see org.wsml.reasoner.api.WSMLReasoner#checkConsistency(org.wsmo.common.IRI)
     */
    public Set<ConsistencyViolation> checkConsistency(IRI ontologyID) {
        
        //not sure actually... TODO: CHECK ME!
        //should not be a consisteny violation anyway
        LogicalExpression le;
        try {
            le = leFactory.createLogicalExpression("_\"foo:a\" or naf _\"foo:a\"");
        } catch (ParserException e) {
            throw new RuntimeException("should never happen!");
        }
        EntailmentType result = checkEntailment(ontologyID, le);
        Set<ConsistencyViolation> cons = new HashSet<ConsistencyViolation>();
        if (result==EntailmentType.notEntailed){
            cons.add(new ConsistencyViolation(ontologyID));
        }
        return cons;
    }

    public void deRegisterOntology(IRI ontologyID) {
        deRegisterOntology(ontologyID);
    }

    public void deRegisterOntology(Set<IRI> ontologyIDs) {
        for (IRI iri: ontologyIDs){
            deRegisterOntology(iri);
        }
        
    }

    /**
     * stupid we should change existing IF at some point!
     */
    public boolean entails(IRI ontologyID, LogicalExpression expression) {
        return (checkEntailment(ontologyID, expression)==EntailmentType.entailed);
    }

    /**
     * stupid we should change existing IF at some point!
     */
    public boolean entails(IRI ontologyID, Set<LogicalExpression> expressions) {
        List<EntailmentType> l = checkEntailment(ontologyID, new ArrayList<LogicalExpression>(expressions));
        boolean result = true;
        for (EntailmentType t :l){
            if (t!=EntailmentType.entailed){
                result = false;
            }
        }
        return result;
    }

    public boolean executeGroundQuery(IRI ontologyID, LogicalExpression query) {
        return entails(ontologyID, query);
    }

    public void registerOntologies(Set<Ontology> ontologies) throws InconsistencyException {
        for (Ontology o: ontologies){
            registerOntology(o);
        }
    }

    public void registerOntology(Ontology ontology) throws InconsistencyException {
        Ontology ontologyAsExpressions;
        
        //in order to keep track of cyclic imports
        Set<Ontology> importedOntologies = new HashSet<Ontology>();
        
        // Convert conceptual syntax to logical expressions
        OntologyNormalizer normalizer = new AxiomatizationNormalizer(wsmoManager, importedOntologies);
        ontologyAsExpressions = normalizer.normalize(ontology);

        normalizer = new MoleculeNormalizer(wsmoManager);
        ontologyAsExpressions = normalizer.normalize(ontologyAsExpressions);
        
//      System.out.println("\n-------\n Ontology after Normalization:\n" +
//      BaseNormalizationTest.serializeOntology(normalizedOntology));

        //TODO shall we handle constraints in some way?
        
        //TODO convert all molecules to Atoms (predicates!)

        //TODO validate the ontology according to our definition of FOL
        
        //TODO convert anon ids to unique constants (both numbered and unnumbered)
        
        //TODO add auxiliary rules:
        // Inference of attr value types: 
        //   mof(I2,C2) <- itype(C1, att, C2), mo(I1,C1), hval(I1,att, I2)

        // reflexivity: sco(?c,?c) :- ?c is an IRI that explicitly occures in the ontology
        // (i.e. concepts, relations, attr, instances, 

        // transitivity: sco(?c1,?c3) <- sco(?c1,?c2) and sco(?c2,?c3)
        // extension-subset: mo(?o,?c2) <- mo(?o,?c1) and sco(?c1,?c2)
        


        Set<LogicalExpression> les = new HashSet<LogicalExpression>();
        for (Axiom a :(Set<Axiom>)ontologyAsExpressions.listAxioms()){
            for (LogicalExpression le : (Set<LogicalExpression>)a.listDefinitions()){
                les.add(quantify(le));
            }
        }
        try {
            builtInFacade.register(ontology.getIdentifier().toString(), les);
        } catch (ExternalToolException e) {
            throw new RuntimeException(e);
        }
        
    }

    LogicalExpressionVariableVisitor lev = new LogicalExpressionVariableVisitor();
    private LogicalExpression quantify(LogicalExpression le){;
        lev.reset();
        le.accept(lev);
        Set<Variable> freeVars = lev.getFreeVariables(le);
        if (freeVars.isEmpty()) return le;
        return leFactory.createUniversalQuantification(freeVars, le);
    }
    


    /* ##################################
     * 
     * Interface got messy, below methods could be implemented,
     * but are a bit over the top to require for all reasoners...
     * 
     *  FIXME: INTERFACE DESIGN!
     * 
     * ##################################
     */
    
    public Set<Map<Variable, Term>> executeQuery(IRI ontologyID, LogicalExpression query) {
        throw new UnsupportedOperationException("This method is not yet implemented");
    }

    public Set<Concept> getAllConcepts(IRI ontologyID) {
        throw new UnsupportedOperationException("This method is not yet implemented");
    }

    public Set<Concept> getConcepts(IRI ontologyID, Instance instance) {
        throw new UnsupportedOperationException("This method is not yet implemented");
    }

    public Set<Instance> getInstances(IRI ontologyID, Concept concept) {
        throw new UnsupportedOperationException("This method is not yet implemented");
    }

    public Set<Concept> getSubConcepts(IRI ontologyID, Concept concept) {
        throw new UnsupportedOperationException("This method is not yet implemented");
    }

    public Set<Concept> getSuperConcepts(IRI ontologyID, Concept concept) {
        throw new UnsupportedOperationException("This method is not yet implemented");
    }

    public boolean isMemberOf(IRI ontologyID, Instance instance, Concept concept) {
        throw new UnsupportedOperationException("This method is not yet implemented");
    }

    public boolean isSatisfiable(IRI ontologyID) {
        throw new UnsupportedOperationException("This method is not yet implemented");
    }

    public boolean isSubConceptOf(IRI ontologyID, Concept subConcept, Concept superConcept) {
        throw new UnsupportedOperationException("This method is not yet implemented");
    }
    
    public void registerOntologyNoVerification(Ontology ontology) {
        throw new UnsupportedOperationException("This method is not yet implemented");
    }

    public void registerOntologiesNoVerification(Set<Ontology> ontologies) {
        throw new UnsupportedOperationException("This method is not yet implemented");
    }
    
	public Set<Instance> getAllInstances(IRI ontologyID) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<IRI> getAllAttributes(IRI ontologyID) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<IRI> getAllConstraintAttributes(IRI ontologyID) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<IRI> getAllInferenceAttributes(IRI ontologyID) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<Concept> getEquivalentConcepts(IRI ontologyID, Concept concept) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public boolean isEquivalentConcept(IRI ontologyID, Concept concept1, Concept concept2) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<Concept> getDirectConcepts(IRI ontologyID, Instance instance) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<IRI> getSubRelations(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<IRI> getSuperRelations(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<IRI> getEquivalentRelations(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<IRI> getInverseRelations(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<Concept> getConceptsOf(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<IRI> getRangesOfInferingAttribute(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<IRI> getRangesOfConstraintAttribute(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Map<IRI, Set<Term>> getInferingAttributeValues(IRI ontologyID, Instance instance) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Map<IRI, Set<Term>> getConstraintAttributeValues(IRI ontologyID, Instance instance) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Map<Instance, Set<Term>> getInferingAttributeInstances(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Map<Instance, Set<Term>> getConstraintAttributeInstances(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Instance getInferingAttributeValue(IRI ontologyID, Instance subject, Identifier attributeId) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public String getConstraintAttributeValue(IRI ontologyID, Instance subject, Identifier attributeId) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<Instance> getInferingAttributeValues(IRI ontologyID, Instance subject, Identifier attributeId) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<String> getConstraintAttributeValues(IRI ontologyID, Instance subject, Identifier attributeId) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<Concept> getDirectSubConcepts(IRI ontologyID, Concept concept) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<Concept> getDirectSuperConcepts(IRI ontologyID, Concept concept) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<IRI> getDirectSubRelations(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}

	public Set<IRI> getDirectSuperRelations(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException("This method is not yet implemented");
	}
}
