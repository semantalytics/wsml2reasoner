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
package org.wsml.reasoner.builtin.kaon2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

import org.apache.log4j.Logger;
import org.mindswap.pellet.query.QueryResults;
import org.semanticweb.kaon2.api.*;
import org.semanticweb.kaon2.api.reasoner.Reasoner;
import org.semanticweb.owl.impl.model.OWLConcreteDataImpl;
import org.semanticweb.owl.impl.model.OWLConcreteDataTypeImpl;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.model.*;
import org.wsml.reasoner.DLReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.serializer.owl.OWLSerializerImpl;

/**
 * Integrates the KAON reasoner system into the WSML-DL reasoner framework.
 *
 * <pre>
 *  Created on July 3rd, 2006
 *  Committed by $Author: hlausen $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/builtin/kaon2/KAON2DLFacade.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * Holger Lausen, DERI Innsbruck
 * @version $Revision: 1.1 $ $Date: 2007-01-02 11:30:50 $
 */
public class KAON2DLFacade implements DLReasonerFacade {

    Reasoner reasoner;
    private Logger log = Logger.getLogger(KAON2DLFacade.class);

	private Map<String, Reasoner> registeredOntologies = null;

	/**
     * Creates a facade object that allows to invoke the PELLET system for
     * performing reasoning tasks.
     */
    public KAON2DLFacade() {
        super();
        registeredOntologies = new HashMap<String, Reasoner>();
    }

    /**
     * Registers the OWL ontology at the PELLET reasoner.
     */
	public void register(OWLOntology owlOntology)
			throws ExternalToolException {

        StringBuffer buf = new StringBuffer();
        try {
            new OWLSerializerImpl().serialize(owlOntology, buf);
        } catch (RendererException e) {
            log.error(e);
            throw new ExternalToolException("could not serialize ontology as owl",e);
        }
        InputStream in = new ByteArrayInputStream(buf.toString().getBytes());

        KAON2Connection connection = KAON2Manager.newConnection();
        Map<String, Object> m = new HashMap<String, Object>();
        m.put(KAON2Connection.LOAD_FROM_INPUT_STREAM, in);
        try {
        Ontology ontology = connection.openOntology(
                "http://example.com/"+
                URLEncoder.encode(owlOntology.getURI().toString(),"UTF-8"),m);


			Reasoner reasoner = ontology.createReasoner();
			registeredOntologies.put(owlOntology.getURI().toString(),
					reasoner);
        } catch (Exception e) {
            log.error(e);
            throw new ExternalToolException("could not register ontology with KAON",e);
        }
	}

	/**
	 * The Knowledge base, that PELLET derived from this OWL
	 * ontology is cleared.
	 * @throws ExternalToolException
	 */
	public void deRegister(String ontologyURI)
			throws ExternalToolException {
		reasoner = getReasoner(ontologyURI);
		reasoner.dispose();
		registeredOntologies.remove(ontologyURI);
	}

	public boolean isConsistent(String ontologyURI) {
		reasoner = getReasoner(ontologyURI);
		try {
            return reasoner.isSatisfiable();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

	public boolean isConsistent(String ontologyURI, OWLDescription description)
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.isSatisfiable(description);
	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allClasses(String ontologyURI) {
		reasoner = getReasoner(ontologyURI);

        throw new UnsupportedOperationException("not yet");
//		return reasoner.getClasses();
	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allIndividuals(String ontologyURI) {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.getIndividuals();
	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allProperties(String ontologyURI) {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.getProperties();
	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allDataProperties(String ontologyURI) {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.getDataProperties();
	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allObjectProperties(String ontologyURI) {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.getObjectProperties();
	}

	@SuppressWarnings("unchecked")
	public Set<Set> descendantClassesOf(String ontologyURI,
			OWLDescription clazz) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.descendantClassesOf(clazz);
	}

	@SuppressWarnings("unchecked")
	public Set<Set> subClassesOf(String ontologyURI,
			OWLDescription clazz) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.subClassesOf(clazz);
	}

	@SuppressWarnings("unchecked")
	public Set<Set> ancestorClassesOf(String ontologyURI,
			OWLDescription clazz) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.ancestorClassesOf(clazz);
	}

	@SuppressWarnings("unchecked")
	public Set<Set> superClassesOf(String ontologyURI,
			OWLDescription clazz) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.superClassesOf(clazz);
	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> equivalentClassesOf(String ontologyURI,
			OWLDescription clazz) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.equivalentClassesOf(clazz);
	}

	public boolean isEquivalentClass(String ontologyURI, OWLDescription clazz1,
			OWLDescription clazz2) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.isEquivalentClass(clazz1, clazz2);
	}

	public boolean isSubClassOf(String ontologyURI, OWLDescription clazz1,
			OWLDescription clazz2) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.isSubClassOf(clazz1, clazz2);
	}

