/*
 wsmo4j - a WSMO API and Reference Implementation

 Copyright (c) 2005, University of Innsbruck, Austria

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License along
 with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.deri.wsml.reasoner;

/**
 * Test Class
 *
 * <pre>
 * Created on 12.12.2005
 * Committed by $Author$
 * $Source$,
 * </pre>
 *
 * @author Holger Lausen
 *
 * @version $Revision$ $Date$
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ReasonerWS ws  = new ReasonerWS();
        String ont = "namespace {_\"http:e.orf\" } \n" +
                "ontology x concept a instance b memberOf a";
        String query = "?x memberOf ?y";
        System.out.print(ws.getQueryAnswer(query, ont));

    }

}
