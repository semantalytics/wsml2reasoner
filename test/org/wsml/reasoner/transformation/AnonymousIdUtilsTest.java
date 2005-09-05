/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
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
package org.wsml.reasoner.transformation;

import junit.framework.TestCase;

import org.deri.wsmo4j.io.parser.wsml.LogExprParserImpl;
import org.omwg.logexpression.Molecule;
import org.omwg.logexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.transformation.MoleculeUtils;
import org.wsmo.common.IRI;
import org.wsmo.common.Namespace;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;

public class AnonymousIdUtilsTest extends TestCase {

    public void testIriGeneration() {
        String iri1 = AnonymousIdUtils.getNewIri();
        System.out.println("First IRI: '" + iri1+"'");
        String iri2 = AnonymousIdUtils.getNewIri();
        System.out.println("Second IRI: '" + iri2+"'");
        assertFalse(iri1.equals(iri2));
    }
    
    public void testIsAnonymousIri() {
        String iri1 = AnonymousIdUtils.getNewIri();
        String iri2 = "urn:blah";
        assertTrue(AnonymousIdUtils.isAnonymousIri(iri1));
        assertFalse(AnonymousIdUtils.isAnonymousIri(iri2));
    }

}
