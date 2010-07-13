package org.wsml.reasoner.builtin.elly;

import org.omwg.ontology.DataValue;
import org.sti2.elly.terms.ConcreteTerm;
import org.wsml.reasoner.builtin.iris.TermHelper;

/**
 * Helper Class to convert data values to terms, copied from Wsml2Datalog translation.
 * 
 */
public class Wsml2EllyDataValueTranslator {

	static org.sti2.elly.api.terms.IConcreteTerm convertWsmo4jDataValueToEllyTerm(final DataValue v) {
		return ConcreteTerm.fromIRISConcreteTerm(TermHelper.convertWsmo4jDataValueToIrisTerm(v));
	}
	
}
