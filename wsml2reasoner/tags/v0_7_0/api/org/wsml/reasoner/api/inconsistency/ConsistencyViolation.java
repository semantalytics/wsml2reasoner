package org.wsml.reasoner.api.inconsistency;

import org.deri.wsmo4j.io.serializer.wsml.SerializeWSMLTermsVisitor;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Type;
import org.omwg.ontology.Value;
import org.omwg.ontology.WsmlDataType;
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
            return toString(((WsmlDataType) t).getIdentifier(), te);
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
            SerializeWSMLTermsVisitor v = new SerializeWSMLTermsVisitor(te);
            t.accept(v);
            return v.getSerializedObject();
        }
        return t.toString();
    }

}
