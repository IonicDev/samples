package com.ionic.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import org.opensaml.core.config.Configuration;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;

public class SVREnroll {

	public static void main(String[] args) {
		try {
			InitializationService.initialize();
		} catch (InitializationException e) {
			e.printStackTrace();
			return;
		}

		File file = new File(EnrollmentConstants.PRIVATE_KEY_FILE);
		IDPCredentials.loadCredential(file);
		IonicSamlResponse samlResponse = new IonicSamlResponse(EnrollmentConstants.ENROLLMENT_USER, UUID.randomUUID().toString());
		samlResponse.write(new PrintWriter(System.out));
		File assertionFile = new File(EnrollmentConstants.ASSERTION_FILE);
		try {
			assertionFile.createNewFile();
			try {
				samlResponse.write(new PrintWriter(assertionFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Can't create assertion file");
			return;
		}
	}

}
