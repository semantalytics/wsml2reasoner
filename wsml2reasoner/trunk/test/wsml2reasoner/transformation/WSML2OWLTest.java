/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
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
package wsml2reasoner.transformation;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.semanticweb.owl.model.OWLOntology;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.DLBasedWSMLReasoner;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Parser;

import wsml2reasoner.normalization.WSMLNormalizationTest;

/**
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 *
 */
public class WSML2OWLTest extends WSMLNormalizationTest {
	
	protected DLBasedWSMLReasoner dlReasoner;
	
	protected Ontology ontology;
	
	protected OWLOntology owlOntology;
	
	protected Axiom axiom;
	
	protected String ns = "http://ex.org#";
	
	protected void setUp() throws Exception {
        super.setUp();
        ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "ont" + System.currentTimeMillis()));
        ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));
        axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom" + System.currentTimeMillis()));
        ontology.addAxiom(axiom);
        dlReasoner = new DLBasedWSMLReasoner(WSMLReasonerFactory.BuiltInReasoner.PELLET, 
        		new WSMO4JManager());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        ontology = null;
        owlOntology = null;
        System.gc();
    }
    
    @SuppressWarnings("unchecked")
	public void testWSML2OWL() throws Exception {
    	// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "wsml2reasoner/transformation/wsml2owlExample.wsml");
        assertNotNull(is);
        Parser wsmlParser = Factory.createParser(null);
        // assuming first topentity in file is an ontology  
        ontology = (Ontology)wsmlParser.parse(new InputStreamReader(is))[0];  
        
//        System.out.println(serializeOntology(ontology)+"\n\n\n-------------\n\n\n");
		
		// transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));    
//		System.out.println(dlReasoner.serialize2OWLRDFSyntax(owlOntology));
    } 
	
    /**
     * This test checks if an exception is thrown if a transformed owl 
     * ontology is not valid owl dl.
     */
    public void testInvalidOWLDL() throws Exception {
//    	 read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "wsml2reasoner/transformation/invalidOWLDL.wsml");
        assertNotNull(is);
        Parser wsmlParser = Factory.createParser(null);
        // assuming first topentity in file is an ontology  
        ontology = (Ontology)wsmlParser.parse(new InputStreamReader(is))[0];  
        
//        System.out.println(serializeOntology(ontology)+"\n\n\n-------------\n\n\n");
		    
		try {
			// transform ontology to OWL ontology
			owlOntology = dlReasoner.convertOntology(ontology);
            fail("Should fail because the ontology is not valid owl dl!");
        } catch (RuntimeException e){e.getMessage();} 
//        System.out.println(dlReasoner.serializeWSML2OWL(ontology));
    }
    
    /**
     * This test checks for transformation of wsml subConceptOf to owl subClass.
     */
    public void testSubClassOf() throws Exception {
    	String s = "Man subConceptOf Human.";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml AttributeConstraintMolecule to 
     * owl datatype property.
     */
    public void testDatatypeProperty() throws Exception {
    	String s = "Human[hasBirthday ofType _date].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml AttributeInferenceMolecule to 
     * owl object property.
     */
    public void testObjectProperty() throws Exception {
    	String s = "Human[hasChild impliesType Child].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml memberOf to owl individual 
     * with type.
     */
    public void testMemberOf() throws Exception {
    	String s = "Mary memberOf Human.";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml AttributeValueMolecule to owl 
     * datatype property and individual with datatype value.
     */
    public void testIndividualDatatypeValue() throws Exception {
    	String s = "Mary[ageOfHuman hasValue 31].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml AttributeValueMolecule to owl 
     * object property and individual with object value.
     */
    public void testIndividualObjectValue() throws Exception {
    	String s = "Mary[hasChild hasValue Bob].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml subattribute axiom to owl 
     * subProperty.
     */
    public void testSubPropertyOf() throws Exception {
    	String s = "?x[hasParent hasValue ?y] impliedBy ?x[hasMother hasValue ?y].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml transitive axiom to owl transitive 
     * object property.
     */
    public void testTransitiveProperty() throws Exception {
    	String s = "?x[isRelatedTo hasValue ?y] and ?y[isRelatedTo hasValue ?z] " +
    			"implies ?x[isRelatedTo hasValue ?z].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml symmetric axiom to owl symmetric 
     * object property.
     */
    public void testSymmetricProperty() throws Exception {
    	String s = "?x[isMarriedTo hasValue ?y] impliedBy ?y[isMarriedTo hasValue ?x].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml inverse axiom to owl inverse 
     * object property.
     */
    public void testInverseProperty() throws Exception {
    	String s = "?x[hasHolder hasValue ?y] implies ?y[hasPet hasValue ?x].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml inverse implication to owl 
     * object property domain.
     */
    public void testObjectPropertyDomain() throws Exception {
    	String s = "?x memberOf Human impliedBy ?x[isMarriedTo hasValue ?y].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml inverse implication to owl 
     * object property range.
     */
    public void testObjectPropertyRange() throws Exception {
    	String s = "?y memberOf Human impliedBy ?x[isMarriedTo hasValue ?y].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml logical expression to owl object 
     * property domain and reange definition.
     */
    public void testImplicationWithLeftConjunction() throws Exception {
    	String s = "?x memberOf Pet and ?y memberOf Human impliedBy " +
    			"?x[hasHolder hasValue ?y].";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml logical expression to owl object 
     * property domain and owl subClass definition.
     */
    public void testImplicationWithRightDisjunction() throws Exception {
    	String s = "?x memberOf Woman impliedBy ?x[isMotherOf hasValue ?y] or " +
    			"neg(?x memberOf Man).";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml equivalence to owl subClass
     * definitions.
     */
    public void testSubClassOf2() throws Exception {
    	String s = "?x memberOf Human equivalent ?x memberOf Person.";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml equivalence logical expression 
     * with conjunction to two owl subClass definitions with intersectionOf.
     */
    public void testSubClassOf3() throws Exception {
    	String s = "?x memberOf Girl equivalent ?x memberOf Child and ?x memberOf Woman.";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml equivalence logical expression 
     * with conjunction to owl subClasses.
     */
    public void testSubClassOf4() throws Exception {
    	String s = "?x memberOf NiceAnimal and ?x memberOf DomesticAnimal impliedBy " +
    			"?x memberOf Pet.";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology);
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml equivalence logical expression 
     * with conjunction to owl subClass with intersectionOf.
     */
    public void testIntersectionOf() throws Exception {
    	String s = "?x memberOf NiceAnimal impliedBy ?x memberOf Pet and " +
    			"?x memberOf DomesticAnimal.";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology);
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml logical expression 
     * with disjunction to owl subClass definitions with intersectionOf.
     */
    public void testUnionOf() throws Exception {
    	String s = "?x memberOf Pet or ?x memberOf DomesticAnimal impliedBy " +
    			"?x memberOf NotWildAnimal.";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml logical expression 
     * with negation to owl subClass definitions with intersectionOf.
     */
    public void testComplementOf() throws Exception {
    	String s = "neg(?x memberOf Human) impliedBy ?x memberOf Machine.";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml logical expression with forall 
     * to owl definitions with allValuesFrom.
     */
    public void testAllValuesFrom() throws Exception {
    	String s = "?x memberOf SmallDogOwner implies ?x memberOf Human and forall " +
    			"?x(?x[hasDog hasValue ?y] implies ?y memberOf SmallDog).";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml logical expression with forall 
     * and exists to owl definitions with someValuesFrom.
     */
    public void testAllAndSomeValuesFrom() throws Exception {
    	String s = "?x memberOf Human implies exists ?y(?x[father hasValue ?y] " +
				"and ?y memberOf Human) and forall ?y(?x[father hasValue ?y] " +
				"implies ?y memberOf Human).";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le);
    }
    
    /**
     * This test checks for transformation of wsml logical expression with exists 
     * to owl definitions with someValuesFrom.
     */
    public void testSomeValuesFrom() throws Exception {
    	String s = "?x memberOf Human implies exists ?y(?x[father hasValue ?y] " +
				"and ?y memberOf Man).";
    	LogicalExpression le = (LogicalExpression) leFactory.createLogicalExpression(
                s, ontology); 
        axiom.addDefinition(le);
        
        // transform ontology to OWL ontology
		owlOntology = dlReasoner.convertOntology(ontology);
		System.out.println(dlReasoner.serialize2OWLAbstractSyntax(owlOntology));
		
        axiom.removeDefinition(le); 
    }
    
}
/*
 * $Log: not supported by cvs2svn $
 *
 *
 */