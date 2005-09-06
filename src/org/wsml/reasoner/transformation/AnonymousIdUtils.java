package org.wsml.reasoner.transformation;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.omwg.logexpression.LogicalExpression;
import org.omwg.logexpression.LogicalExpressionFactory;
import org.omwg.logexpression.terms.IRI;
import org.omwg.logexpression.terms.Identifier;
import org.omwg.logexpression.terms.NbAnonymousID;
import org.omwg.logexpression.terms.UnNbAnonymousID;
import org.wsmo.factory.Factory;

/**
 * @author Gabor Nagypal (FZI)
 */
public class AnonymousIdUtils
{
    protected static final AnonymousIdUtils enclInstance = new AnonymousIdUtils();
    protected static AnonymousIdTranslator anonymousIdTranslator;
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
    public static String getNewIri()
    {
        String newURI = PREFIX + System.currentTimeMillis() + "-" + Math.abs(RND.nextInt());
        return newURI;
    }

    /**
     * Checks whether an IRI represents an automatically generated anonymous ID
     * 
     * @param iri
     * @return
     */
    public static boolean isAnonymousIri(String iri)
    {
        return iri.startsWith(PREFIX);
    }
    
    public static AnonymousIdTranslator getAnonymousIdTranslator()
    {
        if(anonymousIdTranslator == null)
        {
            anonymousIdTranslator = enclInstance.new AnonymousIdTranslator();
        }
        return anonymousIdTranslator;
    }
    
    public final class AnonymousIdTranslator
    {
        private Map<Byte, String> nbIdMap;
        private LogicalExpression scope;
        private LogicalExpressionFactory leFactory;

        public AnonymousIdTranslator()
        {
            Map createParams = new HashMap();
            createParams.put(Factory.PROVIDER_CLASS, "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");
            leFactory = (LogicalExpressionFactory)Factory.createLogicalExpressionFactory(createParams);
            nbIdMap = new HashMap<Byte, String>();
        }

        public IRI translate(Identifier id)
        {
            return translate(scope,id);
        }

        public IRI translate(LogicalExpression scope, Identifier id)
        {
            if(id instanceof UnNbAnonymousID)
            {
                return leFactory.createIRI(getNewIri());
            }
            else if(id instanceof NbAnonymousID)
            {
                return translate(scope, (NbAnonymousID)id);
            }
            else
                return (IRI)id;
        }
        
        public void setScope(LogicalExpression scope)
        {
            if(scope != this.scope)
            {
                this.scope = scope;
                nbIdMap.clear();
            }
        }
        
        private IRI translate(LogicalExpression scope, NbAnonymousID nbId)
        {
            setScope(scope);
            Byte number = new Byte(nbId.getNumber());
            String idString = nbIdMap.get(number);
            if(idString == null)
            {
                idString = AnonymousIdUtils.getNewIri();
                nbIdMap.put(number, idString);
            }
            return leFactory.createIRI(idString);
        }
    }
}
