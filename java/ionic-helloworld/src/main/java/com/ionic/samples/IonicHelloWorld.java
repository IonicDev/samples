/*
 * (c) 2017-2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * Developed with Ionic Java SDK 2.4.0
 */

package com.ionic.samples;

import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPassword;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.key.KeyAttributesMap;
import com.ionic.sdk.agent.cipher.chunk.data.ChunkCryptoEncryptAttributes;
import com.ionic.sdk.agent.cipher.chunk.ChunkCipherAuto;
import com.ionic.sdk.error.IonicException;
import static java.util.Arrays.asList;

public class IonicHelloWorld
{
    public static void main(String[] args)
    {
        String message = "Hello, World!";

        // read persistor password from environment variable
        String persistorPassword = System.getenv("IONIC_PERSISTOR_PASSWORD");
        if (persistorPassword == null) {
            System.out.println("[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD");
            System.exit(1);
        }        

        // initialize agent with password persistor
        Agent agent = new Agent();
        try {
            String persistorPath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pw";
            DeviceProfilePersistorPassword persistor = new DeviceProfilePersistorPassword(persistorPath);
            persistor.setPassword(persistorPassword);
            agent.initialize(persistor);
        } catch (IonicException e) {
            System.out.println(e);
            if (e.getReturnCode() == AgentErrorModuleConstants.ISAGENT_NO_DEVICE_PROFILE) {
                System.out.println("Profile does not exist");
            }
            if (e.getReturnCode() == AgentErrorModuleConstants.ISAGENT_KEY_DENIED) {
                System.out.println("Key request denied");
            }
            System.out.println("Error initializing agent: " + e.getMessage());
            System.exit(1);
        }

        // set application metadata
        agent.setMetadata("ionic-application-name", "ionic-helloworld");
        agent.setMetadata("ionic-application-version", "1.1.0");

        // Define data marking clearance-level
        KeyAttributesMap dataMarkings = new KeyAttributesMap();
        dataMarkings.put("clearance-level", asList("secret"));
        ChunkCryptoEncryptAttributes cipherDataMarkings = new ChunkCryptoEncryptAttributes(dataMarkings);

        // initialize chunk cipher object
        ChunkCipherAuto cipher = new ChunkCipherAuto(agent);

        // encrypt with data markings
        String ciphertext = null;
        try {
            ciphertext = cipher.encrypt(message, cipherDataMarkings);
        } catch(IonicException e) {
            System.out.println("Error encrypting plaintext: " + e.getMessage());
            System.exit(1);
        }

        // decrypt
        String plaintext = null;
        try { 
            plaintext = cipher.decrypt(ciphertext);
        } catch(IonicException e) {
            System.out.println("Error decrypting ciphertext: " + e.getMessage());
            System.out.println("You don't have the correct clearance.");
            System.out.println("");
            System.exit(1);
        }

        // display data
        System.out.println("");
        System.out.println("Input : " + message);
        System.out.println("Ionic Chunk Encrypted Ciphertext: " + ciphertext);
        System.out.println("Plaintext : " + plaintext);
        System.out.println("");
    }
}
