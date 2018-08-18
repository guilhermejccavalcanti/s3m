package br.ufpe.cin.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionUtils {

	/**
	 * Returns a string representation of the cause of a given exception.
	 */
	public static String getCauseMessage(Throwable exception){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		return sw.toString();
	}
}
