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
package org.wsml.reasoner.transformation;

import java.util.HashMap;
import java.util.Map;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.wsmo.common.IRI;
import org.wsmo.common.NumberedAnonymousID;
import org.wsmo.common.UnnumberedAnonymousID;
import org.wsmo.factory.WsmoFactory;

public final class AnonymousIdTranslator {
    private WsmoFactory wsmoFactory;

    private Map<Byte, String> nbIdMap;

    private LogicalExpression scope;

    public AnonymousIdTranslator(WsmoFactory wsmoFactory) {
        this.wsmoFactory = wsmoFactory;
        nbIdMap = new HashMap<Byte, String>();
    }

    public Term translate(Term term) {
        return translate(scope, term);
    }

    public Term translate(LogicalExpression scope, Term term) {
        if (term instanceof UnnumberedAnonymousID) {
            return wsmoFactory.createIRI(AnonymousIdUtils.getNewAnonymousIri());
        }
        else if (term instanceof NumberedAnonymousID) {
            return translate(scope, (NumberedAnonymousID) term);
        }
        else{
            return term;
        }
    }

    public void setScope(LogicalExpression scope) {
        if (scope != this.scope) {
            this.scope = scope;
            nbIdMap.clear();
        }
    }

    private IRI translate(LogicalExpression scope, NumberedAnonymousID nbId) {
        setScope(scope);
        Byte number = new Byte(nbId.getNumber());
        String idString = nbIdMap.get(number);
        if (idString == null) {
            idString = AnonymousIdUtils.getNewAnonymousIri();
            nbIdMap.put(number, idString);
        }
        return wsmoFactory.createIRI(idString);
    }
}
