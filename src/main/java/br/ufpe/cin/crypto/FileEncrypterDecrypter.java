package br.ufpe.cin.crypto;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import br.ufpe.cin.exceptions.CryptoException;

/**
 * Class responsible for encrypting and decrypting files.
 */
public class FileEncrypterDecrypter {

    private final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private final char[] KEY_STORE_PASSWORD = "thisiss3mkeystorepassword".toCharArray();
    private final int KEY_SIZE = 256;
    private final SecureRandom RANDOM = new SecureRandom();
    private final Path KEY_STORE_PATH = Paths.get(System.getProperty("user.home"), "keystore.ks");

    /**
     * Encrypts a file and stores the result also in a file.
     * 
     * @param fileToEncrypt file to be encrypted
     * @param outputFile    file that will contain the result of the encryption
     */
    public void cipher(Path fileToEncrypt, Path outputFile) throws CryptoException {

        try {
            // Generate and store random key.
            SecretKey key = generateKey();
            storeKey(key, outputFile.toString());

            // Initialize cipher and initialization vector.
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            IvParameterSpec iv = generateIV(cipher);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            byte[] plainText = FileUtils.readFileToByteArray(fileToEncrypt.toFile());

            // Digest the data for integrity preservation.
            byte[] digest = digest(plainText);

            // Encrypt the digest + the plain text and write the IV + the result in the output file.
            byte[] cipherText = cipher.doFinal(ArrayUtils.addAll(digest, plainText));
            FileUtils.writeByteArrayToFile(outputFile.toFile(), ArrayUtils.addAll(iv.getIV(), cipherText));

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException
                | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            throw new CryptoException("Error encrypting file " + fileToEncrypt.toString(), e);
        }
    }

	/**
     * Decrypts a file and stores the result also in a file.
     * 
     * @param fileToDecrypt file to be decrypted
     * @param outputFile    file that will contain the result of the decryption
     */
    public void decipher(Path fileToDecrypt, Path outputFile) throws CryptoException {
        
        try {
            // Retrieve stored key.
            SecretKey key = loadKey(fileToDecrypt.toString());

            // Retrieve IV and initialize cipher.
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            byte[] cipherTextAndIV = FileUtils.readFileToByteArray(fileToDecrypt.toFile());
            byte[] iv = ArrayUtils.subarray(cipherTextAndIV, 0, cipher.getBlockSize());
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            // Decrypt.
            byte[] cipherText = ArrayUtils.subarray(cipherTextAndIV, iv.length, cipherTextAndIV.length);
            byte[] digestAndPlainText = cipher.doFinal(cipherText);

            // Analyse digest (see if plain text was not modified).
            byte[] digest = ArrayUtils.subarray(digestAndPlainText, 0, 32);
            byte[] plainText = ArrayUtils.subarray(digestAndPlainText, 32, digestAndPlainText.length);

            if(integrityHasBeenPreserved(plainText, digest)) {
                FileUtils.writeByteArrayToFile(outputFile.toFile(), plainText);
            } else {
                throw new CryptoException("File " + fileToDecrypt.toString() + " has been modified by an attacker", new Exception());
            }

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException
                | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            throw new CryptoException("Error decrypting file " + fileToDecrypt.toString(), e);
        }

    }

    private boolean integrityHasBeenPreserved(byte[] plainText, byte[] digest) throws NoSuchAlgorithmException {
        return Arrays.equals(digest, digest(plainText));
    }

    private byte[] digest(byte[] plainText) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(plainText);
        
        return messageDigest.digest();
	}

    private SecretKey generateKey() throws CryptoException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(KEY_SIZE, RANDOM);

            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("Error generating symmetric key", e);
        }
    }

    private IvParameterSpec generateIV(Cipher cipher) {
        byte[] iv = new byte[cipher.getBlockSize()];
        RANDOM.nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private SecretKey loadKey(String entryName) throws CryptoException {
        try (InputStream keyStoreData = new FileInputStream(KEY_STORE_PATH.toString())) {

            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(keyStoreData, KEY_STORE_PASSWORD);

            return (SecretKey) keyStore.getKey(entryName, KEY_STORE_PASSWORD);

        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
                | UnrecoverableEntryException e) {
            throw new CryptoException("Error loading symmetric key", e);
        }
    }

    private void storeKey(SecretKey key, String entryName) throws CryptoException {

        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");

            if (!Files.exists(KEY_STORE_PATH)) {
                keyStore.load(null, KEY_STORE_PASSWORD);
            } else {
                InputStream keyStoreData = new FileInputStream(KEY_STORE_PATH.toString());
                keyStore.load(keyStoreData, KEY_STORE_PASSWORD);
                keyStoreData.close();
            }

            SecretKeyEntry secretKeyEntry = new SecretKeyEntry(key);
            ProtectionParameter entryPassword = new PasswordProtection(KEY_STORE_PASSWORD);
            keyStore.setEntry(entryName, secretKeyEntry, entryPassword);

            OutputStream keyStoreOutputStream = new FileOutputStream(KEY_STORE_PATH.toString());
            keyStore.store(keyStoreOutputStream, KEY_STORE_PASSWORD);
            keyStoreOutputStream.close();

        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new CryptoException("Error storing symmetric key", e);
        }
    }
}