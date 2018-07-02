/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * Developed with Ionic Java SDK 2.1.0
 */

package com.ionic.samples;

import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPassword;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.cipher.aes.AesCtrCipher;
import com.ionic.sdk.error.IonicException;
import javax.xml.bind.DatatypeConverter;

public class CryptoAesCtr
{
    public static void main(String[] args)
    {
        // initialize agent
        Agent agent = new Agent();
        try {
            String persistorPath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pw";
            String persistorPassword = System.getenv("IONIC_PERSISTOR_PASSWORD");

            DeviceProfilePersistorPassword persistor = new DeviceProfilePersistorPassword(persistorPath);
            persistor.setPassword(persistorPassword);

            agent.initialize(persistor);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        String message = "this is a secret message";

        // create new key
        CreateKeysResponse.Key key = null;
        try {
            key = agent.createKey().getKeys().get(0);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // initialize aes cipher object
        AesCtrCipher cipher = null;
        try {
            cipher = new AesCtrCipher();
            cipher.setKey(key.getKey());
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // encrypt
        byte[] ciphertext = null;
        try {
            ciphertext = cipher.encrypt(message.getBytes());
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // decrypt
        byte[] plaintext = null;
        try { 
            plaintext = cipher.decrypt(ciphertext);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // display data
        System.out.println("Ciphertext  : " + DatatypeConverter.printHexBinary(ciphertext));
        System.out.println("Plaintext   : " + new String(plaintext));
    }
}
