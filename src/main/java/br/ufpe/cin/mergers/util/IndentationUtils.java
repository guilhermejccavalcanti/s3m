package br.ufpe.cin.mergers.util;

import org.apache.commons.lang3.StringUtils;

import de.ovgu.cide.fstgen.ast.FSTTerminal;

public final class IndentationUtils {

    /**
     * Indent the first line of FSTTerminal's content.
     * 
     * @param terminal
     * @return indented content
     */
    public static String indentFirstLine(FSTTerminal terminal) {
        if (terminal == null)
            return "";

        String content = terminal.getBody();
        String prefix = terminal.getSpecialTokenPrefix();
        String indentation = getPostIndentation(prefix);

        return indentation + content;
    }

    /**
     * Retrieves and returns indentation after prefix's content. For example, if the
     * prefix is "// Comment\n" followed by 4 spaces, it returns the last 4 spaces.
     * 
     * @param prefix
     * @return indentation after prefix's content, if exists
     */
    public static String getPostIndentation(String prefix) {
        StringBuilder indentation = new StringBuilder();
        for (int i = prefix.length() - 1; i >= 0; i--) {
            char character = prefix.charAt(i);
            if (character == ' ' || character == '\t')
                indentation.append(character);
            else
                break;
        }
        return indentation.toString();
    }

    private static int numPostLineBreaks(String prefix) {
        String content = prefix.trim();
        if (content.isEmpty()) {
            return 0;
        }

        String spacing = StringUtils.substringAfterLast(prefix, content);
        return StringUtils.countMatches(spacing, System.lineSeparator());
    }

    /**
     * Removes post indentation of a prefix content.
     * 
     * @param prefix
     * @return prefix without its last spaces
     */
    public static String removePostIndentation(String prefix) {
        String indentation = getPostIndentation(prefix);
        return StringUtils.substringBeforeLast(prefix, indentation);
    }

    /**
     * Removes post line breaks of a prefix content.
     * 
     * @param prefix
     * @return prefix without its last line breaks
     */
    public static String removePostLineBreaks(String prefix) {
        int numLineBreaks = numPostLineBreaks(prefix);

        String content = prefix.trim();
        if(content.isEmpty()) {
            return "";
        }

        String postLineBreaksAndIndentation = StringUtils.substringAfterLast(prefix, content);
        String previousSubstring = StringUtils.substringBeforeLast(prefix, postLineBreaksAndIndentation);

        String postIndentation = postLineBreaksAndIndentation;
        for(int i = 0; i < numLineBreaks; i++) {
            postIndentation = StringUtils.replace(postIndentation, System.lineSeparator(), "");
        }

        return previousSubstring + postIndentation;
    }

    /**
     * Removes both post indentation and post line breaks from a prefix content.
     * 
     * @param prefix
     * @return prefix without its post fields
     */
    public static String removePostIndentationAndLineBreaks(String prefix) {
        return removePostLineBreaks(removePostIndentation(prefix));
    }

}