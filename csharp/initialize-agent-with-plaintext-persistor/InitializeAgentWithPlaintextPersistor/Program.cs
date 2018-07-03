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
    class InitializeAgentWithPlaintextPersistor
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
            // Set the persistor's path.
            String persistorPath = "../../../../../../sample-data/persistors/sample-persistor.pt";

            // Create an agent object to talk to Ionic.
            Agent agent = new Agent();

            // Create a plaintext persistor and initialize the agent.
            try
            {              
                DeviceProfilePersistorPlaintext persistor = new DeviceProfilePersistorPlaintext();
                persistor.FilePath = persistorPath;
 
                agent.SetMetadata(Agent.MetaApplicationName, "InitializeAgentWithPlaintextPersistor Sample");
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
                Console.WriteLine("No profiles for plaintext persistor.");
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
