/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using System.Collections.Generic;

using IonicSecurity.SDK;


namespace Samples
{
    class GetResource
    {
        // Waits for any input for console applications.
        // This allows information to be displayed before the 
        // console application window closes.
        static void WaitForInput()
        {
            Console.WriteLine("\nPress return to exit.");
            Console.ReadKey();
            return;
        }


        static void Main(string[] args)
        {
            // Get the user's home path and password persistor from the environment.
            String homePath = Environment.GetEnvironmentVariable("USERPROFILE");

            String persistorPassword = Environment.GetEnvironmentVariable("IONIC_PERSISTOR_PASSWORD");
            if (persistorPassword == null || persistorPassword.Length == 0)
            {
                Console.WriteLine("Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD");
                WaitForInput();
                Environment.Exit(1);
            }

            // Create an agent object to talk to Ionic.
            Agent agent = new Agent();

            // Create a password persistor for agent initialization.
            try
            {
                DeviceProfilePersistorPassword persistor = new DeviceProfilePersistorPassword();
                persistor.FilePath = homePath + "\\.ionicsecurity\\profiles.pw";
                persistor.Password = persistorPassword;

                agent.SetMetadata(Agent.MetaApplicationName, "GetResource Sample");
                agent.Initialize(persistor);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Agent initialization error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Create a resource request for "classification" marking values.
            // configured in the dashboard.
            GetResourcesRequest.Resource resourceRequest = new GetResourcesRequest.Resource();          
            resourceRequest.ResourceId = "marking-values";
            resourceRequest.Args = "classification";
            GetResourcesResponse resourcesResponse = null;

            // Fetch the resource "classification" marking values.
            try
            {
                resourcesResponse = agent.GetResource(resourceRequest);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Agent GetResources error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Extract the resources from the get resources response.
            List<GetResourcesResponse.Resource> resources = resourcesResponse.Resources;
            if (resources.Count == 0) {
                Console.WriteLine("There are no resources available");
                WaitForInput();
                Environment.Exit(1);
            }

            Console.WriteLine("Classification values:");
            foreach (GetResourcesResponse.Resource resource in resources)
            {
                Console.WriteLine("Data   : " + resource.Data);
            }

            WaitForInput();
        }
    }
}
