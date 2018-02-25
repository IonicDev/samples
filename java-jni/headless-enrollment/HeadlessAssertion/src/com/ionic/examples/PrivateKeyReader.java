package com.ionic.examples;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.apache.commons.io.IOUtils;


/**
 * 
 * @author ionicsecurity.com
 * 
 * This class reads a private key (used only in development and test) from either a File or a String
 *
 */
public class PrivateKeyReader {
	
	/**
	 * 
	 * @param pemKeyString
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * 
	 * Retrieve a key from a String.  The string MUST be a base64 PKCS1 encoded RSA key
	 */
	static public PrivateKey get(String pemKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
		pemKeyString = pemKeyString.replaceAll("(-+BEGIN PRIVATE KEY-+|-+END PRIVATE KEY-+|\\s)", "");
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] keyBytes = decoder.decode(pemKeyString);
		// generate private key
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(spec);		  

		return privateKey;
	} 
	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws KeyReaderException
	 * 
	 * Retrieve a key from a File.  The File MUST contain a base64 PKCS8 encoded RSA key
	 */
	static public PrivateKey get(File file) throws NoSuchAlgorithmException, InvalidKeySpecException, KeyReaderException {
		FileInputStream fileStream;
		try {
			fileStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new KeyReaderException("Key file not found: " + e.getMessage());
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
		StringBuilder keyString = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				keyString.append(line);
			}
		} catch (IOException e) {
			throw new KeyReaderException("Can't read line: " + e.getMessage());
		} finally {
			IOUtils.closeQuietly(fileStream);
		}
		return PrivateKeyReader.get(keyString.toString());
	}
}