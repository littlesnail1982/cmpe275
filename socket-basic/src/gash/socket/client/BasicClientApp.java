package gash.socket.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * console interface to the socket example
 * 
 * @author gash
 * 
 */
public class BasicClientApp {
	private Properties setup;

	public BasicClientApp(Properties setup) {
		this.setup = setup;
	}

	public void run() {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String name = null;
		do {
			try {
				if (name == null) {
					System.out.print("Enter your name in order to join: ");
					System.out.flush();
					name = br.readLine();
				}
				System.out.println("");
			} catch (Exception e2) {
			}

			if (name != null)
				break;
		} while (true);

		BasicClient bc = new BasicClient(setup);
		bc.startSession();
		bc.setName(name);
		bc.join(name);

		System.out.println("\nWelcome " + name + "\n");
		System.out.println("Commands");
		System.out.println("-----------------------------------------------");
		System.out.println("help - show this menu");
		System.out.println("post - send a message to the group (default)");
		System.out.println("whoami - list my settings");
		System.out.println("exit - end session");
		System.out.println("");

		boolean forever = true;
		while (forever) {
			try {
				String choice = br.readLine();

				System.out.println("");

				if (choice == null) {
					continue;
				} else if (choice.equalsIgnoreCase("whoami")) {
					System.out.println("You are " + bc.getName());
				} else if (choice.equalsIgnoreCase("exit")) {
					System.out.println("EXIT CMD!");
					bc.stopSession();
					forever = false;
				} else if (choice.equalsIgnoreCase("post")) {
					System.out.print("Enter message: ");
					String msg = br.readLine();
					bc.sendMessage(msg);
				} else if (choice.equalsIgnoreCase("help")) {
					System.out.println("");
					System.out.println("Commands");
					System.out.println("-------------------------------");
					System.out.println("help - show this menu");
					System.out.println("post - send a message");
					System.out.println("list - list connections");
					System.out.println("exit - end session");
					System.out.println("");
				} else {
					bc.sendMessage(choice);
				}

				// System.out.println( "" ) ;
			} catch (Exception e) {
				forever = false;
				e.printStackTrace();
			}
		}

		System.out.println("\nGoodbye");
		bc.stopSession();
	}

	public static void main(String[] args) {
		Properties p = new Properties();
		p.setProperty("host", "127.0.0.1");
		p.setProperty("port", "2100");

		BasicClientApp ca = new BasicClientApp(p);
		ca.run();
	}
}
