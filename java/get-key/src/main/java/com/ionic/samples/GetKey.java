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
import com.ionic.sdk.error.IonicException;
import javax.xml.bind.DatatypeConverter;

public class GetKey
{
    public static void main(String[] args)
    {
        String keyId = "HVzG3oKSL7A";

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

        // get single key
        GetKeysResponse.Key key = null;
        try {
            key = agent.getKey(keyId).getKeys().get(0);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // display fetched key
        System.out.println("KeyId        : " + key.getId());
        System.out.println("KeyBytes     : " + DatatypeConverter.printHexBinary(key.getKey()));
        System.out.println("FixedAttrs   : " + key.getAttributesMap());
        System.out.println("MutableAttrs : " + key.getMutableAttributes());
    }
}
