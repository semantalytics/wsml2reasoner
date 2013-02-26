package org.wsml.reasoner;

/**
 * Represents an exception that is caused by an external tool during the
 * execution of a query.
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public class ExternalToolException extends Exception {

	/**
	 * Needed for Java 5
	 */
	private static final long serialVersionUID = 5436234289071988044L;

	public ExternalToolException(String message) {
		super(message);
	}

	public ExternalToolException(String message, Throwable t) {
		super(message, t);
	}

	/**
	 * Creates an
	 */
	public ExternalToolException(ConjunctiveQuery q) {
		super("Failed to translate query: "
				+ (q == null ? "(none)" : q.toString()));
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ExternalToolException(ConjunctiveQuery q, Throwable t) {
		super("Failed to translate query: "
				+ (q == null ? "(none)" : q.toString()), t);
	}

}
