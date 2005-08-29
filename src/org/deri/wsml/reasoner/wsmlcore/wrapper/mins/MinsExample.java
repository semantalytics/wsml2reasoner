/*
 * Copyright (c) 2005 University of Innsbruck, Austria
 */
package org.deri.wsml.reasoner.wsmlcore.wrapper.mins;

import java.util.Iterator;
import java.util.Vector;

import com.ontoprise.inference.*;

/**
 * Interface or class description
 *
 * <pre>
 * Created on Jun 28, 2005
 * Committed by $Author: hlausen $
 * $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/deri/wsml/reasoner/wsmlcore/wrapper/mins/MinsExample.java,v $,
 * </pre>
 *
 * @author AuthorFirstName AuthorLastName
 * @author ContributorFirstName ContributorLastName
 * @author ContributorFirstName ContributorLastName
 *
 * @version $Revision: 1.2 $ $Date: 2005-08-29 15:01:29 $
 */
public class MinsExample {

	/**
	 * 
	 */
	public MinsExample() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MinsExample i = new MinsExample();
		i.test();
	}
	
	public void test(){
		Evaluator eval = new Evaluator();
		EvaluatorConfig conf = new EvaluatorConfig();
		//conf.EVALUATIONMETHOD=40;
		eval.init();
		try {
			//facts
			eval.compileString("holger:animal and jos:human and human::animal. " +
					"holger[name->\"Lausen\"].");
            eval.compileString("FORALL X X:dummy <- not(X).");
			//query
			eval.compileString("FORALL X <- X:dummy.");
			eval.compileString("FORALL Y, X <- Y[name->X].");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//do evaluation
		eval.evaluate();
		Vector v = eval.computeSubstitutions();
		//for all queries in KB
		Iterator<Vector> queries = v.iterator();
		while(queries.hasNext()){
			System.out.println("QUERY ");
			//for all substitution of each query
			Iterator<Vector> subst = queries.next().iterator();
			while(subst.hasNext()){
				System.out.println("  SUBSTITUTIONS ");
				//for all vars in one substitution
				Iterator<FLSubst> vars = subst.next().iterator();
				while(vars.hasNext()){
					FLSubst sub = vars.next();
					System.out.println("    VAR:"+sub.Var +"--->"+sub.getSubstitutionString());;					
				}
			}
		}
		
	}

}
