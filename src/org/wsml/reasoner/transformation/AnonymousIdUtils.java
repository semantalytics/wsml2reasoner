package org.wsml.reasoner.transformation;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.NumberedAnonymousID;
import org.omwg.logicalexpression.terms.Term;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.UnnumberedAnonymousID;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

/**
 * @author Gabor Nagypal (FZI)
 */
public abstract class AnonymousIdUtils
{
    protected static AnonymousIdTranslator anonymousIdTranslator;
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
            anonymousIdTranslator = new AnonymousIdUtils.AnonymousIdTranslator();
        }
        return anonymousIdTranslator;
    }
    
    public final static class AnonymousIdTranslator
    {
        private WsmoFactory wsmoFactory;
        private Map<Byte, String> nbIdMap;
        private LogicalExpression scope;
        private LogicalExpressionFactory leFactory;

        public AnonymousIdTranslator()
        {
            wsmoFactory = WSMO4JManager.getWSMOFactory();
            Map createParams = new HashMap();
            createParams.put(Factory.PROVIDER_CLASS, "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");
            leFactory = WSMO4JManager.getLogicalExpressionFactory();
            nbIdMap = new HashMap<Byte, String>();
        }

        public Term translate(Term term)
        {
            return translate(scope, term);
        }

        public Term translate(LogicalExpression scope, Term term)
        {
            if(term instanceof UnnumberedAnonymousID)
            {
                return wsmoFactory.createIRI(getNewIri());
            }
            else if(term instanceof NumberedAnonymousID)
            {
                return translate(scope, (NumberedAnonymousID)term);
            }
            else
                return term;
        }
        
        public void setScope(LogicalExpression scope)
        {
            if(scope != this.scope)
            {
                this.scope = scope;
                nbIdMap.clear();
            }
        }
        
        private IRI translate(LogicalExpression scope, NumberedAnonymousID nbId)
        {
            setScope(scope);
            Byte number = new Byte(nbId.getNumber());
            String idString = nbIdMap.get(number);
            if(idString == null)
            {
                idString = AnonymousIdUtils.getNewIri();
                nbIdMap.put(number, idString);
            }
            return wsmoFactory.createIRI(idString);
        }
    }
}
