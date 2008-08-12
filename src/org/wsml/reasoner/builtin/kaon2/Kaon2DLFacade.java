/**
 * WSML Reasoner Implementation.
 * 
 * Copyright (c) 2005, University of Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 */
package org.wsml.reasoner.builtin.kaon2;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import org.semanticweb.owl.impl.model.OWLConcreteDataImpl;
import org.semanticweb.owl.impl.model.OWLConcreteDataTypeImpl;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLProperty;
import org.wsml.reasoner.DLReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.api.exception.InternalReasonerException;

/**
 * Integrates the KAON2 reasoner system into the WSML-DL reasoner framework.
 */
public class Kaon2DLFacade implements DLReasonerFacade
{

	/**
	 * Creates a facade object that allows to invoke the KAON2 system for
	 * performing reasoning tasks.
	 * 
	 * @throws OWLException
	 */
	public Kaon2DLFacade() throws OWLException
	{
        try {
        	mKaon2Wrapper = (DLReasonerFacade) Class.forName(WRAPPER_IMPLEMENTATION_CLASS).getConstructor().newInstance();
        }
        catch (Exception e) {
            throw new InternalReasonerException("Couldn't use the constructor for " + WRAPPER_IMPLEMENTATION_CLASS + " taking no arguments", e);
        }
	}

	public Set<OWLEntity> allClasses() throws OWLException, URISyntaxException
    {
	    return mKaon2Wrapper.allClasses();
    }

	public Set<OWLEntity> allDataProperties() throws OWLException, URISyntaxException
    {
	    return mKaon2Wrapper.allDataProperties();
    }

	public Set<OWLEntity> allIndividuals() throws OWLException, URISyntaxException
    {
	    return mKaon2Wrapper.allIndividuals();
    }

	public Set<OWLEntity> allInstancesOf( OWLClass clazz ) throws OWLException, URISyntaxException
    {
	    return mKaon2Wrapper.allInstancesOf( clazz );
    }

	public Set<OWLEntity> allObjectProperties() throws OWLException, URISyntaxException
    {
	    return mKaon2Wrapper.allObjectProperties();
    }
	
