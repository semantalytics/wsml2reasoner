package org.wsml.reasoner.builtin.streamingiris;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.sti2.streamingiris.api.basics.IPredicate;
import at.sti2.streamingiris.api.basics.IQuery;
import at.sti2.streamingiris.compiler.Parser;
import at.sti2.streamingiris.compiler.ParserException;
import at.sti2.streamingiris.storage.IRelation;

/**
 * This thread waits for a connection to a specified port and starts a new
 * thread as soon as the connection is established.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class StreamingIrisOutputServerThread extends Thread {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private AbstractStreamingIrisFacade facade;
	private Socket inputSocket;
	private IQuery query;

	/**
	 * Constructor.
	 * 
	 * @param streamingIrisPort
	 *            The port of streaming IRIS where to listen.
	 * @param outputHost
	 *            The host to output the result.
	 * @param outputPort
	 *            The port to output the result.
	 */
	public StreamingIrisOutputServerThread(AbstractStreamingIrisFacade facade,
			Socket socket, IQuery query) {
		this.facade = facade;
		this.inputSocket = socket;
		this.query = query;
	}

	public void run() {
		BufferedReader streamReader = null;

		try {
			streamReader = new BufferedReader(new InputStreamReader(
					inputSocket.getInputStream()));
			String factLine = null;

			Parser parser = new Parser();
			while (!Thread.interrupted()
					&& (factLine = streamReader.readLine()) != null) {
				parser.parse(factLine);
				Map<IPredicate, IRelation> newFacts = parser.getFacts();
				if (newFacts != null && newFacts.size() != 0) {
					facade.sendResults(query, newFacts);
				}
			}
		} catch (IOException e) {
			logger.error("IO exception occured!", e);
			e.printStackTrace();
		} catch (ParserException e) {
			logger.error("Parse exception occured!", e);
			e.printStackTrace();
		} finally {
			try {
				if (streamReader != null)
					streamReader.close();
				inputSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
