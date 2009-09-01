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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Parameter;
import org.omwg.ontology.Relation;
import org.omwg.ontology.RelationInstance;
import org.omwg.ontology.Type;
import org.omwg.ontology.Value;
import org.wsml.reasoner.transformation.AnonymousIdTranslator;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsmo.common.Entity;
import org.wsmo.common.Identifier;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

/**
 * A normalizer for WSML-DL relations, subRelations and relation instances.
 * 
 * <pre>
 *   Created on July 3rd, 2006
 *   Committed by $Author: graham $
 *   $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/transformation/dl/Relation2AttributeNormalizer.java,v $,
 * </pre>
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.5 $ $Date: 2007-04-26 17:39:14 $
 */
public class Relation2AttributeNormalizer implements OntologyNormalizer {

    private WsmoFactory wsmoFactory;

    private AnonymousIdTranslator anonymousIdTranslator;

    public Relation2AttributeNormalizer(FactoryContainer factory) {
    	
    	wsmoFactory = factory.getWsmoFactory();
        anonymousIdTranslator = new AnonymousIdTranslator(factory.getWsmoFactory());
    }

    public Set<Axiom> normalizeAxioms(Collection<Axiom> theAxioms) {
        throw new UnsupportedOperationException();
    }

    public Set<Entity> normalizeEntities(Collection<Entity> theEntities) {
        Set<Entity> result = new HashSet<Entity>();
        for (Entity e : theEntities) {
            if (e instanceof Relation) {
                try {
                    result.addAll(normalizeRelation((Relation) e));
                }
                catch (InvalidModelException e1) {
                    e1.printStackTrace();
                }
            }
            else if (e instanceof RelationInstance) {
                try {
                    result.addAll(normalizeRelationInstance((RelationInstance) e));
                }
                catch (InvalidModelException e1) {
                    e1.printStackTrace();
                }
            }
            else {
                result.add(e);
            }
        }
        return result;
    }

    /*
     * Relations are replaced by concept attributes.
     */
    private Set<Entity> normalizeRelation(Relation relation) throws InvalidModelException {
        Set<Entity> result = new HashSet<Entity>();
        Parameter p1 = relation.getParameter((byte) 0);
        Parameter p2 = relation.getParameter((byte) 1);
        Concept newConcept = null;
        for (Type t : p1.listTypes()) {
            if (t instanceof Concept) {
                Identifier id = ((Concept) t).getIdentifier();

                newConcept = relation.getOntology().findConcept(id);
                if (newConcept == null) {
                    newConcept = wsmoFactory.createConcept(id);
                }

                Attribute attribute = newConcept.createAttribute(relation.getIdentifier());
                for (Type type : p2.listTypes()) {
                    attribute.addType(type);
                    if (p2.isConstraining()) {
                        attribute.setConstraining(true);
                    }
                }
            }
        }
        if (relation.listSuperRelations().size() > 0) {
            Axiom axiom = normalizeSuperRelations(relation.listSuperRelations(), relation);
            if (axiom != null) {
                result.add(axiom);
            }
        }
        result.add(newConcept);
        return result;
    }

    /*
     * Superrelations are replaced by implication logical expressions.
     */
    private Axiom normalizeSuperRelations(Set<Relation> superRelations, Relation relation) {
        Axiom result = wsmoFactory.createAxiom((Identifier) anonymousIdTranslator.translate(wsmoFactory.createAnonymousID()));
        for (Relation sr : superRelations){
            String le = "?x[_\"" + relation.getIdentifier() + "\" hasValue ?y] implies " + "?x[_\"" + sr.getIdentifier() + "\" hasValue ?y].";
            LogicalExpressionParser leParser = new WsmlLogicalExpressionParser();
            try {
                result.addDefinition(leParser.parse(le));
            }
            catch (ParserException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /*
     * Relation instances are replaced by attribute values.
     */

    private Set<Entity> normalizeRelationInstance(RelationInstance relationInstance) throws InvalidModelException {
        Set<Entity> result = new HashSet<Entity>();
        Value v1 = relationInstance.getParameterValue((byte) 0);
        Value v2 = relationInstance.getParameterValue((byte) 1);
        Instance newInstance = null;
        if (v1 instanceof Instance) {
            newInstance = (Instance) v1;
        }
        if (newInstance == null) {
            newInstance = wsmoFactory.createInstance(((Instance) v1).getIdentifier());
        }
        if (v2 instanceof Instance) {
            Instance tmp = wsmoFactory.createInstance(((Instance) v2).getIdentifier());
            newInstance.addAttributeValue(relationInstance.getRelation().getIdentifier(), tmp);
        }
        else {
            newInstance.addAttributeValue(relationInstance.getRelation().getIdentifier(), v2);
        }
        result.add(newInstance);
        return result;
    }
}