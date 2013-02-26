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
		else {
			return reasoner.getNormalizationTime();
		}
	}

	public long getConvertTime() {
		if (reasoner == null) {
			return -1;
		} else {
			return reasoner.getConvertionTime();
		}
	}

	public long getConsistencyCheckTime() {
		if (reasoner == null) {
			return -1;
		} else {
			return reasoner.getConsistencyCheckTime();
		}
	}
}