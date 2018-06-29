package client;

import client.io.ClientConnection;

public class Client {
	
	// This is just a testPWD and a testUSR
	public final static String usr = "admin";
	public final static String pwd = "password123";
	
	private static void startUI() {
		ClientConnection.connect(usr, pwd);
	}
	
	public static void main(String[] args) {
		startUI();	
	}
}
