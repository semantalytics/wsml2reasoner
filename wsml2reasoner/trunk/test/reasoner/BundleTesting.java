package reasoner;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLFlightReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

public class BundleTesting
{
    protected static WSMLFlightReasoner wsmlReasoner = null;
    protected static Ontology o = null;
    protected static WsmoFactory wsmoFactory = null;
    protected static LogicalExpressionFactory leFactory = null;
    protected static IRI ontologyID;

    public BundleTesting(String ontologyFile)
    {
        // Set up factories for creating WSML elements
        WSMO4JManager wsmoManager = new WSMO4JManager();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        wsmoFactory = wsmoManager.getWSMOFactory();
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLFlightReasoner();

        // Set up WSML parser
        Map<String, Object> parserProperties = new HashMap<String, Object>();
//        parserProperties.put(Parser.PARSER_WSMO_FACTORY, wsmoFactory);
//        parserProperties.put(Parser.PARSER_LE_FACTORY, leFactory);
//        parserProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS, "com.ontotext.wsmo4j.parser.WSMLParserImpl");
        Parser wsmlparserimpl = org.wsmo.factory.Factory.createParser(parserProperties);

        // Read simple ontology from file
        TopEntity[] identifiable = null;
        try
        {
            final Reader ontoReader = new FileReader(ontologyFile);
            identifiable = wsmlparserimpl.parse(ontoReader);
        } catch(Exception e)
        {
            System.err.println("...error while parsing!");
            e.printStackTrace();
        }

        if(identifiable.length > 0 && identifiable[0] instanceof Ontology)
        {
            o = (Ontology)identifiable[0];
        }
        else
        {
            System.err.println("no ontology!");
            return;
        }

        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLFlightReasoner();

        // Register ontology
        System.out.println("Registering ontology:\n\t\"" + o.getIdentifier().toString() + "\"");
        try {
            wsmlReasoner.registerOntology(o);
        } catch (InconsistencyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ontologyID = (IRI)o.getIdentifier();
    }

    protected Set<Map<Variable, Term>> performQuery(String query)
    {
//        System.out.println("Starting reasoner with query '" + query + "'");
        LogicalExpression qExpression = null;
        try
        {
            qExpression = leFactory.createLogicalExpression(query, o);
        } catch(ParserException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return wsmlReasoner.executeQuery((IRI)o.getIdentifier(), qExpression);
    }
    
    protected boolean performGroundQuery(String query)
    {
        LogicalExpression qExpression = null;
        try
        {
            qExpression = leFactory.createLogicalExpression(query, o);
        } catch(ParserException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return wsmlReasoner.executeGroundQuery((IRI)o.getIdentifier(), qExpression);
    }    

    protected void printResult(Set<Map<Variable, Term>> result)
    {
        int i = 0;
        for(Map<Variable, Term> vBinding : result)
        {
            System.out.println("(" + (++i) + ") -- " + vBinding.toString());
        }
    }

    public void testConsistency()
    {
        String satString = (wsmlReasoner.isSatisfiable(ontologyID)) ? "satisfiable" : "unsatisfiable";
        System.out.println("\nOntology \"" + ontologyID + "\"\n\tis " + satString + ".\n");
    }
    
    public void testBundleType()
    {
        String query = "MyBundle memberOf OnlineBundle";
        System.out.println("\nQuery: \"" + query + "\" <-> is MyBundle an OnlineBundle?\n\t" + ((performGroundQuery(query))?"yes" : "no" ) + "\n");
        query = "MyBundle memberOf ?b and ?b subConceptOf Bundle";
        Set<Map<Variable,Term>> result = performQuery(query);
        System.out.println("Query: \"" + query + "\" <-> as what kinds of bundle can MyBundle be sold?");
        for(Map<Variable, Term> binding : result)
        {
            System.out.println("\t" + binding.get(binding.keySet().iterator().next()));
        }
        System.out.println();
    }
    
    public void testBundleParts()
    {
        String query = "MyBundle[hasPart hasValue ?p]";
        System.out.println("\nQuery: \"" + query + "\" <-> what are the parts of my bundle?");
        Set<Map<Variable,Term>> result = performQuery(query);
        for(Map<Variable, Term> binding : result)
        {
            System.out.println("\t" + binding.get(binding.keySet().iterator().next()));
        }
        System.out.println();
    }
    
    public static void main(String[] args)
    {
        String ontologyFilename = "c:/projects/DIP/WP1/reviewPrep/bundles.wsml";
        String param = "consistency";
        if(args.length > 0)
        {
            param = args[0];
        }
        
        System.out.println("\n\n**************************************");
        System.out.println("** DIP Reasoning on Telecom Bundles **\n\n");
        BundleTesting bundleTesting = new BundleTesting(ontologyFilename);
        if(param.equals("consistency"))
            bundleTesting.testConsistency();
        if(param.equals("querying1"))
            bundleTesting.testBundleType();
        if(param.equals("querying2"))
            bundleTesting.testBundleParts();
    }

}
