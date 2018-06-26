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
            // Initialize agent with default persistor
            DeviceProfilePersistorDefault persistor = new DeviceProfilePersistorDefault();
            Agent agent = new Agent();
            try
            {
                agent.SetMetadata(Agent.MetaApplicationName, "Initialize agent with default persistor");
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
                Environment.Exit(1);
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