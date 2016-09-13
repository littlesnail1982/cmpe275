package gash.socket.client;

import gash.socket.common.MessageBuilder;

import java.net.Socket;
import java.util.Properties;

/**
 * client chat
 * 
 * @author gash
 * 
 */
public class BasicClient {
	private Properties setup;

	private String _host = "localhost"; // "127.0.0.1" ;

	private Socket socket;
	private String name;

	/**
	 * empty constructor
	 */
	public BasicClient() {
	}

	/**
	 * specify the host and port to connect to
	 */
	public BasicClient(Properties setup) {
		this.setup = setup;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * connect to server
	 */
	public void startSession() {
		if (socket != null) {
			return;
		}

		String host = setup.getProperty("host");
		String port = setup.getProperty("port");
		if (host == null || port == null)
			throw new RuntimeException("Missing port and/or host");

		try {
			socket = new Socket(host, Integer.parseInt(port));
			System.out.println("Connected to " + socket.getInetAddress().getHostAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * close this session
	 */
	public void stopSession() {
		if (socket == null) {
			System.out.println("message not sent");
			return;
		}

		try {
			MessageBuilder builder = new MessageBuilder();
			byte[] msg = builder.encode(MessageBuilder.MessageType.leave, name, null, null).getBytes();
			socket.getOutputStream().write(msg);
			socket.getOutputStream().flush();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		socket = null;
	}

	/**
	 * announce that client has joined the network
	 * 
	 * @param name
	 *            String
	 */
	public void join(String name) {
		if (socket == null) {
			System.out.println("message not sent");
			return;
		}

		try {
			MessageBuilder builder = new MessageBuilder();
			byte[] msg = builder.encode(MessageBuilder.MessageType.join, name, null, null).getBytes();
			socket.getOutputStream().write(msg);
			socket.getOutputStream().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * send a general (public) message to the server
	 * 
	 * @param msg
	 *            String
	 */
	public void sendMessage(String message) {
		if (socket == null) {
			System.out.println("message not sent");
			return;
		} else if (message != null && message.length() > 1024) {
			System.out.println("message exceeds 1024 size limit");
			return;
		}

		try {
			MessageBuilder builder = new MessageBuilder();
			byte[] msg = builder.encode(MessageBuilder.MessageType.msg, name, message, null).getBytes();
			socket.getOutputStream().write(msg);
			socket.getOutputStream().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
