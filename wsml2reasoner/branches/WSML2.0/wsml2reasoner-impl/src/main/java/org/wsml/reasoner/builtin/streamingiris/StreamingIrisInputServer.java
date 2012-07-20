package org.wsml.reasoner.builtin.streamingiris;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.wsml.reasoner.StreamingDatalogReasonerFacade;

/**
 * This thread waits for a connection to a specified port and starts a new
 * thread as soon as the connection is established.
 * 
 * @author Norbert Lanzanasto
 * 
 */
public class StreamingIrisInputServer extends Thread {

	private StreamingDatalogReasonerFacade facade;
	private int port;
	private ServerSocket server;
	private List<Thread> inputThreads;

	/**
	 * Constructor.
	 * 
	 * @param facade
	 *            The knowledge base that processes the incoming data.
	 * @param port
	 *            The port of the socket to listen on.
	 */
	public StreamingIrisInputServer(StreamingDatalogReasonerFacade facade, int port) {
		this.facade = facade;
		this.port = port;
		this.inputThreads = new ArrayList<Thread>();
	}

	public void run() {
		try {
			server = new ServerSocket(port);

			Thread inputThread;
			while (!Thread.interrupted()) {
				Socket sock = server.accept();
				inputThread = new Thread(new StreamingIrisInputServerThread(facade,
						sock), "Input thread");
				inputThread.start();
				inputThreads.add(inputThread);
			}
		} catch (IOException e) {
			// for (Thread thread : inputThreads) {
			// thread.interrupt();
			// }
			// logger.info("KnowledgeBaseServer shut down!");
		}
	}

	public boolean shutdown() {
		try {
			if (server != null)
				server.close();
			for (Thread thread : inputThreads) {
				thread.interrupt();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public int getPort() {
		return server.getLocalPort();
	}
}
