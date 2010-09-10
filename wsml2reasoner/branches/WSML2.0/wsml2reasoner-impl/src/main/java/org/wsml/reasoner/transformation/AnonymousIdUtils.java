package org.wsml.reasoner.transformation;


/**
 * @author Gabor Nagypal (FZI)
 */
public abstract class AnonymousIdUtils {
    //protected static final Random RND = new Random();
    
    public static final String ANONYMOUS_PREFIX = "http://www.wsmo.org/reasoner/anonymous_";
    public static final String OFTYPE_PREFIX = "http://www.wsmo.org/reasoner/oftype_";
    public static final String MINCARD_PREFIX = "http://www.wsmo.org/reasoner/mincard_";
    public static final String MAXCARD_PREFIX = "http://www.wsmo.org/reasoner/maxcard_";
    public static final String NAMED_AXIOM_SUFFIX = "~~";

    /**
     * Generates a new IRI URI with reasonable degree of uniqueness.
     * 
     * @param uriPrefix
     *            the prefix of the URI
     * @return unique URI
     */
    private static synchronized String getNewIri(String prefix) {
    		return prefix + Long.toHexString( ++mNextIriTrailer );
//        return prefix + System.currentTimeMillis() + "-" + Math.abs(RND.nextInt());
    }
    
    private static long mNextIriTrailer = 0x1000000000000000L;

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
