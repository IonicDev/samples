/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * Developed with Ionic Java SDK 2.1.0
 */

package com.ionic.samples;

import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.device.profile.DeviceProfile;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorAesGcm;
import com.ionic.sdk.error.IonicException;
import java.util.List;
import javax.xml.bind.DatatypeConverter;

public class InitializeAgentWithAesPersistor
{
    public static void main(String[] args)
    {
        String persistorPath = "../../sample-data/persistors/sample-persistor.aes";
        byte[] persistorKey = DatatypeConverter.parseHexBinary("A0444B8B5A7209780823617A98986831B8240BAA851A0B1696B0329280286B17");
        byte[] persistorAuthData = "persistor auth data".getBytes();

        // initialize agent with aes persistor        
        Agent agent = new Agent();
        try {
            DeviceProfilePersistorAesGcm persistor = new DeviceProfilePersistorAesGcm(persistorPath);
            persistor.setKey(persistorKey);
            persistor.setAuthData(persistorAuthData);
            agent.initialize(persistor);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // display all profiles in persistor
        List<DeviceProfile> profiles = agent.getAllProfiles();
        for (DeviceProfile profile : profiles) {
            System.out.println("---");
            System.out.println("Id       : " + profile.getDeviceId());
            System.out.println("Name     : " + profile.getName());
            System.out.println("Keyspace : " + profile.getKeySpace());
            System.out.println("ApiUrl   : " + profile.getServer());
        }
    }
}
