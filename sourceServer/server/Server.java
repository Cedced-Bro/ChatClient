package server;

import server.io.ConfigAdapter;
import server.io.Logger;
import server.io.ServerConnection;

/**
 * 
 * @author Cedric
 * @version 0.0
 */
public class Server {
	
	// This is just a testPWD and a testUSR
	public final static String usr = "root";
	public final static String pwd = "toor";
	
	// ********************
	// * Public variables *
	// ********************
	public static final String loggerIniPath;
	
	static {
		loggerIniPath = "/res/logger.ini";
	}
	
	// *******************
	// * Private methods *
	// *******************
	/**
	 * Initializes the Server-Listener
	 */
	private static void initServerConnection() {
		ServerConnection.setPort(Integer.parseInt(ConfigAdapter.getConfigString("port")));
		ServerConnection.setMaxConnections(Integer.parseInt(ConfigAdapter.getConfigString("maxConnections")));
		ServerConnection.runConInDaemon(Boolean.parseBoolean(ConfigAdapter.getConfigString("connectionAsDaemon")));
	}
	
	// ******************
	// * Public Methods *
	// ******************
	/**
	 * This is the Main method which starts the Server
	 * 
	 * @param args not important
	 */
	public static void main(String[] args) {
		Logger.getDefaultLogger().logInfo("Starting Server");
		initServerConnection();
		ServerConnection.start();
	}
	
}
