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
	
	// ********************
	// * Public variables *
	// ********************
	public static final String loggerIniPath;
	
	static {
		loggerIniPath = "/res/logger.ini";
	}
	
	private static void initServerConnection() {
		ServerConnection.setPort(Integer.parseInt(ConfigAdapter.getConfigString("port")));
		ServerConnection.setMaxConnections(Integer.parseInt(ConfigAdapter.getConfigString("maxConnections")));
		ServerConnection.runConInDaemon(Boolean.parseBoolean(ConfigAdapter.getConfigString("connectionAsDaemon")));
	}
	
	public static void main(String[] args) {
		Logger.getDefaultLogger().logInfo("Starting Server");
		initServerConnection();
		
	}
	
}
