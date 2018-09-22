package br.ufpe.cin.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * A class to handle encoding of files involved in a merge.
 * @author João Victor
 */
public class FilesEncoding {

    private static Map<File, String> encodings = new HashMap<File, String>();
    private static final String DEFAULT_ENCODING = "UTF-8";
    
    public void analyseFiles(File... files) {

        try {
            for (File file : files) {
                if (file != null) {
                    encodings.put(file, detectEncoding(file));
                }
            }

        } catch (IOException e) {
            System.err.println("An error occurred while opening files for encoding detection. Shutting down.");
            System.exit(1);
        }
    }

    static String retrieveEncoding(File file) {
        return encodings.getOrDefault(file, DEFAULT_ENCODING);
    }

    private String detectEncoding(File file) throws IOException {
        InputStream reader = Files.newInputStream(Paths.get(file.getAbsolutePath()));
        UniversalDetector detector = new UniversalDetector(null);

        byte[] data = new byte[4096];
        int dataRead = reader.read(data);
        while(dataRead > 0 && !detector.isDone()) {
            detector.handleData(data, 0, dataRead);
            dataRead = reader.read(data);
        }
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        detector.reset();

        if(encoding == null)
            return DEFAULT_ENCODING;
        else
            return encoding;
    }

}
