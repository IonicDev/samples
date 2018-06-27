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
            // Get the user's home path and password persistor from the environment.
            String homePath = Environment.GetEnvironmentVariable("USERPROFILE");
            
            // Initialize the agent.
            Agent agent = new Agent();

            try
            {
                // Create a password persistor for agent initialization.
                DeviceProfilePersistorPlaintext persistor = new DeviceProfilePersistorPlaintext();
                persistor.FilePath = homePath + "\\.ionicsecurity\\profiles.pt";
 
                agent.SetMetadata(Agent.MetaApplicationName, "Initialize agent with plaintext persistor");
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
