package br.ufpe.cin.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;

public class CryptoPerformanceTest {

    private static final int NUM_REPETITIONS = 5;

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

    @Test
    public void testCryptographyPerformance() {
        
	JFSTMerge.isCryptographed = false;
	long totalTimeNoCrypto = computeTimeOfMergeProcedure();
        
	JFSTMerge.isCryptographed = true;
	long totalTimeCrypto = computeTimeOfMergeProcedure();

	double acceptablePercentage = (double) (totalTimeNoCrypto - totalTimeCrypto) / 100;
        
        assertTrue("Got: " + (totalTimeNoCrypto - totalTimeCrypto), acceptablePercentage <= 1.1 * totalTimeNoCrypto);
    }

    public long computeTimeOfMergeProcedure() {

        long totalTime = 0;
        for (int i = 0; i < NUM_REPETITIONS; i++) {
            long initialTime = System.currentTimeMillis();
            new JFSTMerge().mergeFiles(
                new File("/home/jvcoutinho/Documentos/jFSTMerge/testfiles/cryptoperformance/test"+i+"/base.java"), 
                new File("/home/jvcoutinho/Documentos/jFSTMerge/testfiles/cryptoperformance/test"+i+"/left.java"),
                new File("/home/jvcoutinho/Documentos/jFSTMerge/testfiles/cryptoperformance/test"+i+"/right.java"),
                null);
            long finalTime = System.currentTimeMillis();
            totalTime = finalTime - initialTime;
        }

	    return totalTime / NUM_REPETITIONS;
    }  
    
}
