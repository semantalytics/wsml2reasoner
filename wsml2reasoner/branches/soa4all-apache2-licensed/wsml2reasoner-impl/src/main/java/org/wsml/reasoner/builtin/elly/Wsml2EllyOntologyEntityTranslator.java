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

import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.sti2.elly.api.basics.IAtomicConcept;
import org.sti2.elly.api.basics.IAtomicRole;
import org.sti2.elly.api.factory.IBasicFactory;
import org.sti2.elly.api.factory.ITermFactory;
import org.sti2.elly.api.terms.IIndividual;
import org.sti2.elly.basics.BasicFactory;
import org.sti2.elly.terms.TermFactory;
import org.wsmo.common.IdentifiableEntity;
import org.wsmo.common.Identifier;

public class Wsml2EllyOntologyEntityTranslator {

	private static IBasicFactory BASIC = BasicFactory.getInstance();
	private static ITermFactory TERM = TermFactory.getInstance();

	private static String asString(IdentifiableEntity entity) {
		return asString(entity.getIdentifier());
	}
	
	private static String asString(Identifier identifier) {
		return identifier.toString();
	}

	public static IIndividual createIndividual(Instance instance) {
		return TERM.createIndividual(asString(instance));
	}

	public static IAtomicConcept createConcept(Concept concept) {
		return BASIC.createAtomicConcept(asString(concept));
	}

	public static IAtomicRole createRole(Identifier attributeId) {
		return BASIC.createAtomicRole(asString(attributeId));
	}

}
