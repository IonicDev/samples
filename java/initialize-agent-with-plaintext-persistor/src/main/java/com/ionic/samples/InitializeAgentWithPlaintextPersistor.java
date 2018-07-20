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
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.error.IonicException;
import java.util.List;

public class InitializeAgentWithPlaintextPersistor
{
    public static void main(String[] args)
    {
        String persistorPath = "../../sample-data/persistors/sample-persistor.pt";

        // initialize agent with plaintext persistor
        Agent agent = new Agent();
        try {
            DeviceProfilePersistorPlainText persistor = new DeviceProfilePersistorPlainText(persistorPath);
            agent.initialize(persistor);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // display all profiles in persistor
        List<DeviceProfile> profiles = agent.getAllProfiles();
        for (DeviceProfile profile : profiles) {
            System.out.println("Id       : " + profile.getDeviceId());
            System.out.println("Name     : " + profile.getName());
            System.out.println("Keyspace : " + profile.getKeySpace());
            System.out.println("ApiUrl   : " + profile.getServer());
        }
    }
}
