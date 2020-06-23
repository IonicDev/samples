/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using System.Collections.Generic;
using System.Web.Script.Serialization;
using IonicSecurity.SDK;

namespace Samples
{
    class UpdateKey
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
            // Please set keyId to a key you have already created.
            String keyId = null;

            if (keyId == null)
            {
                Console.WriteLine("Please set the keyId to a key you have already created.");
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

                agent.SetMetadata(Agent.MetaApplicationName, "UpdateKey Sample");
                agent.Initialize(persistor);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Agent initialization error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }
           
            // Fetch the key from the agent.
            GetKeysResponse fetchedResponse = null;
            try
            {
                fetchedResponse = agent.GetKey(keyId);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Error fetching key {0}: {1}", keyId, sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Pull the key out of the response.
            GetKeysResponse.Key fetchedKey = fetchedResponse.Keys[0];

            // Define mutable key attributes
            AttributesDictionary newMutableKeyAttrs = new AttributesDictionary();
            newMutableKeyAttrs.Add("classification", new List<string> { "Highly Restricted" });

            // Create the update key request.
            bool forceUpdate = false;
            UpdateKeysRequest updateKeysRequest = new UpdateKeysRequest();
            UpdateKeysRequest.Key updateKey = new UpdateKeysRequest.Key(fetchedKey, forceUpdate);
            updateKey.MutableAttributes = newMutableKeyAttrs;
            updateKeysRequest.addKey(updateKey);

            // Update the key attributes on the agent.
            UpdateKeysResponse.Key key = null;
            try
            {
                key = agent.UpdateKeys(updateKeysRequest).Keys[0];
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Error updating key {0}: {1}", keyId, sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            
            Console.WriteLine("Key ID             : " + key.Id);
            Console.WriteLine("Key Bytes          : " + BitConverter.ToString(key.KeyBytes).Replace("-", String.Empty));
            Console.WriteLine("Fixed Attributes   : " + JsonDump(key.Attributes));
            Console.WriteLine("Mutable Attributes : " + JsonDump(key.MutableAttributes));

            WaitForInput();
            return 0;
        }
    }
}

