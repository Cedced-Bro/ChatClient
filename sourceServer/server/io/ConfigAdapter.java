package server.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.swing.JOptionPane;

/** 
 * 	@author Cedric	
 *	@version 1.0
 *	@category io
 **/

public class ConfigAdapter {
	
	private Properties configFile;
	private static ConfigAdapter instance;
	private static final String path;
	
	static {
		path = "/res/config.cfg";
	}
	
	private ConfigAdapter() {
		configFile = new Properties();
		try {
			if (!new File(path).exists()) {
				exportDefaultConfig(path);
			}
			configFile.load(new FileInputStream(new File(path)));
		} catch(Exception exception) {
			for(StackTraceElement st : Thread.currentThread().getStackTrace())
				if (st.getClassName().contains("io.Logger")) {
					exception.printStackTrace();
					return;
				}
			Logger.getDefaultLogger().logError("An Error occured while loading the config!");
			String error = Logger.getDefaultLogger().logException(exception);
			Logger.getDefaultLogger().logWarning("Exiting now...");
			JOptionPane.showMessageDialog(null, "An error occured while loading Config!\n\nError: " + error + "\n\nExiting...");
			System.exit(1);
		}
		instance = this;
	}
	
	private String getValue(String key) {
		return configFile.getProperty(key);
	}
	
	private boolean setValue(String key, String value) {
		configFile.setProperty(key, value);
		try {
			configFile.store(new FileOutputStream(new File(path)), null);
		} catch (IOException e) {
			String error = "";
			for(StackTraceElement s : e.getStackTrace()) {
				error += s.toString() + '\n';
			}
			Logger.getDefaultLogger().logError("An Error occured while loading the config!");
			Logger.getDefaultLogger().logError("Error: " + error);
			Logger.getDefaultLogger().logWarning("Exiting now...");
			JOptionPane.showMessageDialog(null, "An error occured while setting new Config-String!\n\nError: " + error + "\n\nYou should consider to install the game.");
			return false;
		}
		instance = new ConfigAdapter();
		return true;
	}
	
	
	private static void exportDefaultConfig(String fileName) throws IOException, URISyntaxException {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = ConfigAdapter.class.getResourceAsStream(fileName);
			if (inputStream == null)
				Logger.getDefaultLogger().logError("Couldn't export default-config!");
			int readBytes;
			byte[] buffer = new byte[4096];
			outputStream = new FileOutputStream(new File(ConfigAdapter.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/')+fileName);
			while ((readBytes = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, readBytes);
			}
		} finally {
			inputStream.close();
			outputStream.close();
		}
	}
	
	public static String getConfigString(String key) {
		if(!(Thread.currentThread().getStackTrace()[1].getClassName().contains("io.Logger"))) Logger.getDefaultLogger().logInfo("Loading " + key + " from config");
		if(instance == null) new ConfigAdapter();
		return instance.getValue(key);
	}
	
	public static boolean setConfigString(String key, String value) {
		if(!(Thread.currentThread().getStackTrace()[1].getClassName().contains("io.Logger"))) Logger.getDefaultLogger().logInfo("Setting " + key + " to " + value + " in config");
		if(instance == null) new ConfigAdapter();
		return instance.setValue(key, value);
	}
}
