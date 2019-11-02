package br.ufpe.cin.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * Factory of loggers. 
 * @author Guilherme
 */
public class LoggerFactory {
	
	private static final Path LOG_FILE_PATH = Paths.get(System.getProperty("user.home"), ".jfstmerge", "jfstmerge.log");
	
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

		try {
			if(!Files.exists(LOG_FILE_PATH)) {
				
					// assuring that the directories exists
					Files.createDirectory(LOG_FILE_PATH.getParent());
		
					//creating FileHandler to record the logs
					FileHandler fileHandler = new FileHandler(LOG_FILE_PATH.toString(), true);
		
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
			} else {
				manageLogBuffer();
			}
		} catch(IOException e) {
			logger.log(Level.SEVERE, "Error occur during logging's creation.", e);
		}		
		
		return logger;
	}

	/**
	 * When log's size reaches 10 megabytes,a new empty log is started, and the previous one is backup.
	 * @param logpath
	 */
	private static void manageLogBuffer() throws IOException {
			long logSizeMB = LOG_FILE_PATH.toFile().length() / (1024 * 1024);
			if(logSizeMB >= 10){
				Files.move(LOG_FILE_PATH, LOG_FILE_PATH.resolveSibling(Paths.get(LOG_FILE_PATH.toString() + System.currentTimeMillis())));
			}
	}

	public static String logFile() {
		return LOG_FILE_PATH.toString();
	}
	
}
