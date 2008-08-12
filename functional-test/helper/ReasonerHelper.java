package helper;

import java.util.HashMap;
import java.util.Map;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;

public class ReasonerHelper
{
	// These two are here just so we don't forget them.
	public static final boolean ALLOW_IMPORTS = false;
    public static final int EVALUATION_METHOD = -1;

    public static WSMLReasoner getReasoner(WSMLReasonerFactory.BuiltInReasoner reasoner, final Map<String, Object> config) {
		// Create reasoner
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,reasoner);
        
        if( EVALUATION_METHOD >= 0 )
        	params.put(WSMLReasonerFactory.PARAM_EVAL_METHOD,EVALUATION_METHOD);

        params.put(WSMLReasonerFactory.PARAM_ALLOW_IMPORTS,ALLOW_IMPORTS);
        
        // overwrite the default configuration
        if (config != null) {
        	params.putAll(config);
        }
        
        WSMLReasoner wsmlReasoner;
        
        /**
         * This IF should be changed:
         * KAON2 can also handle DL reasoning
         * DL reasoning should be handled by
         */
        if(reasoner.equals(WSMLReasonerFactory.BuiltInReasoner.IRIS) |
        		reasoner.equals(WSMLReasonerFactory.BuiltInReasoner.KAON2)){
        	wsmlReasoner = DefaultWSMLReasonerFactory.getFactory()
            	.createFlightReasoner(params);
        	
        } 
        else if(reasoner.equals(WSMLReasonerFactory.BuiltInReasoner.MINS)){
        	wsmlReasoner = DefaultWSMLReasonerFactory.getFactory()
        		.createRuleReasoner(params);	
        }
        else if(reasoner.equals(WSMLReasonerFactory.BuiltInReasoner.XSB)){
        	wsmlReasoner = DefaultWSMLReasonerFactory.getFactory()
        		.createRuleReasoner(params);	
        }
        else if(reasoner.equals(WSMLReasonerFactory.BuiltInReasoner.PELLET)){
        	wsmlReasoner = (LPReasoner) DefaultWSMLReasonerFactory.getFactory()
        		.createDLReasoner(params);
        }
        else
        	throw new RuntimeException( "Unknown reasoner type: " + reasoner );
        
        return wsmlReasoner;
    }
}
