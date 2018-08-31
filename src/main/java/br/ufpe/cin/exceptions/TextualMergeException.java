package br.ufpe.cin.exceptions;


/**
 * In case of errors when performing unstructured/textual merge. 
 * @author Guilherme
 */
public class TextualMergeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8945603711346067898L;

	public TextualMergeException(){
		super("Unexpected error while performing unstructured/textual merge.");
	}
	
	public TextualMergeException(String message){
		super("Unexpected error while performing unstructured/textual merge:/n"
				+ message);
	}

	public TextualMergeException(String incomingMessage, String leftContent,String baseContent, String rightContent) {
		super(detailErrorMessage(incomingMessage, leftContent, baseContent, rightContent));
	}
	
	private static String detailErrorMessage(String incomingMessage, String leftContent,String baseContent, String rightContent) {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("Unexpected error while performing unstructured/textual merge: ");
		messageBuilder.append(incomingMessage);
		messageBuilder.append("\nLEFT CONTENT:\n" + ((leftContent !=null && !leftContent.isEmpty())?leftContent:"<empty left>"));
		messageBuilder.append("\nBASE CONTENT:\n" + ((baseContent !=null && !baseContent.isEmpty() )?baseContent:"<empty base>"));
		messageBuilder.append("\nRIGHT CONTENT:\n"+ ((rightContent!=null && !rightContent.isEmpty())?rightContent:"<empty right>"));
		messageBuilder.append("\nTerminates execution.");
		return messageBuilder.toString();
	}
}
