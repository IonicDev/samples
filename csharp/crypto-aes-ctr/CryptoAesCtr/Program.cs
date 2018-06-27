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
    class CryptoAesCtr
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


        static int Main(string[] args)
        {
            // The message to encrypt.
            String message = "secret message";
            
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

                agent.SetMetadata(Agent.MetaApplicationName, "Encrypting with AES Counter Mode");
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

            byte[] keyBytes = key.KeyBytes;

            // Initialize AES CTR cipher object.
            AesCtrCipher aes = new AesCtrCipher();
            aes.KeyBytes = keyBytes;

            // Encrypt
            byte[] cipherText = new byte[256];
            aes.Encrypt(message, ref cipherText);
   
            // Decrypt
            string plainText = null;
            aes.Decrypt(cipherText, ref plainText);

            // Verify encrypt and decrypt worked.
            if (message != plainText)
            {
                Console.WriteLine("Encryption/Decrption does not match!");
                Console.WriteLine("Message: {0} - PlainText: {1}", message, plainText);
                WaitForInput();
                Environment.Exit(1);
            }

            Console.WriteLine("CipherText : {0}", BitConverter.ToString(cipherText).Replace("-", String.Empty));
            Console.WriteLine("PlainText  : {0}", plainText);

            WaitForInput();
            return 0;
        }
    }
}

