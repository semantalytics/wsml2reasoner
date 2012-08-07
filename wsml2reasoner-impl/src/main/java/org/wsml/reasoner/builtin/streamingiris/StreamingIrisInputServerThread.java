package org.wsml.reasoner.builtin.streamingiris;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.omwg.ontology.Ontology;
import org.wsml.reasoner.StreamingDatalogReasonerFacade;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.FactoryContainer;

import de.ifgi.envision.io.parser.rdf.RDFParser;
import de.ifgi.envision.io.parser.rdf.RDFParser.Syntax;

/**
 * This thread reads the input from a socket, parses it and hands it over to the
 * Knowledge Base.
 * 
 * @author norlan
 * 
 */
public class StreamingIrisInputServerThread extends Thread {

	private StreamingDatalogReasonerFacade facade = null;
	private Socket socket = null;
	private FactoryContainer factory;

	/**
	 * Constructor.
	 * 
	 * @param factory
	 *            The factory container.
	 * @param knowledgeBase
	 *            The Knowledge Base where to give the new data.
	 * @param socket
	 *            The socket where the new data is read from.
	 */
	public StreamingIrisInputServerThread(
			StreamingDatalogReasonerFacade facade, FactoryContainer factory,
			Socket socket) {
		this.facade = facade;
		this.factory = factory;
		this.socket = socket;
	}

	public void run() {

		try {
			InputStream inputStream = socket.getInputStream();

			// TODO Norbert: get syntax of incoming facts (RDF/XML, N3)
			Syntax syntax = Syntax.N3;

			// create corresponding parser
			RDFParser rdfParser = new RDFParser(syntax, factory);
			final TopEntity[] identifiable = rdfParser
					.parse(new InputStreamReader(inputStream));

			Ontology ontology = null;
			if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
				ontology = (Ontology) identifiable[0];
				// add facts to facade
				facade.addFacts(ontology);
			} else {
				System.out.println("First Element of file no ontology ");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (org.wsmo.wsml.ParserException e) {
			e.printStackTrace();
		} catch (InvalidModelException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}
}
