package com.ionic.examples;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;

import java.io.File;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

public class IDPCredentials {
	private static Credential credential;

	/**
	 * Load the credential from the private key file.. This file MUST be a pkcs8
	 * PEM file.
	 * 
	 * @param file
	 * @return
	 */
	public static Credential loadCredential(File file) {
		try {
			PrivateKey privateKey = PrivateKeyReader.get(file);
			RSAPrivateCrtKey privk = (RSAPrivateCrtKey) privateKey;

			RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(privk.getModulus(),
					privk.getPublicExponent());

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey myPublicKey = keyFactory.generatePublic(publicKeySpec);
			credential = CredentialSupport.getSimpleCredential(myPublicKey, privateKey);
			return credential;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | KeyReaderException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static Credential getCredential() {
		return credential;
	}

}