package server.io;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerConnection {
	// http://cs.lmu.edu/~ray/notes/javanetexamples/
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
	
	private ServerConnection() {}
	
	private static void listen() {
		try {
			while (running) new Connection(socket.accept()).start();
		} catch (IOException exception) {
			exception.printStackTrace();
		} 
	}
	
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
	
	public static void setPort(int port) {
		ServerConnection.port = port;
	}
	
	public static void setMaxConnections(int maxConnections) {
		ServerConnection.maxConnections = maxConnections;
	}
	
	public static void runConInDaemon(boolean startConAsDaemon) {
		ServerConnection.startConAsDaemon = startConAsDaemon;
	}
	
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
