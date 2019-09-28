package br.ufpe.cin.unit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.ufpe.cin.crypto.FileEncrypterDecrypter;
import br.ufpe.cin.exceptions.CryptoException;

/**
 * Tests {@link br.ufpe.cin.crypto.FileEncrypterDecrypter FileEncrypterDecrypter} class.
 */
public class FileEncrypterDecrypterTest {

    private FileEncrypterDecrypter fileEncrypterDecrypter = new FileEncrypterDecrypter();
    private Path plainFile = Paths.get("README.md");
    private Path cipherFile = Paths.get("encryptedREADME.md");

    private Path keyStorePath = Paths.get(System.getProperty("user.home"), "keystore.ks");
    private Path keyStoreTempPath = Paths.get(System.getProperty("user.home"), "keystoreTemp.ks");

    @Before
    public void renameKeyStoreFile() throws IOException {
        if(Files.exists(keyStorePath)) {
            Files.move(keyStorePath, keyStoreTempPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @After
    public void restoreKeyStoreFile() throws IOException {
        if(Files.exists(keyStoreTempPath)) {
            Files.move(keyStoreTempPath, keyStorePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Test
    public void testCryptography_whenEncryptingIntoFile_andDecryptingFileAgain_shouldResultInOriginalString()
            throws CryptoException, IOException {
    
        fileEncrypterDecrypter.cipher(plainFile, cipherFile);
        fileEncrypterDecrypter.decipher(cipherFile, cipherFile);

        String originalContent = FileUtils.readFileToString(plainFile.toFile());
        String contentAfterEncryptionAndDecryption = FileUtils.readFileToString(cipherFile.toFile());
        
        Files.delete(cipherFile);
        
        assertEquals(originalContent, contentAfterEncryptionAndDecryption);
    }

    @Test(expected = CryptoException.class)
    public void testCryptography_whenDecryptingPlainFile_shouldThrowCryptoException() throws CryptoException {
        fileEncrypterDecrypter.decipher(plainFile, plainFile);
    }

    @Test(expected = CryptoException.class)
    public void testDigest_whenAttackerModifiesCipherFile_shouldThrowCryptoExceptionAtDecryption() throws CryptoException, IOException {
        fileEncrypterDecrypter.cipher(plainFile, cipherFile);
        
        String content = FileUtils.readFileToString(cipherFile.toFile());
        FileUtils.writeStringToFile(cipherFile.toFile(), content + "modification");

        try {
            fileEncrypterDecrypter.decipher(cipherFile, cipherFile);
        } finally {
            Files.delete(cipherFile);
        }
    }
    
}