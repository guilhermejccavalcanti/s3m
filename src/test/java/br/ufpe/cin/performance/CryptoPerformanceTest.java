package br.ufpe.cin.performance;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.*;

import br.ufpe.cin.crypto.CryptoUtils;
import br.ufpe.cin.exceptions.CryptoException;
import org.junit.*;

import br.ufpe.cin.app.JFSTMerge;

public class CryptoPerformanceTest {

	private static final int NUM_TESTS = 5;
	private static final int NUM_ITERATIONS = 5;
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
    }

    @Before
	public void decryptFiles() {
		try {
			decrypt("jfstmerge.statistics");
			decrypt("jfstmerge.files");
		} catch (CryptoException e) {
			// The files are already decrypted.
			System.out.println("Cryptography performance test: the files are already decrypted.");
		}
	}

	@After
	public void encryptFiles() throws CryptoException {
		encrypt("jfstmerge.statistics");
		encrypt("jfstmerge.files");
	}
		
	@Test
	public void testPerformance() throws CryptoException {

		JFSTMerge.isCryptographed = false;
		long noCryptoMean = computeMeanTimeOfMergeProcedure(0);

		JFSTMerge.isCryptographed = true;
		long cryptoMean = computeMeanTimeOfMergeProcedure(0);

		assertTrue((double) (cryptoMean - noCryptoMean) <= ACCEPTED_MARGIN * noCryptoMean);

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

	private static void decrypt(String fileName) throws CryptoException {
		String logPath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator + fileName;

		File file = new File(logPath);
		CryptoUtils.decrypt(file, file);
	}

	private static void encrypt(String fileName) throws CryptoException {
		String logPath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator + fileName;

		File file = new File(logPath);
		CryptoUtils.encrypt(file, file);
	}
	
}
