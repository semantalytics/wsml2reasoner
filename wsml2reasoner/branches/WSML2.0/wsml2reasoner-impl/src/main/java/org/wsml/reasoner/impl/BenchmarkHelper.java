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
package org.wsml.reasoner.impl;

import org.wsml.reasoner.api.WSMLReasoner;

/**
 * A helper class for the performance benchmarks.
 * 
 * <pre>
 *   Created on June 27th, 2007
 *   Committed by $Author: nathalie $
 *   $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/impl/BenchmarkHelper.java,v $,
 * </pre>
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.1 $ $Date: 2007-06-27 15:57:54 $
 */
public class BenchmarkHelper {

    DatalogBasedWSMLReasoner reasoner = null;

    public BenchmarkHelper(WSMLReasoner reasoner) {
        this.reasoner = (DatalogBasedWSMLReasoner) reasoner;
    }

    public long getNormalizationTime() {
        if (reasoner == null)
            return -1;
        else{
            return reasoner.getNormalizationTime();
        }
    }

    public long getConvertTime() {
        if (reasoner == null){
            return -1;
        }
        else{
            return reasoner.getConvertionTime();
        }
    }

    public long getConsistencyCheckTime() {
        if (reasoner == null){
            return -1;
        }
        else{
            return reasoner.getConsistencyCheckTime();
        }
    }
}