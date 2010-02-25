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

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;

/**
 * This class is an adapter between reasoner results and a format that is
 * convenient to store in a database.
 * 
 * This involves deriving correct data-types per Entry, then per column and also
 * setting common column names based on the names of variables.
 * 
 * @author Florian Fischer, florian.fischer@deri.at
 * 
 */
public class WSMLResultProcessor {
    /**
     * Converts a ReasonerResult to a Table.
     * 
     * @param result
     *            The ReasonerResult.
     * @return The Table.
     */
    public Table process(ReasonerResult result) {
        Table orderedTable = new Table();
        DatatypeVisitor visitor = new DatatypeVisitor();
        Set<Map<Variable, Term>> r = result.getResult();

        int i = 0;
        // if necessary this is also the place to add further typing information
        for (Map<Variable, Term> row : r) {
            ArrayList<Entry> entryRow = new ArrayList<Entry>();
            for (Variable var : row.keySet()) {
                Term t = row.get(var);
                t.accept(visitor);
                Entry entry = visitor.getMapping();
                entry.setName(var.getName());
                entryRow.add(entry);
            }
            orderedTable.storeRow(i, entryRow);
            i++;
        }

        orderedTable.promoteDataTypes();
        orderedTable.determineColumnNames();
        return orderedTable;
    }

}
