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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.semanticweb.kaon2.id;
import org.semanticweb.kaon2.api.KAON2Connection;
import org.semanticweb.kaon2.api.KAON2Exception;
import org.semanticweb.kaon2.api.KAON2Manager;
import org.semanticweb.kaon2.api.Namespaces;
import org.semanticweb.kaon2.api.Ontology;
import org.semanticweb.kaon2.api.Request;
import org.semanticweb.kaon2.api.logic.Constant;
import org.semanticweb.kaon2.api.owl.axioms.ClassMember;
import org.semanticweb.kaon2.api.owl.axioms.DataPropertyMember;
import org.semanticweb.kaon2.api.owl.axioms.DataPropertyRange;
import org.semanticweb.kaon2.api.owl.axioms.EquivalentDataProperties;
import org.semanticweb.kaon2.api.owl.axioms.EquivalentObjectProperties;
import org.semanticweb.kaon2.api.owl.axioms.InverseObjectProperties;
import org.semanticweb.kaon2.api.owl.axioms.ObjectPropertyDomain;
import org.semanticweb.kaon2.api.owl.axioms.ObjectPropertyMember;
import org.semanticweb.kaon2.api.owl.axioms.ObjectPropertyRange;
import org.semanticweb.kaon2.api.owl.axioms.SubDataPropertyOf;
import org.semanticweb.kaon2.api.owl.axioms.SubObjectPropertyOf;
import org.semanticweb.kaon2.api.owl.elements.DataProperty;
import org.semanticweb.kaon2.api.owl.elements.Description;
import org.semanticweb.kaon2.api.owl.elements.Individual;
import org.semanticweb.kaon2.api.owl.elements.OWLClass;
import org.semanticweb.kaon2.api.owl.elements.ObjectProperty;
import org.semanticweb.kaon2.api.reasoner.Query;
import org.semanticweb.kaon2.api.reasoner.Reasoner;
import org.semanticweb.kaon2.api.reasoner.SubsumptionHierarchy;
import org.semanticweb.kaon2.api.reasoner.SubsumptionHierarchy.Node;
import org.semanticweb.owl.impl.model.OWLConcreteDataImpl;
import org.semanticweb.owl.impl.model.OWLConcreteDataTypeImpl;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.util.OWLManager;
import org.wsml.reasoner.DLReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.serializer.owl.OWLSerializerImpl;

/**
 * Integrates the KAON2 reasoner system into the WSML-DL reasoner framework.
 * 
 * <pre>
 *  Created on July 3rd, 2006
 *  Committed by $Author: nathalie $
 *  $Source: /usr/local/cvsroot/wsml2reasoner/src/org/wsml/reasoner/builtin/kaon2/Kaon2DLFacade.java,v $,
 * </pre>
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck; Holger Lausen, DERI Innsbruck
 * @version $Revision: 1.10 $ $Date: 2007-11-07 16:26:26 $
 */
public class Kaon2DLFacade implements DLReasonerFacade
{

	private Reasoner reasoner = null;

	private Logger log = Logger.getLogger( Kaon2DLFacade.class );

	private Ontology ontology = null;

	private OWLDataFactory owlDataFactory = null;

	private boolean equivalentPropertiesCheck = false;

	private String owlOntologyURI;

	/**
	 * Creates a facade object that allows to invoke the KAON2 system for
	 * performing reasoning tasks.
	 * 
	 * @throws OWLException
	 */
	public Kaon2DLFacade() throws OWLException
	{
		super();
		Map<Object, String> parameters = new HashMap<Object, String>();
		parameters.put( OWLManager.OWL_CONNECTION, "org.semanticweb.owl.impl.model.OWLConnectionImpl" );
		owlDataFactory = OWLManager.getOWLConnection( parameters ).getDataFactory();
	}

	/**
	 * Creates a new Kaon2DL based facade.
	 * 
	 * @param m the wsmo4j manager, config map
	 * @throws OWLException
	 */
	public Kaon2DLFacade( final WSMO4JManager m, final Map<String, Object> config ) throws OWLException
	{
		this();
	}

	/**
	 * Registers the OWL ontology at the KAON2 reasoner.
	 */
	public void register( OWLOntology owlOntology ) throws ExternalToolException
	{
		try
		{
			this.owlOntologyURI = owlOntology.getURI().toString();
		}
		catch( OWLException e )
		{
			throw new ExternalToolException( "could not deregister ontology with KAON", e );
		}
		StringBuffer buf = new StringBuffer();
		try
		{
			new OWLSerializerImpl().serialize( owlOntology, buf );
		}
		catch( RendererException e )
		{
			log.error( e );
			throw new ExternalToolException( "could not serialize ontology as owl", e );
		}
		InputStream in = new ByteArrayInputStream( buf.toString().getBytes() );
		KAON2Connection connection;
		try
		{
			connection = KAON2Manager.newConnection();
			Map<String, Object> m = new HashMap<String, Object>();
			m.put( KAON2Connection.LOAD_FROM_INPUT_STREAM, in );

			ontology = connection.openOntology( owlOntology.getURI().toString(), m );
			reasoner = ontology.createReasoner();
		}
		catch( Exception e )
		{
			log.error( e );
			throw new ExternalToolException( "could not register ontology with KAON", e );
		}
	}

	/**
	 * The Knowledge base, that KAON2 derived from this OWL ontology is cleared.
	 * 
	 * @throws ExternalToolException
	 */
	public void deRegister() throws ExternalToolException
	{
		if( owlOntologyURI != null )
		{

			owlOntologyURI = null;
		}
		reasoner.dispose();
	}

	public boolean isConsistent()
	{

		try
		{
			return reasoner.isSatisfiable();
		}
		catch( Exception e )
		{
			throw new RuntimeException( e );
		}
	}

	public boolean isConsistent( OWLDescription description ) throws OWLException, InterruptedException
	{
		try
		{

			Description des = null;
			if( description.getClass().getName().toString().equals( "org.semanticweb.owl.impl.model.OWLClassImpl" ) )
			{
				des = KAON2Manager.factory().description(
				                description.toString().substring( description.toString().indexOf( "]" ) + 2 ),
				                Namespaces.INSTANCE );
			}
			else if( description.getClass().getName().toString().equals( "org.semanticweb.owl.impl.model.OWLAndImpl" ) )
			{
				Iterator<OWLDescription> it = ((OWLAnd) description).getOperands().iterator();
				Vector<OWLDescription> list = new Vector<OWLDescription>();
				while( it.hasNext() )
				{
					list.add( it.next() );
				}
				Description arg1 = KAON2Manager.factory().description(
				                list.elementAt( 0 ).toString().substring(
				                                list.elementAt( 0 ).toString().indexOf( "]" ) + 2 ),
				                Namespaces.INSTANCE );
				Description arg2 = KAON2Manager.factory().description(
				                list.elementAt( 1 ).toString().substring(
				                                list.elementAt( 1 ).toString().indexOf( "]" ) + 2 ),
				                Namespaces.INSTANCE );
				des = KAON2Manager.factory().objectAnd( arg1, arg2 );
			}
			return reasoner.isSatisfiable( des );
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}
	}

