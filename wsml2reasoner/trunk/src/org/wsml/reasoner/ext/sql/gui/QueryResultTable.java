package org.wsml.reasoner.ext.sql.gui;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.ext.sql.QueryUtil;

public class QueryResultTable extends AbstractTableModel {

    private static final long serialVersionUID = -6135102911102779203L;
    
    boolean empty;
    private void setEmpty()
    {
    	assert columnNames != null;
    	
    	empty = true;
        columnNames.add( "info" );
    }
    
    private void populate( Set<Map<Variable, Term>> content, Ontology ontology )
    {
    	assert content != null;
    	assert ontology != null;

    	empty = false;
        o = ontology;

        int j = 0;
        for (Map<Variable, Term> row : content) {
            ArrayList<Term> r = new ArrayList<Term>();

            int i = 0;
            for (Variable var : row.keySet()) {
                if (j == 0) // first row
                {
                    columnNames.add(i, var.getName());
                }

                Term t = row.get(var);
                r.add(i, t);
                i++;
            }
            entries.add(j, r);
            j++;
        }
    }

    public void setContent(Set<Map<Variable, Term>> content, Ontology ontology) {
        columnNames = new ArrayList<String>();
        entries = new ArrayList<ArrayList<Term>>();

        if (content == null || ontology == null) {
            throw new IllegalArgumentException("Cannot set content of table model with null parameter");
        }
        
        if( content.size() == 0 )
        	setEmpty();
        else
        	populate( content, ontology );

        fireTableStructureChanged();
    }

    public String getColumnName(int col) {
        return columnNames.get(col);
    }

    public int getColumnCount() {
        return columnNames.size();
    }

    public int getRowCount() {
    	if( empty )
    		return 1;
    	else
    		return entries.size();
    }

    public Object getValueAt(int row, int col) {
    	if( empty )
    		return "The query returned no results";
    	else
    	{
	        Term t = entries.get(row).get(col);
	
	        return QueryUtil.termToString(t, o);
    	}
    }

    // public Class<?> getColumnClass(int c) {
    // return getValueAt(0, c).getClass();
    // }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    private ArrayList<ArrayList<Term>> entries = new ArrayList<ArrayList<Term>>();

    private ArrayList<String> columnNames = new ArrayList<String>();

    private Ontology o;
}