	public Set<OWLEntity> allProperties() throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.allProperties();
    }
	
	public Set<Set> allTypesOf( OWLIndividual individual ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.allTypesOf( individual );
    }
	
	public Set<Set<OWLEntity>> ancestorClassesOf( OWLDescription clazz ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.ancestorClassesOf( clazz );
    }
	public Set<Set> ancestorPropertiesOf( OWLProperty property ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.ancestorPropertiesOf( property );
    }
	
	public void deRegister() throws ExternalToolException
    {
		mKaon2Wrapper.deRegister();
    }
	
	public Set<Set> descendantClassesOf( OWLDescription clazz ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.descendantClassesOf( clazz );
    }
	
	public Set<Set> descendantPropertiesOf( OWLProperty property ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.descendantPropertiesOf( property );
    }
	
	public Set<OWLEntity> domainsOf( OWLProperty property ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.domainsOf( property );
    }
	
	public Set<OWLEntity> equivalentClassesOf( OWLDescription clazz ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.equivalentClassesOf( clazz );
    }
	
	public Set<OWLEntity> equivalentPropertiesOf( OWLProperty property ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.equivalentPropertiesOf( property );
    }
	
	public OWLDataValue getDataPropertyValue( OWLIndividual subject, OWLDataProperty property ) throws OWLException,
                    InterruptedException, URISyntaxException
    {
		return mKaon2Wrapper.getDataPropertyValue( subject, property );
    }
	
	public Set<OWLDataValue> getDataPropertyValues( OWLIndividual subject, OWLDataProperty property )
                    throws OWLException, InterruptedException, URISyntaxException
    {
		return mKaon2Wrapper.getDataPropertyValues( subject, property );
    }
	
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getDataPropertyValues( OWLIndividual individual )
                    throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.getDataPropertyValues( individual );
    }
	
	public OWLIndividual getObjectPropertyValue( OWLIndividual subject, OWLObjectProperty property )
                    throws OWLException, InterruptedException, URISyntaxException
    {
		return mKaon2Wrapper.getObjectPropertyValue( subject, property );
    }
	
	public Set<OWLEntity> getObjectPropertyValues( OWLIndividual subject, OWLObjectProperty property )
                    throws OWLException, InterruptedException, URISyntaxException
    {
		return mKaon2Wrapper.getObjectPropertyValues( subject, property );
    }
	
	public Map<OWLEntity, Set<OWLEntity>> getObjectPropertyValues( OWLIndividual individual ) throws OWLException,
                    URISyntaxException
    {
		return mKaon2Wrapper.getObjectPropertyValues( individual );
    }
	
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getPropertyValues( OWLDataProperty property ) throws OWLException,
                    URISyntaxException
    {
		return mKaon2Wrapper.getPropertyValues( property );
    }
	
	public Map<OWLEntity, Set<OWLEntity>> getPropertyValues( OWLObjectProperty property ) throws OWLException,
                    URISyntaxException
    {
		return mKaon2Wrapper.getPropertyValues( property );
    }
	
	public boolean hasPropertyValue( OWLIndividual subject, OWLDataProperty property, OWLDataValue object )
                    throws OWLException, InterruptedException
    {
		return mKaon2Wrapper.hasPropertyValue( subject, property, object );
    }
	
	public boolean hasPropertyValue( OWLIndividual subject, OWLObjectProperty property, OWLIndividual object )
                    throws OWLException, InterruptedException
    {
		return mKaon2Wrapper.hasPropertyValue( subject, property, object );
    }
	
	public Set<OWLEntity> inversePropertiesOf( OWLObjectProperty property ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.inversePropertiesOf( property );
    }
	
	public boolean isConsistent()
    {
		return mKaon2Wrapper.isConsistent();
    }
	
	public boolean isConsistent( OWLDescription description ) throws OWLException, InterruptedException
    {
		return mKaon2Wrapper.isConsistent( description );
    }
	
	public boolean isEquivalentClass( OWLDescription clazz1, OWLDescription clazz2 ) throws OWLException,
                    URISyntaxException
    {
		return mKaon2Wrapper.isEquivalentClass( clazz1, clazz2 );
    }
	
	public boolean isInstanceOf( OWLIndividual individual, OWLDescription clazz ) throws OWLException,
                    URISyntaxException
    {
		return mKaon2Wrapper.isInstanceOf( individual, clazz );
    }
	
	public boolean isSubClassOf( OWLDescription clazz1, OWLDescription clazz2 ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.isSubClassOf( clazz1, clazz2 );
    }
	
	public Set<OWLConcreteDataTypeImpl> rangesOf( OWLDataProperty property ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.rangesOf( property );
    }
	
	public Set<OWLEntity> rangesOf( OWLObjectProperty property ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.rangesOf( property );
    }
	
	public void register( OWLOntology owlOntology ) throws ExternalToolException
    {
		mKaon2Wrapper.register( owlOntology );
    }

	public Set<Set> subClassesOf( OWLDescription clazz ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.subClassesOf( clazz );
    }
	
	public Set<Set> subPropertiesOf( OWLProperty property ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.subPropertiesOf( property );
    }
	
	public Set<Set> superClassesOf( OWLDescription clazz ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.superClassesOf( clazz );
    }
	
	public Set<Set<OWLEntity>> superPropertiesOf( OWLProperty property ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.superPropertiesOf( property );
    }
	
	public Set<Set> typesOf( OWLIndividual individual ) throws OWLException, URISyntaxException
    {
		return mKaon2Wrapper.typesOf( individual );
    }

	private static final String WRAPPER_IMPLEMENTATION_CLASS = "org.wsml.reasoner.builtin.kaon2.Kaon2DLWrapperImplementation";
	private DLReasonerFacade mKaon2Wrapper;
}
