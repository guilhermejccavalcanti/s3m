package br.ufpe.cin.exceptions.st;

public class CryptoException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3244213124898864135L;

	public CryptoException() {
    }
 
    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
