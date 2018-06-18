/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.samples;

import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.createdevice.CreateDeviceRequest;
import com.ionic.sdk.agent.request.createdevice.CreateDeviceResponse;
import com.ionic.sdk.device.profile.DeviceProfile;
import com.ionic.sdk.error.IonicException;
import java.util.Scanner;

public class CreateProfile
{
    public static void main(String[] args)
    {
        // prompt user for auth components
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nKEYSPACE: ");
        String keyspace = scanner.nextLine();
        System.out.println("\nAPI_URL: ");
        String apiUrl = scanner.nextLine();
        System.out.println("\nTOKEN: ");
        String token = scanner.nextLine();
        System.out.println("\nIONIC_ASSERTION: ");
        String ionicAssertion = scanner.nextLine();
        System.out.println("\nES_PUBKEY: ");
        String esPubKey = scanner.nextLine();

        // initialize new agent without profiles
        Agent agent = null;
        try {
            agent = new Agent();
            agent.initializeWithoutProfiles();
        } catch (IonicException e) {
            System.out.println("Failed to initialize new agent: " + e);
            System.exit(1);
        }

        // create profile
        DeviceProfile profile = null;
        try {
            CreateDeviceRequest createDeviceRequest = new CreateDeviceRequest(
                "example", 
                apiUrl,
                keyspace, 
                token,
                ionicAssertion, 
                esPubKey
            );
            CreateDeviceResponse createDeviceResponse = agent.createDevice(createDeviceRequest);
            createDeviceResponse = agent.createDevice(createDeviceRequest);
            profile = createDeviceResponse.getDeviceProfile();
        } catch (IonicException e) {
            System.out.println("Failed to create profile: " + e);
            System.exit(1);
        }

        // display profile
        System.out.println("Id       : " + profile.getDeviceId());
        System.out.println("Name     : " + profile.getName());
        System.out.println("Keyspace : " + profile.getKeySpace());
        System.out.println("ApiUrl   : " + profile.getServer());
    }
}
