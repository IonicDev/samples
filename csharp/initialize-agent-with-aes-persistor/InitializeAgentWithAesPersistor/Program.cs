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
    class InitializeAgentWithAesPersistor
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
            string persistorKeyHex = "A0444B8B5A7209780823617A98986831B8240BAA851A0B1696B0329280286B17";
            string persistorAuthData = "persistor auth data";
            
            String persistorPassword = Environment.GetEnvironmentVariable("IONIC_PERSISTOR_PASSWORD");
            if (persistorPassword == null || persistorPassword.Length == 0)
            {
                Console.WriteLine("Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD");
                WaitForInput();
                Environment.Exit(1);
            }

            // Initialize the agent.
            Agent agent = new Agent();

            try
            {
                // Create a password persistor for agent initialization.
                DeviceProfilePersistorAesGcm persistor = new DeviceProfilePersistorAesGcm();
                persistor.FilePath = homePath + "\\.ionicsecurity\\profiles.aes";
                persistor.KeyHex = persistorKeyHex;
                persistor.AuthDataHex = persistorAuthData;
 
                agent.SetMetadata(Agent.MetaApplicationName, "Initialize agent with AES persistor");
                agent.Initialize(persistor);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Agent initialization error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Get the profiles and check if the are any.
            List<DeviceProfile> profiles = agent.AllProfiles;
            if (profiles.Count == 0)
            {
                Console.WriteLine("No profiles for password persistor.");
                WaitForInput();
                return;
            }

            // Display profile information.
            foreach (DeviceProfile profile in profiles)
            {
                Console.WriteLine("-----");
                Console.WriteLine("ID       : " + profile.DeviceId);
                Console.WriteLine("Name     : " + profile.Name);
                Console.WriteLine("Keyspace : " + profile.KeySpace);
                Console.WriteLine("API URL  : " + profile.Server);
            }

            WaitForInput();
        }
    }
}
