package org.wsml.reasoner.builtin.streamingiris;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.omwg.ontology.Ontology;
import org.wsml.reasoner.StreamingDatalogReasonerFacade;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.wsml.Parser;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

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

	/**
	 * Constructor.
	 * 
	 * @param knowledgeBase
	 *            The Knowledge Base where to give the new data.
	 * @param socket
	 *            The socket where the new data is read from.
	 */
	public StreamingIrisInputServerThread(
			StreamingDatalogReasonerFacade facade, Socket socket) {
		this.facade = facade;
		this.socket = socket;
	}

	public void run() {

		try {
			Parser wsmlParser = new WsmlParser();
			Ontology ontology;

			InputStream inputStream = socket.getInputStream();

			final TopEntity[] identifiable = wsmlParser
					.parse(new InputStreamReader(inputStream));
			if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
				ontology = (Ontology) identifiable[0];
				facade.addFacts(ontology);
			} else {
				System.out.println("First Element of file no ontology ");
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidModelException e) {
			e.printStackTrace();
		} catch (org.wsmo.wsml.ParserException e) {
			e.printStackTrace();
		}
	}
}
