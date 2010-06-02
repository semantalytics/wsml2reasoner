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
