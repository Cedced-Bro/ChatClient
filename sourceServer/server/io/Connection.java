package server.io;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Connection extends Thread {

	public static List<Socket> runningCons;

	static {
		runningCons = new ArrayList<>();
	}
	
	private boolean logConState;
	private boolean logIp;
	
	public Connection(Socket socket) {
		runningCons.add(socket);
		// Loading logConState from Config --> needs to be catched seperatly
		try {
			logConState = Boolean.parseBoolean(ConfigAdapter.getConfigString("logConState"));
		} catch (Exception e) {
			Logger.getDefaultLogger().logException(e);
			Logger.getDefaultLogger().logWarning("logConState couldn't be loaded from Config. Assuming [true] for this case.");
			logConState = true;
		}
		// Loading logIp from Config --> needs to be catched seperatly
		try {
			logIp = Boolean.parseBoolean(ConfigAdapter.getConfigString("logIp"));
		} catch (Exception e) {
			Logger.getDefaultLogger().logException(e);
			Logger.getDefaultLogger().logWarning("logIp couldn't be loaded from Config. Assuming [true] in this case.");
			logIp = true;
		}

		if (logConState && logIp) Logger.getDefaultLogger().logInfo("New Connection established -> User " + socket.getInetAddress() + ":" + socket.getPort() + " tries to connect");
		
		try {
			System.out.println(socket.getInputStream().read());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
