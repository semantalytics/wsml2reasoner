package org.wsml.reasoner;

/**
 * Represents an exception that occurs when constructing a datalog element, e.g.
 * Literal, Rule, Program
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class DatalogException extends RuntimeException {

	/**
	 * Default id for serialization.
	 */
	private static final long serialVersionUID = 16645645647L;

	/**
	 * @param arg0
	 *            - explanation for the expection
	 */
	public DatalogException(String arg0) {
		super(arg0);
	}

}
