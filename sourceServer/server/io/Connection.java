package server.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import server.Encrypter;
import server.Encrypter.AES;
import server.Encrypter.RSA;
import server.Server;
import server.User;

public class Connection extends Thread {

	public static List<Connection> runningCons;

	static {
		runningCons = new ArrayList<>();
	}
	
	// **********
	// * Fields *
	// **********
	// Maybe I change some of those attributes into static -> can't be changed during the run
	private boolean logConState;
	private boolean logIp;
	private boolean encrypted;
	private int maxLoginAttempts;
	private int maxWarnings;
	private int warnings;
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	private Encrypter encrypter;
	private AES aes;
	private User usr;
	
	// ****************
	// * Constructors *
	// ****************
	public Connection(Socket socket) {
		warnings = 0;
		encrypted = false;
		this.socket = socket;
		runningCons.add(this);
		// Loading logConState from Config --> needs to be catched seperatly
		try {
			logConState = Boolean.parseBoolean(ConfigAdapter.getConfigString("logConState"));
		} catch (Exception e) {
			Logger.getDefaultLogger().logException(e);
			Logger.getDefaultLogger().logWarning("logConState couldn't be loaded from Config. Assuming [true] for this case for User " + getIpFormat(socket));
			logConState = true;
		}
		// Loading logIp from Config --> needs to be catched seperatly
		try {
			logIp = Boolean.parseBoolean(ConfigAdapter.getConfigString("logIp"));
		} catch (Exception e) {
			Logger.getDefaultLogger().logException(e);
			Logger.getDefaultLogger().logWarning("logIp couldn't be loaded from Config. Assuming [true] in this case for User " + getIpFormat(socket));
			logIp = true;
		}
		
		// Loading logIp from Config --> needs to be catched seperatly
		try {
			maxLoginAttempts = Integer.parseInt(ConfigAdapter.getConfigString("maxLoginAttempts"));
		} catch (Exception e) {
			Logger.getDefaultLogger().logException(e);
			Logger.getDefaultLogger().logWarning("maxLoginAttempts couldn't be loaded from Config. Assuming [10] in this case for User " + getIpFormat(socket));
			maxLoginAttempts = 10;
		}
		
		// Loading logIp from Config --> needs to be catched seperatly
		try {
			maxWarnings = Integer.parseInt(ConfigAdapter.getConfigString("maxWarnings"));
		} catch (Exception e) {
			Logger.getDefaultLogger().logException(e);
			Logger.getDefaultLogger().logWarning("maxWarnings couldn't be loaded from Config. Assuming [infinity] in this case for User " + getIpFormat(socket));
			maxWarnings = -1;
		}

		if (logConState && logIp) Logger.getDefaultLogger().logInfo("New Connection established -> User " + getIpFormat(socket) + " tries to connect");
		
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			Logger.getDefaultLogger().logError("Could not init Reader and Writer for " + getIpFormat(socket) + " -> Disconnecting");
			Logger.getDefaultLogger().logException(e);
			disconnect(socket, output, input);
		}
	}

	@Override
	public void run() {
		try {
			encrypter = new Encrypter();
			RSA rsa = encrypter.new RSA();
			rsa.setKeySize();
			aes = encrypter.new AES();
			
			if (readLine().equals("RSA")) {
				print("true");
				flush();
				print(rsa.getKeySize() + "");
				flush();
			}
			else {
				print("false");
				flush();
				Logger.getDefaultLogger().logError("Wrong format for Client " + getIpFormat(socket) + " -> Disconnecting");
				disconnect(socket, output, input);
				return;
			}
			
			// Setup Encryption
			try {
				rsa.generateKeyPair();
				rsa.setOutsideKey(readLine());
				print(rsa.getPublicKey().replaceAll("[\r\n]", ""));
				flush();
				aes.setKey(readLine());
				print("SUCCESS");
				flush();
				encrypted = true;
//				Logger.getDefaultLogger().logInfo("Using AES encryption to " + getIpFormat(socket) + " now"); // Use this in own Network Logger
			} catch (Exception e) {
				try { Thread.sleep(100); } catch(Exception ex) {}
				e.printStackTrace();
				print("ERROR");
				flush();
				disconnect(socket, output, input);
				return;
			}
			
			// Just for buffering received messages from a client
			String netBuffer = null;
			if ((netBuffer = readLine()).equals("LOGIN")) { // Login
				// TODO! Benutze User aus der Database --> Siehe DatabaseConnector + benutze SQLight + userIds
				int i = 0;
				boolean correctLogin = false;
				while (!correctLogin) { // Check for correct login
					netBuffer = readLine();
					for (String s : Server.usr) {
						if (s.equals(netBuffer.substring(0, netBuffer.indexOf("\\")))) {
							for (int j = 0; j < Server.pwd.length; j++) {
								if (netBuffer.substring(netBuffer.indexOf("\\")+1).equals(Server.pwd[j])) {
									this.usr = new User(Server.ID[j], s, Server.pwd[j]);
									correctLogin = true;
									j = Server.pwd.length;
								}
							}
						}
						if (correctLogin) break;
					}
					if (correctLogin) continue;
					print("INV_LOGIN"+i);
					flush();
					i++;
					if (i > maxLoginAttempts) {
						print("TOO_MANY_ATTEMPTS");
						flush();
						Logger.getDefaultLogger().logWarning("Too many login attempts for " + getIpFormat(socket));
						disconnect(socket, output, input);
						return;
					}
				}
				print("OK");
				flush();
			} else if (netBuffer.substring(0, 3).equals("REG")) { 			// Register
				createNewUser(netBuffer);
			} else if (netBuffer.substring(0, 3).equals("RES")) { 			// Reset
				resetUser(netBuffer);
			} else {
				print("ERROR");
				flush();
				disconnect(socket, output, input);
			}

			// From now on: listening for Commands
			boolean running = true, handled = false;
			while (running) {
				netBuffer = readLine();
				if (netBuffer.equals("DISC")) {								// Disconnect
					runningCons.remove(this);
					running = false;
					try {
						Thread.sleep(500); 									// The Client has 500 ms to disconnect by himself
					} catch(InterruptedException e) {}						// --> there should be no exception
					handled = true;
				} else if (netBuffer.equals("DELUSR")) {					// Delete User
					deleteUser();
					print("OK");
					flush();
					handled = true;
				} else if (netBuffer.length() > 12) {
					if (netBuffer.substring(0, 5).equals("MSGG\\")) {		// Group-Message
						addGroupMessage(netBuffer.substring(6));
						print("OK");
						flush();
						handled = true;
					}
				} 
				if (netBuffer.length() > 11) {
					if (netBuffer.substring(0, 4).equals("MSG\\")) {		// Message
						if (addMessage(netBuffer.substring(4))) print("OK");
						else print("ERR");
						flush();
						handled = true;
					}
				}
				if (netBuffer.length() > 9) {								// Change User-Data
					if (netBuffer.substring(0, 7).equals("CHGUSR\\")) {
						changeUser(netBuffer.substring(7));
						print("OK");
						flush();
						handled = true;
					}
				}
				if (!handled) {
					if (maxWarnings == -1 || maxWarnings < warnings) { // Unknown command
					print("WARN");											// Warning
					flush();
					} else {
						print("TMWARN");										// Too many warnings
						flush();
						Logger.getDefaultLogger().logWarning("Disconnecting " + getIpFormat(socket) + " because of too many wrong packets");
						disconnect(socket, output, input);
					}
				}
			}
			
		} catch (IndexOutOfBoundsException | IOException e) {
			Logger.getDefaultLogger().logError("Error occured while sending or receiving packages from " + getIpFormat(socket) + " -> Disconnecting");
			Logger.getDefaultLogger().logException(e);
		} finally {
			disconnect(socket, output, input);
		}
	}

	public static String getIpFormat(Socket socket) {
		return socket.getInetAddress() + ":" + socket.getPort();
	}
	
	public static void disconnect(Socket socket) {
		try {
			socket.close();
			Logger.getDefaultLogger().logInfo("Connection " + getIpFormat(socket) + " was successfully unbinded");
		} catch (IOException e) {
			Logger.getDefaultLogger().logError("Connection " + getIpFormat(socket) + " was  insecurely unbinded");
			Logger.getDefaultLogger().logException(e);
		} finally {
			for (int i = 0; i < runningCons.size(); i++) if (runningCons.get(i).socket.equals(socket)) runningCons.remove(i);
		}
	}
	
	public static void disconnect(Socket socket, PrintWriter output, BufferedReader input) {
		try {
			output.close();
			input.close();
		} catch (IOException e) {
			Logger.getDefaultLogger().logError("Closing Reader or Writer of socket " + getIpFormat(socket) + " failed");
			Logger.getDefaultLogger().logException(e);
		} finally {
			disconnect(socket);
		}
	}
	
	/**
	 * This method stops all running Connections
	 */
	public static void disconnectAll() {
		for (Connection connection : runningCons) disconnect(connection.socket);
		Logger.getDefaultLogger().logInfo("All running connections interruped");
	}
	
	private boolean addMessage(String msg) {
		try {
			User msgUsr = new User(msg.substring(0, msg.indexOf("\\\\")));
			User targetUsr = new User(msg.substring(msg.indexOf(msgUsr.id)+msgUsr.id.length()+2, msg.indexOf("\\\\", msg.indexOf("\\\\")+1)));
			for (Connection connection : runningCons) {
				if (connection.usr.id.equals(targetUsr.id)) {
					connection.print("MSG\\" + connection.usr.id + "\\\\" + msg.substring(msg.indexOf("\\\\", msg.indexOf("\\\\")+1)+2));
					connection.flush();
					return true;
				}
			}
			Logger.getDefaultLogger().logWarning("No User (id: " + targetUsr.id + ", usr: " + targetUsr.getUsr() + ") found. Message could not be sent!");
			return false;
		} catch(Exception e) {
			Logger.getDefaultLogger().logError("Wrong message-format! No message was sent.");
			return false;
		}
	}
	
	private void addGroupMessage(String string) {
		// TODO Auto-generated method stub
		System.out.println("Hello from addGroupMessage: " + string);
	}
	
	private void deleteUser() {
		// TODO Auto-generated method stub
		System.out.println("Hello from deleteUser");
	}

	private void changeUser(String string) {
		// TODO Auto-generated method stub
		System.out.println("Hello from changeUser: " + string);
	}

	private void createNewUser(String netBuffer) {
		// TODO Auto-generated method stub
		System.out.println("Hello from createNewUser: " + netBuffer);
	}
	
	private void resetUser(String netBuffer) {
		// TODO Auto-generated method stub
		System.out.println("Hello from resetUser: " + netBuffer);
	}
	
	private void print(String msg) {
		// I added this method to maybe add a network logger in the future
		System.out.println("->"+msg);
		if (encrypted) msg = aes.encrypt(msg);
		output.print(msg);
	}
	
	private void flush() {
		// I added this method to maybe add a network logger in the future
		output.print("\n");
		output.flush();
	}
	
	private String readLine() throws IOException {
		// I added this method to maybe add a network logger in the future
		String msg = input.readLine();
		if (encrypted) msg = aes.decrypt(msg);
		System.out.println("<-"+msg);
		return msg;
	}
	
}
