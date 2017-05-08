package br.ufpe.cin.logging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;

import br.ufpe.cin.exceptions.CryptoException;
import br.ufpe.cin.exceptions.ExceptionUtils;
import br.ufpe.cin.exceptions.PrintException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;

public class LoggerStatistics {

	public static void log(String msg, MergeContext context) throws PrintException{
		try{
			initializeLogger();

			//logging
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			String logpath   = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
			String logentry	 = timeStamp+","+msg+"\n";
			logpath = logpath + "jfstmerge.statistics";
			File statisticsLog = new File(logpath);

			//UNCOMMENT ONLY WITH THE CRYPTO KEY
			//CryptoUtils.decrypt(statisticsLog, statisticsLog);

			FileUtils.write(statisticsLog, logentry, true);

			//UNCOMMENT ONLY WITH THE CRYPTO KEY
			//CryptoUtils.encrypt(statisticsLog, statisticsLog);

			//logging merged files for further analysis
			logFiles(timeStamp,context);

		}catch(Exception e){
			throw new PrintException(ExceptionUtils.getCauseMessage(e));
		}
	}

	private static void initializeLogger() throws IOException, CryptoException {
		String logpath = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
		new File(logpath).mkdirs(); //assuring that the directories exists	
		logpath = logpath + "jfstmerge.statistics";

		manageLogBuffer(logpath);

		String header = "date,files,ssmergeconfs,ssmergeloc,ssmergerenamingconfs,ssmergedeletionconfs,ssmergetaeconfs,ssmergenereoconfs,ssmergeinitlblocksconfs,unmergeconfs,unmergeloc,unmergetime,ssmergetime\n";

		//reading the log file to see if it is not empty neither contains the header
		if(!new File(logpath).exists()){
			File statisticsLog = new File(logpath);
			FileUtils.write(statisticsLog, header, true);

			//UNCOMMENT ONLY WITH THE CRYPTO KEY
			//CryptoUtils.encrypt(statisticsLog, statisticsLog);
		}
	}

	/**
	 * When log's size reaches 10 megabytes,a new empty log is started, and the previous one is backup.
	 * @param logpath
	 * @throws CryptoException 
	 */
	private static void manageLogBuffer(String logpath) throws CryptoException {
		File log = new File(logpath);
		if(log.exists()){
			long logSizeMB = log.length() / (1024 * 1024);
			if(logSizeMB >= 10){
				File newLog = new File(logpath+System.currentTimeMillis());
				log.renameTo(newLog);
			}
		}
	}

	private static void logFiles(String timeStamp, MergeContext context) throws IOException, CryptoException {
		//initialization
		String logpath = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
		new File(logpath).mkdirs(); //assuring that the directories exists	
		logpath = logpath + "jfstmerge.files";
		manageLogBuffer(logpath);
		File logfiles = new File(logpath);

		if(!logfiles.exists()){
			logfiles.createNewFile();

			//UNCOMMENT ONLY WITH THE CRYPTO KEY
			//CryptoUtils.encrypt(logfiles, logfiles);
		}

		//UNCOMMENT ONLY WITH THE CRYPTO KEY
		//CryptoUtils.decrypt(logfiles, logfiles);

		//writing source code content
		//left
		String leftcontent = FilesManager.readFileContent(context.getLeft());
		if(!leftcontent.isEmpty()){
			FileUtils.write(logfiles, timeStamp+","+context.getLeft().getAbsolutePath()+"\n", true);
			FileUtils.write(logfiles, leftcontent + "\n", true);
			FileUtils.write(logfiles, "!@#$%\n", true); //separator
		}

		//base
		String basecontent = FilesManager.readFileContent(context.getBase());
		if(!basecontent.isEmpty()){
			FileUtils.write(logfiles, timeStamp+","+context.getBase().getAbsolutePath()+"\n", true);
			FileUtils.write(logfiles, basecontent + "\n", true);
			FileUtils.write(logfiles, "!@#$%\n", true); 
		}

		//right
		String rightcontent = FilesManager.readFileContent(context.getRight());
		if(!rightcontent.isEmpty()){
			FileUtils.write(logfiles, timeStamp+","+context.getRight().getAbsolutePath()+"\n", true);
			FileUtils.write(logfiles, rightcontent + "\n", true);
			FileUtils.write(logfiles, "!@#$%\n", true); 
		}

		//UNCOMMENT ONLY WITH THE CRYPTO KEY
		//CryptoUtils.encrypt(logfiles, logfiles);
	}

	public static void logScenario(String loggermsg) throws IOException {
		String logpath = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
		new File(logpath).mkdirs(); //assuring that the directories exists	
		logpath = logpath + "jfstmerge.scenarios";

		//reading the log file to see if it is not empty neither contains the header
		String header = "revision,ssmergeconfs,ssmergeloc,ssmergerenamingconfs,ssmergedeletionconfs,ssmergetaeconfs,ssmergenereoconfs,ssmergeinitlblocksconfs,unmergeconfs,unmergeloc,unmergetime,ssmergetime\n";
		File statisticsLog = new File(logpath);
		if(!statisticsLog.exists()){
			FileUtils.write(statisticsLog, header, true);
		}
		
		FileUtils.write(statisticsLog, loggermsg, true);
	}
}
