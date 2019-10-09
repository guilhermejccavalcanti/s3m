package br.ufpe.cin.performance;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
public class FileEncryptionPerformanceTest {

	private JFSTMerge merger = new JFSTMerge();

	private final int NUM_ITERATIONS = 5;
	private final double ACCEPTED_MARGIN = 1000; // milliseconds

	private String[] testFilesPath = new String[] {
		"testfiles/deletioninnerinleft",
		"testfiles/duplicationsconflicting",
		"testfiles/initlblocksnobase",
		"testfiles/nereomethodfield",
		"testfiles/renaminginright"
	};

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
	public void testFileEncryptionPerformance() {

		merger.isCryptographyEnabled(false);
		long noCryptoMean = computeMeanTimeOfMergeProcedure();

		merger.isCryptographyEnabled(true);
		long cryptoMean = computeMeanTimeOfMergeProcedure();

		assertThat((double) (cryptoMean - noCryptoMean)).isLessThanOrEqualTo(ACCEPTED_MARGIN);
	}

	private long computeMeanTimeOfMergeProcedure() {
		long initialTime, finalTime, meanTime = 0, totalTime = 0;

		for (int i = 0; i < testFilesPath.length; i++) {
			for(int j = 0; j < NUM_ITERATIONS; j++) {

				initialTime = System.currentTimeMillis();
				merger.mergeFiles(
						new File(testFilesPath[i] + "/left/Test.java"),
						new File(testFilesPath[i] + "/base/Test.java"),
						new File(testFilesPath[i] + "/right/Test.java"),
						null);
				finalTime = System.currentTimeMillis();
	
				totalTime += finalTime - initialTime;
			}

			meanTime += totalTime / (NUM_ITERATIONS * testFilesPath.length);
		}
		
		return meanTime;
	}
}