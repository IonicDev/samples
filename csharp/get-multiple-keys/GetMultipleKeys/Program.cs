/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using System.Collections.Generic;
using System.Web.Script.Serialization;
using IonicSecurity.SDK;

namespace Samples
{
    class GetMultipleKeys
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


        // Formats JSON.
        static String JsonDump(AttributesDictionary ad)
        {
            return new JavaScriptSerializer().Serialize(ad);

        }


        static int Main(string[] args)
        {
            // Please set keyIds to keys you have already created.
            String keyId1 = null;
            String keyId2 = null;
            String keyId3 = null;

            if (keyId1 == null || keyId2 == null || keyId3 == null)
            {
                Console.WriteLine("Please set the keyIds to keys you have already created.");
                WaitForInput();
                Environment.Exit(1);
            }
            
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

                agent.SetMetadata(Agent.MetaApplicationName, "GetMultipleKeys Sample");
                agent.Initialize(persistor);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Agent initialization error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Create a key request for multiple keys.
            GetKeysRequest keyRequest = new GetKeysRequest();
            keyRequest.KeyIds.Add(keyId1);
            keyRequest.KeyIds.Add(keyId2);
            keyRequest.KeyIds.Add(keyId3);

            // Fetch multiple keys from the agent.
            GetKeysResponse getKeysResponse = null;
            try
            {
                getKeysResponse = agent.GetKeys(keyRequest);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Error fetching keys: {1}", sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Pull the keys out of the response.
            List<GetKeysResponse.Key> keys = getKeysResponse.Keys;
            if (keys.Count == 0)
            {
                Console.WriteLine("No keys returned for external IDs. (Key does not exist or access was denied.)");
                WaitForInput();
                Environment.Exit(1);
            }

            foreach (GetKeysResponse.Key key in keys)
            {
                Console.WriteLine("-----");
                Console.WriteLine("Key ID             : " + key.Id);
                Console.WriteLine("Key Bytes          : " + BitConverter.ToString(key.KeyBytes).Replace("-", String.Empty));
                Console.WriteLine("Fixed Attributes   : " + JsonDump(key.Attributes));
                Console.WriteLine("Mutable Attributes : " + JsonDump(key.MutableAttributes));
            }
            
            WaitForInput();
            return 0;
        }
    }
}

