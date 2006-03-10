package org.wsml.reasoner.transformation;

import java.util.Random;

/**
 * @author Gabor Nagypal (FZI)
 */
public abstract class AnonymousIdUtils {
    protected static final Random RND = new Random();

    protected static final String ANONYMOUS_PREFIX = "http://www.wsmo.org/reasoner/anonymous_";

    protected static final String OFTYPE_PREFIX = "http://www.wsmo.org/reasoner/oftype_";

    protected static final String MINCARD_PREFIX = "http://www.wsmo.org/reasoner/mincard_";

    protected static final String MAXCARD_PREFIX = "http://www.wsmo.org/reasoner/maxcard_";

    /**
     * Generates a new IRI URI with reasonable degree of uniqueness.
     * 
     * @param uriPrefix
     *            the prefix of the URI
     * @return unique URI
     */
    private static String getNewIri(String prefix) {
        String newURI = prefix + System.currentTimeMillis() + "-"
                + Math.abs(RND.nextInt());
        return newURI;
    }

    public static String getNewAnonymousIri() {
        return getNewIri(ANONYMOUS_PREFIX);
    }

    public static String getNewOfTypeIri() {
        return getNewIri(OFTYPE_PREFIX);
    }

    public static String getNewMinCardIri() {
        return getNewIri(MINCARD_PREFIX);
    }

    public static String getNewMaxCardIri() {
        return getNewIri(MAXCARD_PREFIX);
    }

    public static boolean isOfTypeIri(String iri) {
        return iri.startsWith(OFTYPE_PREFIX);
    }
    
    public static boolean isMinCardIri(String iri) {
        return iri.startsWith(MINCARD_PREFIX);
    }
    
    public static boolean isMaxCardIri(String iri) {
        return iri.startsWith(MAXCARD_PREFIX);
    }
    
    /**
     * Checks whether an IRI represents an automatically generated anonymous ID
     * 
     * @param iri
     * @return
     */
    public static boolean isAnonymousIri(String iri) {
        return iri.startsWith(ANONYMOUS_PREFIX);
    }

}
