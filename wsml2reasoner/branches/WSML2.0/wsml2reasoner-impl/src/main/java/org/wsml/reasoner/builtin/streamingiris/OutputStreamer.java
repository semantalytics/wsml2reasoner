package org.wsml.reasoner.builtin.streamingiris;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;

import org.apache.log4j.Logger;

public class OutputStreamer {

	static Logger logger = Logger.getLogger(OutputStreamer.class);

	private String host = null;
	private int port = 0;
	private Socket socket = null;

	private PrintWriter streamWriter;

	/**
	 * Constructor.
	 * 
	 * @param host
	 *            The host where the results are sent.
	 * @param port
	 *            The port where the results are sent.
	 */
	public OutputStreamer(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void stream(String facts) {
		// long factCounter = 0;

		try {
			socket = new Socket(host, port);
			streamWriter = new PrintWriter(socket.getOutputStream());
			BufferedReader bufferedReader = new BufferedReader(
					new StringReader(facts));
			String factLine = null;
			// logger.info("Beginning of streaming.");

			while ((factLine = bufferedReader.readLine()) != null) {
				streamWriter.println(factLine);
				// logger.debug(factLine);
				// factCounter++;
			}

			streamWriter.flush();

			// logger.info("End of streaming.");
			// logger.info("Streamed " + factCounter + " fact(s) to " + host +
			// ":"
			// + port + ".");

			bufferedReader.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean shutdown() {
		boolean result;
		try {
			if (streamWriter != null)
				streamWriter.close();
			if (socket != null)
				socket.close();
			logger.info("Disconnected.");
		} catch (IOException e) {
			logger.error("IO exception occured!", e);
			e.printStackTrace();
		} finally {
			result = true;
		}
		return result;
	}
}
