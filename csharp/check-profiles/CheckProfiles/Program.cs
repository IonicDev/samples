/*
 * (c) 2018 Ionic Security Inc.
 *
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html) and the
 * Privacy Policy (https://www.ionic.com/privacy-notice/).
 *
*/

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using IonicSecurity.SDK;

namespace CheckProfiles
{
    class Program
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

                agent.SetMetadata(Agent.MetaApplicationName, "Encrypting files generically");
                agent.Initialize(persistor);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Agent initialization error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Check if there are profiles. 
            // This code below will work for all persistor types.
            if (! agent.HasAnyProfiles)
            {
                Console.WriteLine("There are no device profiles on this device.");
                WaitForInput();
                return;
            }

            foreach (DeviceProfile profile in agent.AllProfiles)
            {
                Console.WriteLine("Name: " + profile.Name +
                                  ", Id: " + profile.DeviceId);
            }

            // Check if there is an active profile.
            if (agent.HasActiveProfile)
            {
                Console.WriteLine("\nActive profile, Name: " + agent.ActiveProfile.Name +
                                  ", Id: " + agent.ActiveProfile.DeviceId);
            }
            else
            {
                Console.WriteLine("There is not an active device profile selected on this device.");
            }

            WaitForInput();
        }
    }
}