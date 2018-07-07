package client.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import client.Client;
import client.Encrypter;
import client.Encrypter.AES;
import client.Encrypter.RSA;

public class ClientConnection extends Thread {

	private static int port;
	private static boolean encrypted;
	private static boolean running;
	private static String host;
	private static String usr;
	private static String pwd;
	private static Socket socket;
	private static BufferedReader input;
	private static PrintWriter output;
	
	
	private ClientConnection (String host, int port, String usr, String pwd) {
		encrypted = false;
		running = false;
		ClientConnection.port = port;
		ClientConnection.host = host;
		ClientConnection.usr = usr;
		ClientConnection.pwd = pwd;
	}
	
	@Override
	public void run () {
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
			
			print("MSG\\Das ist ein Test@Hallo");
			flush();
			
			if (!readLine().equals("OK")) {
				print("DISC");
				flush();
			}
			
			print("MSGG\\Das ist ein Test2@Group");
			flush();
			
			if (!readLine().equals("OK")) {
				print("DISC");
				flush();
			}
			
			print("CHGUSR\\asdf");
			flush();
			
			if (!readLine().equals("OK")) {
				print("DISC");
				flush();
			}
			
			print("DELUSR");
			flush();
			
			if (!readLine().equals("OK")) {
				print("DISC");
				flush();
			}
			
			print("DISC");
			flush();
			
			if (readLine().equals("OK")) disconnect(socket, output, input);			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeConnection() {
	}
	
	public static boolean connect(String usr, String pwd) {
		try {
			String host = Client.defaultIni.getString(Client.defaultIniPath, "networking.serverCon.ipAddress");
			int port = Integer.parseInt(Client.defaultIni.getString(Client.defaultIniPath, "networking.serverCon.port"));
			new ClientConnection(host, port, usr, pwd).start();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return true;
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
