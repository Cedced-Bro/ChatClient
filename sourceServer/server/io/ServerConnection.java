package server.io;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerConnection {
	// http://cs.lmu.edu/~ray/notes/javanetexamples/
	private static boolean running;
	private static int port;
	private static ServerSocket socket;
	
	static {
		running = false;
		port = -1;
	}
	
	private ServerConnection() {}
	
	private static void listen() {
		try {
			while (running) new Connection(socket.accept()).start();
		} catch (IOException exception) {
			// TODO: Do proper Error-Catching
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
	
	public static int getPort() {
		return port;
	}
}
