/**
 * WSML Reasoner based on KAON2
 * 
 * Copyright (c) 2005, FZI, Germany
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package org.wsml.reasoner.builtin.kaon2;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.ConjunctiveQuery;
import org.wsml.reasoner.DatalogReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.Rule;
import org.wsml.reasoner.api.exception.InternalReasonerException;
import org.wsmo.factory.FactoryContainer;

/**
 * Integrates the Kaon2 system into the WSML Core/Flight Reasoner framework for
 * query answering. Kaon2 can compute models for disjunctive logic programs and
 * thus can deal with query answering with (non-disjunctive) datalog as well. In
 * addition, Kaon2 supports the basic WSML datatypes: string, integer and
 * decimal (float)
 * 
 * @author Gabor Nagypal, FZI, Germany
 */
public class Kaon2LPFacade implements DatalogReasonerFacade
{
	public Kaon2LPFacade( FactoryContainer factory, final Map<String, Object> config )
	{
        assert factory != null : "The manager must not be null";

        try {
            final Class< ? > facade = Class.forName(WRAPPER_IMPLEMENTATION_CLASS);
            final Constructor< ? > constructor = facade.getConstructor(FactoryContainer.class, Map.class);
            mKaon2Wrapper = (DatalogReasonerFacade) constructor.newInstance(factory, config);
        }
        catch (Exception e) {
            throw new InternalReasonerException("Unabbe to instantiate a kaon2 wrapper object. Are the Kaon2 and wsml2reasoner proprietary extension available jar files on the classpath?", e);
        }
	}

	public void deregister() throws ExternalToolException
    {
	    mKaon2Wrapper.deregister();
    }

	public Set<Map<Variable, Term>> evaluate( ConjunctiveQuery q ) throws ExternalToolException
    {
	    return mKaon2Wrapper.evaluate( q );
    }

	public boolean checkQueryContainment( ConjunctiveQuery query1, ConjunctiveQuery query2 )
    {
	    return mKaon2Wrapper.checkQueryContainment( query1, query2 );
    }

	public Set<Map<Variable, Term>> getQueryContainment( ConjunctiveQuery query1, ConjunctiveQuery query2 )
                    throws ExternalToolException
    {
	    return mKaon2Wrapper.getQueryContainment( query1, query2 );
    }

	public void register( Set<Rule> kb ) throws ExternalToolException
    {
	    mKaon2Wrapper.register( kb );
    }

	private static final String WRAPPER_IMPLEMENTATION_CLASS = "org.wsml.reasoner.builtin.kaon2.Kaon2LPWrapperImplementation";
	private DatalogReasonerFacade mKaon2Wrapper;
	
}
