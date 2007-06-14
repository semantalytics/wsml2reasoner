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
package org.wsml.reasoner.builtin.tptp;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface or class description
 *
 * <pre>
 * Created on 20.03.2007
 * Committed by $Author: hlausen $
 * $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/builtin/tptp/TPTPSymbolMap.java,v $,
 * </pre>
 *
 * @author Rosi, Holger
 *
 * @version $Revision: 1.2 $ $Date: 2007-06-14 16:38:59 $
 */
public class TPTPSymbolMap {
    
    private Map<String, String> iri2tptpTerm = new HashMap<String, String>();
    private Map<String, String> tptp2iriTerm = new HashMap<String, String>();
    
    public String getTPTPTerm(String wsmlTerm){
        String tptpTerm = iri2tptpTerm.get(wsmlTerm);
        if (tptpTerm!=null) return tptpTerm;
        tptpTerm = getLastAlphaNumerics(wsmlTerm);
        int unique=1;
        while (tptp2iriTerm.containsKey(tptpTerm)){
            if (unique!=1){
                tptpTerm=tptpTerm.substring(1,tptpTerm.length()-2);
            }
            tptpTerm+=unique++;
        }
        iri2tptpTerm.put(wsmlTerm, tptpTerm);
        tptp2iriTerm.put(tptpTerm, wsmlTerm);
        return tptpTerm;
    }

    
    private String getLastAlphaNumerics(String iri){
        StringBuffer buf = new StringBuffer();
        for (int i= iri.length()-1; i>=0;i--){
            char current = iri.charAt(i);
            if ((current>='a' && current<='z')||
                    (current>='A' && current<='Z')){
                buf.append(iri.charAt(i));
            }else{
                if (buf.length()!=0) break;
            }
        }
        if (buf.length()==0) return "a";
        buf.reverse();
        String result = Character.toLowerCase(buf.charAt(0)) + buf.substring(1);
        return result;
    }
    

    
    public static void main(String[] a){
        TPTPSymbolMap t = new TPTPSymbolMap();
        System.out.println(t.getTPTPTerm("urn:/#"));
        System.out.println(t.getTPTPTerm("urn://"));
    }
    
    
}
