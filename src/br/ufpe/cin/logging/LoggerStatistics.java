package br.ufpe.cin.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

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
			logpath = logpath + "jfstmerge.statistics";
			File statisticsLog = new File(logpath);
			CryptoUtils.decrypt(statisticsLog, statisticsLog);
			
			PrintWriter pw = new PrintWriter(new FileOutputStream(statisticsLog, true), true); 
			pw.append(timeStamp+","+msg+"\n");
			pw.close();
			CryptoUtils.encrypt(statisticsLog, statisticsLog);
		}catch(Exception e){
			throw new PrintException(ExceptionUtils.getCauseMessage(e));
		}
	}
	
	private static void initializeLogger() throws IOException, CryptoException {
		String logpath = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
		new File(logpath).mkdirs(); //assuring that the directories exists	
		logpath = logpath + "jfstmerge.statistics";
		manageLogBuffer(logpath);

		String header = "date,files,ssmergeconfs,ssmergeloc,ssmergerenamingconfs,ssmergedeletionconfs,ssmergetaeconfs,ssmergenereoconfs,unmergeconfs,unmergeloc";

		//reading the log file to see if it is not empty neither contains the header
		if(!new File(logpath).exists()){
			File statisticsLog = new File(logpath);
			PrintWriter pw = new PrintWriter(new FileOutputStream(statisticsLog, true), true); 
			pw.append(header+"\n");
			pw.close();
			CryptoUtils.encrypt(statisticsLog, statisticsLog);
			
		}
	}

	/**
	 * When log's size reaches 20 megabytes,a new empty log is started, and the previous one is backup.
	 * @param logpath
	 */
	private static void manageLogBuffer(String logpath) {
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
