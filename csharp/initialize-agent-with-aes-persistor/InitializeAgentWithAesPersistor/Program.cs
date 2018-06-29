/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using System.Collections.Generic;
using System.Text;

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
            // Set the persistor's path, key value, and authorization data.
            // Information can be found in ../../../../../../sample-data/persistors/README.md.
            String persistorPath = "../../../../../../sample-data/persistors/sample-persistor.aes";
            string persistorKeyHex = "A0444B8B5A7209780823617A98986831B8240BAA851A0B1696B0329280286B17";
            string persistorAuthData = "persistor auth data";

            // Create a blank agent.
            Agent agent = new Agent();

            // Create an AES persistor and initialize agent.
            try
            {               
                DeviceProfilePersistorAesGcm persistor = new DeviceProfilePersistorAesGcm();
                persistor.FilePath = persistorPath;
                persistor.KeyHex = persistorKeyHex;
                persistor.AuthDataBytes = Encoding.ASCII.GetBytes(persistorAuthData);

                agent.SetMetadata(Agent.MetaApplicationName, "InitializeAgentWithAesPersistor Sample");
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
