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
import com.ionic.sdk.agent.request.getresources.GetResourcesRequest;
import com.ionic.sdk.agent.request.getresources.GetResourcesResponse;
import com.ionic.sdk.error.IonicException;
import java.util.List;
import javax.xml.bind.DatatypeConverter;

public class GetResource
{
    public static void main(String[] args)
    {
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

        // Create a resource request for "classification" marking values
        // configured in the dashboard.
        String refId = new String("java demo");
        String resourceId = new String("marking-values");
        String suppArgs = new String("classification");
        
        GetResourcesRequest.Resource resourceRequest =
            new GetResourcesRequest.Resource(refId, resourceId, suppArgs);
        GetResourcesResponse resourcesResponse = null;

        // Fetch the resource "classification" marking values.
        try
        {
            resourcesResponse = agent.getResource(resourceRequest);
        }
        catch (IonicException e)
        {
            System.out.println("Error requesting resource: " + e.getMessage());
            System.exit(1);
        }
        
        // Extract the resources from the get resources response.
        List<GetResourcesResponse.Resource> resources = resourcesResponse.getResources();
        if (resources.size() == 0) {
            System.out.println("There are no resources available.");
            System.exit(1);
        }

        // Display resources.
        System.out.println("Classification values:");
        for (GetResourcesResponse.Resource resource : resources)
        {
        	System.out.println("Data   : " + resource.getData());
        }


    }
}
