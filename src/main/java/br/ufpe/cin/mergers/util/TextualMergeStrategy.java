package br.ufpe.cin.mergers.util;

import br.ufpe.cin.exceptions.TextualMergeException;

public interface TextualMergeStrategy {
    public String merge(String leftContent, String baseContent, String rightContent, boolean ignoreWhiteSpaces) throws TextualMergeException;
}
