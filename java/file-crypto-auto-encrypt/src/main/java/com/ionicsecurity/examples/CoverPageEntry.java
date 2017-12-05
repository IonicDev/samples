/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */
package com.ionicsecurity.examples;

public class CoverPageEntry {
	private byte[] fileContent;
	private String hash;
	private int expiration;

	public CoverPageEntry(byte[] inFile, String inHash, int inExpiration) {
		fileContent = inFile;
		hash = inHash;
		expiration = inExpiration;
	}

	public byte[] getFile() {
		return fileContent;
	}

	public void setFile(byte[] file) {
		fileContent = file;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String newHash) {
		hash = newHash;
	}

	public int getExpiration() {
		return expiration;
	}

	public void setExpiration(int newExpiration) {
		expiration = newExpiration;
	}
}
