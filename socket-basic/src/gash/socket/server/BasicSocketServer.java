package gash.socket.server;

import gash.socket.common.MessageBuilder;
import gash.socket.common.MessageBuilder.MessageType;
import gash.socket.data.Message;

import java.io.BufferedInputStream;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * server to manage incoming clients
 * 
 * @author gash
 * 
 */
public class BasicSocketServer {
	private Properties setup;
	private ServerSocket socket;
	private long idCounter = 1;
	private boolean forever = true;
	private ArrayList<SessionHandler> connections = new ArrayList<SessionHandler>();

	public BasicSocketServer() {
	}

	/**
	 * construct a new server listening on the specified port
	 */
	public BasicSocketServer(Properties setup) {
		this.setup = setup;
	}

	/**
	 * start monitoring socket for new connections
	 */
	public void start() {
		if (setup == null)
			throw new RuntimeException("Missing configuration properties");

		try {
			int port = Integer.parseInt(setup.getProperty("port"));
			socket = new ServerSocket(port);

			System.out.println("Server Host: "
					+ socket.getInetAddress().getHostAddress());

			// monitors and removes idle sessions
			MonitorSessions monitor = new MonitorSessions(30 * 1000 * 60,
					60 * 1000 * 60);

			while (forever) {
				Socket s = socket.accept();
				if (!forever) {
					break;
				}

				System.out.println("--> server got a client connection");
				System.out.flush();

				SessionHandler sh = new SessionHandler(s, idCounter++);
				connections.add(sh);
				sh.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void stopSessions() {
		for (SessionHandler sh : connections) {
			sh.stopSession();
		}

		connections = null;
		forever = false;
	}

	/**
	 * 
	 * @author gash
	 * 
	 */
	public class SessionHandler extends Thread {
		private Socket connection;
		private long id;
		private String name;
		private long lastContact;
		private boolean forever = true;
		private int timeout = 10 * 1000; // 10 seconds
		private BufferedInputStream in = null;
		private PrintWriter out = null;

		public SessionHandler(Socket connection, long id) {
			this.connection = connection;
			this.id = id;
		}

		/**
		 * stops session on next timeout cycle
		 */
		public void stopSession() {
			forever = false;
			if (connection != null) {
				try {
					removeSession();
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			connection = null;
		}

		public long getSessionId() {
			return id;
		}

		public long getLastContact() {
			return lastContact;
		}

		public void setTimeOut(int v) {
			timeout = v;
		}

		public void setSessionName(String n) {
			name = n;
		}

		public String getSessionName() {
			return name;
		}

		/**
		 * process incoming data
		 */
		public void run() {
			System.out.println("Session " + id + " started");

			try {
				connection.setSoTimeout(timeout);
				in = new BufferedInputStream(connection.getInputStream());
				out = new PrintWriter(connection.getOutputStream(), true);

				if (in == null || out == null)
					throw new RuntimeException("Unable to get in/out streams");

				byte[] raw = new byte[2048];
				MessageBuilder builder = new MessageBuilder();
				while (forever) {
					try {
						int len = in.read(raw);
						if (len == 0)
							continue;
						else if (len == -1)
							break;

						List<Message> list = builder.decode(new String(raw, 0,
								len).getBytes());
						for (Message msg : list) {
							if (msg.getType() == MessageType.leave) {
								return;
							} else if (msg.getType() == MessageType.join) {
								System.out.println("--> join: "
										+ msg.getSource());
							} else if (msg.getType() == MessageType.msg) {
								System.out.println("--> msg: "
										+ msg.getPayload());
							}
						}

						// update last valid read
						lastContact = System.currentTimeMillis();

					} catch (InterruptedIOException ioe) {
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					System.out.println("Session " + (name == null ? "" : name)
							+ " [" + id + "] exiting");
					System.out.flush();
					stopSession();
				} catch (Exception re) {
					re.printStackTrace();
				}
			}
		}

		/**
		 * send message to all connections
		 * 
		 * @param msg
		 *            String
		 * @throws Exception
		 */
		private synchronized void send(String msg) throws Exception {
			for (SessionHandler sh : connections) {
			}
		}

		/**
		 * send message to a connection
		 * 
		 * @param msg
		 *            String
		 * @throws Exception
		 */
		private synchronized void send(String to, String msg) throws Exception {
			for (SessionHandler sh : connections) {
				if (sh.getSessionName().equalsIgnoreCase(to)) {
					break;
				}
			}
		}

		/**
		 * remove connection
		 * 
		 * @param msg
		 *            String
		 * @throws Exception
		 */
		private synchronized void removeSession() throws Exception {
			connections.remove(this);
		}

	} // class SessionHandler

	/**
	 * 
	 * @author gash
	 * 
	 */
	public class MonitorSessions extends Thread {
		private boolean forever = true;

		private long interval;

		private long idleTime;

		/**
		 * create a new monitor
		 * 
		 * @param interval
		 *            long how often to check
		 * @param idleness
		 *            long what is considered idle
		 */
		public MonitorSessions(long interval, long idleness) {
			this.interval = interval;
			this.idleTime = idleness;
		}

		/**
		 * stop monitoring on the next interval
		 */
		public void stopMonitoring() {
			forever = false;
		}

		/**
		 * ran in the thread to monitor for idle threads
		 */
		public void run() {
			while (forever) {
				try {
					long idle = System.currentTimeMillis() - idleTime;
					Thread.sleep(interval);
					if (!forever) {
						break;
					}

					for (SessionHandler sh : connections) {
						if (sh.getLastContact() < idle) {
							System.out
									.println("MonitorSessions stopping session "
											+ sh.getSessionId());

							sh.stopSession();
							connections.remove(sh);
						}
					}
				} catch (Exception e) {
					break;
				}
			}
		}
	} // class MonitorSessions

}
