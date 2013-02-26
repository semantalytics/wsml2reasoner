package org.wsml.reasoner.api.exception;

/**
 * <p>
 * Exception to indicate, that something went wrong in the reasoner while
 * evaluation.
 * </p>
 */
public class InternalReasonerException extends RuntimeException {

	/**
	 * Id used by the java serialization to identify the version of the class.
	 */
	private static final long serialVersionUID = -2424081972392190338L;

	/**
	 * Creates a new exception with a message.
	 * 
	 * @param msg
	 *            the exception message
	 */
	public InternalReasonerException(String msg) {
		super(msg);
	}

	/**
	 * Creates a new exception empty exception.
	 */
	public InternalReasonerException() {
		super();
	}

	/**
	 * Creates a new exception with a cause.
	 * 
	 * @param e
	 *            the cause for this exception
	 */
	public InternalReasonerException(Throwable e) {
		super(e);
	}

	/**
	 * Creates a new exception with a message and a cause.
	 * 
	 * @param msg
	 *            the exception message
	 * @param cause
	 *            the cause for this exception
	 */
	public InternalReasonerException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
