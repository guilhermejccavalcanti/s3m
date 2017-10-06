package br.ufpe.cin.crypto;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptoKey{

	private static final String ALGORITHM = "AES";
	private static final String keyString = "0Nr0ozDd12aUT1RFnt5ZHg=="; //toy key

	public static SecretKey getKey()
	{
		byte[] decodedKey = Base64.getDecoder().decode(keyString);
		return new SecretKeySpec(decodedKey, 0, decodedKey.length ,ALGORITHM);

	}
}
