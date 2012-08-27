package org.wsml.reasoner.builtin.streamingiris;

public class HostPortPair {

	private int port;
	private String host;

	public HostPortPair(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof HostPortPair))
			return false;

		HostPortPair pair = (HostPortPair) object;
		if (pair.getHost().equals(host) && pair.getPort() == port)
			return true;
		else
			return false;
	}

}
