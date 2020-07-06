/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * Developed with Ionic Java SDK 2.2.1
 */

package com.ionic.samples;

import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPassword;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.getkey.GetKeysRequest;
import com.ionic.sdk.agent.request.getkey.GetKeysResponse;
import com.ionic.sdk.error.IonicException;
import java.util.List;
import javax.xml.bind.DatatypeConverter;

public class GetKeyByExternalId
{
    public static void main(String[] args)
    {
        String externalId = null;  // Modify null to a valid key ID.

        // read persistor password from environment variable
        String persistorPassword = System.getenv("IONIC_PERSISTOR_PASSWORD");
        if (persistorPassword == null) {
            System.out.println("[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD");
            System.exit(1);
        }        

        // Initialize agent.
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

        // Build a KeyRequest with an external ID.
        GetKeysRequest keysRequest = new GetKeysRequest();
        keysRequest.addExternalId(externalId);

        // Get one or more keys with an external ID.
        List<GetKeysResponse.Key> keys = null;
        try {
            keys = agent.getKeys(keysRequest).getKeys();
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        if (keys.size() == 0) {
            System.out.println("No keys returned for external ID: " + externalId);
            System.out.println("There were no keys or access was denied to the keys");
            System.exit(1);
        }
            
        // Display fetched keys.
        System.out.println("Keys fetch for external ID: " + externalId );
        for (GetKeysResponse.Key key : keys) {
            System.out.println("---");
            System.out.println("KeyId        : " + key.getId());
            System.out.println("KeyBytes     : " + DatatypeConverter.printHexBinary(key.getKey()));
            System.out.println("FixedAttrs   : " + key.getAttributesMap());
            System.out.println("MutableAttrs : " + key.getMutableAttributesMap());
        }
    }
}
