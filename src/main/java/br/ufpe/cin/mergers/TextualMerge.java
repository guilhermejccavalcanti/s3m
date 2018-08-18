package br.ufpe.cin.mergers;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.merge.MergeAlgorithm;
import org.eclipse.jgit.merge.MergeFormatter;
import org.eclipse.jgit.merge.MergeResult;

import br.ufpe.cin.exceptions.ExceptionUtils;
import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;

/**
 * Represents unstructured, linebased, textual merge.
 * @author Guilherme
 */
public final class TextualMerge {

	/**
	 * Three-way unstructured merge of three given files.
	 * @param left
	 * @param base
	 * @param right
	 * @param ignoreWhiteSpaces to avoid false positives conflicts due to different spacings.
	 * @return string representing merge result (might be null in case of errors).
	 * @throws TextualMergeException 
	 */
	public static String merge(File left, File base, File right, boolean ignoreWhiteSpaces) throws TextualMergeException{
		/* this commented code is an alternative to call unstructured merge by command line 		
		 * String mergeCommand = ""; 
			if(System.getProperty("os.name").contains("Windows")){
				mergeCommand = "C:/KDiff3/bin/diff3.exe -m -E " + "\"" 
						+ left.getPath() + "\"" + " " + "\"" 
						+ base.getPath() + "\"" + " " + "\"" 
						+ right.getPath()+ "\"";
			} else {
				mergeCommand = "git merge-file -q -p " 
						+ left.getPath() + " " 
						+ base.getPath() + " " 
						+ right.getPath();// + " > " + fileVar1.getName() + "_output";
			}
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(mergeCommand);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			textualMergeResult = reader.lines().collect(Collectors.joining("\n"));*/

		String textualMergeResult = null;
		//we treat invalid files as empty files 
		String leftContent = ((left == null || !left.exists()) ? "" : FilesManager.readFileContent(left));
		String baseContent = ((base == null || !base.exists()) ? "" : FilesManager.readFileContent(base));
		String rightContent= ((right== null || !right.exists())? "" : FilesManager.readFileContent(right));
		textualMergeResult = merge(leftContent,baseContent,rightContent,ignoreWhiteSpaces);
		return textualMergeResult;
	}

	/**
	 * Merges textually three strings.
	 * @param leftContent
	 * @param baseContent
	 * @param rightContent
	 * @param ignoreWhiteSpaces to avoid false positives conflicts due to different spacings.
	 * @return merged string.
	 * @throws TextualMergeException 
	 */
	public static String merge(String leftContent, String baseContent, String rightContent, boolean ignoreWhiteSpaces) throws TextualMergeException{
		String textualMergeResult = null;
		try{
			RawTextComparator textComparator = ((ignoreWhiteSpaces) ? RawTextComparator.WS_IGNORE_ALL : RawTextComparator.DEFAULT);
			@SuppressWarnings("rawtypes") MergeResult mergeCommand = new MergeAlgorithm().merge(textComparator,
					new RawText(Constants.encode(baseContent)), 
					new RawText(Constants.encode(leftContent)), 
					new RawText(Constants.encode(rightContent))
					);		
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			(new MergeFormatter()).formatMerge(output, mergeCommand, "BASE", "MINE", "YOURS", Constants.CHARACTER_ENCODING);
			textualMergeResult = new String(output.toByteArray(), Constants.CHARACTER_ENCODING);
		}catch(Exception e){
			throw new TextualMergeException(ExceptionUtils.getCauseMessage(e), leftContent,baseContent,rightContent);
		}
		return textualMergeResult;
	}
}
