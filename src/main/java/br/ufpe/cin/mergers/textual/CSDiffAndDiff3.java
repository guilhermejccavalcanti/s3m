package br.ufpe.cin.mergers.textual;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.TextualMergeStrategy;

public class CSDiffAndDiff3 implements TextualMergeStrategy {
    public String merge(String leftContent, String baseContent, String rightContent, boolean ignoreWhiteSpaces) throws TextualMergeException {
        String mergeOutput = CSDiff.merge(leftContent, baseContent, rightContent);

        if (!thereAreConflicts(mergeOutput)) return mergeOutput;
        return Diff3.mergeTexts(leftContent, baseContent, rightContent, ignoreWhiteSpaces);
    }

    private boolean thereAreConflicts(String mergeOutput) {
        return FilesManager.extractMergeConflicts(mergeOutput).size() > 0;
    }
}
