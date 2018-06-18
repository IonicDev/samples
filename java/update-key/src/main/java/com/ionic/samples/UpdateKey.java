/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * Developed with Ionic Java SDK 2.1.0
 */

package com.ionic.samples;

import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.getkey.GetKeysResponse;
import com.ionic.sdk.agent.request.updatekey.UpdateKeysResponse;
import com.ionic.sdk.error.IonicException;
import com.ionic.sdk.agent.key.KeyAttributesMap;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import static java.util.Arrays.asList;

public class UpdateKey
{
    public static void main(String[] args)
    {
        String keyId = "HVzG5eMZrc8";

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

        // define new mutable attributes
        KeyAttributesMap newMutableAttributes = new KeyAttributesMap();
        newMutableAttributes.put("classification", asList("Highly Restricted"));

        // fetch key
        GetKeysResponse.Key fetchedKey = null;
        try {
            fetchedKey = agent.getKey(keyId).getKeys().get(0);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // merge new and existing attributes
        KeyAttributesMap updatedAttributes = new KeyAttributesMap(fetchedKey.getMutableAttributes());
        updatedAttributes.putAll(newMutableAttributes);
        fetchedKey.setMutableAttributes(updatedAttributes);

        // update key
        UpdateKeysResponse.Key key = null;
        try {
            key = agent.updateKey(fetchedKey, false).getKeys().get(0);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // display updated key
        System.out.println("KeyId        : " + key.getId());
        System.out.println("KeyBytes     : " + DatatypeConverter.printHexBinary(key.getKey()));
        System.out.println("FixedAttrs   : " + key.getAttributesMap());
        System.out.println("MutableAttrs : " + key.getMutableAttributes());
    }
}
