package org.wsml.reasoner;

/**
 * An exception that should be thrown by a facade implementation for a specific
 * tool in case that some feature present in the query or in the knowledgebase
 * is not supported by the tool.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class UnsupportedFeatureException extends Exception {

	/**
	 * Default version number for serialization purposes.
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedFeatureException(String s) {
		super(s);
	}
}
