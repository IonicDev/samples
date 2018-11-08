/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * Developed with Ionic Java SDK 2.3.0
 */

package com.ionic.samples;

import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPassword;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.agent.key.KeyAttributesMap;
import com.ionic.sdk.agent.cipher.file.GenericFileCipher;
import com.ionic.sdk.agent.cipher.file.data.FileCryptoEncryptAttributes;
import com.ionic.sdk.agent.cipher.file.data.FileCipher.Generic;
import com.ionic.sdk.error.IonicException;
import javax.xml.bind.DatatypeConverter;
import static java.util.Arrays.asList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class CryptoFileGeneric
{
    public static void main(String[] args)
    {
        // Execute this sample in the crypto-file-generic directory.
        // java -jar target/crypto-file-cipher-generic.jar

        // The files to encrypt from and decrypt to.
        String fileOriginal   = "../../sample-data/files/Message.txt";
        String fileCipherText = "./Message-Protected.txt";
        String filePlainText  = "./Message.txt";

        // read persistor password from environment variable
        String persistorPassword = System.getenv("IONIC_PERSISTOR_PASSWORD");
        if (persistorPassword == null) {
            System.out.println("[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD");
            System.exit(1);
        }

        // initialize agent
        Agent agent = new Agent();
        try {
            String persistorPath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pw";
            DeviceProfilePersistorPassword persistor = new DeviceProfilePersistorPassword(persistorPath);
            persistor.setPassword(persistorPassword);
            //agent.setMetadata(Agent.APPLICATION_NAME, "CryptoFileCipherGeneric Sample");
            agent.initialize(persistor);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // Create single key without attributes.
        CreateKeysResponse.Key key = null;
        try {
            key = agent.createKey().getKeys().get(0);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // Define mutable attributes.
        KeyAttributesMap mutableAttributes = new KeyAttributesMap();
        mutableAttributes.put("classification", asList("Restricted"));
        FileCryptoEncryptAttributes fileCryptoEncryptAttrs = new FileCryptoEncryptAttributes(mutableAttributes);

        // Initialize generic file cipher object.
        GenericFileCipher cipher = new GenericFileCipher(agent);

        // Encrypt
        try {
            System.out.format("Encrypting file %s and saving in cipher file %s%n", fileOriginal, fileCipherText);
            cipher.encrypt(fileOriginal, fileCipherText, fileCryptoEncryptAttrs);
        } catch (IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // Decrypt
        try {
            System.out.format("Decrypting file %s and saving in plaintext file %s%n", fileCipherText, filePlainText);
            cipher.decrypt(fileCipherText, filePlainText);
        } catch (IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // Read the files for comparison.  Read the original file first.
        String message = new String();
        String plainText = new String();

        try {
            message = new String(Files.readAllBytes(Paths.get(fileOriginal))); 
        } catch (IOException ioe) {
            System.out.println("Error reading original file" + ioe.getMessage());
            System.exit(1);
        }

        // Now read the plaintext or decrypted file.
        try {
            plainText = new String(Files.readAllBytes(Paths.get(filePlainText))); 
        } catch (IOException ioe) {
            System.out.println("Error reading decrypted file" + ioe.getMessage());
            System.exit(1);
        }

        // Verify encrypt and decrypt worked.
        if (! message.equals(plainText)) {
            System.out.println("Encryption/Decrption does not match!");
            System.out.format("Original:%n%s", message);
            System.out.format("Decrypted:%n%s", plainText);
        }
    }
}
