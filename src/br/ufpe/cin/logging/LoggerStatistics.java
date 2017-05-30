package br.ufpe.cin.logging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;

import br.ufpe.cin.crypto.CryptoUtils;
import br.ufpe.cin.exceptions.CryptoException;
import br.ufpe.cin.exceptions.ExceptionUtils;
import br.ufpe.cin.exceptions.PrintException;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.Source;

public class LoggerStatistics {

	public static int recursionCounter = 0;

	public static void logContext(String msg, MergeContext context) throws PrintException{
		try{
			initializeLogger();

			//logging
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			String logpath   = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
			String logentry	 = timeStamp+","+msg+"\n";
			logpath = logpath + "jfstmerge.statistics";
			File statisticsLog = new File(logpath);

			CryptoUtils.decrypt(statisticsLog, statisticsLog);

			FileUtils.write(statisticsLog, logentry, true);

			CryptoUtils.encrypt(statisticsLog, statisticsLog);

			//logging merged files for further analysis
			logFiles(timeStamp,context);
			logSummary();

		}
		catch (CryptoException c)
		{
			String logpath   = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
			logpath = logpath + "jfstmerge.statistics";
			File log = new File(logpath);
			if (log.exists())
			{
				File log_defect = new File(logpath+"_defect"+System.currentTimeMillis());
				log.renameTo(log_defect);
			}
			if(recursionCounter < 1)
			{
				recursionCounter++;
				logContext(msg,context);
			}
		}
		catch(Exception e){
			throw new PrintException(ExceptionUtils.getCauseMessage(e));
		}
	}

	public static void logScenario(String loggermsg) throws IOException {
		String logpath = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
		new File(logpath).mkdirs(); //ensuring that the directories exists	
		logpath = logpath + "jfstmerge.statistics.scenarios";

		//reading the log file to see if it is not empty neither contains the header
		String header = "revision,ssmergeconfs,ssmergeloc,ssmergerenamingconfs,ssmergedeletionconfs,ssmergetaeconfs,ssmergenereoconfs,ssmergeinitlblocksconfs,unmergeconfs,unmergeloc,unmergetime,ssmergetime,duplicateddeclarationerrors,equalconfs\n";
		File statisticsLog = new File(logpath);
		if(!statisticsLog.exists()){
			FileUtils.write(statisticsLog, header, true);
		}

		FileUtils.write(statisticsLog, loggermsg, true);
	}

