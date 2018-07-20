/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.ihw;

import com.ionic.sdk.error.IonicException;
import com.ionic.sdk.error.AgentErrorModuleConstants;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.cipher.chunk.ChunkCipherV2;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPassword;

public class IonicHelloWorld
{
    public static void main(String[] args)
    {
        try {

            // read persistor password from environment variable
            String persistorPassword = System.getenv("IONIC_PERSISTOR_PASSWORD");
            if (persistorPassword == null) {
                System.out.println("[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD");
                System.exit(1);
            }

            // initialize agent with password persistor
            Agent isAgent = new Agent();
            String profilePath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pw";
            DeviceProfilePersistorPassword persistor = new DeviceProfilePersistorPassword(profilePath);
            persistor.setPassword(persistorPassword);
            isAgent.initialize(persistor);

            // Encrypt and decrypt string "hello world"
            ChunkCipherV2 cipher = new ChunkCipherV2(isAgent);
            String cipherText = cipher.encrypt("hello world");
            String plainText = cipher.decrypt(cipherText);
            System.out.println("Plain Text: " + plainText);
            System.out.println("Ionic Chunk Encrypted Text: " + cipherText);

        } catch (IonicException e) {
            System.out.println(e);
            if(e.getReturnCode() == AgentErrorModuleConstants.ISAGENT_NO_DEVICE_PROFILE) {
                System.out.println("Profile does not exist");
            }
            if(e.getReturnCode() == AgentErrorModuleConstants.ISAGENT_KEY_DENIED) {
                System.out.println("Key request denied");
            }
        }
    }
}
