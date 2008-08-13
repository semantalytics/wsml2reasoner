package helper;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;
import org.deri.wsmo4j.io.serializer.wsml.LogExprSerializerWSML;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;
import org.wsmo.wsml.Serializer;

public class OntologyHelper
{
    public static Ontology loadOntology( String ontologyFile  ) throws IOException, ParserException, InvalidModelException
    {
    	return parseThis(getReaderForFile(ontologyFile ));
    }
    
    public static Ontology parseOntology( String ontologyString ) throws IOException, ParserException, InvalidModelException
    {
    	return parseThis(new StringReader(ontologyString));
    }
    
    public static String toString( Ontology ontology )
    {
        Serializer ontologySerializer = org.wsmo.factory.Factory.createSerializer(null);

		StringWriter sw = new StringWriter();
		try
        {
	        ontologySerializer.serialize(new TopEntity[] { ontology }, sw);
        }
        catch( IOException e )
        {
	        // Writing to a StringWriter, so can not get this exception.
        }
		return sw.toString();
    }

    public static String toString( Ontology ontology, LogicalExpression logicalExpression )
    {
		LogExprSerializerWSML logExprSerializer = new LogExprSerializerWSML(ontology);
		
		return logExprSerializer.serialize( logicalExpression );
    }
    
    public static String toString( Set<Map<Variable, Term>> resultSet )
    {
    	StringWriter writer = new StringWriter();
    	PrintWriter output = new PrintWriter( writer );
		output.println(resultSet.size() + " results to the query:");
		int i = 0;
		for (Map<Variable, Term> binding : resultSet) {
			output.println("(" + (++i) + ") - " + binding.toString());
		}
		output.flush();
		return writer.toString();
    }
    
    private static Ontology parseThis(Reader ontoReader) throws IOException, ParserException, InvalidModelException{
      	 final TopEntity[] identifiable = wsmlparserimpl.parse(ontoReader);
      	 for( TopEntity entity : identifiable )
      	 {
      		 if (entity instanceof Ontology)
      			 return (Ontology) entity;
      	 }

      	 throw new RuntimeException( "No ontology found in input." );
      }
      
    private static Reader getReaderForFile(String location) {
        Reader ontoReader = null;
        try {
            ontoReader = new FileReader(location);
        } catch (FileNotFoundException e) {
            // get current class loader and try to load from there...
            InputStream is = LPHelper.class.getClassLoader()
                    .getResourceAsStream(location);
            // System.out.println();
            Assert.assertNotNull("Could not load file from class path: " + location,
                    is);
            ontoReader = new InputStreamReader(is);
        }
        Assert.assertNotNull("Could not load file from file system: " + location,
                ontoReader);
        return ontoReader;
    }
    
    private static Parser wsmlparserimpl = org.wsmo.factory.Factory.createParser(null);
}
