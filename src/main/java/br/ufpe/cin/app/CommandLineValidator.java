package br.ufpe.cin.app;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import com.beust.jcommander.ParameterException;

/**
 * Class responsible for validating command line options
 * @author Guilherme
 */
public final class CommandLineValidator {

	/**
	 * Verifies if the given command line options are valid
	 * @param merger
	 * @throws ParameterException in case of invalid command line options
	 */
	public static void validateCommandLineOptions(JFSTMerge merger) {

		List<File> files = merger.files;
		checkExistence(files);
		checkIfAreAllFilesOrAllDirectories(files);
	}

	private static void checkIfAreAllFilesOrAllDirectories(List<File> files) {
		if(!files.stream().allMatch(File::isFile) && !files.stream().allMatch(File::isDirectory)) {
			throw new ParameterException("Enter only files or only directories.");
		}
	}

	private static void checkExistence(List<File> files) {
		for(File file : files) {
			if(!file.exists())
				throw new ParameterException(file + " does not exists.");
		}
	}

}