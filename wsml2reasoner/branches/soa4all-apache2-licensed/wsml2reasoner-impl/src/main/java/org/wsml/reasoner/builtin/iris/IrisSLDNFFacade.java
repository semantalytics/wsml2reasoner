/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wsml.reasoner.builtin.iris;

import java.util.Map;

import org.deri.iris.Configuration;
import org.deri.iris.evaluation.topdown.sldnf.SLDNFEvaluationStrategyFactory;
import org.wsmo.factory.FactoryContainer;

/**
 * <p>
 * The facade for the iris reasoner with well-founded negation and unsafe rule-handling,
 * i.e. for WSML-Rule.
 * </p>
 */
public class IrisSLDNFFacade extends IrisStratifiedFacade
{
    public IrisSLDNFFacade(final FactoryContainer factory, final Map<String, Object> config) {
    	super( factory, config );
    }

    @Override
    protected void configureIris( Configuration configuration )
    {
        configuration.evaluationStrategyFactory = new SLDNFEvaluationStrategyFactory();
    }
}