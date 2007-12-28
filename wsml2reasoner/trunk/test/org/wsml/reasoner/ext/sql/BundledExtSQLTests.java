package org.wsml.reasoner.ext.sql;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BundledExtSQLTests extends TestCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for the WSML-Core variant");
		//$JUnit-BEGIN$
		suite.addTestSuite(FunctionalQueryTest.class);
		suite.addTestSuite(WSMLReasonerFacadeTest.class);
		suite.addTestSuite(QueryProcessorTest.class);
		suite.addTestSuite(VisitorDataTypeTest.class);
		//$JUnit-END$
		return suite;
	}

}
