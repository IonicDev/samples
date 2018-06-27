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
    class InitializeAgentWithPasswordPersistor
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

            // Initialize the agent.
            Agent agent = new Agent();

            try
            {
                // Create a password persistor for agent initialization.
                DeviceProfilePersistorPassword persistor = new DeviceProfilePersistorPassword();
                persistor.FilePath = homePath + "\\.ionicsecurity\\profiles.pw";
                persistor.Password = persistorPassword;
 
                agent.SetMetadata(Agent.MetaApplicationName, "Initialize agent with password persistor");
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
