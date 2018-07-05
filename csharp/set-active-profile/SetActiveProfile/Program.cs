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
    class SetActiveProfile
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


        // Displays the device profiles and show which one is active.
        static void DisplayProfiles(List<DeviceProfile> profiles, String activeDeviceId)
        {
            // Display profile information.
            foreach (DeviceProfile profile in profiles)
            {
                Console.WriteLine("-----");
                string idLine = (profile.DeviceId == activeDeviceId) ? profile.DeviceId + " (*)" : profile.DeviceId;
                Console.WriteLine("ID       : " + idLine);
                Console.WriteLine("Name     : " + profile.Name);
                Console.WriteLine("Keyspace : " + profile.KeySpace);
                Console.WriteLine("API URL  : " + profile.Server);
            }

            Console.WriteLine("");
        }


        static void Main(string[] args)
        {
            // Set the persistor's path.
            String persistorPath = "../../../../../../sample-data/persistors/sample-persistor.pt";

            // Create an agent object to talk to Ionic.
            Agent agent = new Agent();

            // Create a plaintext persistor and initialize the agent.
            DeviceProfilePersistorPlaintext persistor = new DeviceProfilePersistorPlaintext();
            try
            {
                persistor.FilePath = persistorPath;
 
                agent.SetMetadata(Agent.MetaApplicationName, "SetActiveProfile Sample");
                agent.Initialize(persistor);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Agent initialization error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Get the current active profile's device ID. 
            String activeDeviceId = agent.ActiveProfile.DeviceId;

            // Get the profiles and check if the are any.
            List<DeviceProfile> profiles = agent.AllProfiles;
            if (profiles.Count == 0)
            {
                Console.WriteLine("No profiles for plaintext persistor.");
                WaitForInput();
                return;
            }

            // Display current profile information.
            Console.WriteLine("Current Profiles:");
            DisplayProfiles(profiles, activeDeviceId);

            // If the number of profiles is equal to one, then there is nothing to set.
            if (profiles.Count == 1)
            {
                Console.WriteLine("Only one profile, nothing to change.");
                WaitForInput();
                return;
            }

            // Set the active profile to the hard-coded profile device ID.
            String profileDeviceId = "ABcd.1.48sdf0-cs80-5802-sd80-d8s0df80sdfj";
            try
            {              
                 agent.SetActiveProfileById(profileDeviceId);    
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Couldn't set active profile to: " + profileDeviceId);
                Console.WriteLine("SetActiveProfileById error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Get the current active profile's device ID again.
            activeDeviceId = agent.ActiveProfile.DeviceId;

            // Display modified profile information.
            Console.WriteLine("Modified Profiles:");
            DisplayProfiles(profiles, activeDeviceId);
            
            WaitForInput();
        }
    }
}
