package server.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.LinkedBlockingQueue;

import server.Server;

/**
 * @author Leo, Cedric
 * @version 2.0
 * @category io
 */
public class Logger {

	// ******************************
	// Constants
	// ******************************
	public final static IniAdapter ini;
	private static final String INFO_PREFIX = "[Info]";
	private static final String WARNING_PREFIX = "[Warning]";
	private static final String ERROR_PREFIX = "[Error]";

	static {
		ini = new IniAdapter();
		
		boolean fileLogging = false;
		setDefaultLogger(new Logger(fileLogging));
	}

	
	
	
	// ******************************
	// Fields
	// ******************************
	private static Logger defaultLogger;
	
	private LinkedBlockingQueue<String> messages;
	private PrintWriter logFileWriter;

	
	

	// ******************************
	// Constructors
	// ******************************
	/**
	 * Creates a new logger.
	 */
	public Logger() {
		this(null);
	}
	
	/**
	 * Creates a new logger. If fileLogging is true, the log output will be written into the standard logfile.
	 * 
	 * @param fileLogging whether the log output will be written into the standard logfile
	 */
	public Logger(boolean fileLogging) {
		this(fileLogging ? new File(ini.getString(Server.loggerIniPath, "path").replace("*", "") + "server_chatclient.log") : null);
	}

