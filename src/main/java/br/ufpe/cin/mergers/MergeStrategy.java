package br.ufpe.cin.mergers;

import br.ufpe.cin.exceptions.TextualMergeException;

public interface MergeStrategy {
    public String merge(String leftContent, String baseContent, String rightContent, boolean ignoreWhiteSpaces) throws TextualMergeException;
}
