/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.samples;

import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPassword;
import com.ionic.sdk.agent.data.MetadataMap;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.error.IonicException;
import javax.xml.bind.DatatypeConverter;

public class CreateKeyOnBehalfOf
{
    public static void main(String[] args)
    {
        String delegatedUserEmail = "test@ionic.com";

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

        // define on-behalf-of user in the request metadata
        MetadataMap requestMetadata = new MetadataMap();
        requestMetadata.set("ionic-delegated-email", delegatedUserEmail);

        // create single key
        CreateKeysResponse.Key key = null;
        try {
            key = agent.createKey(requestMetadata).getKeys().get(0);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // display new key
        System.out.println("KeyId        : " + key.getId());
        System.out.println("KeyBytes     : " + DatatypeConverter.printHexBinary(key.getKey()));
        System.out.println("FixedAttrs   : " + key.getAttributesMap());
        System.out.println("MutableAttrs : " + key.getMutableAttributesMap());
    }
}
