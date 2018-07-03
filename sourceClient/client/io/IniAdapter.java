package client.io;

import java.util.Properties;

import javax.swing.JOptionPane;

/** 
 * 	@author Cedric	
 *	@version 1.0
 *	@category io
 **/

public class IniAdapter {
	
	private Properties lastIni;
	private String lastIniPath;
	
	public IniAdapter() {
		lastIni = new Properties();
	}
	
	public String getString(String path, String key) {
		return getString(path, key, false);
	}
	
	public String getString(String path, String key, boolean suppressErrors) {
		/** Da normalerweise mehr als ein Attribut aus einem INI-File gezogen wird,
		 *  habe ich es hier so implementiert, dass auch mehr als ein Attribut herausgezogen
		 *	werden kann, ohne nocheinmal das File neuladen zu müssen -> hoffentlich schneller!
		 */
		boolean notFromLogger = true;
		
		for(StackTraceElement st : Thread.currentThread().getStackTrace())
			notFromLogger = (!st.getClassName().contains("client.io.Logger") && notFromLogger);
		
		if(notFromLogger) {
			Logger.getDefaultLogger().logInfo("Loading " + key + " from " + path);
		}
		
		if(path != lastIniPath) {
			try {
				lastIni.load(this.getClass().getResourceAsStream(path));
				lastIniPath = path;
			} catch (Exception exception) {
				// TODO: Error Handling
				// This Error can't be suppressed
				if(notFromLogger) {
					Logger.getDefaultLogger().logError("Could't load " + key + " from " + path);
					String error = Logger.getDefaultLogger().logException(exception);
					JOptionPane.showMessageDialog(null, "Error while loading " + key + " from " + path + "!\nTrying to continue!\n\nError:\n" + error, "Warning", JOptionPane.WARNING_MESSAGE);
				}
				return null;
			}
		}
		return lastIni.getProperty(key);
	}
	
}
