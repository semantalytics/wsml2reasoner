package org.wsml.reasoner.builtin.elly;

import java.util.TimeZone;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.Instance;
import org.sti2.elly.api.basics.IAtomicConcept;
import org.sti2.elly.api.basics.IAtomicDescription;
import org.sti2.elly.api.terms.IIndividual;
import org.sti2.elly.api.terms.ITerm;
import org.sti2.elly.terms.ConcreteTerm;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.builtin.iris.TermHelper;
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

	/**
	 * Converts a iris term to an wsmo term
	 * 
	 * @param t
	 *            the iris term
	 * @return the converted wsmo term
	 * @throws IllegalArgumentException
	 *             if the term is {@code null}
	 * @throws IllegalArgumentException
	 *             if the term-type couldn't be converted
	 */
	public Term createTerm(ITerm t) {
		Term term = _createTerm(t);
		if (term == null) {
			term = _createDataValue(t);
		}
		
		if (term == null)
			throw new IllegalArgumentException("Can't convert a term of type " + t.getClass().getName());
		else
			return term;
	}

	/**
	 * Converts a iris term to an wsmo term
	 * 
	 * @param t
	 *            the iris term
	 * @return the converted wsmo term
	 * @throws IllegalArgumentException
	 *             if the term is {@code null}
	 * @throws IllegalArgumentException
	 *             if the term-type couldn't be converted
	 */
	public DataValue createDataValue(ITerm t) {
		DataValue dataValue = _createDataValue(t);
		
		if (dataValue == null)
			throw new IllegalArgumentException("Can't convert a term of type " + t.getClass().getName());
		else
			return dataValue;
	}

	/**
	 * Converts an elly term to an wsmo term
	 * 
	 * @param t
	 *            the elly term
	 * @return the converted wsmo term
	 * @throws IllegalArgumentException
	 *             if the term is {@code null}
	 * @throws IllegalArgumentException
	 *             if the term-type couldn't be converted
	 */
	private Term _createTerm(ITerm term) {
		if (term == null) {
			throw new IllegalArgumentException("The term must not be null");
		}

		if (term instanceof IIndividual) {
			return container.getWsmoFactory().createIRI(((IIndividual) term).getValue());
		}
		return null;
	}

	/**
	 * Converts a elly term to an wsmo term
	 * 
	 * @param t
	 *            the iris term
	 * @return the converted wsmo term
	 * @throws IllegalArgumentException
	 *             if the term is {@code null}
	 * @throws IllegalArgumentException
	 *             if the term-type couldn't be converted
	 */
	private DataValue _createDataValue(ITerm ellyTerm) {
		if (ellyTerm == null) {
			throw new IllegalArgumentException("The term must not be null");
		}
		
		if (!(ellyTerm instanceof ConcreteTerm))
			throw new IllegalArgumentException("The term must be instanceof org.sti2.elly.terms.ConcreteTerm");
		
		org.deri.iris.api.terms.ITerm irisTerm = ((ConcreteTerm) ellyTerm).asIRISTerm();
		
		return TermHelper.convertDataValueFromIrisToWsmo4j(irisTerm, container);
	}

	/**
	 * Calculates the timezone hours and timezone minutes for a given timezone
	 * 
	 * @param t
	 *            the timezon for which to calculate the hours and minutes
	 * @return an array with the hours at index 0 and minutes at index 1
	 * @throws NullPointerException
	 *             if the timezone is {@code null}
	 */
	static int[] getTZData(final TimeZone t) {
		if (t == null) {
			throw new NullPointerException("The TimeZone must not be null");
		}
		return new int[] { t.getRawOffset() / 3600000, t.getRawOffset() % 3600000 / 60000 };
	}

}