	public boolean isInstanceOf(String ontologyURI, OWLIndividual individual,
			OWLDescription clazz) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.isInstanceOf(individual, clazz);
	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> allInstancesOf(String ontologyURI, org.semanticweb.owl.model.OWLClass clazz)
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.allInstancesOf(clazz);
	}

	@SuppressWarnings("unchecked")
	public Set<Set> typesOf(String ontologyURI, OWLIndividual individual)
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.typesOf(individual);
	}

	@SuppressWarnings("unchecked")
	public Set<Set> allTypesOf(String ontologyURI, OWLIndividual individual)
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.allTypesOf(individual);
	}

	@SuppressWarnings("unchecked")
	public Set<Set> descendantPropertiesOf(String ontologyURI,
			OWLProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.descendantPropertiesOf(property);
	}

	@SuppressWarnings("unchecked")
	public Set<Set> subPropertiesOf(String ontologyURI,
			OWLProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.subPropertiesOf(property);
	}

	@SuppressWarnings("unchecked")
	public Set<Set> ancestorPropertiesOf(String ontologyURI,
			OWLProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.ancestorPropertiesOf(property);
	}

	@SuppressWarnings("unchecked")
	public Set<Set> superPropertiesOf(String ontologyURI,
			OWLProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.superPropertiesOf(property);
	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> equivalentPropertiesOf(String ontologyURI,
			OWLProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.equivalentPropertiesOf(property);
	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> inversePropertiesOf(String ontologyURI,
			OWLObjectProperty property)	throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.inversePropertiesOf(property);
	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> domainsOf(String ontologyURI, OWLProperty property)
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.domainsOf(property);
	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> rangesOf(String ontologyURI,
			OWLObjectProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.rangesOf(property);
	}

	@SuppressWarnings("unchecked")
	public Set<OWLConcreteDataTypeImpl> rangesOf(String ontologyURI,
			OWLDataProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.rangesOf(property);
	}

	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getDataPropertyValues(
			String ontologyURI, OWLIndividual individual) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.getDataPropertyValues(individual);
	}

	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLEntity>> getObjectPropertyValues(
			String ontologyURI, OWLIndividual individual) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.getObjectPropertyValues(individual);
	}

	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLEntity>> getPropertyValues(String ontologyURI,
			OWLObjectProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.getPropertyValues(property);
	}

	@SuppressWarnings("unchecked")
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getPropertyValues(
			String ontologyURI, OWLDataProperty property) throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.getPropertyValues(property);
	}

	@SuppressWarnings("unchecked")
	public boolean hasPropertyValue(String ontologyURI, OWLIndividual subject,
			OWLObjectProperty property, OWLIndividual object)
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.hasPropertyValue(subject, property, object);
	}

	@SuppressWarnings("unchecked")
	public boolean hasPropertyValue(String ontologyURI, OWLIndividual subject,
			OWLDataProperty property, OWLDataValue object)
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.hasPropertyValue(subject, property, object);
	}

	@SuppressWarnings("unchecked")
	public Set<OWLEntity> getObjectPropertyValues(String ontologyURI,
			OWLIndividual subject, OWLObjectProperty property)
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.getPropertyValues(subject, property);
	}

	@SuppressWarnings("unchecked")
	public Set<OWLDataValue> getDataPropertyValues(String ontologyURI,
			OWLIndividual subject, OWLDataProperty property)
			throws OWLException{
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException("not yet");
//		return reasoner.getPropertyValues(subject, property);
	}

	private Reasoner getReasoner(String ontologyURI) {
		if (registeredOntologies.containsKey(ontologyURI)) {
			return registeredOntologies.get(ontologyURI);
		}
		else {
			try {
				throw new ExternalToolException("Ontology is not registrated!");
			} catch (ExternalToolException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public OWLIndividual getObjectPropertyValue(String ontologyURI,
			OWLIndividual subject, OWLObjectProperty property)
			throws OWLException {
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException();
//		return reasoner.getPropertyValue(subject, property);
	}

	public OWLDataValue getDataPropertyValue(String ontologyURI,
			OWLIndividual subject, OWLDataProperty property)
			throws OWLException{
		reasoner = getReasoner(ontologyURI);
        throw new UnsupportedOperationException();
//		return reasoner.getPropertyValue(subject, property);
	}

	public QueryResults evaluate(String ontologyURI, String queryString) {
		throw new UnsupportedOperationException();
//		reasoner = registeredOntologies.get(ontologyURI);
//		QueryResults results = null;
//		Set set = reasoner.getKB().getObjectProperties();
//		Iterator it = set.iterator();
//		while (it.hasNext())
//			System.out.println(it.next().toString());
//		results = QueryEngine.execSPARQL(queryString, reasoner.getKB());
//		return results;
	}

}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.9  2006/11/30 16:54:57  nathalie
 * added methods to get direct super-/sub-concepts and direct super-/sub-relations
 *
 * Revision 1.8  2006/09/19 13:47:28  nathalie
 * added example for using Pellet Logger printClassTree function
 *
 * Revision 1.7  2006/09/14 18:37:06  hlausen
 * enabled the print of class tree if pellet facade logging is set to DEBUG
 *
 * when register inconsitent ontologies the method should not throw the pellete exception but the wsml2reasoner inconsistency exception, however the inconsistencyexception class still needs some fix
 *
 * Revision 1.6  2006/08/31 12:36:00  nathalie
 * removed methods from WSMLDLReasoner interface to the WSMLReasoner interface. Replaced some methods by entails() and groundQuery() methods.
 *
 * Revision 1.5  2006/08/10 08:30:59  nathalie
 * added request for getting direct concept/concepts of an instance
 *
 * Revision 1.4  2006/08/08 10:14:27  nathalie
 * implemented support for registering multiple ontolgies at wsml-dl reasoner
 *
 * Revision 1.3  2006/07/21 16:25:21  nathalie
 * completing the pellet reasoner integration
 *
 * Revision 1.2  2006/07/20 17:50:23  nathalie
 * integration of the pellet reasoner
 *
 * Revision 1.1  2006/07/18 08:21:01  nathalie
 * adding wsml dl reasoner interface,
 * transformation from wsml dl to owl-dl
 *
 *
 */