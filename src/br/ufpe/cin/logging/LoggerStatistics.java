package br.ufpe.cin.logging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
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
				String logpath   = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;

				logpath = logpath + "jfstmerge.statistics";
				File file = new File(logpath);
				CryptoUtils.decrypt(file, file);

				logpath = logpath + "jfstmerge.files";
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
			String logpath   = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
			String logentry	 = timeStamp+","+msg+"\n";
			logpath = logpath + "jfstmerge.statistics";
			File statisticsLog = new File(logpath);

			if(JFSTMerge.isCryptographed){
				CryptoUtils.decrypt(statisticsLog, statisticsLog);
			}

			FileUtils.write(statisticsLog, logentry, true);

			if(JFSTMerge.logFiles){
				//logging merged files for further analysis
				logFiles(timeStamp,context);
			}
			
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
		String logpath = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
		new File(logpath).mkdirs(); //ensuring that the directories exists	
		logpath = logpath + "jfstmerge.statistics.scenarios";

		//reading the log file to see if it is not empty neither contains the header
		String header = "revision,ssmergeconfs,ssmergeloc,ssmergerenamingconfs,ssmergedeletionconfs,ssmergetaeconfs,ssmergenereoconfs,"
				+ "ssmergeinitlblocksconfs,ssmergeacidentalconfs,unmergeconfs,unmergeloc,unmergetime,ssmergetime,unmergeduplicateddeclarationerrors,"
				+ "unmergeorderingconfs,equalconfs\n";
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
				int ssmergeacidentalconfs = 0;
				int unmergeconfs = 0;
				int unmergeloc = 0;
				long unmergetime = 0;
				long ssmergetime = 0;
				int duplicateddeclarationerrors = 0;
				int unmergeorderingconfs = 0;
				int equalconfs = 0;

				List<String> lines = Files.readAllLines(statistics.toPath());
				for(int i = 1; i <lines.size(); i++){
					String[] columns = lines.get(i).split(",");

					ssmergeconfs += Integer.valueOf(columns[2]);
					ssmergeloc 	 += Integer.valueOf(columns[3]);
					ssmergerenamingconfs += Integer.valueOf(columns[4]);
					ssmergedeletionconfs += Integer.valueOf(columns[5]);
					ssmergetaeconfs   += Integer.valueOf(columns[6]);
					ssmergenereoconfs += Integer.valueOf(columns[7]);
					ssmergeinitlblocksconfs += Integer.valueOf(columns[8]);
					ssmergeacidentalconfs 	+= Integer.valueOf(columns[9]);
					unmergeconfs += Integer.valueOf(columns[10]);
					unmergeloc 	 += Integer.valueOf(columns[11]);
					unmergetime  += Long.parseLong(columns[12]);
					ssmergetime  += Long.parseLong((columns[13]));
					duplicateddeclarationerrors += Integer.valueOf(columns[14]);
					unmergeorderingconfs += Integer.valueOf(columns[15]);
					equalconfs 	 += Integer.valueOf(columns[16]);
				}

				if(JFSTMerge.isCryptographed){
					CryptoUtils.encrypt(statistics, statistics);
				}

				//summarizing retrieved statistics
				int JAVA_FILES = lines.size() -1;
				//int FP_UN = (unmergeconfs - ssmergeconfs) + duplicateddeclarationerrors - (ssmergetaeconfs + ssmergenereoconfs + ssmergeinitlblocksconfs);FP_UN=(FP_UN>0)?FP_UN:0;
				int FP_UN = unmergeorderingconfs;
				int FN_UN = duplicateddeclarationerrors;
				int FP_SS = ssmergerenamingconfs; //actually they are common renaming conflicts, not additional false positives
				int FN_SS = (ssmergetaeconfs + ssmergenereoconfs + ssmergeinitlblocksconfs) + ssmergeacidentalconfs;
				double M = ((double)ssmergetime / 1000000000);
				double N = ((double)unmergetime / 1000000000);

				StringBuilder summary = fillSummaryMsg(ssmergeconfs, ssmergeloc,
						unmergeconfs, unmergeloc, equalconfs, JAVA_FILES, FP_UN,
						FN_UN, FP_SS, FN_SS, M, N);

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
			if(numberOfCriptographyFixAttempts < 1)
			{
				numberOfCriptographyFixAttempts++;
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
			String logpath   = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
			logpath = logpath + "jfstmerge.files";
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
		String logpath = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator;
		new File(logpath).mkdirs(); //ensuring that the directories exists	
		logpath = logpath + "jfstmerge.statistics";

		//manageLogBuffer(logpath);

		String header = "date,files,ssmergeconfs,ssmergeloc,ssmergerenamingconfs,ssmergedeletionconfs,ssmergetaeconfs,ssmergenereoconfs,"
				+ "ssmergeinitlblocksconfs,ssmergeacidentalconfs,unmergeconfs,unmergeloc,unmergetime,ssmergetime,unmergeduplicateddeclarationerrors,"
				+ "unmergeorderingconfs,equalconfs\n";

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

	private static StringBuilder fillSummaryMsg(int ssmergeconfs,
			int ssmergeloc, int unmergeconfs, int unmergeloc, int equalconfs,
			int JAVA_FILES, int FP_UN, int FN_UN, int FP_SS, int FN_SS,
			double M, double N) {
		StringBuilder summary = new StringBuilder();
		summary.append("s3m was invoked in " +JAVA_FILES+ " JAVA files so far.\n");

		if(FP_UN > 0 && FN_UN > 0){
			summary.append("In these files, you avoided at least " +FP_UN+ " false positive(s),");
			summary.append(" and at least "+FN_UN+" false negative(s) in relation to unstructured merge.\n");
		} else if(FP_UN == 0 && FN_UN == 0){
			summary.append("In these files, s3m did not find any occurrence of unstructured merge false positives and false negatives.\n");
		} else if(FP_UN > 0 && FN_UN == 0){
			summary.append("In these files, you avoided at least " +FP_UN+" false positive(s), and s3m did not find any occurrence of unstructured merge false negatives.\n");
		} else if(FP_UN == 0 && FN_UN > 0){
			summary.append("In these files, s3m did not find any occurrence of unstructured merge false positives, but you avoided at least "+FN_UN+" false negative(s) in relation to unstructured merge.\n");
		}

		summary.append("Conversely,");
		if(FN_SS == 0) {
			summary.append(" you had no extra false positives, nor potential extra false negatives.");
		} else if(FN_SS > 0) {
			summary.append(" you had no extra false positives, but you had at most "+FN_SS+" potential extra false negative(s).");
		}

		summary.append("\n\ns3m reported "+ssmergeconfs+" conflicts, totaling " +ssmergeloc+ " conflicting LOC,");
		summary.append(" compared to "+unmergeconfs+" conflicts and " +unmergeloc+ " conflicting LOC from unstructured merge.");
		/*		if(equalconfs >0){
			summary.append("\nWith " +equalconfs+ " similar conflict(s) between the tools.");
		}*/

		summary.append("\n\nAltogether, ");
		if(ssmergeconfs != unmergeconfs){
			if(ssmergeconfs < unmergeconfs){
				summary.append("these numbers represent a reduction of " + String.format("%.2f",((double)((unmergeconfs - ssmergeconfs)/(double)unmergeconfs))*100) +"% in the number of conflicts by s3m.\n");
			} else if(ssmergeconfs > unmergeconfs){
				summary.append("these numbers represent no reduction of conflicts by s3m.\n");
			}
		} else {
			summary.append("these numbers represent no difference in terms of number of reported conflicts.\n");
		}

		if(FP_UN > 0){
			summary.append("A reduction of " + String.format("%.2f",((double)((FP_UN - 0)/(double)FP_UN))*100,2) +"% in the number of false positives.\n");
		} else {
			summary.append("No difference in terms of false positives.\n");
		}

		if(FN_UN != FN_SS){
			if(FN_UN > FN_SS) {
				summary.append("And a reduction of " + String.format("%.2f",((double)((FN_UN - FN_SS)/(double)FN_UN))*100,2) +"% in the number of false negatives.");
			} else if(FN_SS > FN_UN){
				summary.append("And no reduction of false negatives.");
			}
		}  else {
			summary.append("And no difference in terms of false negatives.");
		}


		summary.append("\n\nFinally, s3m took " + (new DecimalFormat("#.##").format(M))+" seconds, and unstructured merge " + (new DecimalFormat("#.##").format(N)) + " seconds to merge all these files.");

		summary.append("\n\n\n");
		summary.append("LAST TIME UPDATED: " + (new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime())));
		return summary;
	}

}
