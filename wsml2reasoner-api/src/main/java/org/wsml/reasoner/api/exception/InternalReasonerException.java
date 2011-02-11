/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
