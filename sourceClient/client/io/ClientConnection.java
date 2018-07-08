package client.io;

import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import client.Encrypter;
import client.Encrypter.AES;
import client.Encrypter.RSA;
import client.FiFo_List;
import client.FiFo_List.MessageData;
import client.Main;
import client.User;

public class ClientConnection extends Thread {

	private static int port;
	private static boolean encrypted;
	private static boolean running;
	private static boolean receivedMessage;
	private static String host;
	private static String usr;
	private static String pwd;
	private static Socket socket;
	private static BufferedReader input;
	private static PrintWriter output;
	private static FiFo_List sendMessages;
	private static FiFo_List receivedMessages;
	
	
	private ClientConnection (String host, int port, String usr, String pwd) {
		setDaemon(true);
		encrypted = false;
		ClientConnection.port = port;
		ClientConnection.host = host;
		ClientConnection.usr = usr;
		ClientConnection.pwd = pwd;
	}
	
	@Override
	public void run () {
		sendMessages = new FiFo_List();
		receivedMessages = new FiFo_List();
		try {
			socket = new Socket(host, port);
			output = new PrintWriter(socket.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// Setup Encryption
			print("RSA");
			flush();
			if (!Boolean.parseBoolean(readLine())) {
				// TODO!
				disconnect(socket, output, input);
				return;
			}
			
			try {
				int keySize = Integer.parseInt(readLine());
				RSA.generateKeyPair(keySize);
				print(RSA.getPublicKey().replaceAll("[\r\n]", ""));
				flush();
				String response = readLine();
				if (response.equals("ERROR")) {
					// TODO!
				}
				RSA.setOutsideKey(response);
				AES.generateKey();
				print(AES.getKey().replaceAll("[\r\n]", ""));
				flush();
				if (readLine().equals("SUCCESS")) {
					// TODO!
					encrypted = true;
				} else {
					// TODO!
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
				// TODO!
			}
			
			print("LOGIN");
			flush();
			
			String sendUsr = usr;
			String sendPwd = pwd;
			if (usr.contains("\\")) sendUsr = usr.replaceAll("\\", "\\\\");
			if (pwd.contains("\\")) sendPwd = pwd.replaceAll("\\", "\\\\");
			
			// Last letter of Usr mustn't be a \\ !
			print(sendUsr + "\\" + sendPwd);
			flush();
			
			if (readLine().equals("OK")) {
				// TODO!
			} else {
				// TODO!
			}
			running = true;
			receivedMessage = false;
			Runnable r = new Runnable() {
				@Override
				public void run() {
					while (running) {
						try {
							if (!receivedMessage) {
								receivedMessages.addMessageElement(receivedMessages.new MessageData(readLine()));
								receivedMessage = true;
							}
							Thread.sleep(10);
						} catch (IOException | InterruptedException e) {
							Logger.getDefaultLogger().logWarning("Connection to the Server was closed");
							running = false;
							Main.getGui().dispatchEvent(new WindowEvent(Main.getGui(), WindowEvent.WINDOW_CLOSING));
							// TODO Auto-generated catch block
//							e.printStackTrace();
						}
					}
				}
			};
			Thread receiver = new Thread(r);
//			receiver.setDaemon(true);
			receiver.start();
			
			String messageBuffer = null;
			while (running) {
				if (sendMessages.get(0) != null) {
					if ((messageBuffer = ((MessageData)sendMessages.removeFirstElement()).message) != null) {
						print("MSG\\" + Main.getOwnUser().id + "\\\\" + Main.getContactUserID() + "\\\\" + messageBuffer);
						flush();
					}
				} else if (receivedMessage) {
					System.out.println("end");
					receivedMessage = false;
					messageBuffer = ((MessageData) receivedMessages.removeFirstElement()).message;
					if (messageBuffer != null) {
						if (messageBuffer.length() > 11) {
							if (messageBuffer.substring(0, 4).equals("MSG\\")) {
								Main.setUser(new User(messageBuffer.substring(4, messageBuffer.indexOf("\\\\")), Main.getUsr(messageBuffer.substring(4, messageBuffer.indexOf("\\\\")))));
								Main.showReceivedMessage(messageBuffer.substring(messageBuffer.indexOf("\\\\")+2));
							}
						}
					}
				}
				try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
			}
			
			print("DISC");
			flush();
			
			disconnect(socket, output, input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeConnection() {
		running = false;
	}
	
	public static boolean connect(String usr, String pwd) {
		try {
			String host = Main.defaultIni.getString(Main.defaultIniPath, "networking.serverCon.ipAddress");
			int port = Integer.parseInt(Main.defaultIni.getString(Main.defaultIniPath, "networking.serverCon.port"));
			new ClientConnection(host, port, usr, pwd).start();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static void addToPrintList(String msg) {
		sendMessages.addMessageElement(sendMessages.new MessageData(msg));
	}
	
	private static void disconnect(Socket socket) {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void disconnect(Socket socket, PrintWriter output, BufferedReader input) {
		disconnect(socket);
		try {
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		output.close();
	}
	
	private static void print(String msg) {
		System.out.println("->"+msg);
		if (encrypted) msg = Encrypter.AES.encrypt(msg);
		output.print(msg);
	}
	
	private static void flush() {
		output.print("\n");
		output.flush();
	}
	
	private static String readLine() throws IOException {
		String msg = input.readLine();
		if (encrypted) msg = Encrypter.AES.decrypt(msg);
		System.out.println("<-"+msg);
		return msg;
	}
}
