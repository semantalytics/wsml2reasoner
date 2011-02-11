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
import org.deri.wsmo4j.io.serializer.wsml.WSMLSerializerImpl;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.ResourceHelper;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;
import org.wsmo.wsml.Serializer;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

/**
 * Helper to make loading, parsing and serialising ontologies and their components simpler.
 */
public class OntologyHelper
{
	// TODO gigi: probably won't work as expected since the factories changed and are now stateful
	//private static WsmoFactory wsmoFactory = new WsmlFactoryContainer().getWsmoFactory();
	private static Parser wsmlparserimpl = new WsmlParser();

	/**
	 * Load an ontology from a wsml file.
	 * @param ontologyFile The filenanme containing the wsml.
	 * @return The loaded ontology.
	 */
    public static Ontology loadOntology( String ontologyFile  ) throws IOException, ParserException, InvalidModelException
    {
    	return parseThis(getReaderForFile(ontologyFile ));
    }
    
	/**
	 * Parse an ontology from a string containing the wsml.
	 * @param ontologyString The wsml ontology.
	 * @return The parsed ontology.
	 */
    public static Ontology parseOntology( String ontologyString ) throws IOException, ParserException, InvalidModelException
    {
    	return parseThis(new StringReader(ontologyString));
    }
    
    /**
     * Serialise an ontology to a string.
     * @param ontology The ontology instance.
     * @return The string-ised ontology.
     */
    public static String toString( Ontology ontology )
    {
		Serializer ontologySerializer = new WSMLSerializerImpl();

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

    /**
     * Serialise a logical expression to a string.
     * @param ontology The ontology instance to use to create the seriliser.
     * @param logicalExpression The logical expression to serialise.
     * @return The string-ised logical expression.
     */
    public static String toString( Ontology ontology, LogicalExpression logicalExpression )
    {
		LogExprSerializerWSML logExprSerializer = new LogExprSerializerWSML(ontology);
		
		return logExprSerializer.serialize( logicalExpression );
    }
    
    /**
     * Serialise a result set to a string.
     * @param resultSet The result set to serialise.
     * @return The string-ised result set.
     */
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
      
    private static Reader getReaderForResource(String resourceName) {
    	InputStream input = ResourceHelper.loadResourceAsStream(resourceName);
    	Reader ontoReader = new InputStreamReader(input);
    	return ontoReader;
    }
    
    private static Reader getReaderForFile(String location) {
        Reader ontoReader = null;
        try {
            ontoReader = new FileReader(location);
        } catch (FileNotFoundException e) {
            ontoReader = getReaderForResource(location);
            Assert.assertNotNull("Could not load file from class path: " + location,
                    ontoReader);
        }
        Assert.assertNotNull("Could not load file from file system: " + location,
                ontoReader);
        return ontoReader;
    }
    
}
