/*
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.WsmoFactory;
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
	private static WsmoFactory wsmoFactory = new FactoryImpl().getWsmoFactory();
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
    
}
