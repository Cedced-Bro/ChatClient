package server.io;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerConnection {
	// http://cs.lmu.edu/~ray/notes/javanetexamples/
	// *****************
	// * Static fields *
	// *****************
	private static boolean running;
	private static boolean startConAsDaemon;
	private static int port;
	private static int maxConnections;
	private static ServerSocket socket;
	
	static {
		running = false;
		startConAsDaemon = false;
		port = -1;
		maxConnections = -1;
	}
	
	// ****************
	// * Constructors *
	// ****************
	private ServerConnection() {}
	
	// *******************
	// * Private Methods *
	// *******************
	private static void listen() {
		try {
			while (running) {
				Connection c = null;
				if (maxConnections > Connection.runningCons.size()) c = new Connection(socket.accept());
				if (startConAsDaemon) c.setDaemon(true);
				c.start();
			}
			socket.close();
			Logger.getDefaultLogger().logInfo("Server does not listen any more");
		} catch (IOException exception) {
			exception.printStackTrace();
		} 
	}
	
	// ******************
	// * Public Methods *
	// ******************
	public static boolean start() {
		if (port == -1) return false;
		try {
			socket = new ServerSocket(port);
		} catch (IOException exception) {
			// TODO: Do a proper Error-Catching
			exception.printStackTrace();
		}
		running = true;
		Thread t = new Thread (new Runnable() {
			@Override
			public void run() {
				listen();
			}
		});
		t.start();
		return true;
	}
	
	public static void stop() {
		running = false;
	}
	
	
	// ***********
	// * Setters *
	// ***********
	public static void setPort(int port) {
		ServerConnection.port = port;
		Logger.getDefaultLogger().logInfo("Setting Server to port " + port);;
	}
	
	public static void setMaxConnections(int maxConnections) {
		ServerConnection.maxConnections = maxConnections;
		Logger.getDefaultLogger().logInfo("Setting Max_Connections to " + (maxConnections == -1 ? "infinity" : maxConnections));
	}
	
	public static void runConInDaemon(boolean startConAsDaemon) {
		ServerConnection.startConAsDaemon = startConAsDaemon;
		Logger.getDefaultLogger().logInfo(startConAsDaemon ? "Starting incoming connections as new Daemon" : "Incoming connections will be started as new Thread");
	}
	
	// ***********
	// * Getters *
	// ***********
	public static int getPort() {
		return port;
	}
	
	public static int getMaxConnections() {
		return maxConnections;
	}
	
	public static boolean getStartConAsDaemon() {
		return startConAsDaemon;
	}
}
