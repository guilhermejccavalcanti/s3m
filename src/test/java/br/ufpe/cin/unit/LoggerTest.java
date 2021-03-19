package br.ufpe.cin.unit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;

/**
 * Tests logger behavior.
 */
public class LoggerTest {

    private final Path s3mFilesDirectory = Paths.get(System.getProperty("user.home"), ".jfstmerge");

    
	@BeforeClass
	public static void setUpBeforeClass() throws UnsupportedEncodingException {
		//hidding sysout output
		@SuppressWarnings("unused")
		PrintStream originalStream = System.out;
		PrintStream hideStream    = new PrintStream(new OutputStream(){
			public void write(int b) {}
		}, true, Charset.defaultCharset().displayName());
		System.setOut(hideStream);
    }
    
    @Test
    public void testLogger_givenTheresOneMergerObject_whenTwoConsecutiveMergesOccur_shouldProduceOnlyOneLogFile() throws IOException {
        int numberOfLogFilesBeforeExecution = numberOfLogFiles();
        
        JFSTMerge merger = new JFSTMerge();

        merge(merger, "deletioninleft");
        merge(merger, "deletioninright");
        
        assertEquals(numberOfLogFilesBeforeExecution, numberOfLogFiles());
    }

    @Test
    public void testLogger_whenTwoConsecutiveMergesOccur_shouldProduceOnlyOneLogFile() throws IOException {
        int numberOfLogFilesBeforeExecution = numberOfLogFiles();

        merge(new JFSTMerge(), "deletioninleft");
        merge(new JFSTMerge(), "deletioninright");
        
        assertEquals(numberOfLogFilesBeforeExecution, numberOfLogFiles());
    }

    private int numberOfLogFiles() throws IOException {
        Stream<Path> walk = Files.walk(s3mFilesDirectory);
        int numberLogFiles = (int) walk.map(Path::getFileName)
                                       .map(Path::toString)
                                       .filter(path -> path.startsWith("jfstmerge.log") && !path.endsWith("lck"))
                                       .count();

        walk.close();
        return numberLogFiles;
    }

    private void merge(JFSTMerge merger, String directoryName) {
        Path filesPath = Paths.get("testfiles").resolve(directoryName);

        merger.mergeFiles(filesPath.resolve("left/Test.java").toFile(), 
                filesPath.resolve("base/Test.java").toFile(), 
                filesPath.resolve("right/Test.java").toFile(), 
                null);
    }
    
}