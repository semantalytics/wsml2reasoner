/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Austria.
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
package org.wsml.reasoner.api;

import java.util.List;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsmo.common.IRI;


/**
 * An interface for invoking a WSML-FOL reasoner with a particular reasoning task.
 * 
 * @author Holger Lausen, DERI Innsbruck
 */
public interface WSMLFOLReasoner extends WSMLReasoner{
    
    public enum EntailmentType {entailed, notEntailed, unkown};
        
    public List<EntailmentType> checkEntailment (List<LogicalExpression> conjectures);
    
    public EntailmentType checkEntailment (LogicalExpression conjectures);
    
    /**@deprecated*/
    public List<EntailmentType> checkEntailment (IRI ontologyID, List<LogicalExpression> conjectures);
    
    /**@deprecated*/
    public EntailmentType checkEntailment (IRI ontologyID, LogicalExpression conjectures);
	
	
}
