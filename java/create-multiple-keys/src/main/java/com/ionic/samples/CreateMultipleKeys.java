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
import com.ionic.sdk.agent.request.createkey.CreateKeysRequest;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.error.IonicException;
import javax.xml.bind.DatatypeConverter;
import java.util.List;

public class CreateMultipleKeys
{
    public static void main(String[] args)
    {
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
        
        String keyRef = "sample";
        int keyCount = 5;

        // create multiple keys
        CreateKeysRequest.Key keysToCreate = new CreateKeysRequest.Key(keyRef, keyCount);
        CreateKeysRequest createRequest = new CreateKeysRequest();
        createRequest.add(keysToCreate);
        List<CreateKeysResponse.Key> keys = null;
        try {
            keys = agent.createKeys(createRequest).getKeys();
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // display created keys
        for (CreateKeysResponse.Key key : keys) {
            System.out.println("---");
            System.out.println("KeyId        : " + key.getId());
            System.out.println("KeyBytes     : " + DatatypeConverter.printHexBinary(key.getKey()));
        }
    }
}
