/**
 * Copyright (C) 2007 Digital Enterprise Research Institute (DERI), 
 * Leopold-Franzens-Universitaet Innsbruck, Technikerstrasse 21a, 
 * A-6020 Innsbruck. Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.wsml.reasoner.ext.sql;

import java.util.Map;
import java.util.Set;

import org.deri.wsmo4j.io.serializer.wsml.VisitorSerializeWSMLTerms;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Serializer;

/**
 * This class mainly contains several debugging and utility related methods. The
 * purpose of the methods is mostly self explanatory.
 * 
 * @author Florian Fischer, florian.fischer@deri.at
 * 
 */
public class QueryUtil {

    public static void printResults(Set<Map<Variable, Term>> result, Ontology o) {
        for (Map<Variable, Term> vBinding : result) {
            for (Variable var : vBinding.keySet()) {
                System.out.print(var + ": " + termToString(vBinding.get(var), o) + "\t ");
            }
            System.out.println();
        }
    }

    public static String toString(Ontology ont) {
        Serializer wsmlSerializer = Factory.createSerializer(null);

        StringBuffer str = new StringBuffer();
        wsmlSerializer.serialize(new TopEntity[] { ont }, str);
        return str.toString();
    }

    public static String termToString(Term t) {
    	return termToString(t, null);
    }
    public static String termToString(Term t, Ontology o) {
        VisitorSerializeWSMLTerms v = new VisitorSerializeWSMLTerms(o);
        t.accept(v);
        return v.getSerializedObject();
    }
}
