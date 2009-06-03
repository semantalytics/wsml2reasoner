/**
 * 
 */
package org.wsml.reasoner.builtin.elly;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.kaon2.api.Ontology;
import org.semanticweb.kaon2.api.reasoner.Reasoner;
import org.semanticweb.owl.impl.model.OWLConcreteDataImpl;
import org.semanticweb.owl.impl.model.OWLConcreteDataTypeImpl;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
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
import org.wsml.reasoner.serializer.owl.OWLSerializerImpl;

public class Elly2DLFacade implements DLReasonerFacade {

	private Reasoner reasoner = null;

	private Logger log = Logger.getLogger(Elly2DLFacade.class);

	private Ontology ontology = null;

	private OWLDataFactory owlDataFactory = null;
	
	private String owlOntologyURI = null;
	
	 /** knowledge-base. */
//    private org.sti2.elly.api.basics. ;

	/**
	 * Creates a facade object that allows to invoke the Elly system for
	 * performing reasoning tasks.
	 * 
	 * @throws OWLException
	 * 
	 */
	public Elly2DLFacade() throws OWLException {
		super();

		Map<Object, String> parameters = new HashMap<Object, String>();
		parameters.put(OWLManager.OWL_CONNECTION,
				"org.semanticweb.owl.impl.model.OWLConnectionImpl");
		owlDataFactory = OWLManager.getOWLConnection(parameters)
				.getDataFactory();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#register(org.semanticweb.owl.model
	 * .OWLOntology)
	 */
	@Override
	public void register(OWLOntology owlOntology) throws ExternalToolException {
		try
		{
			this.owlOntologyURI = owlOntology.getURI().toString();
		}
		catch( OWLException e )
		{
			throw new ExternalToolException( "could not deregister ontology with Elly", e );
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
//		org.sti2.elly.api.factory.IBasicFactory

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wsml.reasoner.DLReasonerFacade#allClasses()
	 */
	@Override
	public Set<OWLEntity> allClasses() throws OWLException, URISyntaxException {
		return null;	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wsml.reasoner.DLReasonerFacade#allDataProperties()
	 */
	@Override
	public Set<OWLEntity> allDataProperties() throws OWLException,
			URISyntaxException {
		// org.sti2.elly.api.factory.
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wsml.reasoner.DLReasonerFacade#allIndividuals()
	 */
	@Override
	public Set<OWLEntity> allIndividuals() throws OWLException,
			URISyntaxException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#allInstancesOf(org.semanticweb.owl
	 * .model.OWLClass)
	 */
	@Override
	public Set<OWLEntity> allInstancesOf(OWLClass clazz) throws OWLException,
			URISyntaxException {
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wsml.reasoner.DLReasonerFacade#allObjectProperties()
	 */
	@Override
	public Set<OWLEntity> allObjectProperties() throws OWLException,
			URISyntaxException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wsml.reasoner.DLReasonerFacade#allProperties()
	 */
	@Override
	public Set<OWLEntity> allProperties() throws OWLException,
			URISyntaxException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#allTypesOf(org.semanticweb.owl.model
	 * .OWLIndividual)
	 */
	@Override
	public Set<Set> allTypesOf(OWLIndividual individual) throws OWLException,
			URISyntaxException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#ancestorClassesOf(org.semanticweb.
	 * owl.model.OWLDescription)
	 */
	@Override
	public Set<Set<OWLEntity>> ancestorClassesOf(OWLDescription clazz)
			throws OWLException, URISyntaxException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#ancestorPropertiesOf(org.semanticweb
	 * .owl.model.OWLProperty)
	 */
	@Override
	public Set<Set> ancestorPropertiesOf(OWLProperty property)
			throws OWLException, URISyntaxException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wsml.reasoner.DLReasonerFacade#deRegister()
	 */
	@Override
	public void deRegister() throws ExternalToolException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#descendantClassesOf(org.semanticweb
	 * .owl.model.OWLDescription)
	 */
	@Override
	public Set<Set> descendantClassesOf(OWLDescription clazz)
			throws OWLException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#descendantPropertiesOf(org.semanticweb
	 * .owl.model.OWLProperty)
	 */
	@Override
	public Set<Set> descendantPropertiesOf(OWLProperty property)
			throws OWLException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#domainsOf(org.semanticweb.owl.model
	 * .OWLProperty)
	 */
	@Override
	public Set<OWLEntity> domainsOf(OWLProperty property) throws OWLException,
			URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#equivalentClassesOf(org.semanticweb
	 * .owl.model.OWLDescription)
	 */
	@Override
	public Set<OWLEntity> equivalentClassesOf(OWLDescription clazz)
			throws OWLException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#equivalentPropertiesOf(org.semanticweb
	 * .owl.model.OWLProperty)
	 */
	@Override
	public Set<OWLEntity> equivalentPropertiesOf(OWLProperty property)
			throws OWLException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#getDataPropertyValue(org.semanticweb
	 * .owl.model.OWLIndividual, org.semanticweb.owl.model.OWLDataProperty)
	 */
	@Override
	public OWLDataValue getDataPropertyValue(OWLIndividual subject,
			OWLDataProperty property) throws OWLException,
			InterruptedException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#getDataPropertyValues(org.semanticweb
	 * .owl.model.OWLIndividual)
	 */
	@Override
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getDataPropertyValues(
			OWLIndividual individual) throws OWLException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#getDataPropertyValues(org.semanticweb
	 * .owl.model.OWLIndividual, org.semanticweb.owl.model.OWLDataProperty)
	 */
	@Override
	public Set<OWLDataValue> getDataPropertyValues(OWLIndividual subject,
			OWLDataProperty property) throws OWLException,
			InterruptedException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#getObjectPropertyValue(org.semanticweb
	 * .owl.model.OWLIndividual, org.semanticweb.owl.model.OWLObjectProperty)
	 */
	@Override
	public OWLIndividual getObjectPropertyValue(OWLIndividual subject,
			OWLObjectProperty property) throws OWLException,
			InterruptedException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#getObjectPropertyValues(org.semanticweb
	 * .owl.model.OWLIndividual)
	 */
	@Override
	public Map<OWLEntity, Set<OWLEntity>> getObjectPropertyValues(
			OWLIndividual individual) throws OWLException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#getObjectPropertyValues(org.semanticweb
	 * .owl.model.OWLIndividual, org.semanticweb.owl.model.OWLObjectProperty)
	 */
	@Override
	public Set<OWLEntity> getObjectPropertyValues(OWLIndividual subject,
			OWLObjectProperty property) throws OWLException,
			InterruptedException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#getPropertyValues(org.semanticweb.
	 * owl.model.OWLObjectProperty)
	 */
	@Override
	public Map<OWLEntity, Set<OWLEntity>> getPropertyValues(
			OWLObjectProperty property) throws OWLException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#getPropertyValues(org.semanticweb.
	 * owl.model.OWLDataProperty)
	 */
	@Override
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getPropertyValues(
			OWLDataProperty property) throws OWLException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#hasPropertyValue(org.semanticweb.owl
	 * .model.OWLIndividual, org.semanticweb.owl.model.OWLObjectProperty,
	 * org.semanticweb.owl.model.OWLIndividual)
	 */
	@Override
	public boolean hasPropertyValue(OWLIndividual subject,
			OWLObjectProperty property, OWLIndividual object)
			throws OWLException, InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#hasPropertyValue(org.semanticweb.owl
	 * .model.OWLIndividual, org.semanticweb.owl.model.OWLDataProperty,
	 * org.semanticweb.owl.model.OWLDataValue)
	 */
	@Override
	public boolean hasPropertyValue(OWLIndividual subject,
			OWLDataProperty property, OWLDataValue object) throws OWLException,
			InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#inversePropertiesOf(org.semanticweb
	 * .owl.model.OWLObjectProperty)
	 */
	@Override
	public Set<OWLEntity> inversePropertiesOf(OWLObjectProperty property)
			throws OWLException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wsml.reasoner.DLReasonerFacade#isConsistent()
	 */
	@Override
	public boolean isConsistent() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#isConsistent(org.semanticweb.owl.model
	 * .OWLDescription)
	 */
	@Override
	public boolean isConsistent(OWLDescription description)
			throws OWLException, InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#isEquivalentClass(org.semanticweb.
	 * owl.model.OWLDescription, org.semanticweb.owl.model.OWLDescription)
	 */
	@Override
	public boolean isEquivalentClass(OWLDescription clazz1,
			OWLDescription clazz2) throws OWLException, URISyntaxException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#isInstanceOf(org.semanticweb.owl.model
	 * .OWLIndividual, org.semanticweb.owl.model.OWLDescription)
	 */
	@Override
	public boolean isInstanceOf(OWLIndividual individual, OWLDescription clazz)
			throws OWLException, URISyntaxException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#isSubClassOf(org.semanticweb.owl.model
	 * .OWLDescription, org.semanticweb.owl.model.OWLDescription)
	 */
	@Override
	public boolean isSubClassOf(OWLDescription clazz1, OWLDescription clazz2)
			throws OWLException, URISyntaxException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#rangesOf(org.semanticweb.owl.model
	 * .OWLObjectProperty)
	 */
	@Override
	public Set<OWLEntity> rangesOf(OWLObjectProperty property)
			throws OWLException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#rangesOf(org.semanticweb.owl.model
	 * .OWLDataProperty)
	 */
	@Override
	public Set<OWLConcreteDataTypeImpl> rangesOf(OWLDataProperty property)
			throws OWLException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#subClassesOf(org.semanticweb.owl.model
	 * .OWLDescription)
	 */
	@Override
	public Set<Set> subClassesOf(OWLDescription clazz) throws OWLException,
			URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#subPropertiesOf(org.semanticweb.owl
	 * .model.OWLProperty)
	 */
	@Override
	public Set<Set> subPropertiesOf(OWLProperty property) throws OWLException,
			URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#superClassesOf(org.semanticweb.owl
	 * .model.OWLDescription)
	 */
	@Override
	public Set<Set> superClassesOf(OWLDescription clazz) throws OWLException,
			URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#superPropertiesOf(org.semanticweb.
	 * owl.model.OWLProperty)
	 */
	@Override
	public Set<Set<OWLEntity>> superPropertiesOf(OWLProperty property)
			throws OWLException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.DLReasonerFacade#typesOf(org.semanticweb.owl.model.
	 * OWLIndividual)
	 */
	@Override
	public Set<Set> typesOf(OWLIndividual individual) throws OWLException,
			URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

}
