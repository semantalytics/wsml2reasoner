/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
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
package org.wsml.reasoner.transformation.le.moleculedecomposition;

import org.wsml.reasoner.transformation.AnonymousIdTranslator;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsml.reasoner.transformation.le.Rules;
import org.wsml.reasoner.transformation.le.common.AtomAnonymousIDRule;
import org.wsml.reasoner.transformation.le.common.MoleculeAnonymousIDRule;
import org.wsmo.factory.Factory;

/**
 * This singleton class represents a set of normalization rules for replacing
 * complex molecules inside a logical expression by conjunctions of simple ones.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class MoleculeDecompositionRules extends Rules <NormalizationRule>{

    public MoleculeDecompositionRules(Factory factory) {
        AnonymousIdTranslator anonymousIDTranslator = new AnonymousIdTranslator(factory.getWsmoFactory());
        
        addRule(new MoleculeDecompositionRule(factory));
        addRule(new MoleculeAnonymousIDRule(factory, anonymousIDTranslator));
        addRule(new AtomAnonymousIDRule(factory, anonymousIDTranslator));
    }
}
