package org.wsml.reasoner.builtin.elly;

import org.deri.iris.api.terms.ITerm;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.sti2.elly.api.basics.IAtomicConcept;
import org.sti2.elly.api.basics.IAtomicDescription;
import org.sti2.elly.api.terms.IIndividual;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.factory.FactoryContainer;

public class Elly2WsmlOntologyEntityTranslator {

	private final FactoryContainer container;

	public Elly2WsmlOntologyEntityTranslator() {
		this(new WsmlFactoryContainer());
	}
	
	public Elly2WsmlOntologyEntityTranslator(FactoryContainer container) {
		this.container = container;
	}
	
	public Concept createConcept(IAtomicConcept concept) {
		Identifier identifier = createIRI(concept);
		return container.getWsmoFactory().createConcept(identifier);
	}

	private String asString(IAtomicDescription description) {
		return description.getPredicate().toString();
	}

	public Instance createInstance(IIndividual individual) {
		Identifier identifier = container.getWsmoFactory().createIRI(asString(individual));
		return container.getWsmoFactory().createInstance(identifier);
	}

	private String asString(ITerm term) {
		return term.toString();
	}

	public IRI createIRI(IAtomicDescription description) {
		return container.getWsmoFactory().createIRI(asString(description));
	}
	
}
