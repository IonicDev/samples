/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * Developed with Ionic Java SDK 2.1.0
 */

package com.ionic.samples;

import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.device.profile.DeviceProfile;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.error.IonicException;

public class SetActiveProfile
{
    public static void main(String[] args)
    {
        String profileId = "ABcd.1.48sdf0-cs80-5802-sd80-d8s0df80sdfj";

        // initialize agent
        Agent agent = new Agent();
        try {
            String persistorPath = "../../sample-data/persistors/sample-persistor.pt";
            DeviceProfilePersistorPlainText persistor = new DeviceProfilePersistorPlainText(persistorPath);
            agent.initialize(persistor);
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        
        // verify there is at least one profile
        if (agent.getAllProfiles().size == 0) {
            System.println("No profiles found in persistor");
            System.exit(1);
        }
        
        // list all profiles
        System.out.println("All Profiles:");
        for (DeviceProfile profile : agent.getAllProfiles()) {
            System.out.println("---");
            System.out.println(profile.getDeviceId());
        }

        // 

        /*
        // set active profile
        agent.setActiveProfile(profileId);

        // display agent active profile
        DeviceProfile profile = agent.getActiveProfile();
        System.out.println("Id       : " + profile.getDeviceId());
        System.out.println("Name     : " + profile.getName());
        System.out.println("Keyspace : " + profile.getKeySpace());
        System.out.println("ApiUrl   : " + profile.getServer());
        */
    }
}
