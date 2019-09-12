package br.ufpe.cin.mergers.spacing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;

/**
 * Tests line spacing preservation.
 */
public class LineSpacingTest {

    private JFSTMerge merger = new JFSTMerge();

    @BeforeClass
	public static void hideSystemOutput() throws UnsupportedEncodingException {
		//hidding sysout output
		@SuppressWarnings("unused")
		PrintStream originalStream = System.out;
		PrintStream hideStream    = new PrintStream(new OutputStream(){
			public void write(int b) {}
		}, true, Charset.defaultCharset().displayName());
		System.setOut(hideStream);
    }

    @Test
    public void testSpacingPreservation_whenLeftAddsAnImportStatementPost2Lines_andRightAddsADifferentImportStatementPost5Lines_shouldPreserveBothSpacing() {
        String mergeResult = merge("imports");

        assertEquals(2, numLineBreaksBetweenDeclarations(mergeResult, "package mypackage;", "import importstatement.A;"));
        assertEquals(5, numLineBreaksBetweenDeclarations(mergeResult, "import importstatement.A;", "import importstatement2.*;"));
    }

    @Test
    public void testSpacingPreservation_whenLeftAddsAnAttributePost2Lines_andRightAddsADifferentAttributePost4Lines_shouldPreserveBothSpacing() {
        String mergeResult = merge("addsattributeaddsdiffattribute");
        
        assertEquals(2, numLineBreaksBetweenDeclarations(mergeResult, "{", "int a;"));
        assertEquals(4, numLineBreaksBetweenDeclarations(mergeResult, "int a;", "int c;"));
    }

    @Test
    public void testSpacingPreservation_whenLeftRemovesAnAttributePost2Lines_andRightAddsADifferentAttributePost3LinesThePreviousAttribute_shouldPreserveAdditionSpacing() {
        String mergeResult = merge("removesattributeaddsattribute");
        
        assertEquals(3, numLineBreaksBetweenDeclarations(mergeResult, "{", "int c;"));
    }

    @Test
    public void testSpacingPreservation_givenMergeIgnoresWhitespaces_whenLeftPushesAnAttribute2Lines_andRightPullsTheSameAttribute1Line_shouldPreserveLeftSpacing() {
        String mergeResult = merge("pushattributepullattribute");
        
        assertEquals(4, numLineBreaksBetweenDeclarations(mergeResult, "{", "int a;"));
    }

    @Test
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    public void testSpacingPreservation_givenMergeDoesNotIgnoreWhitespaces_whenLeftPushesAnAttribute2Lines_andRightPullsTheSameAttribute1Line_shouldReportConflict() {
        JFSTMerge.isWhitespaceIgnored = false;
        String mergeResult = merge("pushattributepullattribute");
        JFSTMerge.isWhitespaceIgnored = true;
        
        assertThat(StringUtils.deleteWhitespace(mergeResult), containsString("<<<<<<<MINE=======>>>>>>>YOURSinta;"));
    }

    @Test
    public void testSpacingPreservation_givenMergeIgnoresWhitespaces_whenLeftAddsAnAttributePost2Lines_andRightAddsTheSameAttributePost3Lines_shouldPreserveRightSpacing() {
        String mergeResult = merge("addsattributeaddssameattribute");
        
        assertEquals(3, numLineBreaksBetweenDeclarations(mergeResult, "{", "int a;"));
    }

    @Test
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    public void testSpacingPreservation_givenMergeDoesNotIgnoreWhitespaces_whenLeftAddsAnAttributePost2Lines_andRightAddsTheSameAttributePost3Lines_shouldReportConflict() {
        JFSTMerge.isWhitespaceIgnored = false;
        String mergeResult = merge("addsattributeaddssameattribute");
        JFSTMerge.isWhitespaceIgnored = true;
        
        assertThat(StringUtils.deleteWhitespace(mergeResult), containsString("<<<<<<<MINE=======>>>>>>>YOURSinta;"));
    }

    @Test
    public void testSpacingPreservation_whenLeftAddsACommentJustAboveAnAttributePost2Lines_andRightDeletesTheAttribute_AndAddsAnAttributePost1Line_shouldPreserveBothSpacing() {
        String mergeResult = merge("addcommentremove-addattribute");
        
        assertEquals(1, numLineBreaksBetweenDeclarations(mergeResult, "// Comment.", "int c;"));
        assertEquals(2, numLineBreaksBetweenDeclarations(mergeResult, "{", "// Comment."));
    }
    
    private int numLineBreaksBetweenDeclarations(String mergeResult, String initialDeclaration, String finalDeclaration) {
        String substringBetween = StringUtils.substringBetween(mergeResult, initialDeclaration, finalDeclaration);
        return StringUtils.countMatches(substringBetween, "\n");
    }

    private String merge(String testFilesPath) {
        Path left = Paths.get("testfiles/spacing/linespacing").resolve(testFilesPath).resolve("left/Test.java");
        Path base = Paths.get("testfiles/spacing/linespacing").resolve(testFilesPath).resolve("base/Test.java");
        Path right = Paths.get("testfiles/spacing/linespacing").resolve(testFilesPath).resolve("right/Test.java");

        return merger.mergeFiles(left.toFile(), base.toFile(), right.toFile(), null).semistructuredOutput;
    }
    
}