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