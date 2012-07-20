package org.wsml.reasoner.builtin.streamingiris;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.sti2.streamingiris.EvaluationException;
import at.sti2.streamingiris.ProgramNotStratifiedException;
import at.sti2.streamingiris.RuleUnsafeException;
import at.sti2.streamingiris.api.IKnowledgeBase;
import at.sti2.streamingiris.api.basics.IQuery;

/**
 * This thread waits for a connection to a specified port and starts a new
 * thread as soon as the connection is established.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class StreamingIrisOutputServer extends Thread {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private AbstractStreamingIrisFacade facade;
	private ServerSocket server;
	private List<Thread> inputThreads;
	private IQuery query;
	private IKnowledgeBase prog;

	/**
	 * Constructor.
	 * 
	 * @param facade
	 *            The knowledge base that processes the incoming data.
	 * @param port
	 *            The port of the socket to listen on.
	 */
	public StreamingIrisOutputServer(AbstractStreamingIrisFacade facade,
			IQuery query, IKnowledgeBase prog) {
		this.facade = facade;
		this.inputThreads = new ArrayList<Thread>();
		this.query = query;
		this.prog = prog;
	}

	public void run() {
		try {
			server = new ServerSocket(0);
			logger.info("Server: " + server);

			prog.registerQueryListener(query, "localhost",
					server.getLocalPort());

			Thread inputThread;
			while (!Thread.interrupted()) {
				logger.info("Waiting for connection...");
				Socket sock = server.accept();
				inputThread = new Thread(new StreamingIrisOutputServerThread(
						facade, sock, query), "Input thread");
				inputThread.start();
				inputThreads.add(inputThread);
				logger.info("Connected: " + sock);
			}

		} catch (ProgramNotStratifiedException e) {
			e.printStackTrace();
		} catch (RuleUnsafeException e) {
			e.printStackTrace();
		} catch (EvaluationException e) {
			e.printStackTrace();
		} catch (IOException e) {
		}
	}

	public boolean shutdown() {
		try {
			prog.deregisterQueryListener(query, "localhost",
					server.getLocalPort());
			if (server != null)
				server.close();
			for (Thread thread : inputThreads) {
				thread.interrupt();
			}
			logger.info("StreamingIrisOutputServer shut down!");
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
