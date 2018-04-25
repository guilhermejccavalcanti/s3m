package br.ufpe.cin.logging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.crypto.CryptoUtils;
import br.ufpe.cin.exceptions.CryptoException;
import br.ufpe.cin.exceptions.ExceptionUtils;
import br.ufpe.cin.exceptions.PrintException;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.Source;

public class LoggerStatistics {

	//variable to avoid infinite recursion when trying to fix cryptographic issues 
	public static int numberOfCriptographyFixAttempts = 0;

	//managing enable/disable of cryptography
	static{ 
		if(!JFSTMerge.isCryptographed){
			try {
				String logpath   = System.getProperty("user.home")+ File.separator + ".stjfstmerge" + File.separator;

				logpath = logpath + "stjfstmerge.statistics";
				File file = new File(logpath);
				CryptoUtils.decrypt(file, file);

				logpath = logpath + "stjfstmerge.files";
				file = new File(logpath);
				CryptoUtils.decrypt(file, file);

			} catch (CryptoException e) {
				// the files are already decrypted, no need for further action
			}
		}
	}

	public static void logContext(String msg, MergeContext context) throws PrintException{
		try{
			initializeLogger();

			//logging
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			String logpath   = System.getProperty("user.home")+ File.separator + ".stjfstmerge" + File.separator;
			String logentry	 = timeStamp+","+msg+"\n";
			logpath = logpath + "stjfstmerge.statistics";
			File statisticsLog = new File(logpath);

			if(JFSTMerge.isCryptographed){
				CryptoUtils.decrypt(statisticsLog, statisticsLog);
			}

			FileUtils.write(statisticsLog, logentry, true);

			if(JFSTMerge.isCryptographed){
				CryptoUtils.encrypt(statisticsLog, statisticsLog);
			}
			
			if(JFSTMerge.logFiles){
				//logging merged files for further analysis
				logFiles(timeStamp,context);
			}
		}
		catch (CryptoException c)
		{
			String logpath   = System.getProperty("user.home")+ File.separator + ".stjfstmerge" + File.separator;
			logpath = logpath + "stjfstmerge.statistics";
			File log = new File(logpath);
			if (log.exists())
			{
				File log_defect = new File(logpath+"_defect"+System.currentTimeMillis());
				log.renameTo(log_defect);

			}
			if(numberOfCriptographyFixAttempts < 1)
			{
				numberOfCriptographyFixAttempts++;
				logContext(msg,context);
			}
		}
		catch(Exception e){
			throw new PrintException(ExceptionUtils.getCauseMessage(e));
		}
	}

	public static void logScenario(String loggermsg) throws IOException {
		String logpath = System.getProperty("user.home")+ File.separator + ".stjfstmerge" + File.separator;
		new File(logpath).mkdirs(); //ensuring that the directories exists	
		logpath = logpath + "stjfstmerge.statistics.scenarios";

		//reading the log file to see if it is not empty neither contains the header
		String header = "revision,stmergeconfs,stmergeloc,unmergeconfs,unmergeloc,unmergetime,stmergetime,equalconfs\n";
		File statisticsLog = new File(logpath);
		if(!statisticsLog.exists()){
			FileUtils.write(statisticsLog, header, true);
		}

		FileUtils.write(statisticsLog, loggermsg, true);
	}

	public static void logConflicts(List<MergeConflict> conflicts, Source source) throws IOException {
		String logpath = System.getProperty("user.home")+ File.separator + ".stjfstmerge" + File.separator;
		new File(logpath).mkdirs(); //ensuring that the directories exists	
		for(MergeConflict mc : conflicts){
			String origin =  ((mc.leftOriginFile != null) ? mc.leftOriginFile.getAbsolutePath() : "<empty left>") 
					+ ";" + ((mc.baseOriginFile  != null) ? mc.baseOriginFile.getAbsolutePath() : "<empty base>") 
					+ ";" + ((mc.rightOriginFile != null) ? mc.rightOriginFile.getAbsolutePath(): "<empty right>");
			if(source == null){
				File f = new File(logpath + "conflicts.equals");
				FileUtils.write(f,(origin+'\n'+mc.body+'\n'),true);
				break;
			}else {
				switch (source) {
				case UNSTRUCTURED:
					File f = new File(logpath + "conflicts.unstructured");
					FileUtils.write(f,(origin+'\n'+mc.body+'\n'),true);
					break;
				case STRUCTURED:
					f = new File(logpath + "conflicts.structured");
					FileUtils.write(f,(origin+'\n'+mc.body+'\n'),true);
					break;
				}
			}
		}
	}

