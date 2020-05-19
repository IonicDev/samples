/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.samples;

import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.createdevice.CreateDeviceResponse;
import com.ionic.sdk.device.create.EnrollIonicAuth;
import com.ionic.sdk.device.profile.DeviceProfile;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPassword;
import com.ionic.sdk.error.IonicException;

import java.util.Scanner;

public class CreateProfileStartForFree {

    public static void main(String[] args) {
        // read persistor password from environment variable
        String persistorPassword = System.getenv("IONIC_PERSISTOR_PASSWORD");
        if (persistorPassword == null) {
            System.out.println("[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD");
            System.exit(1);
        }

        // prompt user for auth components
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nKEYSPACE: ");
        String keyspace = scanner.nextLine();
        System.out.println("\nACCOUNT_NAME: ");
        String accountName = scanner.nextLine();
        System.out.println("\nACCOUNT_PASSWORD: ");
        char[] accountPassword = System.console().readPassword();

        // lookup enrollment URL associated with specified keyspace
        String urlEnrollment = "";
        try {
            urlEnrollment = EnrollUtil.getEnrollmentURL(keyspace);
        } catch (IonicException e) {
            System.out.println("Failed to retrieve enrollment configuration: " + e);
            System.exit(1);
        }

        // create profile
        DeviceProfile profile = null;
        try {
            final EnrollIonicAuth enrollIonicAuth = new EnrollIonicAuth(urlEnrollment, new Agent(), null);
            // enroll device to specified Ionic key tenant server
            final CreateDeviceResponse createDeviceResponse = enrollIonicAuth.enroll(
                    accountName, new String(accountPassword), "example");
            profile = createDeviceResponse.getDeviceProfile();
            // add the newly created profile to the existing profile set in the Ionic Secure Enrollment Profile file
            String persistorPath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pw";
            DeviceProfilePersistorPassword persistor = new DeviceProfilePersistorPassword(persistorPath);
            persistor.setPassword(persistorPassword);
            // create a new Agent instance to add the newly created profile to the existing profile set
            // in the Ionic Secure Enrollment Profile file
            final Agent agent = new Agent();
            agent.initializeWithoutProfiles();
            agent.loadProfiles(persistor);
            agent.addProfile(profile);
            agent.setActiveProfile(profile.getDeviceId());
            agent.saveProfiles(persistor);
        } catch (IonicException e) {
            System.out.println("Failed to create profile: " + e);
            System.exit(1);
        }

        // display profile
        System.out.println("Device ID: " + profile.getDeviceId());
        System.out.println("Name     : " + profile.getName());
        System.out.println("Keyspace : " + profile.getKeySpace());
        System.out.println("API URL  : " + profile.getServer());
    }
}
