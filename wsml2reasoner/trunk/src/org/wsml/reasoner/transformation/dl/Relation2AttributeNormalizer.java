/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Austria.
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
package org.wsml.reasoner.transformation.dl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Parameter;
import org.omwg.ontology.Relation;
import org.omwg.ontology.RelationInstance;
import org.omwg.ontology.Type;
import org.omwg.ontology.Value;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.AnonymousIdTranslator;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsmo.common.Entity;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.common.exception.SynchronisationException;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

/**
 * A normalizer for WSML-DL relations, subRelations and relation instances.
 *
 * <pre>
 *  Created on July 3rd, 2006
 *  Committed by $Author: nathalie $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/transformation/dl/Relation2AttributeNormalizer.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.1 $ $Date: 2006-07-18 08:21:01 $
 */
public class Relation2AttributeNormalizer implements OntologyNormalizer {

	protected WsmoFactory wsmoFactory;
    
    protected LogicalExpressionFactory leFactory;
    
    protected DataFactory dataFactory;

    protected AnonymousIdTranslator anonymousIdTranslator;
    
	public Relation2AttributeNormalizer(WSMO4JManager wsmoManager) {
        wsmoFactory = wsmoManager.getWSMOFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        dataFactory = wsmoManager.getDataFactory();
        anonymousIdTranslator = new AnonymousIdTranslator(wsmoFactory);
	}
	
	public Ontology normalize(Ontology ontology) {
		try {		
			// gather attributes from normalized relations and logical expressions 
			// from normalized subRelations	
			ontology = normalizeRelations(ontology);
			// gather attribute values from normalized relationInstances
			ontology = normalizeRelationInstances(ontology);
		} catch (SynchronisationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidModelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return ontology;
	}

	/*
	 * Relations are replaced by concept attributes.
	 */
	@SuppressWarnings("unchecked")
	private Ontology normalizeRelations(Ontology ontology) throws InvalidModelException {
		Set<Relation> relations = (Set<Relation>) ontology.listRelations();
		Iterator<Relation> it = relations.iterator();
		while (it.hasNext()) {
			Relation relation = it.next();
			Parameter p1 = relation.getParameter((byte) 0);
			Parameter p2 = relation.getParameter((byte) 1);
			Concept newConcept = null;
			Set<Concept> types1 = p1.listTypes();
			Set types2 = p2.listTypes();
			Iterator<Concept> it1 = types1.iterator();
			while (it1.hasNext()) {
				Concept concept = it1.next();
				newConcept = ontology.findConcept(concept.getIdentifier());
				if (newConcept == null) {
					newConcept = wsmoFactory.createConcept(concept.getIdentifier());
				}
				Attribute attribute = newConcept.createAttribute(relation.getIdentifier());	
				Iterator it2 = types2.iterator();
				while (it2.hasNext()) {
					Type type = (Type) it2.next();
					attribute.addType(type);
					if (p2.isConstraining()) {
						attribute.setConstraining(true);
					}
				}
				if (relation.listNFPValues().size() > 0) {
					Map nfps = relation.listNFPValues();
					Set<Entry> nfpsSet = nfps.entrySet();
					attribute = (Attribute) transferNFPs(nfpsSet, attribute);
				}
			}			
			if (relation.listSuperRelations().size() > 0) {
				Set<Relation> superRelations = relation.listSuperRelations();
				Axiom axiom = normalizeSuperRelations(superRelations, relation);
				if (axiom != null) {
					ontology.addAxiom(axiom);
				}
			}
			ontology.removeRelation(relation);
			ontology.addConcept(newConcept);
		}
		return ontology;
	}
	
	/*
	 * Superrelations are replaced by implication logical expressions.
	 */
	private Axiom normalizeSuperRelations(Set<Relation> superRelations, Relation relation) {
		Axiom axiom = null;
		LogicalExpression logExpr = null;
		Iterator<Relation> it = superRelations.iterator();
		while (it.hasNext()) {
			Relation superRelation = it.next();
			axiom = wsmoFactory.createAxiom((Identifier) anonymousIdTranslator.translate(
					wsmoFactory.createAnonymousID())) ;
			String le = "?x[_\"" + relation.getIdentifier() + "\" hasValue ?y] implies " +
					"?x[_\"" + superRelation.getIdentifier() + "\" hasValue ?y].";
			try {
				logExpr = leFactory.createLogicalExpression(le);
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			axiom.addDefinition(logExpr);
		}
		return axiom;	
	}
	
	/*
	 * Relation instances are replaced by attribute values.
	 */
	@SuppressWarnings("unchecked")
	private Ontology normalizeRelationInstances(Ontology ontology) 
			throws SynchronisationException, InvalidModelException {
		Set<RelationInstance> relationInstances = (Set<RelationInstance>) 
				ontology.listRelationInstances();
		Iterator<RelationInstance> it = relationInstances.iterator();
		while (it.hasNext()) {
			RelationInstance relationInstance = it.next();
			Value v1 = relationInstance.getParameterValue((byte) 0);
			Value v2 = relationInstance.getParameterValue((byte) 1);
			Instance newInstance = null;
			newInstance = ontology.findInstance(((Instance) v1).getIdentifier());
			if (newInstance == null) {
				newInstance = wsmoFactory.createInstance(((Instance) v1).getIdentifier());
			}
			if (v2 instanceof Instance) {
				Instance tmp = wsmoFactory.createInstance(((Instance) v2).getIdentifier());
				newInstance.addAttributeValue(relationInstance.getRelation().getIdentifier(), 
						tmp);
			}
			else {
				newInstance.addAttributeValue(relationInstance.getRelation().getIdentifier(), v2);
			}
			if (relationInstance.listNFPValues().size() > 0) {
				Map nfps = relationInstance.listNFPValues();
				Set<Entry> nfpsSet = nfps.entrySet();
				newInstance = (Instance) transferNFPs(nfpsSet, newInstance);
			}			
			ontology.removeRelationInstance(relationInstance);
			ontology.addInstance(newInstance);
		}
		return ontology;
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