	private static void logFiles(String timeStamp, MergeContext context) throws IOException {
		try{
			//initialization
			String logpath = System.getProperty("user.home")+ File.separator + ".stjfstmerge" + File.separator;
			new File(logpath).mkdirs(); //ensuring that the directories exists	
			logpath = logpath + "stjfstmerge.files";
			manageLogBuffer(logpath);
			File logfiles = new File(logpath);

			if(!logfiles.exists()){
				logfiles.createNewFile();

				if(JFSTMerge.isCryptographed){
					CryptoUtils.encrypt(logfiles, logfiles);
				}
			}

			if(JFSTMerge.isCryptographed){
				CryptoUtils.decrypt(logfiles, logfiles); 
			}

			//writing source code content
			//left
			String leftcontent = context.getLeftContent();
			if(!leftcontent.isEmpty()){

				FileUtils.write(logfiles, timeStamp+","+context.getLeft().getAbsolutePath()+"\n", true);
				FileUtils.write(logfiles, leftcontent + "\n", true);
				FileUtils.write(logfiles, "!@#$%\n", true); //separator
			}

			//base
			String basecontent = context.getBaseContent();
			if(!basecontent.isEmpty()){

				FileUtils.write(logfiles, timeStamp+","+context.getBase().getAbsolutePath()+"\n", true);
				FileUtils.write(logfiles, basecontent + "\n", true);
				FileUtils.write(logfiles, "!@#$%\n", true); 
			}

			//right
			String rightcontent = context.getRightContent();
			if(!rightcontent.isEmpty()){

				FileUtils.write(logfiles, timeStamp+","+context.getRight().getAbsolutePath()+"\n", true);
				FileUtils.write(logfiles, rightcontent + "\n", true);
				FileUtils.write(logfiles, "!@#$%\n", true); 
			}

			if(JFSTMerge.isCryptographed){
				CryptoUtils.encrypt(logfiles, logfiles); 
			}
		}
		catch (CryptoException c)
		{
			String logpath   = System.getProperty("user.home")+ File.separator + ".stjfstmerge" + File.separator;
			logpath = logpath + "stjfstmerge.files";
			File log = new File(logpath);
			if (log.exists())
			{
				File log_defect = new File(logpath+"_defect"+System.currentTimeMillis());
				log.renameTo(log_defect);
			}
			if(numberOfCriptographyFixAttempts < 1)
			{
				numberOfCriptographyFixAttempts++;
				logFiles(timeStamp,context);
			}
		}
	}

	private static void initializeLogger() throws IOException, CryptoException {
		String logpath = System.getProperty("user.home")+ File.separator + ".stjfstmerge" + File.separator;
		new File(logpath).mkdirs(); //ensuring that the directories exists	
		logpath = logpath + "stjfstmerge.statistics";

		//manageLogBuffer(logpath);

		String header = "date,files,stmergeconfs,stmergeloc,unmergeconfs,unmergeloc,unmergetime,stmergetime,equalconfs\n";

		//reading the log file to see if it is not empty neither contains the header
		if(!new File(logpath).exists()){
			File statisticsLog = new File(logpath);
			FileUtils.write(statisticsLog, header, true);

			if(JFSTMerge.isCryptographed){
				CryptoUtils.encrypt(statisticsLog, statisticsLog);
			}
		}
	}

	/**
	 * When log's size reaches 4 megabytes,a new empty log is started, and the previous one is backup.
	 * @param logpath
	 * @throws CryptoException 
	 */
	private static void manageLogBuffer(String logpath) throws CryptoException {
		File log = new File(logpath);
		if(log.exists()){
			long logSizeMB = log.length() / (1024 * 1024);
			if(logSizeMB >= 4){
				File newLog = new File(logpath+System.currentTimeMillis());
				log.renameTo(newLog);
			}
		}
	}

}
