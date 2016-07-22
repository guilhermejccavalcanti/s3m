package br.ufpe.cin.logging;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;


/**
 * Factory of loggers. 
 * @author Guilherme
 */
public class LoggerFactory {

	/**
	 * Creates and configures a logger.
	 * @return configured Logger
	 */
	public static Logger make(boolean statistics) {
		//determining the caller of the factory
		Throwable t = new Throwable();
		StackTraceElement directCaller = t.getStackTrace()[1];

		//instantiating the logger
		Logger logger =  Logger.getLogger(directCaller.getClassName());

		try{
			//creating FileHandler to record the logs
			String logpath = "";
			if(statistics){
				logpath = "./jfstmerge.statistics";
			} else {
				logpath = "./jfstmerge.log";
			}

			FileHandler fileHandler = new FileHandler(logpath,true);

			//setting formatter to the handler
			if(statistics){
				fileHandler.setFormatter(new SimpleFormatter());
			} else {
				fileHandler.setFormatter(new XMLFormatter());
			}

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
}