	public Set<OWLEntity> allClasses() throws OWLException, URISyntaxException
	{
		try
		{

			Set<OWLEntity> resultSet = new HashSet<OWLEntity>();

			SubsumptionHierarchy hierarchy = reasoner.getSubsumptionHierarchy();
			Iterator<Node> it = hierarchy.iterator();
			while( it.hasNext() )
			{
				Node n = it.next();
				Set<OWLClass> classSet = n.getOWLClasses();
				for( OWLClass c : classSet )
				{
					OWLEntity entity = owlDataFactory.getOWLClass( new URI( c.getURI() ) );
					resultSet.add( entity );
				}
			}
			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}
		catch( InterruptedException e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<OWLEntity> allIndividuals() throws OWLException, URISyntaxException
	{
		try
		{

			Set<OWLEntity> resultSet = new HashSet<OWLEntity>();
			Request<Individual> entityRequest = ontology.createEntityRequest( Individual.class );
			Set<Individual> individualsAxioms = entityRequest.get();
			for( Individual axiom : individualsAxioms )
			{
				OWLEntity entity = owlDataFactory.getOWLIndividual( new URI( axiom.toString() ) );
				resultSet.add( entity );
			}
			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<OWLEntity> allProperties() throws OWLException, URISyntaxException
	{

		Set<OWLEntity> resultSet = new HashSet<OWLEntity>();
		resultSet = allDataProperties();
		resultSet.addAll( allObjectProperties() );
		return resultSet;
	}

	public Set<OWLEntity> allDataProperties() throws OWLException, URISyntaxException
	{
		try
		{

			Set<OWLEntity> resultSet = new HashSet<OWLEntity>();
			Request<DataProperty> entityRequest = ontology.createEntityRequest( DataProperty.class );
			Set<DataProperty> dataPropertyAxioms = entityRequest.get();
			for( DataProperty axiom : dataPropertyAxioms )
			{
				OWLEntity entity = owlDataFactory.getOWLDataProperty( new URI( axiom.toString() ) );
				resultSet.add( entity );
			}
			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<OWLEntity> allObjectProperties() throws OWLException, URISyntaxException
	{

		try
		{

			Set<OWLEntity> resultSet = new HashSet<OWLEntity>();
			Request<ObjectProperty> entityRequest = ontology.createEntityRequest( ObjectProperty.class );
			Set<ObjectProperty> objectPropertyAxioms = entityRequest.get();
			for( ObjectProperty axiom : objectPropertyAxioms )
			{
				OWLEntity entity = owlDataFactory.getOWLObjectProperty( new URI( axiom.toString() ) );
				resultSet.add( entity );
			}
			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<Set> descendantClassesOf( OWLDescription clazz ) throws OWLException, URISyntaxException
	{
		try
		{

			Set<Set> resultSet = new HashSet<Set>();
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();

			SubsumptionHierarchy hierarchy = reasoner.getSubsumptionHierarchy();
			OWLClass owlClass = KAON2Manager.factory().owlClass(
			                clazz.toString().substring( clazz.toString().indexOf( "]" ) + 2 ) );
			Node node = hierarchy.getNodeFor( owlClass );
			Set<Node> nodeSet = node.getDescendantNodes();
			for( Node n : nodeSet )
			{
				Set<OWLClass> classSet = n.getOWLClasses();
				for( OWLClass c : classSet )
				{
					OWLEntity entity = owlDataFactory.getOWLClass( new URI( c.getURI() ) );
					entitySet.add( entity );
				}
			}
			resultSet.add( entitySet );
			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}
		catch( InterruptedException e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<Set> subClassesOf( OWLDescription clazz ) throws OWLException, URISyntaxException
	{
		try
		{

			Set<Set> resultSet = new HashSet<Set>();
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			SubsumptionHierarchy hierarchy = reasoner.getSubsumptionHierarchy();
			OWLClass owlClass = KAON2Manager.factory().owlClass(
			                clazz.toString().substring( clazz.toString().indexOf( "]" ) + 2 ) );
			Node node = hierarchy.getNodeFor( owlClass );
			Set<Node> nodeSet = node.getChildNodes();
			for( Node n : nodeSet )
			{
				Set<OWLClass> classSet = n.getOWLClasses();
				for( OWLClass c : classSet )
				{
					OWLEntity entity = owlDataFactory.getOWLClass( new URI( c.getURI() ) );
					entitySet.add( entity );
				}
			}
			resultSet.add( entitySet );
			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}
		catch( InterruptedException e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<Set<OWLEntity>> ancestorClassesOf( OWLDescription clazz ) throws OWLException, URISyntaxException
	{
		try
		{

			Set<Set<OWLEntity>> resultSet = new HashSet<Set<OWLEntity>>();
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();

			SubsumptionHierarchy hierarchy = reasoner.getSubsumptionHierarchy();
			OWLClass owlClass = KAON2Manager.factory().owlClass(
			                clazz.toString().substring( clazz.toString().indexOf( "]" ) + 2 ) );
			Node node = hierarchy.getNodeFor( owlClass );
			Set<Node> nodeSet = node.getAncestorNodes();
			for( Node n : nodeSet )
			{
				Set<OWLClass> classSet = n.getOWLClasses();
				for( OWLClass c : classSet )
				{
					OWLEntity entity = owlDataFactory.getOWLClass( new URI( c.getURI() ) );
					entitySet.add( entity );
				}
			}
			resultSet.add( entitySet );
			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}
		catch( InterruptedException e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<Set> superClassesOf( OWLDescription clazz ) throws OWLException, URISyntaxException
	{
		try
		{

			Set<Set> resultSet = new HashSet<Set>();
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();

			SubsumptionHierarchy hierarchy = reasoner.getSubsumptionHierarchy();
			OWLClass owlClass = KAON2Manager.factory().owlClass(
			                clazz.toString().substring( clazz.toString().indexOf( "]" ) + 2 ) );
			Node node = hierarchy.getNodeFor( owlClass );
			Set<Node> nodeSet = node.getParentNodes();
			for( Node n : nodeSet )
			{
				Set<OWLClass> classSet = n.getOWLClasses();
				for( OWLClass c : classSet )
				{
					OWLEntity entity = owlDataFactory.getOWLClass( new URI( c.getURI() ) );
					entitySet.add( entity );
				}
			}
			resultSet.add( entitySet );
			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}
		catch( InterruptedException e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<OWLEntity> equivalentClassesOf( OWLDescription clazz ) throws OWLException, URISyntaxException
	{
		try
		{

			Set<OWLEntity> resultSet = new HashSet<OWLEntity>();

			SubsumptionHierarchy hierarchy = reasoner.getSubsumptionHierarchy();
			OWLClass owlClass = KAON2Manager.factory().owlClass(
			                clazz.toString().substring( clazz.toString().indexOf( "]" ) + 2 ) );
			Node node = hierarchy.getNodeFor( owlClass );
			Set<OWLClass> classSet = node.getOWLClasses();
			for( OWLClass c : classSet )
			{
				OWLEntity entity = owlDataFactory.getOWLClass( new URI( c.getURI() ) );
				resultSet.add( entity );
			}
			if( resultSet.contains( clazz ) )
			{
				resultSet.remove( clazz );
			}
			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}
		catch( InterruptedException e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public boolean isEquivalentClass( OWLDescription clazz1, OWLDescription clazz2 ) throws OWLException,
	                URISyntaxException
	{

		return equivalentClassesOf( clazz1 ).contains( clazz2 );
	}

	public boolean isSubClassOf( OWLDescription clazz1, OWLDescription clazz2 ) throws OWLException, URISyntaxException
	{
		try
		{

			OWLClass owlClass1 = KAON2Manager.factory().owlClass(
			                clazz1.toString().substring( clazz1.toString().indexOf( "]" ) + 2 ) );
			OWLClass owlClass2 = KAON2Manager.factory().owlClass(
			                clazz2.toString().substring( clazz2.toString().indexOf( "]" ) + 2 ) );
			return reasoner.subsumedBy( owlClass1, owlClass2 );

		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}
		catch( InterruptedException e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public boolean isInstanceOf( OWLIndividual individual, OWLDescription clazz ) throws OWLException,
	                URISyntaxException
	{
		try
		{

			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			Request<ClassMember> memberOfRequest = ontology.createAxiomRequest( ClassMember.class );
			OWLClass owlClass = KAON2Manager.factory().owlClass(
			                clazz.toString().substring( clazz.toString().indexOf( "]" ) + 2 ) );
			memberOfRequest.setCondition( "description", owlClass );
			Individual owlIndividual = KAON2Manager.factory().individual(
			                individual.toString().substring( individual.toString().indexOf( "]" ) + 2 ) );
			memberOfRequest.setCondition( "individual", owlIndividual );
			Set<ClassMember> memberOfAxioms = memberOfRequest.get();
			for( ClassMember axiom : memberOfAxioms )
			{
				OWLEntity entity = owlDataFactory.getOWLClass( new URI( axiom.getIndividual().toString() ) );
				entitySet.add( entity );
			}
			if( entitySet.size() > 0 )
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<OWLEntity> allInstancesOf( org.semanticweb.owl.model.OWLClass clazz ) throws OWLException,
	                URISyntaxException
	{
		try
		{

			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			Request<ClassMember> memberOfRequest = ontology.createAxiomRequest( ClassMember.class );
			OWLClass owlClass = KAON2Manager.factory().owlClass(
			                clazz.toString().substring( clazz.toString().indexOf( "]" ) + 2 ) );
			memberOfRequest.setCondition( "description", owlClass );
			Set<ClassMember> memberOfAxioms = memberOfRequest.get();
			for( ClassMember axiom : memberOfAxioms )
			{
				OWLEntity entity = owlDataFactory.getOWLClass( new URI( axiom.getIndividual().toString() ) );
				entitySet.add( entity );
			}
			return entitySet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<Set> typesOf( OWLIndividual individual ) throws OWLException, URISyntaxException
	{
		try
		{

			Set<Set> resultSet = new HashSet<Set>();
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			Request<ClassMember> memberOfRequest = ontology.createAxiomRequest( ClassMember.class );
			Individual owlIndividual = KAON2Manager.factory().individual(
			                individual.toString().substring( individual.toString().indexOf( "]" ) + 2 ) );
			memberOfRequest.setCondition( "individual", owlIndividual );
			Set<ClassMember> memberOfAxioms = memberOfRequest.get();
			for( ClassMember axiom : memberOfAxioms )
			{
				OWLEntity entity = owlDataFactory.getOWLClass( new URI( axiom.getDescription().toString() ) );
				entitySet.add( entity );
			}
			// check for direct concepts that are equivalent to indirect
			// concepts
			Set<Set> allConcepts = allTypesOf( individual );
			for( Set<OWLEntity> allEntities : allConcepts )
			{
				Set<OWLEntity> toBeRemoved = new HashSet<OWLEntity>();
				allEntities.removeAll( entitySet );
				for( OWLEntity entity : allEntities )
				{
					for( OWLEntity ent : entitySet )
					{
						if( isEquivalentClass( (OWLDescription) entity, (OWLDescription) ent ) )
						{
							toBeRemoved.add( ent );
						}
					}
				}
				entitySet.removeAll( toBeRemoved );
			}

			resultSet.add( entitySet );
			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<Set> allTypesOf( OWLIndividual individual ) throws OWLException, URISyntaxException
	{
		try
		{

			Set<Set> resultSet = new HashSet<Set>();
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			Request<ClassMember> memberOfRequest = ontology.createAxiomRequest( ClassMember.class );
			Individual owlIndividual = KAON2Manager.factory().individual(
			                individual.toString().substring( individual.toString().indexOf( "]" ) + 2 ) );
			memberOfRequest.setCondition( "individual", owlIndividual );
			Set<ClassMember> memberOfAxioms = memberOfRequest.get();
			for( ClassMember axiom : memberOfAxioms )
			{
				OWLEntity entity = owlDataFactory.getOWLClass( new URI( axiom.getDescription().toString() ) );
				entitySet.add( entity );
				addSuperConcepts( entity, entitySet );
			}
			resultSet.add( entitySet );
			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<Set> descendantPropertiesOf( OWLProperty property ) throws OWLException, URISyntaxException
	{
		try
		{

			Set<Set> resultSet = new HashSet<Set>();
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			Set<OWLEntity> equivSet = new HashSet<OWLEntity>();

			Request<SubObjectPropertyOf> subObjectPropertyOfRequest = ontology
			                .createAxiomRequest( SubObjectPropertyOf.class );
			ObjectProperty owlObjectProperty = KAON2Manager.factory().objectProperty(
			                property.toString().substring( property.toString().indexOf( "]" ) + 2 ) );
			Set<SubObjectPropertyOf> subObjectPropertyOfAxioms = subObjectPropertyOfRequest.get();
			for( SubObjectPropertyOf axiom : subObjectPropertyOfAxioms )
			{
				if( axiom.getSuperObjectProperty().equals( owlObjectProperty )
				                && axiom.getSubObjectProperty() instanceof ObjectProperty )
				{
					ObjectProperty subObjectProperty = axiom.getSubObjectProperty();
					OWLEntity entity = owlDataFactory.getOWLObjectProperty( new URI( subObjectProperty.toString() ) );
					entitySet.add( entity );
					addSubProperties( entity, entitySet );
					Request<SubObjectPropertyOf> superObjectPropertyOfRequest = ontology
					                .createAxiomRequest( SubObjectPropertyOf.class );
					Set<SubObjectPropertyOf> superObjectPropertyOfAxioms = superObjectPropertyOfRequest.get();
					for( SubObjectPropertyOf ax : superObjectPropertyOfAxioms )
					{
						if( ax.getSuperObjectProperty().equals( subObjectProperty )
						                && ax.getSubObjectProperty() instanceof ObjectProperty )
						{
							if( ax.getSubObjectProperty().equals( owlObjectProperty ) )
							{
								entity = owlDataFactory.getOWLObjectProperty( new URI( subObjectProperty.toString() ) );
								equivSet.add( entity );
							}
						}
					}
				}
			}
			Request<SubDataPropertyOf> subDataPropertyOfRequest = ontology.createAxiomRequest( SubDataPropertyOf.class );
			DataProperty owlDataProperty = KAON2Manager.factory().dataProperty(
			                property.toString().substring( property.toString().indexOf( "]" ) + 2 ) );
			Set<SubDataPropertyOf> subDataPropertyOfAxioms = subDataPropertyOfRequest.get();
			for( SubDataPropertyOf axiom : subDataPropertyOfAxioms )
			{
				if( axiom.getSuperDataProperty().equals( owlDataProperty )
				                && axiom.getSubDataProperty() instanceof DataProperty )
				{
					DataProperty subDataProperty = axiom.getSubDataProperty();
					OWLEntity entity = owlDataFactory.getOWLDataProperty( new URI( axiom.getSubDataProperty()
					                .toString() ) );
					entitySet.add( entity );
					addSubProperties( entity, entitySet );
					Request<SubDataPropertyOf> superDataPropertyOfRequest = ontology
					                .createAxiomRequest( SubDataPropertyOf.class );
					Set<SubDataPropertyOf> superDataPropertyOfAxioms = superDataPropertyOfRequest.get();
					for( SubDataPropertyOf ax : superDataPropertyOfAxioms )
					{
						if( ax.getSuperDataProperty().equals( subDataProperty )
						                && ax.getSubDataProperty() instanceof DataProperty )
						{
							if( ax.getSubDataProperty().equals( owlDataProperty ) )
							{
								entity = owlDataFactory.getOWLDataProperty( new URI( subDataProperty.toString() ) );
								equivSet.add( entity );
							}
						}
					}
				}
			}
			if( entitySet.contains( property ) )
			{
				entitySet.remove( property );
			}
			if( !equivalentPropertiesCheck )
			{
				entitySet.removeAll( equivSet );
			}
			resultSet.add( entitySet );
			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<Set> subPropertiesOf( OWLProperty property ) throws OWLException, URISyntaxException
	{
		try
		{

			Set<Set> resultSet = new HashSet<Set>();
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			Set<OWLEntity> equivSet = new HashSet<OWLEntity>();

			Request<SubObjectPropertyOf> subObjectPropertyOfRequest = ontology
			                .createAxiomRequest( SubObjectPropertyOf.class );
			ObjectProperty owlObjectProperty = KAON2Manager.factory().objectProperty(
			                property.toString().substring( property.toString().indexOf( "]" ) + 2 ) );
			Set<SubObjectPropertyOf> subObjectPropertyOfAxioms = subObjectPropertyOfRequest.get();
			for( SubObjectPropertyOf axiom : subObjectPropertyOfAxioms )
			{
				if( axiom.getSuperObjectProperty().equals( owlObjectProperty )
				                && axiom.getSubObjectProperty() instanceof ObjectProperty )
				{
					ObjectProperty subObjectProperty = axiom.getSubObjectProperty();
					OWLEntity entity = owlDataFactory.getOWLObjectProperty( new URI( axiom.getSubObjectProperty()
					                .toString() ) );
					entitySet.add( entity );
					Request<SubObjectPropertyOf> superObjectPropertyOfRequest = ontology
					                .createAxiomRequest( SubObjectPropertyOf.class );
					Set<SubObjectPropertyOf> superObjectPropertyOfAxioms = superObjectPropertyOfRequest.get();
					for( SubObjectPropertyOf ax : superObjectPropertyOfAxioms )
					{
						if( ax.getSuperObjectProperty().equals( subObjectProperty )
						                && ax.getSubObjectProperty() instanceof ObjectProperty )
						{
							if( ax.getSubObjectProperty().equals( owlObjectProperty ) )
							{
								entity = owlDataFactory.getOWLObjectProperty( new URI( subObjectProperty.toString() ) );
								equivSet.add( entity );
							}
						}
					}
				}
			}
			Request<SubDataPropertyOf> subDataPropertyOfRequest = ontology.createAxiomRequest( SubDataPropertyOf.class );
			DataProperty owlDataProperty = KAON2Manager.factory().dataProperty(
			                property.toString().substring( property.toString().indexOf( "]" ) + 2 ) );
			Set<SubDataPropertyOf> subDataPropertyOfAxioms = subDataPropertyOfRequest.get();
			for( SubDataPropertyOf axiom : subDataPropertyOfAxioms )
			{
				if( axiom.getSuperDataProperty().equals( owlDataProperty )
				                && axiom.getSubDataProperty() instanceof DataProperty )
				{
					DataProperty subDataProperty = axiom.getSubDataProperty();
					OWLEntity entity = owlDataFactory.getOWLDataProperty( new URI( subDataProperty.toString() ) );
					entitySet.add( entity );
					Request<SubDataPropertyOf> superDataPropertyOfRequest = ontology
					                .createAxiomRequest( SubDataPropertyOf.class );
					Set<SubDataPropertyOf> superDataPropertyOfAxioms = superDataPropertyOfRequest.get();
					for( SubDataPropertyOf ax : superDataPropertyOfAxioms )
					{
						if( ax.getSuperDataProperty().equals( subDataProperty )
						                && ax.getSubDataProperty() instanceof DataProperty )
						{
							if( ax.getSubDataProperty().equals( owlDataProperty ) )
							{
								entity = owlDataFactory.getOWLDataProperty( new URI( subDataProperty.toString() ) );
								equivSet.add( entity );
							}
						}
					}
				}
			}
			entitySet.removeAll( equivSet );
			resultSet.add( entitySet );
			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<Set> ancestorPropertiesOf( OWLProperty property ) throws OWLException, URISyntaxException
	{
		try
		{

			Set<Set> resultSet = new HashSet<Set>();
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			Set<OWLEntity> equivSet = new HashSet<OWLEntity>();

			Request<SubObjectPropertyOf> subObjectPropertyOfRequest = ontology
			                .createAxiomRequest( SubObjectPropertyOf.class );
			ObjectProperty owlObjectProperty = KAON2Manager.factory().objectProperty(
			                property.toString().substring( property.toString().indexOf( "]" ) + 2 ) );
			Set<SubObjectPropertyOf> subObjectPropertyOfAxioms = subObjectPropertyOfRequest.get();
			for( SubObjectPropertyOf axiom : subObjectPropertyOfAxioms )
			{
				if( axiom.getSubObjectProperty().equals( owlObjectProperty )
				                && axiom.getSuperObjectProperty() instanceof ObjectProperty )
				{
					ObjectProperty superObjectProperty = axiom.getSuperObjectProperty();
					OWLEntity entity = owlDataFactory.getOWLObjectProperty( new URI( superObjectProperty.toString() ) );
					entitySet.add( entity );
					addSuperProperties( entity, entitySet );
					Request<SubObjectPropertyOf> superObjectPropertyOfRequest = ontology
					                .createAxiomRequest( SubObjectPropertyOf.class );
					Set<SubObjectPropertyOf> superObjectPropertyOfAxioms = superObjectPropertyOfRequest.get();
					for( SubObjectPropertyOf ax : superObjectPropertyOfAxioms )
					{
						if( ax.getSubObjectProperty().equals( superObjectProperty )
						                && ax.getSuperObjectProperty() instanceof ObjectProperty )
						{
							if( ax.getSuperObjectProperty().equals( owlObjectProperty ) )
							{
								entity = owlDataFactory
								                .getOWLObjectProperty( new URI( superObjectProperty.toString() ) );
								equivSet.add( entity );
							}
						}
					}
				}
			}
			Request<SubDataPropertyOf> subDataPropertyOfRequest = ontology.createAxiomRequest( SubDataPropertyOf.class );
			DataProperty owlDataProperty = KAON2Manager.factory().dataProperty(
			                property.toString().substring( property.toString().indexOf( "]" ) + 2 ) );
			Set<SubDataPropertyOf> subDataPropertyOfAxioms = subDataPropertyOfRequest.get();
			for( SubDataPropertyOf axiom : subDataPropertyOfAxioms )
			{
				if( axiom.getSubDataProperty().equals( owlDataProperty )
				                && axiom.getSuperDataProperty() instanceof DataProperty )
				{
					DataProperty superDataProperty = axiom.getSuperDataProperty();
					OWLEntity entity = owlDataFactory.getOWLDataProperty( new URI( superDataProperty.toString() ) );
					entitySet.add( entity );
					addSuperProperties( entity, entitySet );
					Request<SubDataPropertyOf> superDataPropertyOfRequest = ontology
					                .createAxiomRequest( SubDataPropertyOf.class );
					Set<SubDataPropertyOf> superDataPropertyOfAxioms = superDataPropertyOfRequest.get();
					for( SubDataPropertyOf ax : superDataPropertyOfAxioms )
					{
						if( ax.getSubDataProperty().equals( superDataProperty )
						                && ax.getSuperDataProperty() instanceof DataProperty )
						{
							if( ax.getSuperDataProperty().equals( owlDataProperty ) )
							{
								entity = owlDataFactory.getOWLDataProperty( new URI( superDataProperty.toString() ) );
								equivSet.add( entity );
							}
						}
					}
				}
			}
			if( entitySet.contains( property ) )
			{
				entitySet.remove( property );
			}
			entitySet.removeAll( equivSet );
			resultSet.add( entitySet );
			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<Set<OWLEntity>> superPropertiesOf( OWLProperty property ) throws OWLException, URISyntaxException
	{
		try
		{

			Set<Set<OWLEntity>> resultSet = new HashSet<Set<OWLEntity>>();
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			Set<OWLEntity> equivSet = new HashSet<OWLEntity>();

			Request<SubObjectPropertyOf> subObjectPropertyOfRequest = ontology
			                .createAxiomRequest( SubObjectPropertyOf.class );
			ObjectProperty owlObjectProperty = KAON2Manager.factory().objectProperty(
			                property.toString().substring( property.toString().indexOf( "]" ) + 2 ) );
			Set<SubObjectPropertyOf> subObjectPropertyOfAxioms = subObjectPropertyOfRequest.get();
			for( SubObjectPropertyOf axiom : subObjectPropertyOfAxioms )
			{
				if( axiom.getSubObjectProperty().equals( owlObjectProperty )
				                && axiom.getSuperObjectProperty() instanceof ObjectProperty )
				{
					ObjectProperty superObjectProperty = axiom.getSuperObjectProperty();
					OWLEntity entity = owlDataFactory.getOWLObjectProperty( new URI( superObjectProperty.toString() ) );
					entitySet.add( entity );
					Request<SubObjectPropertyOf> superObjectPropertyOfRequest = ontology
					                .createAxiomRequest( SubObjectPropertyOf.class );
					Set<SubObjectPropertyOf> superObjectPropertyOfAxioms = superObjectPropertyOfRequest.get();
					for( SubObjectPropertyOf ax : superObjectPropertyOfAxioms )
					{
						if( ax.getSubObjectProperty().equals( superObjectProperty )
						                && ax.getSuperObjectProperty() instanceof ObjectProperty )
						{
							if( ax.getSuperObjectProperty().equals( owlObjectProperty ) )
							{
								entity = owlDataFactory
								                .getOWLObjectProperty( new URI( superObjectProperty.toString() ) );
								equivSet.add( entity );
							}
						}
					}
				}
			}
			Request<SubDataPropertyOf> subDataPropertyOfRequest = ontology.createAxiomRequest( SubDataPropertyOf.class );
			DataProperty owlDataProperty = KAON2Manager.factory().dataProperty(
			                property.toString().substring( property.toString().indexOf( "]" ) + 2 ) );
			Set<SubDataPropertyOf> subDataPropertyOfAxioms = subDataPropertyOfRequest.get();
			for( SubDataPropertyOf axiom : subDataPropertyOfAxioms )
			{
				if( axiom.getSubDataProperty().equals( owlDataProperty )
				                && axiom.getSuperDataProperty() instanceof DataProperty )
				{
					DataProperty superDataProperty = axiom.getSuperDataProperty();
					OWLEntity entity = owlDataFactory.getOWLDataProperty( new URI( superDataProperty.toString() ) );
					entitySet.add( entity );
					Request<SubDataPropertyOf> superDataPropertyOfRequest = ontology
					                .createAxiomRequest( SubDataPropertyOf.class );
					Set<SubDataPropertyOf> superDataPropertyOfAxioms = superDataPropertyOfRequest.get();
					for( SubDataPropertyOf ax : superDataPropertyOfAxioms )
					{
						if( ax.getSubDataProperty().equals( superDataProperty )
						                && ax.getSuperDataProperty() instanceof DataProperty )
						{
							if( ax.getSuperDataProperty().equals( owlDataProperty ) )
							{
								entity = owlDataFactory.getOWLDataProperty( new URI( superDataProperty.toString() ) );
								equivSet.add( entity );
							}
						}
					}
				}
			}
			entitySet.removeAll( equivSet );
			resultSet.add( entitySet );
			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<OWLEntity> equivalentPropertiesOf( OWLProperty property ) throws OWLException, URISyntaxException
	{
		equivalentPropertiesCheck = true;
		try
		{

			Set<OWLEntity> resultSet = new HashSet<OWLEntity>();
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			Request<EquivalentObjectProperties> equivObjectPropertiesRequest = ontology
			                .createAxiomRequest( EquivalentObjectProperties.class );
			Set<EquivalentObjectProperties> equivObjectPropertiesAxiom = equivObjectPropertiesRequest.get();
			for( EquivalentObjectProperties axiom : equivObjectPropertiesAxiom )
			{
				Set<ObjectProperty> objectPropertySet = axiom.getObjectProperties();
				Iterator<ObjectProperty> it = objectPropertySet.iterator();
				while( it.hasNext() )
				{
					OWLEntity entity = owlDataFactory.getOWLObjectProperty( new URI( it.next().toString() ) );
					entitySet.add( entity );
				}
				if( entitySet.contains( property ) )
				{
					Iterator<OWLEntity> it2 = entitySet.iterator();
					while( it2.hasNext() )
					{
						OWLEntity next = it2.next();
						if( !next.equals( property ) )
							resultSet.add( next );
					}
				}
				entitySet.clear();
			}
			Request<EquivalentDataProperties> equivDataPropertiesRequest = ontology
			                .createAxiomRequest( EquivalentDataProperties.class );
			Set<EquivalentDataProperties> equivDataPropertiesAxiom = equivDataPropertiesRequest.get();
			for( EquivalentDataProperties axiom : equivDataPropertiesAxiom )
			{
				Set<DataProperty> dataPropertySet = axiom.getDataProperties();
				Iterator<DataProperty> it = dataPropertySet.iterator();
				while( it.hasNext() )
				{
					OWLEntity entity = owlDataFactory.getOWLDataProperty( new URI( it.next().toString() ) );
					entitySet.add( entity );
				}
				if( entitySet.contains( property ) )
				{
					Iterator<OWLEntity> it2 = entitySet.iterator();
					while( it2.hasNext() )
					{
						OWLEntity next = it2.next();
						if( !next.equals( property ) )
							resultSet.add( next );
					}
				}
				entitySet.clear();
			}

			Set<Set> set = descendantPropertiesOf( property );
			for( Set<OWLEntity> set2 : set )
			{
				for( OWLEntity entity : set2 )
				{
					Set<Set> set3 = descendantPropertiesOf( owlDataFactory.getOWLObjectProperty( new URI( entity
					                .toString().substring( entity.toString().indexOf( "]" ) + 2 ) ) ) );
					for( Set<OWLEntity> set4 : set3 )
					{
						if( set4.contains( property ) )
						{
							resultSet.add( entity );
						}
					}
				}
			}

			return resultSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<OWLEntity> inversePropertiesOf( OWLObjectProperty property ) throws OWLException, URISyntaxException
	{
		try
		{

			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			Request<InverseObjectProperties> inverseObjectPropertiesRequest = ontology
			                .createAxiomRequest( InverseObjectProperties.class );
			ObjectProperty owlObjectProperty = KAON2Manager.factory().objectProperty(
			                property.toString().substring( property.toString().indexOf( "]" ) + 2 ) );
			inverseObjectPropertiesRequest.setCondition( "first", owlObjectProperty );
			Set<InverseObjectProperties> inverseObjectPropertiesAxiom = inverseObjectPropertiesRequest.get();
			for( InverseObjectProperties axiom : inverseObjectPropertiesAxiom )
			{
				entitySet.add( owlDataFactory.getOWLObjectProperty( new URI( axiom.getSecond().toString() ) ) );
			}
			Request<InverseObjectProperties> inverseObjectPropertiesRequest2 = ontology
			                .createAxiomRequest( InverseObjectProperties.class );
			inverseObjectPropertiesRequest2.setCondition( "second", owlObjectProperty );
			Set<InverseObjectProperties> inverseObjectPropertiesAxiom2 = inverseObjectPropertiesRequest2.get();
			for( InverseObjectProperties axiom : inverseObjectPropertiesAxiom2 )
			{
				entitySet.add( owlDataFactory.getOWLObjectProperty( new URI( axiom.getFirst().toString() ) ) );
			}
			return entitySet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<OWLEntity> domainsOf( OWLProperty property ) throws OWLException, URISyntaxException
	{
		try
		{

			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			Request<ObjectPropertyDomain> attributeRequest = ontology.createAxiomRequest( ObjectPropertyDomain.class );
			ObjectProperty owlObjectProperty = KAON2Manager.factory().objectProperty(
			                property.toString().substring( property.toString().indexOf( "]" ) + 2 ) );
			attributeRequest.setCondition( "objectProperty", owlObjectProperty );
			Set<ObjectPropertyDomain> objectPropertyDomainAxiom = attributeRequest.get();
			for( ObjectPropertyDomain axiom : objectPropertyDomainAxiom )
			{
				entitySet.add( owlDataFactory.getOWLClass( new URI( axiom.getDomain().toString() ) ) );
			}
			return entitySet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<OWLEntity> rangesOf( OWLObjectProperty property ) throws OWLException, URISyntaxException
	{
		try
		{

			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			Request<ObjectPropertyRange> attributeRequest = ontology.createAxiomRequest( ObjectPropertyRange.class );
			ObjectProperty owlObjectProperty = KAON2Manager.factory().objectProperty(
			                property.toString().substring( property.toString().indexOf( "]" ) + 2 ) );
			attributeRequest.setCondition( "objectProperty", owlObjectProperty );
			Set<ObjectPropertyRange> objectPropertyRangeAxiom = attributeRequest.get();
			for( ObjectPropertyRange axiom : objectPropertyRangeAxiom )
			{
				entitySet.add( owlDataFactory.getOWLClass( new URI( axiom.getRange().toString() ) ) );
			}
			return entitySet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<OWLConcreteDataTypeImpl> rangesOf( OWLDataProperty property ) throws OWLException, URISyntaxException
	{
		try
		{

			Set<OWLConcreteDataTypeImpl> dataTypeSet = new HashSet<OWLConcreteDataTypeImpl>();
			Request<DataPropertyRange> attributeRequest = ontology.createAxiomRequest( DataPropertyRange.class );
			DataProperty owlDataProperty = KAON2Manager.factory().dataProperty(
			                property.toString().substring( property.toString().indexOf( "]" ) + 2 ) );
			attributeRequest.setCondition( "dataProperty", owlDataProperty );
			Set<DataPropertyRange> dataPropertyRangeAxiom = attributeRequest.get();
			for( DataPropertyRange axiom : dataPropertyRangeAxiom )
			{
				OWLDataType dataType = owlDataFactory.getOWLConcreteDataType( new URI( Namespaces.XSD_NS
				                + axiom.getRange().toString()
				                                .substring( axiom.getRange().toString().indexOf( ":" ) + 1 ) ) );
				dataTypeSet.add( (OWLConcreteDataTypeImpl) dataType );
			}
			return dataTypeSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Map<OWLEntity, Set<OWLEntity>> getObjectPropertyValues( OWLIndividual individual ) throws OWLException,
	                URISyntaxException
	{
		try
		{

			Map<OWLEntity, Set<OWLEntity>> resultMap = new HashMap<OWLEntity, Set<OWLEntity>>();
			Individual owlIndividual = KAON2Manager.factory().individual(
			                individual.toString().substring( individual.toString().indexOf( "]" ) + 2 ) );
			Set<Entry<ObjectProperty, Set<Individual>>> entrySet = owlIndividual.getObjectPropertyValues( ontology )
			                .entrySet();
			for( Entry<ObjectProperty, Set<Individual>> entry : entrySet )
			{
				ObjectProperty property = entry.getKey();
				Set<Individual> set = entry.getValue();
				for( Individual ind : set )
				{
					Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
					entitySet.add( owlDataFactory.getOWLIndividual( new URI( ind.toString() ) ) );
					resultMap.put( owlDataFactory.getOWLObjectProperty( new URI( property.toString() ) ), entitySet );
				}
			}
			return resultMap;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getDataPropertyValues( OWLIndividual individual )
	                throws OWLException, URISyntaxException
	{
		try
		{

			Map<OWLEntity, Set<OWLConcreteDataImpl>> resultMap = new HashMap<OWLEntity, Set<OWLConcreteDataImpl>>();
			Individual owlIndividual = KAON2Manager.factory().individual(
			                individual.toString().substring( individual.toString().indexOf( "]" ) + 2 ) );
			Set<Entry<DataProperty, Set<Object>>> entrySet = owlIndividual.getDataPropertyValues( ontology ).entrySet();
			for( Entry<DataProperty, Set<Object>> entry : entrySet )
			{
				DataProperty property = entry.getKey();
				Set<Object> set = entry.getValue();
				for( Object obj : set )
				{
					Set<OWLConcreteDataImpl> dataTypeSet = new HashSet<OWLConcreteDataImpl>();
					OWLDataValue dataValue = null;
					if( ((id) obj).getFunctionSymbol() instanceof String )
					{
						dataValue = owlDataFactory.getOWLConcreteData( new URI( Namespaces.XSD_NS + "string" ), null,
						                ((id) obj).getFunctionSymbol() );
					}
					else if( ((id) obj).getFunctionSymbol() instanceof Integer )
					{
						dataValue = owlDataFactory.getOWLConcreteData( new URI( Namespaces.XSD_NS + "integer" ), null,
						                ((id) obj).getFunctionSymbol() );
					}
					dataTypeSet.add( (OWLConcreteDataImpl) dataValue );
					resultMap.put( owlDataFactory.getOWLDataProperty( new URI( property.toString() ) ), dataTypeSet );
				}
			}
			return resultMap;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}
	}

	public Map<OWLEntity, Set<OWLEntity>> getPropertyValues( OWLObjectProperty property ) throws OWLException,
	                URISyntaxException
	{
		try
		{

			Map<OWLEntity, Set<OWLEntity>> resultMap = new HashMap<OWLEntity, Set<OWLEntity>>();
			ObjectProperty owlObjectProperty = KAON2Manager.factory().objectProperty(
			                property.toString().substring( property.toString().indexOf( "]" ) + 2 ) );
			Set<ObjectPropertyMember> individuals = owlObjectProperty.getObjectPropertyMembers( ontology );
			for( ObjectPropertyMember member : individuals )
			{
				Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
				entitySet.add( owlDataFactory.getOWLIndividual( new URI( member.getTargetIndividual().toString() ) ) );
				resultMap.put( owlDataFactory.getOWLIndividual( new URI( member.getSourceIndividual().toString() ) ),
				                entitySet );
			}
			return resultMap;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getPropertyValues( OWLDataProperty property ) throws OWLException,
	                URISyntaxException
	{
		try
		{

			Map<OWLEntity, Set<OWLConcreteDataImpl>> resultMap = new HashMap<OWLEntity, Set<OWLConcreteDataImpl>>();
			DataProperty owlDataProperty = KAON2Manager.factory().dataProperty(
			                property.toString().substring( property.toString().indexOf( "]" ) + 2 ) );
			Set<DataPropertyMember> individuals = owlDataProperty.getDataPropertyMembers( ontology );
			for( DataPropertyMember member : individuals )
			{
				Set<OWLConcreteDataImpl> dataTypeSet = new HashSet<OWLConcreteDataImpl>();
				if( member.getTargetValue() instanceof Constant )
				{
					if( member.getTargetValue().getValue() instanceof String )
					{
						dataTypeSet.add( (OWLConcreteDataImpl) owlDataFactory.getOWLConcreteData( new URI(
						                Namespaces.XSD_NS + "string" ), null, member.getTargetValue().getValue() ) );
					}
					else if( member.getTargetValue().getValue() instanceof Integer )
					{
						dataTypeSet.add( (OWLConcreteDataImpl) owlDataFactory.getOWLConcreteData( new URI(
						                Namespaces.XSD_NS + "integer" ), null, member.getTargetValue().getValue() ) );
					}
				}
				resultMap.put( owlDataFactory.getOWLIndividual( new URI( member.getSourceIndividual().toString() ) ),
				                dataTypeSet );
			}
			return resultMap;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public boolean hasPropertyValue( OWLIndividual subject, OWLObjectProperty property, OWLIndividual object )
	                throws OWLException, InterruptedException
	{
		try
		{

			Query query = reasoner.createQuery( Namespaces.INSTANCE, "ASK " + "{ <"
			                + subject.toString().substring( subject.toString().indexOf( "]" ) + 2 ) + "> <"
			                + property.toString().substring( property.toString().indexOf( "]" ) + 2 ) + "> <"
			                + object.toString().substring( object.toString().indexOf( "]" ) + 2 ) + "> }" );
			query.open();
			int size = query.getNumberOfTuples();
			query.close();
			return size > 0;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public boolean hasPropertyValue( OWLIndividual subject, OWLDataProperty property, OWLDataValue object )
	                throws OWLException, InterruptedException
	{
		try
		{

			Query query = null;
			if( object.getValue() instanceof String )
			{
				query = reasoner.createQuery( Namespaces.INSTANCE, "ASK " + "{ <"
				                + subject.toString().substring( subject.toString().indexOf( "]" ) + 2 ) + "> <"
				                + property.toString().substring( property.toString().indexOf( "]" ) + 2 ) + "> \""
				                + object.getValue().toString() + "\" }" );
			}
			else if( object.getValue() instanceof Integer || object.getValue() instanceof BigInteger )
			{
				query = reasoner.createQuery( Namespaces.INSTANCE, "ASK " + "{ <"
				                + subject.toString().substring( subject.toString().indexOf( "]" ) + 2 ) + "> <"
				                + property.toString().substring( property.toString().indexOf( "]" ) + 2 ) + "> "
				                + object.getValue().toString() + " }" );
			}
			query.open();
			int size = query.getNumberOfTuples();
			query.close();
			return size > 0;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<OWLEntity> getObjectPropertyValues( OWLIndividual subject, OWLObjectProperty property )
	                throws OWLException, InterruptedException, URISyntaxException
	{
		try
		{

			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			Query query = reasoner.createQuery( Namespaces.INSTANCE, "Select ?x WHERE " + "{ <"
			                + subject.toString().substring( subject.toString().indexOf( "]" ) + 2 ) + "> <"
			                + property.toString().substring( property.toString().indexOf( "]" ) + 2 ) + "> ?x }" );
			query.open();
			while( !query.afterLast() )
			{
				Object[] tupleBuffer = query.tupleBuffer();
				entitySet.add( owlDataFactory.getOWLIndividual( new URI( tupleBuffer[ 0 ].toString() ) ) );
				query.next();
			}
			query.close();
			return entitySet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public Set<OWLDataValue> getDataPropertyValues( OWLIndividual subject, OWLDataProperty property )
	                throws OWLException, InterruptedException, URISyntaxException
	{
		try
		{

			Set<OWLDataValue> dataValueSet = new HashSet<OWLDataValue>();
			Query query = reasoner.createQuery( Namespaces.INSTANCE, "Select ?x WHERE " + "{ <"
			                + subject.toString().substring( subject.toString().indexOf( "]" ) + 2 ) + "> <"
			                + property.toString().substring( property.toString().indexOf( "]" ) + 2 ) + "> ?x }" );
			query.open();
			while( !query.afterLast() )
			{
				Object[] tupleBuffer = query.tupleBuffer();
				if( ((id) tupleBuffer[ 0 ]).getFunctionSymbol() instanceof String )
				{
					dataValueSet.add( owlDataFactory.getOWLConcreteData( new URI( Namespaces.XSD_NS + "string" ), null,
					                ((id) tupleBuffer[ 0 ]).getFunctionSymbol().toString() ) );
				}
				else if( ((id) tupleBuffer[ 0 ]).getFunctionSymbol() instanceof Integer
				                || tupleBuffer[ 0 ] instanceof BigInteger )
				{
					dataValueSet.add( owlDataFactory.getOWLConcreteData( new URI( Namespaces.XSD_NS + "integer" ),
					                null, ((id) tupleBuffer[ 0 ]).getFunctionSymbol().toString() ) );
				}
				query.next();
			}
			query.close();
			return dataValueSet;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public OWLIndividual getObjectPropertyValue( OWLIndividual subject, OWLObjectProperty property )
	                throws OWLException, InterruptedException, URISyntaxException
	{
		try
		{

			Query query = reasoner.createQuery( Namespaces.INSTANCE, "Select ?x WHERE " + "{ <"
			                + subject.toString().substring( subject.toString().indexOf( "]" ) + 2 ) + "> <"
			                + property.toString().substring( property.toString().indexOf( "]" ) + 2 ) + "> ?x }" );
			query.open();
			Object[] tupleBuffer = query.tupleBuffer();
			query.close();
			return owlDataFactory.getOWLIndividual( new URI( tupleBuffer[ 0 ].toString() ) );
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}

	}

	public OWLDataValue getDataPropertyValue( OWLIndividual subject, OWLDataProperty property ) throws OWLException,
	                InterruptedException, URISyntaxException
	{
		try
		{

			Query query = reasoner.createQuery( Namespaces.INSTANCE, "Select ?x WHERE " + "{ <"
			                + subject.toString().substring( subject.toString().indexOf( "]" ) + 2 ) + "> <"
			                + property.toString().substring( property.toString().indexOf( "]" ) + 2 ) + "> ?x }" );
			query.open();
			Object[] tupleBuffer = query.tupleBuffer();
			query.close();
			OWLDataValue dataValue = null;
			if( tupleBuffer[ 0 ] instanceof String )
			{
				dataValue = owlDataFactory.getOWLConcreteData( new URI( Namespaces.XSD_NS + "string" ), null,
				                tupleBuffer[ 0 ] );
			}
			else if( tupleBuffer[ 0 ] instanceof Integer || tupleBuffer[ 0 ] instanceof BigInteger )
			{
				dataValue = owlDataFactory.getOWLConcreteData( new URI( Namespaces.XSD_NS + "integer" ), null,
				                tupleBuffer[ 0 ] );
			}
			return dataValue;
		}
		catch( KAON2Exception e )
		{
			throw new OWLException( "KAON2ERROR", e );
		}
	}

	private void addSuperConcepts( OWLEntity entity, Set<OWLEntity> entitySet ) throws OWLException, URISyntaxException
	{
		Set<Set> set = superClassesOf( (org.semanticweb.owl.model.OWLClass) entity );
		Set<OWLEntity> set2 = set.iterator().next();
		for( OWLEntity ent : set2 )
		{
			if( !entitySet.contains( ent ) )
			{
				entitySet.addAll( set.iterator().next() );
				addSuperConcepts( ent, entitySet );
			}
		}
	}

	private void addSubProperties( OWLEntity entity, Set<OWLEntity> entitySet ) throws OWLException,
	                URISyntaxException, KAON2Exception
	{
		Set<Set> set = subPropertiesOf( (org.semanticweb.owl.model.OWLProperty) entity );
		Set<OWLEntity> set2 = set.iterator().next();
		for( OWLEntity ent : set2 )
		{
			if( !entitySet.contains( ent ) )
			{
				entitySet.addAll( set.iterator().next() );
				addSubProperties( ent, entitySet );
			}
		}
	}

	private void addSuperProperties( OWLEntity entity, Set<OWLEntity> entitySet ) throws OWLException,
	                URISyntaxException
	{
		Set<Set<OWLEntity>> set = superPropertiesOf( (org.semanticweb.owl.model.OWLProperty) entity );
		Set<OWLEntity> set2 = set.iterator().next();
		for( OWLEntity ent : set2 )
		{
			if( !entitySet.contains( ent ) )
			{
				entitySet.addAll( set.iterator().next() );
				addSuperProperties( ent, entitySet );
			}
		}
	}
}
