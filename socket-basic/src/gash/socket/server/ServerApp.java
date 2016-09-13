package gash.socket.server;

import java.util.Properties;

/**
 * server application
 * 
 * @author gash
 * 
 */
class ServerApp {
	public ServerApp() {
	}

	public static void main(String[] args) {
		/** TODO add arg parsing to set timeouts, port, ... */
		Properties p = new Properties();
		p.setProperty("port", "2100");

		BasicSocketServer server = new BasicSocketServer(p);
		server.start();
	}
}
