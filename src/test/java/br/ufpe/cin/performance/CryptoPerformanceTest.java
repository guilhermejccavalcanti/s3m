package br.ufpe.cin.performance;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;

public class CryptoPerformanceTest {
	
	private static final int NUM_ITERATIONS = 5;
	private static final int NUM_TESTS = 3;
	private static final double ACCEPTED_MARGIN = 0.3;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//hidding sysout output
		@SuppressWarnings("unused")
		PrintStream originalStream = System.out;
		PrintStream hideStream    = new PrintStream(new OutputStream(){
			public void write(int b) {}
		});
		System.setOut(hideStream);
		
		renameFile("jfstmerge.statistics", "jfstmerge.statistics2");
    }
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		renameFile("jfstmerge.statistics2", "jfstmerge.statistics");
	}
		
	@Test
	public void testPerformance() throws Exception {
			
		for(int i = 0; i < NUM_TESTS; i++) {
			
			JFSTMerge.isCryptographed = false;
			long noCryptoMean = computeMeanTimeOfMergeProcedure(i);	
		
			JFSTMerge.isCryptographed = true;
			long cryptoMean = computeMeanTimeOfMergeProcedure(i);
			
			assertThat((double) (cryptoMean - noCryptoMean)).isLessThanOrEqualTo(ACCEPTED_MARGIN * noCryptoMean);
			
			// Renaming statistics file to not block next non-cryptographed merge, simplifying operations.
			renameFile("jfstmerge.statistics", "jfstmerge.statistics2");
		}
		
	}

	private long computeMeanTimeOfMergeProcedure(int testCase) {
		long totalTime = 0;
		for(int i = 0; i < NUM_ITERATIONS; i++) {
			long initialTime = System.currentTimeMillis();

			new JFSTMerge().mergeFiles(
					new File("testfiles/cryptoperformance/test" + testCase + "/left.java"),
					new File("testfiles/cryptoperformance/test" + testCase + "/base.java"),
					new File("testfiles/cryptoperformance/test" + testCase + "/right.java"),
					null);

			long finalTime = System.currentTimeMillis();
			totalTime += finalTime - initialTime;

		}

		return totalTime / NUM_ITERATIONS;
	}

    private static File getLogPath(String file) {
        return new File(System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator + file);
    }

    private static void renameFile(String file1, String file2) throws Exception {
        File logPath   = getLogPath(file1);
        File logCopyPath = getLogPath(file2);
        Files.move(logPath.toPath(), logCopyPath.toPath(), REPLACE_EXISTING);
    }
	
}
