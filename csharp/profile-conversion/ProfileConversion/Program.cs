/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using System.Collections.Generic;

using IonicSecurity.SDK;

namespace Samples
{
    class ProfileConversion
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


        static List<DeviceProfile> GetDeviceProfilesFromPlaintextPersistor()
        {
            // Set the persistor's path.
            String persistorPath = "../../../../../../sample-data/persistors/sample-persistor.pt";

            // Create a blank agent.
            Agent ptAgent = new Agent();

            // Create a plaintext persistor and initialize the agent.
            try
            {
                DeviceProfilePersistorPlaintext persistor = new DeviceProfilePersistorPlaintext();
                persistor.FilePath = persistorPath;

                ptAgent.SetMetadata(Agent.MetaApplicationName, "ProfileConversion Sample (plaintext persistor)");
                ptAgent.Initialize(persistor);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Plaintext agent initialization error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Return the device profiles.
            List<DeviceProfile> profiles = ptAgent.AllProfiles;
            return profiles;


        }
        static void Main(string[] args)
        {      
            // Set the persistor's path.
            String passwordPersistorPath = "../../../../../../sample-data/persistors/convert-persistor.pw";
            String persistorPassword = "theSecretPasswordThatShouldNotBeHardcoded!";

            // Initialize the agent.
            Agent pwAgent = new Agent();

            // Create a password persistor and initialize agent initialization.
            DeviceProfilePersistorPassword passwordPersistor = new DeviceProfilePersistorPassword();
            try
            {                
                //DeviceProfilePersistorPassword passwordPersistor = new DeviceProfilePersistorPassword();
                passwordPersistor.FilePath = passwordPersistorPath;
                passwordPersistor.Password = persistorPassword;

                pwAgent.SetMetadata(Agent.MetaApplicationName, "ProfileConversion Sample (password persistor)");
                pwAgent.Initialize(passwordPersistor);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Password agent initialization error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);         
            }

            if (pwAgent.HasAnyProfiles)
            {
                Console.WriteLine("Password persistor already has profiles.");
                Console.WriteLine("Please delete the file " + passwordPersistorPath + " and rerun.");
                WaitForInput();
                Environment.Exit(1);
            }

            List<DeviceProfile> plaintextProfiles = GetDeviceProfilesFromPlaintextPersistor();
            int numPlaintextProfiles = plaintextProfiles.Count;
            Console.WriteLine("There are {0} plaintext profiles to convert.", numPlaintextProfiles);

            // Get each plaintext profile and add it to the password persistor.
            try
            {               
                foreach (DeviceProfile plaintextProfile in plaintextProfiles)
                {
                    pwAgent.AddProfile(plaintextProfile);
                }
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Add Profile error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Save the profiles added to the password persistor.
            try
            {
                //pwAgent.SaveProfiles();
                pwAgent.SaveProfiles(passwordPersistor);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Save Profile error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Get the profiles and check if the are any.
            List<DeviceProfile> profiles = pwAgent.AllProfiles;
            if (profiles.Count == 0)
            {
                Console.WriteLine("No profiles for password persistor.");
                WaitForInput();
                Environment.Exit(1);
                return;
            }

            // Display profile information.
            Console.WriteLine("Password Profiles:");
            foreach (DeviceProfile profile in profiles)
            {
                Console.WriteLine("-----");
                Console.WriteLine("ID       : " + profile.DeviceId);
                Console.WriteLine("Name     : " + profile.Name);
                Console.WriteLine("Keyspace : " + profile.KeySpace);
                Console.WriteLine("API URL  : " + profile.Server);
            }
            Console.WriteLine("-----");

            WaitForInput();
        }
    }
}
