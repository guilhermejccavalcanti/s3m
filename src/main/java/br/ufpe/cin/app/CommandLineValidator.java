package br.ufpe.cin.app;

import java.io.File;

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
		if(merger.filespath.isEmpty() && merger.directoriespath.isEmpty()){ //one merge option must be given
			throw new ParameterException("Please inform one merge option (-f or -d).");

		} else if(!merger.filespath.isEmpty() && !merger.directoriespath.isEmpty()){ //merge options are mutually exclusive
			throw new ParameterException("Choose only one of the merge options (files -f or directories -d), not both");

		} else if(!merger.filespath.isEmpty()) {
			if(merger.filespath.size()!=3){ //three files path must be given
				throw new ParameterException("Invalid number of files. Inform 3.");
			} else { //files must be valid
				for(String path : merger.filespath){
					File f = new File(path);
					if(!f.isFile()){
						throw new ParameterException(path + " is not a valid file path.");
					}
					if(!f.exists()){
						throw new ParameterException(path + " does not exists.");
					}
				}
			}

		} else if(!merger.directoriespath.isEmpty()){
			if(merger.directoriespath.size()!=3){ //three directories path must be given
				throw new ParameterException("Invalid number of directories. Inform 3.");
			} else { //directories must be valid
				for(String path : merger.directoriespath){
					File d = new File(path);
					if(!d.isDirectory()){
						throw new ParameterException(path + " is not a valid directory path.");
					}
					if(!d.exists()){
						throw new ParameterException(path + " does not exists.");
					}
				}
			}
		}		
	}
}
