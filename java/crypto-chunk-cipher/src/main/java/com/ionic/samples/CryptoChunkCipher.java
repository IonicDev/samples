/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * Developed with Ionic Java SDK 2.1.0
 */

package com.ionic.samples;

import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPassword;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.cipher.chunk.ChunkCipherAuto;
import com.ionic.sdk.error.IonicException;

public class CryptoChunkCipher
{
    public static void main(String[] args)
    {
        String message = "secret message";

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
            agent.initialize(persistor);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // initialize chunk cipher object
        ChunkCipherAuto cipher = new ChunkCipherAuto(agent);

        // encrypt
        String ciphertext = null;
        try {
            ciphertext = cipher.encrypt(message.getBytes());
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // decrypt
        String plaintext = null;
        try { 
            plaintext = cipher.decrypt(ciphertext);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // display data
        System.out.println("Ciphertext : " + ciphertext);
        System.out.println("Plaintext  : " + plaintext);
    }
}
