package br.ufpe.cin.logging;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
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
	public static Logger make() {
		//determining the caller of the factory
		Throwable t = new Throwable();
		StackTraceElement directCaller = t.getStackTrace()[1];

		//instantiating the logger
		Logger logger =  Logger.getLogger(directCaller.getClassName());

		try{
			//creating FileHandler to record the logs
			FileHandler fileHandler = new FileHandler("./jfstmerge.log",true);

			//creating SimpleFormatter to record the logs as simple plain text
			//Formatter simpleFormatter = new SimpleFormatter();

			//setting formatter to the handler
			fileHandler.setFormatter(new XMLFormatter());

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

	public static void main(String[] args) {
		Logger l = LoggerFactory.make();
		l.log(Level.SEVERE,"something");
	}
}
