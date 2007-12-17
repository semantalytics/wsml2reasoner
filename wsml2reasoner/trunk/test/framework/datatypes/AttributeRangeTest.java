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
package framework.datatypes;

import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;

import base.BaseReasonerTest;

/**
 * Interface or class description
 * \
 * <pre>
 *   Created on 20.04.2006
 *   Committed by $Author: graham $
 *   $Source: /home/richi/temp/w2r/wsml2reasoner/test/framework/datatypes/AttributeRangeTest.java,v $,
 * </pre>
 * 
 * @author
 * 
 * @version $Revision: 1.6 $ $Date: 2007-08-24 09:59:59 $
 */
public class AttributeRangeTest extends BaseReasonerTest {
    BuiltInReasoner previous;
    
    public void setUp() throws Exception{
    	super.setUp();
    	setupScenario("files/AttributeRangeOntology.wsml");
    	previous = BaseReasonerTest.reasoner;
        
    }

    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
    	resetReasoner(previous);
    }
    /**
     * 
     * @throws Exception
     */
    public void attributeRange() throws Exception {
    	setUp();
    }
    
    public void testAllReasoners() throws Exception{
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS);
    	attributeRange();
    	
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
    	attributeRange();
    	
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.PELLET);
    	attributeRange();
    	
    	if (exists("org.wsml.reasoner.builtin.kaon2.Kaon2Facade")) { 
    		resetReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
    		attributeRange();
    	}
    }
}
