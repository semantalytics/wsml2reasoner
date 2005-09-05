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
package reasoner;

import java.io.Reader;
import java.io.StringWriter;

import org.omwg.ontology.Ontology;
import org.wsmo.common.TopEntity;

import test.BaseTest;

public class AnonymousIRIsTest extends BaseTest {

    private static final String ONTOLOGY_FILE = "examples/anonIDs.wsml";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AnonymousIRIsTest.class);
    }

    public void testReadingAnonymousIds() throws Exception {
        Ontology o = null;
        // Read simple ontology from file
        final Reader ontoReader = BaseTest.getReaderForFile(ONTOLOGY_FILE);
        final TopEntity[] identifiable = wsmlParser.parse(ontoReader);
        if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
            o = (Ontology) identifiable[0];
        } else {
            return;
        }

        System.out.println("Parsed ontology");

        // Print ontology in WSML

        System.out.println("WSML Ontology:\n");
        StringWriter sw = new StringWriter();
        wsmlSerializer.serialize(new TopEntity[] { o }, sw);
        System.out.println(sw.toString());
        System.out.println("--------------\n\n");
    }

}
