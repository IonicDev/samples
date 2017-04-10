/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using System.Collections.Generic;

using IonicSecurity.SDK;

namespace IonicHelloWorld
{
    class Program
    {
        static int convertSepDefaultToPassword(string passwordSepPath, string passwordSepPassword)
        {
            // Temporary variables to hold profiles during conversion.
            string sActiveDeviceId = null;
            List<IonicSecurity.SDK.DeviceProfile> lstProfiles = new List<IonicSecurity.SDK.DeviceProfile>();

            // First create an default persistor that will read in a SEP that you have with your platform's default persistor,
            // for example, one that you created using the Ionic Manager enrollment tool.
            DeviceProfilePersistorDefault defaultPersistor = new DeviceProfilePersistorDefault();
            try
            {
                defaultPersistor.LoadAllProfiles(ref lstProfiles, ref sActiveDeviceId);
            }
            catch (SdkException e)
            {
                Console.WriteLine("Error loading profiles from default SEP persistor: {0}", e.Message);
                return -1;
            }

            Console.WriteLine("{0} profiles loaded by the default persistor.", lstProfiles.Count);

            // Now we create another persistor that outputs the profiles we loaded in a password protected SEP
            DeviceProfilePersistorPassword passwordPersistor = new DeviceProfilePersistorPassword();
            passwordPersistor.FilePath = passwordSepPath;
            passwordPersistor.Password = passwordSepPassword;

            // Save the profiles using the password persistor.
            try
            {
                passwordPersistor.SaveAllProfiles(lstProfiles, sActiveDeviceId);
            }
            catch (SdkException e)
            {
                Console.WriteLine("Error saving profiles with the password-protected SEP: {0}", e.Message);
                return -2;
            }

            Console.WriteLine("Profiles saved to password persistor, in encrypted file {0}", passwordSepPath);
            return 0;
        }

        static int Main(string[] args)
        {
            LogBase logger = LogFactory.GetInstance.CreateSimple("sdk.log", false, LogSeverity.SEV_DEBUG);
            Log.SetSingleton(logger);

            // Set constants to use for the ISAgentDeviceProfilePersistorPassword
            string passwordSepPath = "profiles.pw"; //NOTE: On Linux, this file should typically be placed in ~/.ionicsecurity/profiles.pw
            string passwordSepPassword = "theSecretPasswordShouldNotBeHardcoded!";

            // First create an agent that will try to read in the SEP stored with a password persistor.
            DeviceProfilePersistorPassword passwordPersistor = new DeviceProfilePersistorPassword();
            passwordPersistor.FilePath = passwordSepPath;
            passwordPersistor.Password = passwordSepPassword;

            // Try reading with this persistor, to see if we have a password persisted SEP already:
            string sActiveDeviceId = null;
            List<IonicSecurity.SDK.DeviceProfile> lstProfiles = new List<IonicSecurity.SDK.DeviceProfile>();
            try
            {
                passwordPersistor.LoadAllProfiles(ref lstProfiles, ref sActiveDeviceId);
            }
            catch (SdkException e)
            {
                Console.WriteLine("Error loading from password persistor: {0}", e.Message);
                if (e.ErrorCodeEnum == ErrorCode.AGENT_RESOURCE_NOT_FOUND)
                {
                    Console.WriteLine("The file for the password persistor was not found at {0}.", passwordSepPath);
                }
                Console.WriteLine("We will now try to convert a default persistor SEP to the password persisted SEP for you.");
                if (0 != convertSepDefaultToPassword(passwordSepPath, passwordSepPassword))
                {
                    Console.ReadKey();
                    return -2;
                }
                Console.WriteLine("A password protected SEP should now exist.");
            }

            // Now that we have a password protected SEP, we can load it as we normally would when intitializing an agent:
            Agent agent = new Agent();
            try
            {
                agent.Initialize(passwordPersistor);
                Console.WriteLine("A password protected SEP was loaded from {0}", passwordSepPath);
                Console.WriteLine("{0} were loaded.", agent.AllProfiles.Count);
            }
            catch (SdkException e)
            {
                Console.WriteLine("Error initalizing agent: {0}", e.Message);
            }

            Console.ReadKey();
            return 0;
        }
    }
}