package org.wsml.reasoner.builtin.elly;

import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.sti2.elly.api.basics.IAtomicConcept;
import org.sti2.elly.api.basics.IAtomicRole;
import org.sti2.elly.api.factory.IBasicFactory;
import org.sti2.elly.api.factory.ITermFactory;
import org.sti2.elly.api.terms.IIndividual;
import org.sti2.elly.basics.BasicFactory;
import org.sti2.elly.terms.TermFactory;
import org.wsmo.common.IdentifiableEntity;
import org.wsmo.common.Identifier;

public class Wsml2EllyOntologyEntityTranslator {

	private static IBasicFactory BASIC = BasicFactory.getInstance();
	private static ITermFactory TERM = TermFactory.getInstance();

	private static String asString(IdentifiableEntity entity) {
		return entity.getIdentifier().toString();
	}
	
	public static IIndividual createIndividual(Instance instance) {
		return TERM.createIndividual(asString(instance));
	}

	public static IAtomicConcept createConcept(Concept concept) {
		return BASIC.createAtomicConcept(asString(concept));
	}

	public static IAtomicRole createRole(Identifier attributeId) {
		return BASIC.createAtomicRole(asString(attributeId));
	}

	private static String asString(Identifier identifier) {
		return identifier.toString();
	}

}
