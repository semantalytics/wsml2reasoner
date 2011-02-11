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
package helper;

import java.util.HashMap;
import java.util.Map;

import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;

/**
 * A helper class to make creating reasoner objects very simple.
 */
public class ReasonerHelper
{
	/** The allow imports flag. This is always true. */
	public static final boolean ALLOW_IMPORTS = true;
	
	/**
	 * Create a DL reasoner
	 * @param reasoner The enum identifying the reasoner.
	 * @return The new wsml reasoner object.
	 */
	public static DLReasoner getDLReasoner( WSMLReasonerFactory.BuiltInReasoner reasoner )
	{
        Map<String, Object> params = new HashMap<String, Object>();

        params.put(DefaultWSMLReasonerFactory.PARAM_BUILT_IN_REASONER, reasoner);
        params.put(WSMLReasonerFactory.PARAM_ALLOW_IMPORTS, ALLOW_IMPORTS );

        return DefaultWSMLReasonerFactory.getFactory().createDL2Reasoner(params);
	}

	/**
	 * Create a LP reasoner
	 * @param reasoner The enum identifying the reasoner.
	 * @return The new wsml reasoner object.
	 */
    public static LPReasoner getLPReasoner(WSMLReasonerFactory.BuiltInReasoner reasoner ) {
        Map<String, Object> params = new HashMap<String, Object>();
        
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, reasoner);
        params.put(WSMLReasonerFactory.PARAM_ALLOW_IMPORTS, ALLOW_IMPORTS );
        
        return DefaultWSMLReasonerFactory.getFactory().createRuleReasoner( params );
    }
}
