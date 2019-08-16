package br.ufpe.cin.performance;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import br.ufpe.cin.crypto.CryptoUtils;
import br.ufpe.cin.exceptions.CryptoException;
import org.junit.*;

import br.ufpe.cin.app.JFSTMerge;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
public class CryptoPerformanceTest {

	private static final int NUM_ITERATIONS = 6;
	private static final double ACCEPTED_MARGIN = 0.6;

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

    @Before
	public void decryptFiles() {
		try {
			decrypt("jfstmerge.statistics");
			decrypt("jfstmerge.files");
		} catch (CryptoException e) {
			// The files are already decrypted.
			System.out.println("Cryptography performance test: the files are already decrypted. Proceeding normally.");
		}
	}
		
	@Test
	public void testPerformanceWithDeletion() {

		JFSTMerge.isCryptographed = false;
		long noCryptoMean = computeMeanTimeOfMergeProcedure("testfiles/deletioninnerinleft");

		JFSTMerge.isCryptographed = true;
		long cryptoMean = computeMeanTimeOfMergeProcedure("testfiles/deletioninnerinleft");

		assertThat((double) (cryptoMean - noCryptoMean)).isLessThanOrEqualTo(ACCEPTED_MARGIN * noCryptoMean);
	}

	@Test
	public void testPerformanceWithDuplications() {

		JFSTMerge.isCryptographed = false;
		long noCryptoMean = computeMeanTimeOfMergeProcedure("testfiles/duplicationsconflicting");

		JFSTMerge.isCryptographed = true;
		long cryptoMean = computeMeanTimeOfMergeProcedure("testfiles/duplicationsconflicting");

		assertThat((double) (cryptoMean - noCryptoMean)).isLessThanOrEqualTo(ACCEPTED_MARGIN * noCryptoMean);
	}

	@Test
	public void testPerformanceWithInitializations() {

		JFSTMerge.isCryptographed = false;
		long noCryptoMean = computeMeanTimeOfMergeProcedure("testfiles/initlblocksnobase");

		JFSTMerge.isCryptographed = true;
		long cryptoMean = computeMeanTimeOfMergeProcedure("testfiles/initlblocksnobase");

		assertThat((double) (cryptoMean - noCryptoMean)).isLessThanOrEqualTo(ACCEPTED_MARGIN * noCryptoMean);
	}

	@Test
	public void testPerformanceWithNewReferencing() {

		JFSTMerge.isCryptographed = false;
		long noCryptoMean = computeMeanTimeOfMergeProcedure("testfiles/nereomethodfield");

		JFSTMerge.isCryptographed = true;
		long cryptoMean = computeMeanTimeOfMergeProcedure("testfiles/nereomethodfield");

		assertThat((double) (cryptoMean - noCryptoMean)).isLessThanOrEqualTo(ACCEPTED_MARGIN * noCryptoMean);
	}

	@Test
	public void testPerformanceWithRenaming() {

		JFSTMerge.isCryptographed = false;
		long noCryptoMean = computeMeanTimeOfMergeProcedure("testfiles/renaminginright");

		JFSTMerge.isCryptographed = true;
		long cryptoMean = computeMeanTimeOfMergeProcedure("testfiles/renaminginright");

		assertThat((double) (cryptoMean - noCryptoMean)).isLessThanOrEqualTo(ACCEPTED_MARGIN * noCryptoMean);
	}

	private long computeMeanTimeOfMergeProcedure(String testFilesDirectory) {
		long initialTime, finalTime, totalTime = 0;

		for(int i = 0; i < NUM_ITERATIONS; i++) {

			initialTime = System.currentTimeMillis();
			new JFSTMerge().mergeFiles(
					new File(testFilesDirectory + "/left/Test.java"),
					new File(testFilesDirectory + "/base/Test.java"),
					new File(testFilesDirectory + "/right/Test.java"),
					null);
			finalTime = System.currentTimeMillis();

			totalTime += finalTime - initialTime;
		}

		return totalTime / NUM_ITERATIONS;
	}

	private static void decrypt(String fileName) throws CryptoException {
		String logPath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator + fileName;

		File file = new File(logPath);
		CryptoUtils.decrypt(file, file);
	}

}