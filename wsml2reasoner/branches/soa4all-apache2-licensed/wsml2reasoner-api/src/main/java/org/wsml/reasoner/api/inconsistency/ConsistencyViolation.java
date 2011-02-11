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
package org.wsml.reasoner.api.inconsistency;

import org.deri.wsmo4j.io.serializer.wsml.SerializeWSMLTermsVisitor;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Type;
import org.omwg.ontology.Value;
import org.omwg.ontology.XmlSchemaDataType;
import org.wsmo.common.TopEntity;

public class ConsistencyViolation {

    public ConsistencyViolation() {
    }

    public String toString() {
        return "Consitency Violation!!";
    }

    protected static String toString(Type t, TopEntity te) {
        if (t instanceof Concept) {
            return toString(((Concept) t).getIdentifier(), te);
        }
        else {
            return toString(((XmlSchemaDataType) t).getIdentifier(), te);
        }
    }

    protected static String toString(Value v, TopEntity te) {
        if (v instanceof Instance) {
            return toString(((Instance) v).getIdentifier(), te);
        }
        else {
            return toString((Term) v, te);
        }
    }

    protected static String toString(Term t, TopEntity te) {
        if (te != null) {
        	// TODO only reference of API to wsmo4j-impl ... maybe change serialization?
            SerializeWSMLTermsVisitor v = new SerializeWSMLTermsVisitor(te);
            t.accept(v);
            return v.getSerializedObject();
        }
        return t.toString();
    }

}
