package br.ufpe.cin.mergers.spacing;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
 * Tests indentation preservation.
 */
public class IndentationTest {

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
    public void testIndentationPreservation_whenLeftAddsAnAttributeIndented4Spaces_andRightAddsADifferentAttributeIndented8Spaces_shouldPreserveBothIndentation() {
        String mergeResult = merge("addsattributeaddsdiffattribute");

        assertEquals(4, numIndentationWhitespaces(mergeResult, "int a;"));
        assertEquals(8, numIndentationWhitespaces(mergeResult, "int c;"));
    }

    @Test
    public void testIndentationPreservation_givenMergeIgnoresWhitespaces_whenLeftAddsAnAttributeIndented4Spaces_andRightAddsTheSameAttributeIndented8Spaces_shouldPreserveRightIndentation() {
        String mergeResult = merge("addsattributeaddssameattribute");

        assertEquals(8, numIndentationWhitespaces(mergeResult, "int a;"));
    }

    @Test
    public void testIndentationPreservation_givenMergeDoesNotIgnoreWhitespaces_whenLeftAddsAnAttributeIndented4Spaces_andRightAddsTheSameAttributeIndented8Spaces_shouldReportConflict() {
        JFSTMerge.isWhitespaceIgnored = false;
        String mergeResult = merge("addsattributeaddssameattribute");
        JFSTMerge.isWhitespaceIgnored = true;

        assertThat(StringUtils.deleteWhitespace(mergeResult), containsString("<<<<<<<MINE=======>>>>>>>YOURSinta;"));
    }

    @Test
    public void testIndentationPreservation_whenLeftRemovesAnAttributeIndented4Spaces_andRightAddsADifferentAttributeIndented8SpacesPost1Line_shouldPreserveTheAddedAttributeIndentation() {
        String mergeResult = merge("removesattributeaddsattribute");

        assertEquals(8, numIndentationWhitespaces(mergeResult, "int b;"));
    }

    @Test
    public void testIndentationPreservation_givenMergeIgnoresWhitespaces_whenLeftPushesAnAttributeIndented4Spaces8Spaces_andRightPushesTheSameAttribute4Spaces_shouldPreserveLeftIndentation() {
        String mergeResult = merge("pushesattributepushesattribute");

        assertEquals(12, numIndentationWhitespaces(mergeResult, "int a;"));
    }

    @Test
    public void testIndentationPreservation_givenMergeDoesNotIgnoreWhitespaces_whenLeftPushesAnAttributeIndented4Spaces8Spaces_andRightPushesTheSameAttribute4Spaces_shouldReportConflict() {
        JFSTMerge.isWhitespaceIgnored = false;
        String mergeResult = merge("pushesattributepushesattribute");
        JFSTMerge.isWhitespaceIgnored = true;

        assertThat(StringUtils.deleteWhitespace(mergeResult), containsString("<<<<<<<MINE=======>>>>>>>YOURSinta;"));
    }

    @Test
    public void testIndentationPreservation_givenMergeIgnoresWhitespaces_whenLeftPushesAMethodIndented4Spaces4Spaces_andRightPushesTheSameMethod8Spaces_shouldPreserveRightIndentation() {
        String mergeResult = merge("pushesmethodpushesmethod");

        assertEquals(12, numIndentationWhitespaces(mergeResult, "void m() {")); 
        assertEquals(4, numIndentationWhitespaces(mergeResult, "}")); // the end is messed up by textual merge.
    }

    @Test
    public void testIndentationPreservation_givenMergeDoesNotIgnoreWhitespaces_whenLeftPushesAMethodIndented4Spaces4Spaces_andRightPushesTheSameMethod8Spaces_shouldReportConflict() {
        JFSTMerge.isWhitespaceIgnored = false;
        String mergeResult = merge("pushesmethodpushesmethod");
        JFSTMerge.isWhitespaceIgnored = true;

        assertThat(StringUtils.deleteWhitespace(mergeResult), containsString("<<<<<<<MINE=======>>>>>>>YOURSvoidm(){"));
        assertThat(StringUtils.deleteWhitespace(mergeResult), containsString("<<<<<<<MINE}=======}>>>>>>>YOURS"));
    }

    @Test
    public void testIndentationPreservation_givenMergeIgnoresWhitespaces_whenLeftChangesFrom4SpacesTo1Tab_shouldPreserveRightIndentation() {
        String mergeResult = merge("spacetotab");

        assertEquals(4, numIndentationWhitespaces(mergeResult, "void m() {")); 
    }

    @Test
    public void testIndentationPreservation_givenMergeDoesNotIgnoreWhitespaces_whenLeftChangesFrom4SpacesTo1Tab_shouldReportConflict() {
        JFSTMerge.isWhitespaceIgnored = false;
        String mergeResult = merge("spacetotab");
        JFSTMerge.isWhitespaceIgnored = true;

        assertThat(StringUtils.deleteWhitespace(mergeResult), containsString("<<<<<<<MINE}=======}>>>>>>>YOURS"));
    }

    private int numIndentationWhitespaces(String mergeResult, String declaration) {
        String declarationLine = findDeclarationLine(mergeResult, declaration);
        String indentation = StringUtils.substringBefore(declarationLine, declaration);
        return StringUtils.countMatches(indentation, ' ') + StringUtils.countMatches(indentation, '\t');
    }

    private String findDeclarationLine(String mergeResult, String declaration) {
        for (String line : mergeResult.split("[\r\n]")) {
            if(line.contains(declaration)) {
                return line;
            }
        }
        return "";
    }

    private String merge(String testFilesPath) {
        Path left = Paths.get("testfiles/spacing/indentation").resolve(testFilesPath).resolve("left/Test.java");
        Path base = Paths.get("testfiles/spacing/indentation").resolve(testFilesPath).resolve("base/Test.java");
        Path right = Paths.get("testfiles/spacing/indentation").resolve(testFilesPath).resolve("right/Test.java");

        return merger.mergeFiles(left.toFile(), base.toFile(), right.toFile(), null).semistructuredOutput;
    }
    
}