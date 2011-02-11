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
package org.wsml.reasoner.builtin.elly;

import org.omwg.ontology.DataValue;
import org.sti2.elly.terms.ConcreteTerm;
import org.wsml.reasoner.builtin.iris.TermHelper;

/**
 * Helper Class to convert data values to terms, copied from Wsml2Datalog translation.
 * 
 */
public class Wsml2EllyDataValueTranslator {

	static org.sti2.elly.api.terms.IConcreteTerm convertWsmo4jDataValueToEllyTerm(final DataValue v) {
		return ConcreteTerm.fromIRISConcreteTerm(TermHelper.convertWsmo4jDataValueToIrisTerm(v));
	}
	
}
