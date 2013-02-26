package example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

public class Wsml2ReasonerListener extends Thread {

	static Logger logger = Logger.getLogger(Wsml2ReasonerListener.class);
	private ServerSocket server = null;

	/**
	 * Constructor.
	 * 
	 * @param port
	 *            The port to which the streamer sends the data.
	 * @param fileName
	 *            The file name of the datalog program.
	 */
	public Wsml2ReasonerListener(ServerSocket server) {
		this.server = server;
	}

	public void run() {
		try {
			Socket sock = null;
			while (!Thread.interrupted()) {
				logger.info("Waiting for connection...");
				sock = server.accept();
				logger.info("Connected: " + sock);
				BufferedReader streamReader = new BufferedReader(
						new InputStreamReader(sock.getInputStream()));

				String factLine = null;

				while ((factLine = streamReader.readLine()) != null) {
					logger.debug(factLine);
				}

				streamReader.close();
			}

			if (sock != null) {
				sock.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
