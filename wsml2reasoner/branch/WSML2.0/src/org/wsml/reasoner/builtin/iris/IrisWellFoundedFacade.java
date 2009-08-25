/*
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.wsml.reasoner.builtin.iris;

import java.util.Map;

import org.deri.iris.Configuration;
import org.wsml.reasoner.impl.WSMO4JManager;

/**
 * <p>
 * The facade for the iris reasoner with well-founded negation and unsafe rule-handling,
 * i.e. for WSML-Rule.
 * </p>
 */
public class IrisWellFoundedFacade extends IrisStratifiedFacade
{
    public IrisWellFoundedFacade(final WSMO4JManager m, final Map<String, Object> config) {
    	super( m, config );
    }

    @Override
    protected void configureIris( Configuration configuration )
    {
        configuration.ruleSafetyProcessor = new org.deri.iris.rules.safety.AugmentingRuleSafetyProcessor();
        configuration.evaluationStrategyFactory = new org.deri.iris.evaluation.wellfounded.WellFoundedEvaluationStrategyFactory();
    }
}
