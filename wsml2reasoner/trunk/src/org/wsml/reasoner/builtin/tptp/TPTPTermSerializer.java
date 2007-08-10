/*
 * Copyright (c) 2006, University of Innsbruck, Austria.
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
 */

package org.wsml.reasoner.builtin.tptp;

import java.util.Map;
import java.util.Vector;

import org.deri.wsmo4j.io.parser.wsml.TempVariable;
import org.omwg.logicalexpression.Constants;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.NumberedAnonymousID;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.logicalexpression.terms.Visitor;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.omwg.ontology.WsmlDataType;
import org.wsmo.common.IRI;
import org.wsmo.common.Namespace;
import org.wsmo.common.TopEntity;
import org.wsmo.common.UnnumberedAnonymousID;


/**
 *   
 * @author Holger Lausen
 * @version $Revision: 1.5 $ $Date: 2007-08-10 09:44:49 $
 * @see org.omwg.logicalexpression.Visitor
 */
public class TPTPTermSerializer implements Visitor{
    
	private Map atoms2ConstructedTerms;
    
    private Vector<String> stack;

    private Namespace[] nsHolder;
    
    //hack a bit too much of memory?
    private static TPTPSymbolMap sym = new TPTPSymbolMap();
    
    public TPTPSymbolMap getSymbolMap(){
    	return sym;
    }
    
    public void SetSymbolMap(TPTPSymbolMap map){
    	sym=map;
    }
    

    /**
     * @param nsC TopEntity
     * @see org.deri.wsmo4j.io.serializer.wsml.VisitorSerializeWSML#VisitorSerializeWSML(TopEntity)
     */
    public TPTPTermSerializer() {
        stack = new Vector<String>();
    }
    
    public void setAtoms2ConstructedTerms(Map atoms2ConstructedTerms){
        this.atoms2ConstructedTerms = atoms2ConstructedTerms;
    }

    /**
     * Builds a String representing the ConstructedTerm and adds it to a vector.
     * @param t ConstructedTerm to be serialized
             * @see org.omwg.logicalexpression.terms.Visitor#visitConstructedTerm(org.omwg.logicalexpression.terms.ConstructedTerm)
     */
    public void visitConstructedTerm(ConstructedTerm t) {
        String iri = t.getFunctionSymbol().toString();
        String s = "";
        t.getFunctionSymbol().accept(this);
        s = s + (String)stack.remove(stack.size() - 1);
        int nbParams = t.getArity();
        if (nbParams > 0) {
            s = s + "(";
            for (int i = 0; i < nbParams; i++) {
                t.getParameter(i).accept(this);
                s = s + (String)stack.remove(stack.size() - 1);
                if (i + 1 < nbParams) {
                    s = s + ",";
                }
            }
            s = s + ")";
        }
        stack.add(s);
    }

    /**
     * Builds a String representing the Variable and adds it to a vector.
     * @param t Variable to be serialized
     * @see org.omwg.logicalexpression.terms.Visitor#visitVariable(org.omwg.logicalexpression.terms.Variable)
     */
    public void visitVariable(Variable t) {
        //TODO convert to what is allowed in TPTP
        if (t instanceof TempVariable){
            Term term = (Term)atoms2ConstructedTerms.get(t);
            if (term!=null) term.accept(this);
            else stack.add(t.toString()+"<NonResolvableDependencyToBuiltInAtom>");
            return;
        }
        String var = t.getName();
        stack.add(Character.toUpperCase(var.charAt(0))+var.substring(1));
    }

    public void visitComplexDataValue(ComplexDataValue value) {
        String s = "";
        value.getType().getIRI().accept(this);
        s = (String)stack.remove(stack.size() - 1);
        int nbParams = value.getArity();
        s = s + "(";
        for (byte i = 0; i < nbParams; i++) {
            ((Term)value.getArgumentValue(i)).accept(this);
            s = s + (String)stack.remove(stack.size() - 1);
            if (i + 1 < nbParams) {
                s = s + ",";
            }
        }
        stack.add(s + ")");
    }

    public void visitSimpleDataValue(SimpleDataValue value) {
        if (value.getType().getIRI().toString().equals(WsmlDataType.WSML_STRING)) {
            //escape \ and "
            String content = (String)value.getValue();
            content = content.replaceAll("\\\\", "\\\\\\\\");
            content = content.replaceAll("\"", "\\\\\"");
            stack.add(Constants.STRING_DEL_START + content + Constants.STRING_DEL_END);
        }
        else { // WSML_DECIMAL || WSML_INTEGER
            stack.add("" + value.getValue());
        }
        String iri = value.getType().getIRI().toString();
    }

    public void visitUnnumberedID(UnnumberedAnonymousID t) {
        throw new RuntimeException("should not be here anymore");
    }

    public void visitNumberedID(NumberedAnonymousID t) {
        throw new RuntimeException("should not be here anymore");
    }

    /**
     * Builds a String representing the IRI and adds it to a vector.
     * @param t IRI to be serialized
     * @see org.omwg.logicalexpression.terms.Visitor#visitIRI(org.omwg.logicalexpression.terms.IRI)
     */
    public void visitIRI(IRI t) {
        stack.add(sym.getTPTPTerm(t.toString()));
    }
    
    /**
     * All serialized elements are added to a vector. This method removes the
     * first serialized object from this vector and shifts any subsequent
     * objects to the left (subtracts one from their indices).
     * @return the serialized String object that is the first element in this vector
     */
    public String getSerializedObject() {
        return stack.remove(0).toString();
    }
}