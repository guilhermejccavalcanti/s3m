package br.ufpe.cin.logging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;

import br.ufpe.cin.crypto.CryptoUtils;
import br.ufpe.cin.exceptions.CryptoException;
import br.ufpe.cin.exceptions.ExceptionUtils;
import br.ufpe.cin.exceptions.PrintException;

public class LoggerStatistics {
	
	public static void log(String msg) throws PrintException{
		try{
			initializeLogger();
			
			//logging
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(Calendar.getInstance().getTime());
			String logpath   = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
			String logentry	 = timeStamp+","+msg+"\n";
			logpath = logpath + "jfstmerge.statistics";
			File statisticsLog = new File(logpath);
		
			//UNCOMMENT ONLY WITH THE CRYPTO KEY
			//CryptoUtils.decrypt(statisticsLog, statisticsLog);
			
			FileUtils.write(statisticsLog, logentry, true);
			
			//UNCOMMENT ONLY WITH THE CRYPTO KEY
			//CryptoUtils.encrypt(statisticsLog, statisticsLog);

		}catch(Exception e){
			throw new PrintException(ExceptionUtils.getCauseMessage(e));
		}
	}
	
	private static void initializeLogger() throws IOException, CryptoException {
		String logpath = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
		new File(logpath).mkdirs(); //assuring that the directories exists	
		logpath = logpath + "jfstmerge.statistics";
		
		manageLogBuffer(logpath);

		String header = "date,files,ssmergeconfs,ssmergeloc,ssmergerenamingconfs,ssmergedeletionconfs,ssmergetaeconfs,ssmergenereoconfs,ssmergeinitlblocksconfs,unmergeconfs,unmergeloc\n";

		//reading the log file to see if it is not empty neither contains the header
		if(!new File(logpath).exists()){
			File statisticsLog = new File(logpath);
			FileUtils.write(statisticsLog, header, true);

			//UNCOMMENT ONLY WITH THE CRYPTO KEY
			//CryptoUtils.encrypt(statisticsLog, statisticsLog);
		}
	}

	/**
	 * When log's size reaches 20 megabytes,a new empty log is started, and the previous one is backup.
	 * @param logpath
	 * @throws CryptoException 
	 */
	private static void manageLogBuffer(String logpath) throws CryptoException {
		File log = new File(logpath);
		if(log.exists()){
			long logSizeMB = log.length() / (1024 * 1024);
			if(logSizeMB > 20){
				File newLog = new File(logpath+System.currentTimeMillis());
				log.renameTo(newLog);
			}
		}
	}
}
