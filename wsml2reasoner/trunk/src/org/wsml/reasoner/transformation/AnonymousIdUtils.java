package org.wsml.reasoner.transformation;

import java.util.Random;

/**
 * @author Gabor Nagypal (FZI)
 */
public abstract class AnonymousIdUtils {
    /** The random number generator. */
    protected static final Random RND = new Random();

    protected static final String PREFIX = "anonymous:";

    /**
     * Generates a new IRI URI with reasonable degree of uniqueness.
     * 
     * @param uriPrefix
     *            the prefix of the URI
     * @return unique URI
     */
    public static String getNewIri() {
        String newURI = PREFIX + System.currentTimeMillis() + "-"
                + Math.abs(RND.nextInt());
        return newURI;
    }

    /**
     * Checks whether an IRI represents an automatically generated anonymous ID
     * 
     * @param iri
     * @return
     */
    public static boolean isAnonymousIri(String iri) {
        return iri.startsWith(PREFIX);
    }

}
