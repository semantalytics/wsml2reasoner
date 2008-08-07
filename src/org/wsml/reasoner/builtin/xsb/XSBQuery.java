/* 
 ** Author(s): Miguel Calejo
 ** Contact:   interprolog@declarativa.com, http://www.declarativa.com
 ** Copyright (C) Declarativa, Portugal, 2000-2005
 ** Use and distribution, without any warranties, under the terms of the 
 ** GNU Library General Public License, readable in http://www.fsf.org/copyleft/lgpl.html
 */
package org.wsml.reasoner.builtin.xsb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.declarativa.interprolog.PrologEngine;
import com.declarativa.interprolog.TermModel;
import com.xsb.interprolog.NativeEngine;

public class XSBQuery {
    public final static String ROOT = "C:/xsb271" + File.separator + "config" + File.separator;

    public final static String TMPFILE = "tmpProgram.P";

    private Set<Map<String, TermModel>> result;

    private List<String> tmpVars;

    private Map<String, TermModel> tmpMap;

    public static void main(String args[]) throws Exception {
        new XSBQuery().query("a(Bdasd,De)", "a(e1,a(e2)).\n ");
    }

    private List<String> getVars(String query) {
        List<String> l = new ArrayList<String>();
        String pattern = "[^\\w](\\p{Upper}[\\w]*)";
        Pattern myPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher myMatcher = myPattern.matcher(query);
        while (myMatcher.find()) {
            l.add(myMatcher.group(1));
        }
        return l;
    }

    private static PrologEngine engine;

    public static PrologEngine getEngine() {
        if (engine == null) {
            engine = new NativeEngine(ROOT);
        }
        return engine;
    }

    public Set<Map<String, TermModel>> query(String query, String program) {
        List<String> vars = getVars(query);
        engine = getEngine();
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(TMPFILE));
            out.write(program + "\n" + "nonDeterministicGoal(InterestingVarsTerm,G,ListTM) :-" + "findall(InterestingVarsTerm,G,L), buildTermModel(L,ListTM).");
            out.close();

            System.out.println("LOADING PROGRAM:\n" + program);
            engine.consultAbsolute(new File(TMPFILE));

            // construct goal:
            StringBuffer goal = new StringBuffer();
            if (vars.size() > 0) {
                goal.append("nonDeterministicGoal(");
                for (String var : vars) {
                    goal.append(var + "+");
                }
                goal.deleteCharAt(goal.length() - 1);
                goal.append("," + query + ",ListModel)");
            }
            else {
                System.out.println("Executing ground Goal: " + query);
                result = new HashSet<Map<String, TermModel>>();
                if (engine.deterministicGoal(query)) {
                    result.add(new HashMap<String, TermModel>());
                    return result;
                }
                else {
                    return result;
                }
            }

            System.out.println("Executing Goal: " + goal);
            TermModel solutionVars = (TermModel) (engine.deterministicGoal(goal.toString(), "[ListModel]")[0]);

            System.out.println("Solution bindings list:" + solutionVars);

            result = new HashSet<Map<String, TermModel>>();
            TermModel tm = solutionVars;
            // for all subsitutions
            while (tm.children != null) {
                // System.out.println("#" + tm.children[0]);
                tmpVars = new ArrayList<String>(vars);
                tmpMap = new HashMap<String, TermModel>();
                addToResult(tm.children[0]);
                result.add(tmpMap);
                tm = tm.children[1];
            }
        }
        catch (IOException e) {
            throw new RuntimeException("could not write/read to tmpfile", e);
        }
        finally {
            engine.shutdown();
        }
        return null;
    }

    private void addToResult(TermModel tm) {
        // System.out.println("-"+tm+"-"+tm.node);
        if (!tm.isLeaf() && tm.node.equals("+")) {
            addToResult(tm.children[0]);
            addToResult(tm.children[1]);
        }
        else {
            // System.out.println(tmpVars.get(0)+"---"+tm);
            tmpMap.put(tmpVars.remove(0), tm);
        }
    }

}