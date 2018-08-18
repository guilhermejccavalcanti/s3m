package br.ufpe.cin.logging;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * Factory of loggers. 
 * @author Guilherme
 */
public class LoggerFactory {
	
	public static String logfile = "";
	
	/**
	 * Creates and configures a logger.
	 * @return configured Logger
	 */
	public static Logger make() {
		//determining the caller of the factory
		Throwable t = new Throwable();
		StackTraceElement directCaller = t.getStackTrace()[1];

		//instantiating the logger
		Logger logger =  Logger.getLogger(directCaller.getClassName());

		try{
			//creating FileHandler to record the logs
			String logpath = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
			new File(logpath).mkdirs(); //assuring that the directories exists
			logpath = logpath + "jfstmerge.log";
			logfile = logpath;
			manageLogBuffer(logpath);

			FileHandler fileHandler = new FileHandler(logpath,true);

			//setting formatter to the handler
			fileHandler.setFormatter(new SimpleFormatter());
			fileHandler.setEncoding("UTF-16");

			//setting Level to ALL
			fileHandler.setLevel(Level.ALL);
			logger.setLevel(Level.ALL);

			//disable console output
			logger.setUseParentHandlers(false);

			//assigning handler to logger
			logger.addHandler(fileHandler);
		} catch(Exception e){
			logger.log(Level.SEVERE, "Error occur during logging's creation.", e);
		}
		return logger;
	}

	/**
	 * When log's size reaches 10 megabytes,a new empty log is started, and the previous one is backup.
	 * @param logpath
	 */
	private static void manageLogBuffer(String logpath) {
		File log = new File(logpath);
		if(log.exists()){
			long logSizeMB = log.length() / (1024 * 1024);
			if(logSizeMB >= 10){
				File newLog = new File(logpath+System.currentTimeMillis());
				log.renameTo(newLog);
			}
		}
	}
	
}