	public static void logConflicts(List<MergeConflict> conflicts, Source source) throws IOException {
		String logpath = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
		new File(logpath).mkdirs(); //ensuring that the directories exists	
		for(MergeConflict mc : conflicts){
			String origin =  ((mc.leftOriginFile != null) ? mc.leftOriginFile.getAbsolutePath() : "<empty left>") 
					+ ";" + ((mc.baseOriginFile != null) ? mc.baseOriginFile.getAbsolutePath() : "<empty base>") 
					+ ";" + ((mc.rightOriginFile != null) ? mc.rightOriginFile.getAbsolutePath() : "<empty right>");
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
				case SEMISTRUCTURED:
					f = new File(logpath + "conflicts.semistructured");
					FileUtils.write(f,(origin+'\n'+mc.body+'\n'),true);
					break;
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private static void logSummary() throws IOException{
		try{
			//retrieving statistics
			String logpath   = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
			new File(logpath).mkdirs(); //ensuring that the directories exists	
			File statistics = new File(logpath+ "jfstmerge.statistics");
			if(statistics.exists()){
				int ssmergeconfs = 0;
				int ssmergeloc = 0;
				int ssmergerenamingconfs = 0;
				int ssmergedeletionconfs = 0;
				int ssmergetaeconfs = 0;
				int ssmergenereoconfs = 0;
				int ssmergeinitlblocksconfs = 0;
				int unmergeconfs = 0;
				int unmergeloc = 0;
				long unmergetime = 0;
				long ssmergetime = 0;
				int duplicateddeclarationerrors = 0;
				int equalconfs = 0;

				CryptoUtils.decrypt(statistics, statistics);
				List<String> lines = Files.readAllLines(statistics.toPath());
				for(int i = 1; i <lines.size(); i++){
					String[] columns = lines.get(i).split(",");

					ssmergeconfs += Integer.valueOf(columns[2]);
					ssmergeloc += Integer.valueOf(columns[3]);
					ssmergerenamingconfs += Integer.valueOf(columns[4]);
					ssmergedeletionconfs += Integer.valueOf(columns[5]);
					ssmergetaeconfs += Integer.valueOf(columns[6]);
					ssmergenereoconfs += Integer.valueOf(columns[7]);
					ssmergeinitlblocksconfs += Integer.valueOf(columns[8]);
					unmergeconfs += Integer.valueOf(columns[9]);
					unmergeloc += Integer.valueOf(columns[10]);
					unmergetime += Long.parseLong(columns[11]);
					ssmergetime += Long.parseLong((columns[12]));
					duplicateddeclarationerrors += Integer.valueOf(columns[13]);
					equalconfs += Integer.valueOf(columns[14]);

				}
				CryptoUtils.encrypt(statistics, statistics);

				//summarizing retrieved statistics
				int X = lines.size()-1;
				int Y = (unmergeconfs - ssmergeconfs) + duplicateddeclarationerrors - (ssmergetaeconfs + ssmergenereoconfs + ssmergeinitlblocksconfs);Y=(Y>0)?Y:0;
				int Z = duplicateddeclarationerrors;
				int A = ssmergerenamingconfs;
				int B = unmergeconfs - equalconfs - Y;B=(B>0)?B:0;
				double M = ((double)ssmergetime / 1000000000);
				double N = ((double)unmergetime / 1000000000);

				StringBuilder summary = new StringBuilder();
				summary.append("s3m was invoked in " +X+ " JAVA files so far.\n");
				summary.append("In these files, you avoided at least " +Y+" false positives and at least "+Z+" false negatives in relation to unstructured merge.\n");
				summary.append("On the other hand, you had at most " +A+ " extra false positives and " +B+ " extra false negatives.\n");
				summary.append("s3m reported "+ssmergeconfs+" conflicts, totalizing " +ssmergeloc+ " conflicting LOC, compared to "+unmergeconfs+" conflicts and " +unmergeloc+ " conflicting LOC from unstructured merge, where " +equalconfs+ " conflicts are equal.\n");
				summary.append("Finally, s3m took " + (new DecimalFormat("#.##").format(M))+" seconds, and unstructured merge " + (new DecimalFormat("#.##").format(N)) + " seconds to merge all these files.");
				summary.append("\n\n\n");
				summary.append("LAST TIME UPDATED: " + (new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime())));

				//print summary
				File fsummary = new File(logpath+ "jfstmerge.summary");
				if(!fsummary.exists()){
					fsummary.createNewFile();
				}
				FileUtils.write(fsummary, summary.toString(),false);
			}
		}
		catch(CryptoException c)
		{
			String logpath   = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
			logpath = logpath + "jfstmerge.statistics";
			File log = new File(logpath);
			if (log.exists())
			{
				File log_defect = new File(logpath+"_defect"+System.currentTimeMillis());
				log.renameTo(log_defect);
			}
			if(recursionCounter < 1)
			{
				recursionCounter++;
				logSummary();
			}
		}
	}

	private static void logFiles(String timeStamp, MergeContext context) throws IOException {
		try{
			//initialization
			String logpath = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
			new File(logpath).mkdirs(); //ensuring that the directories exists	
			logpath = logpath + "jfstmerge.files";
			manageLogBuffer(logpath);
			File logfiles = new File(logpath);

			if(!logfiles.exists()){
				logfiles.createNewFile();

				CryptoUtils.encrypt(logfiles, logfiles);
			}
			CryptoUtils.decrypt(logfiles, logfiles);

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

			CryptoUtils.encrypt(logfiles, logfiles);
		}
		catch (CryptoException c)
		{
			String logpath   = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
			logpath = logpath + "jfstmerge.statistics";
			File log = new File(logpath);
			if (log.exists())
			{
				File log_defect = new File(logpath+"_defect"+System.currentTimeMillis());
				log.renameTo(log_defect);
			}

			if(recursionCounter < 1)
			{
				recursionCounter++;
				logFiles(timeStamp,context);
			}
		}
	}


	private static void initializeLogger() throws IOException, CryptoException {
		String logpath = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
		new File(logpath).mkdirs(); //ensuring that the directories exists	
		logpath = logpath + "jfstmerge.statistics";

		manageLogBuffer(logpath);

		String header = "date,files,ssmergeconfs,ssmergeloc,ssmergerenamingconfs,ssmergedeletionconfs,ssmergetaeconfs,ssmergenereoconfs,ssmergeinitlblocksconfs,unmergeconfs,unmergeloc,unmergetime,ssmergetime,duplicateddeclarationerrors,equalconfs\n";

		//reading the log file to see if it is not empty neither contains the header
		if(!new File(logpath).exists()){
			File statisticsLog = new File(logpath);
			FileUtils.write(statisticsLog, header, true);

			CryptoUtils.encrypt(statisticsLog, statisticsLog);
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

}
