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
import com.ionic.sdk.agent.request.getkey.GetKeysResponse;
import com.ionic.sdk.agent.request.updatekey.UpdateKeysResponse;
import com.ionic.sdk.agent.request.updatekey.UpdateKeysRequest;
import com.ionic.sdk.error.IonicException;
import com.ionic.sdk.agent.key.KeyAttributesMap;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import static java.util.Arrays.asList;

public class UpdateKey
{
    public static void main(String[] args)
    {
        // TODO: provide key to update
        String keyId = "MagvIP7jrcE";

        if (keyId == null) {
            System.out.println("Please set the 'keyId' variable to a key you have already created");
            System.exit(1);
        }

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

        // define new mutable attributes
        KeyAttributesMap newMutableAttributes = new KeyAttributesMap();
        newMutableAttributes.put("classification", asList("Highly Restricted"));

        // fetch key
        GetKeysResponse.Key fetchedKey = null;
        try {
            GetKeysResponse keyResp = agent.getKey(keyId);
            if (keyResp.getKeys().size() == 0) {
                throw new IonicException(100, "No keys from getKeys().");
            }
            fetchedKey = keyResp.getKeys().get(0);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // merge new and existing attributes
        KeyAttributesMap updatedAttributes = new KeyAttributesMap(fetchedKey.getMutableAttributesMap());
        updatedAttributes.putAll(newMutableAttributes);
        
        // create update request key
        UpdateKeysRequest.Key updateRequestKey = new UpdateKeysRequest.Key(fetchedKey);
        updateRequestKey.setMutableAttributesMap(updatedAttributes);

        // update key
        UpdateKeysResponse.Key key = null;
        try {
            key = agent.updateKey(updateRequestKey).getKeys().get(0);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // display updated key
        System.out.println("KeyId        : " + key.getId());
        System.out.println("KeyBytes     : " + DatatypeConverter.printHexBinary(key.getKey()));
        System.out.println("FixedAttrs   : " + key.getAttributesMap());
        System.out.println("MutableAttrs : " + key.getMutableAttributesMap());
    }
}
