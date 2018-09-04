package test.java.br.ufpe.cin.performance;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;

public class CryptoPerformanceTest {
	
	private static final int NUM_ITERATIONS = 5;
	private static final double ACCEPTED_MARGIN = 0.3;
	
	public static File getLogPath(String file) {
		return new File(System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator + file);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//hidding sysout output
		@SuppressWarnings("unused")
		PrintStream originalStream = System.out;
		PrintStream hideStream    = new PrintStream(new OutputStream(){
			public void write(int b) {}
		});
		System.setOut(hideStream);
		
		File logPath   = getLogPath("jfstmerge.statistics");
		File logCopyPath = getLogPath("jfstmerge.statistics2");
		Files.move(logPath.toPath(), logCopyPath.toPath(), REPLACE_EXISTING);
    }
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		File logPath   = getLogPath("jfstmerge.statistics");
		File logCopyPath = getLogPath("jfstmerge.statistics2");
		Files.move(logCopyPath.toPath(), logPath.toPath(), REPLACE_EXISTING);
	}
	
	public long computeMeanTimeOfMergeProcedure() {
		long totalTime = 0;
		for(int i = 0; i < NUM_ITERATIONS; i++) {
			long initialTime = System.currentTimeMillis();
					
			new JFSTMerge().mergeFiles(
					new File("testfiles/cryptoperformance/test"+i+"/left.java"), 
					new File("testfiles/cryptoperformance/test"+i+"/base.java"), 
					new File("testfiles/cryptoperformance/test"+i+"/right.java"),
					null);
			
			long finalTime = System.currentTimeMillis();
			
			totalTime += finalTime - initialTime;
		}
		
		return totalTime / NUM_ITERATIONS;
	}
	
	@Test
	public void testPerformance() {
		
		JFSTMerge.isCryptographed = false;
		long noCryptoMean = computeMeanTimeOfMergeProcedure();	
	
		JFSTMerge.isCryptographed = true;
		long cryptoMean = computeMeanTimeOfMergeProcedure();
		
		assertTrue((double) (cryptoMean - noCryptoMean) <= ACCEPTED_MARGIN * noCryptoMean);
		
	}
	
}
