package org.wsml.reasoner.transformation.le.foldecomposition;

import org.wsml.reasoner.transformation.AnonymousIdTranslator;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsml.reasoner.transformation.le.Rules;
import org.wsml.reasoner.transformation.le.common.AtomAnonymousIDRule;
import org.wsml.reasoner.transformation.le.common.MoleculeAnonymousIDRule;
import org.wsmo.factory.FactoryContainer;

/**
 * This singleton class represents a set of normalization rules for replacing
 * complex molecules inside a logical expression by conjunctions of simple ones.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class FOLMoleculeDecompositionRules extends Rules<NormalizationRule> {

	public FOLMoleculeDecompositionRules(FactoryContainer factory) {
		AnonymousIdTranslator anonymousIDTranslator = new AnonymousIdTranslator(
				factory.getWsmoFactory());

		addRule(new FOLMoleculeDecompositionRule(factory));
		addRule(new MoleculeAnonymousIDRule(factory, anonymousIDTranslator));
		addRule(new AtomAnonymousIDRule(factory, anonymousIDTranslator));
	}
}
