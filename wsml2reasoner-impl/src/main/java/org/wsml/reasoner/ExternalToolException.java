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
        super("Failed to translate query: " + (q==null ? "(none)" : q.toString()));
    }

    /**
     * @param arg0
     * @param arg1
     */
    public ExternalToolException(ConjunctiveQuery q, Throwable t) {
        super("Failed to translate query: " + (q==null ? "(none)" : q.toString()), t);
    }

}
