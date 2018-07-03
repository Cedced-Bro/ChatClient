package client;

import client.io.ClientConnection;
import client.io.IniAdapter;

public class Client {
	
	// This is just a testPWD and a testUSR
	public final static String usr = "root";
	public final static String pwd = "toor";
	
	public static final IniAdapter defaultIni;
	public static final String defaultIniPath;
	
	static {
		defaultIni = new IniAdapter();
		defaultIniPath = "/res/client.ini";
	}
	
	private static void startUI() {
		ClientConnection.connect(usr, pwd);
	}
	
	public static void main(String[] args) {
		startUI();	
	}
}
