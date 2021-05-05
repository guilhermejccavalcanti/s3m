package br.ufpe.cin.mergers.textual;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.mergers.util.CSDiffRunner;
import br.ufpe.cin.mergers.util.CSDiffScript;
import br.ufpe.cin.mergers.util.TextualMergeStrategy;

public class CSDiff implements TextualMergeStrategy {
    private static final CSDiffScript script = CSDiffScript.CSDiff;

    public String merge(String leftContent, String baseContent, String rightContent, boolean ignoreWhiteSpaces) throws TextualMergeException {
        return CSDiffRunner.runCSDiff(script, leftContent, baseContent, rightContent);
    }
}
