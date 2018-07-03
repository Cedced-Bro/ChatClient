package server.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import server.Encrypter;
import server.Server;

public class Connection extends Thread {

	public static List<Socket> runningCons;
	
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
	private PrintStream output;
	private Encrypter encrypter;
	private Encrypter.AES aes;
	
	// ****************
	// * Constructors *
	// ****************
	public Connection(Socket socket) {
		warnings = 0;
		encrypted = false;
		this.socket = socket;
		runningCons.add(socket);
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
			output = (PrintStream) socket.getOutputStream();
		} catch (IOException e) {
			Logger.getDefaultLogger().logError("Could not init Reader and Writer for " + getIpFormat(socket) + " -> Disconnecting");
			Logger.getDefaultLogger().logException(e);
			disconnect(socket, output, input);
		}
	}

	@Override
	public void run() {
		try {
			if (readLine().equals("RSA")) output.print(true);
			else {
				print("false");
				flush();
				Logger.getDefaultLogger().logError("Wrong format for Client " + getIpFormat(socket) + " -> Disconnecting");
				disconnect(socket, output, input);
				return;
			}
			
			// Setup Encryption
			encrypter = new Encrypter();
			Encrypter.RSA rsa = encrypter.new RSA();
			aes = encrypter.new AES();
			try {
				rsa.generateKeyPair();
				rsa.setOutsideKey(readLine());
				print(rsa.getPublicKey());
				flush();
				aes.setKey(rsa.decrypt(readLine()));
				encrypted = true;
				print("SUCCESS");
				flush();
//				Logger.getDefaultLogger().logInfo("Using AES encryption to " + getIpFormat(socket) + " now"); // Use this in own Network Logger
			} catch (Exception e) {
				print("ERROR");
				flush();
				disconnect(socket, output, input);
				return;
			}
			
			// Just for buffering received messages from a client
			String netBuffer = null;
			if ((netBuffer = readLine()).equals("LOGIN")) {
				// TODO! Benutze User aus der Database --> Siehe DatabaseConnector + benutze SQLight + userIds
				int i = 0;
				while (!(readLine()).equals(Server.usr + '\\' + Server.pwd)) {
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
			} else if (netBuffer.substring(0, 3).equals("REG")) {
				createNewUser(netBuffer);
			} else if (netBuffer.substring(0, 3).equals("RES")) {
				resetUser(netBuffer);
			} else {
				print("ERROR");
				flush();
				disconnect(socket, output, input);
			}

			// From now on: listening for Commands
			boolean running = true;
			while (running) {
				netBuffer = readLine();
				if (netBuffer.equals("DISC")) {
					runningCons.remove(socket);
					print("OK");
					flush();
				} else if (netBuffer.substring(0, 4).equals("MSG\\")) {
					addMessage(netBuffer.substring(4));
				} else if (netBuffer.substring(0, 5).equals("MSGG\\")) {
					addGroupMessage(netBuffer.substring(5));
				} else if (netBuffer.substring(0, 7).equals("CHGUSR\\")) {
					changeUser(netBuffer.substring(7));
				} else if (netBuffer.substring(0, 4).equals("DELUSR")) {
					deleteUser();
				} else {
					if (maxWarnings == -1 || maxWarnings < warnings) {
						print("WARN");
						flush();
					} else {
						print("TMWARN");
						flush();
						Logger.getDefaultLogger().logWarning("Disconnecting " + getIpFormat(socket) + " because of too many wrong packets");
						disconnect(socket, output, input);
					}
				}
			}
			
		} catch (IOException e) {
			Logger.getDefaultLogger().logError("Error occured while sending or receiving packages from " + getIpFormat(socket) + " -> Disconnecting");
			Logger.getDefaultLogger().logException(e);
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
			runningCons.remove(socket);
		}
	}
	
	public static void disconnect(Socket socket, PrintStream output, BufferedReader input) {
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
		for (Socket socket : runningCons) disconnect(socket);
		Logger.getDefaultLogger().logInfo("All running connections interruped");
	}
	
	private void addMessage(String string) {
		// TODO Auto-generated method stub
		System.out.println("Hello from addMessage: " + string);
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
		if (encrypted) msg = aes.encrypt(msg);
		output.print(msg);
	}
	
	private void flush() {
		// I added this method to maybe add a network logger in the future
		output.flush();
	}
	
	private String readLine() throws IOException {
		// I added this method to maybe add a network logger in the future
		String msg = input.readLine();
		if (encrypted) msg = aes.decrypt(msg);
		return msg;
	}
	
}
