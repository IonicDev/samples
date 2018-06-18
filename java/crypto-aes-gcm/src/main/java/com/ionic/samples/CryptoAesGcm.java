/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * Developed with Ionic Java SDK 2.1.0
 */

package com.ionic.samples;

import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPassword;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.cipher.aes.AesGcmCipher;
import com.ionic.sdk.error.IonicException;
import javax.xml.bind.DatatypeConverter;

public class CryptoAesGcm
{
    public static void main(String[] args)
    {
        // initialize agent
        Agent agent = new Agent();
        try {
            String persistorPath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pt";
            DeviceProfilePersistorPlainText persistor = new DeviceProfilePersistorPlainText(persistorPath);
            agent.initialize(persistor);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        String message = "secret message";
        byte[] messageAuthData = "data to authenticate".getBytes();

        // create new key
        CreateKeysResponse.Key key = null;
        try {
            key = agent.createKey().getKeys().get(0);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // initialize aes cipher object
        AesGcmCipher cipher = null;
        try {
            cipher = new AesGcmCipher();
            cipher.setKey(key.getKey());
            cipher.setAuthData(messageAuthData);
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