	/**
	 * Creates a new logger that writes its ouput into the given file.
	 * 
	 * @param logfile the file to write the log output into
	 */
	public Logger(File logfile) {
		messages = new LinkedBlockingQueue<>();
		
		//initiates logfile writer if requested
		if(logfile != null) {
			try {
				if(!logfile.exists())
					logfile.createNewFile();
				logFileWriter = new PrintWriter(new FileWriter(logfile, true));
				logFileWriter.write("\n----------------------------new-Session-started----------------------------\n");
				logFileWriter.flush();
			} catch (IOException e) {
				logError("Error while initializing logfile.");
				logError(e.getMessage());
				logFileWriter = null;
			}
		}

		//processes the elements in the messages queue in a separate thread
		Thread loggerWorker = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						String message = messages.take();
						System.out.println(message);

						if(logFileWriter != null) {
							logFileWriter.println(message);
							logFileWriter.flush();
						}

					} catch (InterruptedException e) {
						logError(e.getMessage());
					}
				}
			}
		});
		//optional
		loggerWorker.setDaemon(true);
		//end-optional
		loggerWorker.start();
	}

	
	

	// ******************************
	// Private methods
	// ******************************
	
	private enum Months {
		JANUARY(ini.getString(Server.loggerIniPath, "January")),
		FEBRUARY(ini.getString(Server.loggerIniPath, "February")),
		MARCH(ini.getString(Server.loggerIniPath, "March")),
		APRIL(ini.getString(Server.loggerIniPath, "April")),
		MAY(ini.getString(Server.loggerIniPath, "May")),
		JUNE(ini.getString(Server.loggerIniPath, "June")),
		JULY(ini.getString(Server.loggerIniPath, "July")),
		AUGUST(ini.getString(Server.loggerIniPath, "August")),
		SEPTEMBER(ini.getString(Server.loggerIniPath, "September")),
		OCTOBER(ini.getString(Server.loggerIniPath, "October")),
		NOVEMBER(ini.getString(Server.loggerIniPath, "November")),
		DECEMBER(ini.getString(Server.loggerIniPath, "December"));

		private final String monthShortForm;

		private Months(String monthShortForm) {
			this.monthShortForm = monthShortForm;
		}

		private String getMonthSF() {
			return monthShortForm;
		}

		private static Months getMonth(int id) {
			switch(id) {
			case 1:
				return JANUARY;
			case 2:
				return FEBRUARY;
			case 3:
				return MARCH;
			case 4:
				return APRIL;
			case 5:
				return MAY;
			case 6:
				return JUNE;
			case 7:
				return JULY;
			case 8:
				return AUGUST;
			case 9:
				return SEPTEMBER;
			case 10:
				return OCTOBER;
			case 11:
				return NOVEMBER;
			case 12:
				return DECEMBER;
			default:
				return null;
			}
		}
	}

	/**
	 * Internal method for adding text with a timestamp to the logger buffer.
	 * 
	 * @param text the text to enqueue in the buffer
	 */
	private synchronized void log(String text) {
		try {
			messages.add("["+getTime()+"] " + text);
		} catch (IllegalStateException e) {
			System.out.println("Logger buffer out of bounds!");
			e.printStackTrace();
		}
	}

	
	
	
	// ******************************
	// Public methods
	// ******************************
	/**
	 * Returns the time at the moment, it gets called
	 * 
	 * @return Returns the time at the moment, it gets called
	 */
	public static String getTime() {
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		int day_i = gregorianCalendar.get(Calendar.DAY_OF_MONTH);
		int month_i = gregorianCalendar.get(Calendar.MONTH)+1;
		int year = gregorianCalendar.get(Calendar.YEAR);
		int hour_i = gregorianCalendar.get(Calendar.HOUR);
		int hour24_i = gregorianCalendar.get(Calendar.HOUR_OF_DAY);
		String am_pm = gregorianCalendar.get(Calendar.AM_PM) == Calendar.AM ? "a" : "p";
		int minute_i = gregorianCalendar.get(Calendar.MINUTE);
		int second_i = gregorianCalendar.get(Calendar.SECOND);

		String day = day_i < 10 ? "0" + day_i : ""+day_i;
		String month = month_i < 10 ? "0" + month_i : ""+month_i;
		String hour = hour_i < 10 ? "0" + hour_i : ""+hour_i;
		String hour24 = hour24_i < 10 ? "0" + hour24_i : ""+hour24_i;
		String minute = minute_i < 10 ? "0" + minute_i : ""+minute_i;
		String second = second_i < 10 ? "0" + second_i : ""+second_i;

		switch(ini.getString(Server.loggerIniPath, "time_format")) {
		case "dd/m/yyyy-24hh:mm:ss":
			return day + "/" + month + "/" + year + "-" + hour24 + ":" + minute + ":" + second;
		case "dd/m/yyyy-hh:mm:ss":
			return day + "/" + month + "/" + year + "-" + hour + am_pm + ":" + minute + ":" + second;
		case "m/dd/yyyy-hh:mm:ss":
			return month + "/" + day + "/" + year + "-" + hour + am_pm + ":" + minute + ":" + second;
		case "yyyy/m/dd-24hh:mm:ss":
			return year + "/" + month + "/" + day + "-" + hour24 + ":" + minute + ":" + second;
		case "yyyy/m/dd-hh:mm:ss":
			return year + "/" + month + "/" + day + "-" + hour + am_pm + ":" + minute + ":" + second;
		case "yyyy/dd/m-hh:mm:ss":
			return year + "/" + day + "/" + month + "-" + hour + am_pm + ":" + minute + ":" + second;
		case "dd/mmm/yyyy-24hh:mm:ss":
			return day + "/" + Months.getMonth(month_i).getMonthSF() + "/" + year + "-" + hour24 + ":" + minute + ":" + second;
		case "dd/mmm/yyyy-hh:mm:ss":
			return day + "/" + Months.getMonth(month_i).getMonthSF() + "/" + year + "-" + hour + am_pm + ":" + minute + ":" + second;
		case "mmm/dd/yyyy-hh:mm:ss":
			return Months.getMonth(month_i).getMonthSF() + "/" + day + "/" + year + "-" + hour + am_pm + ":" + minute + ":" + second;
		case "yyyy/mmm/dd-24hh:mm:ss":
			return year + "/" + Months.getMonth(month_i).getMonthSF() + "/" + day + "-" + hour24 + ":" + minute + ":" + second;
		case "yyyy/mmm/dd-hh:mm:ss":
			return year + "/" + Months.getMonth(month_i).getMonthSF() + "/" + day + "-" + hour + am_pm + ":" + minute + ":" + second;
		case "yyyy/dd/mmm-hh:mm:ss":
			return year + "/" + day + "/" + Months.getMonth(month_i).getMonthSF() + "-" + hour + am_pm + ":" + minute + ":" + second;
		default:
			return gregorianCalendar.getTime().toString();
		}
	}
	
	/**
	 * Returns the default logger.
	 * 
	 * @return the default logger
	 */
	public static Logger getDefaultLogger() {
		return defaultLogger;
	}

	/**
	 * Sets the default logger.
	 * 
	 * @param defaultLogger the logger to set as default
	 */
	public static void setDefaultLogger(Logger defaultLogger) {
		Logger.defaultLogger = defaultLogger;
	}

	
	
	/**
	 * Logs an information text.
	 * 
	 * @param text the text to log
	 */
	public synchronized void logInfo(String text) {
		log(INFO_PREFIX + " " + text);
	}

	/**
	 * Logs a warning text.
	 * 
	 * @param text the text to log
	 */
	public synchronized void logWarning(String text) {
		log(WARNING_PREFIX + " " + text);
	}

	/**
	 * Logs an error text.
	 * 
	 * @param text the text to log
	 */
	public synchronized void logError(String text) {
		log(ERROR_PREFIX + " " + text);
	}
	
	/**
	 * Logs an exception like an error message.<br>
	 * (Replaces Error class.)
	 * 
	 * @param exception the exception to log
	 * @return 
	 */
	public synchronized String logException(Exception exception) {
		String msg = "";
		for (StackTraceElement ste : exception.getStackTrace()) {
			msg+=ste.toString()+'\n';
		}
		logError("Error-Message: " + msg);
		return msg;
	}
	
	/**
	 * Logs a plain text.
	 * 
	 * @param text the text to log
	 */
	public synchronized void logPlain(String text) {
		log(text);
	}
	
	/**
	 * Returns whether the logger is writing its output into a file.
	 * 
	 * @return whether file logging is activated
	 */
	public boolean isFileLogging() {
		return logFileWriter != null;
	}
}
