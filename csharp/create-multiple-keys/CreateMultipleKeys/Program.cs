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
    class CreateMultipleKeys
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

                agent.SetMetadata(Agent.MetaApplicationName, "Create Multiple Keys");
                agent.Initialize(persistor);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Agent initialization error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            String keyRef = "sample";
            int keyCount = 5;

            // Create multiple keys request.
            CreateKeysRequest.Key keysToCreate = new CreateKeysRequest.Key(keyRef, keyCount);
            CreateKeysRequest createKeysRequest = new CreateKeysRequest();
            createKeysRequest.Keys.Add(keysToCreate);

            // Invoke the agent to create the keys.
            CreateKeysResponse createKeysResponse = null;
            try
            {
                createKeysResponse = agent.CreateKeys(createKeysRequest);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Create multiple keys error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Pull the keys out of the response.
            List<CreateKeysResponse.Key> keys = createKeysResponse.Keys;

            foreach (CreateKeysResponse.Key key in keys)
            {
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

