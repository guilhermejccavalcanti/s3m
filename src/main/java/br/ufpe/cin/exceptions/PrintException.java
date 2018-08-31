package br.ufpe.cin.exceptions;

/**
 * In case of errors during printing routines.
 * @author Guilherme
 *
 */
public class PrintException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9128350083472735272L;
	
	public PrintException(){
		super("Unexpected error while performing print routines.");
	}
	
	public PrintException(String message){
		super("Unexpected error while performing print routines.\n"
				+ message);
	}

}
