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
    class LogSdkCalls
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
            string logFilePath = "../../../../../../sample-data/logs/sample.log";
            bool appendFile = false;

            // Log SDK to a file with serverity Debug and above.
            LogBase logger = LogFactory.Instance.CreateSimple(logFilePath, appendFile, LogSeverity.SEV_DEBUG);
            Log.SetSingleton(logger);

            // The message to encrypt.
            String message = "top secret message";

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

                agent.SetMetadata(Agent.MetaApplicationName, "Log SDK to a file");
                agent.Initialize(persistor);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Agent initialization error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Create single key without attributes.
            CreateKeysResponse.Key key = null;
            try
            {
                key = agent.CreateKey().Keys[0];
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Key creation error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Initialize chunk cipher object.
            ChunkCipherAuto cipher = new ChunkCipherAuto(agent);

            // Encrypt
            string cipherText = null;
            try
            {
                cipher.Encrypt(message, ref cipherText);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Chunk cipher encrypt error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Decrypt
            string plainText = null;
            try
            {
                cipher.Decrypt(cipherText, ref plainText);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Chunk cipher decrypt error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Verify encrypt and decrypt worked.
            if (message != plainText)
            {
                Console.WriteLine("Encryption/Decrption does not match!");
                Console.WriteLine("Message: {0} - PlainText: {1}", message, plainText);
                WaitForInput();
                Environment.Exit(1);
            }

            Console.WriteLine("CipherText : {0}", cipherText);
            Console.WriteLine("PlainText  : {0}", plainText);

            WaitForInput();
            
        }
    }
}
