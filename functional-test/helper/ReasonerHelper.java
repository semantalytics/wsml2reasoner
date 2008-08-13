package helper;

import java.util.HashMap;
import java.util.Map;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;

public class ReasonerHelper
{
	// This is here just so we don't forget them.
	public static final boolean ALLOW_IMPORTS = true;
	
	public static DLReasoner getDLReasoner( WSMLReasonerFactory.BuiltInReasoner reasoner )
	{
        Map<String, Object> params = new HashMap<String, Object>();

        params.put(DefaultWSMLReasonerFactory.PARAM_BUILT_IN_REASONER, reasoner);
        params.put(WSMLReasonerFactory.PARAM_ALLOW_IMPORTS, ALLOW_IMPORTS );

        return DefaultWSMLReasonerFactory.getFactory().createDLReasoner(params);
	}

    public static LPReasoner getLPReasoner(WSMLReasonerFactory.BuiltInReasoner reasoner ) {
        Map<String, Object> params = new HashMap<String, Object>();
        
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,reasoner);
        params.put(WSMLReasonerFactory.PARAM_ALLOW_IMPORTS, ALLOW_IMPORTS );
        
        return DefaultWSMLReasonerFactory.getFactory().createRuleReasoner( params );
    }
}
