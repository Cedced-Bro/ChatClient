package server;

import server.io.ConfigAdapter;
import server.io.Logger;

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
	
	public static void main(String[] args) {
		Logger.getDefaultLogger().logInfo("Test");
		System.out.println(ConfigAdapter.getConfigString("test"));
	}
	
}
