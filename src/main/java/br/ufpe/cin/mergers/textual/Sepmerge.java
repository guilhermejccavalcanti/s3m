package br.ufpe.cin.mergers.textual;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.TextualMergeStrategy;
import com.fbmadev.sepmerge.sepmerge_module.SepMerge;

import java.io.File;
import java.io.IOException;

public class Sepmerge implements TextualMergeStrategy {
    @Override
    public String merge(String leftContent, String baseContent, String rightContent, boolean ignoreWhiteSpaces) throws TextualMergeException {
        String mergeResult = null;
        try {
            File leftFile = FilesManager.createContributionFile("left", leftContent);
            File baseFile = FilesManager.createContributionFile("base", baseContent);
            File rightFile = FilesManager.createContributionFile("right", rightContent);

            mergeResult = SepMerge.run(leftFile.getAbsolutePath(), baseFile.getAbsolutePath(), rightFile.getAbsolutePath());
        } catch (IOException e) {
            throw new TextualMergeException("Error during opening of temporary input file(s).");
        } catch (Exception e){
            throw new TextualMergeException("Error on sepmerge scripts.");
        }
        return mergeResult;
    }
}
