package br.ufpe.cin.mergers.textual;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.mergers.util.CSDiffRunner;
import br.ufpe.cin.mergers.util.TextualMergeStrategy;

public class CSDiff implements TextualMergeStrategy {
    private static final String CSDiffOutputFileName = "csdiff-merge";
    private static final String diff3OutputFileName = "diff3-merge";

    public String merge(String leftContent, String baseContent, String rightContent, boolean ignoreWhiteSpaces) throws TextualMergeException {
        try {
            Path leftFile = createContributionFile("left", leftContent);
            Path baseFile = createContributionFile("base", baseContent);
            Path rightFile = createContributionFile("right", rightContent);
            Path CSDiffOutputFile = createTempJavaFile(CSDiffOutputFileName);
            Path diff3OutputFile = createTempJavaFile(diff3OutputFileName);

            CSDiffRunner.run(leftFile, baseFile, rightFile, CSDiffOutputFile, diff3OutputFile);

            Files.delete(leftFile);
            Files.delete(baseFile);
            Files.delete(rightFile);
            Files.delete(CSDiffOutputFile);
            Files.delete(diff3OutputFile);

            return "";
        } catch (IOException e) {
            throw new TextualMergeException("Could not open temporary files");
        } catch (InterruptedException e) {
            throw new TextualMergeException("Error during CSDiff's execution");
        }
    }

    private Path createContributionFile(String name, String content) throws IOException {
        Path file = createTempJavaFile(name);
        Files.write(file, content.getBytes(Charset.forName(Diff3.encoding)));
        return file;
    }

    private Path createTempJavaFile(String name) throws IOException {
        return Files.createTempFile(name, ".java");
    }
}
