/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using System.IO;
using System.Collections.Generic;
using IonicSecurity.SDK;

namespace Samples
{
    class CryptoFileCipherGeneric
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
            // The files to encrypt from and decrypt to.
            string fileOriginal   = "../../../../../../sample-data/files/Message.txt";
            string fileCipherText = "./Message-Protected.txt";
            string filePlainText  = "./Message.txt";

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

                agent.SetMetadata(Agent.MetaApplicationName, "CryptoFileCipherGeneric Sample");
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

            // Define mutable attributes and empty fixed attributes.
            AttributesDictionary mutableKeyAttrs = new AttributesDictionary();
            AttributesDictionary fixedKeyAttrs = new AttributesDictionary();
            mutableKeyAttrs.Add("classification", new List<string> { "Restricted" });
            FileCryptoEncryptAttributes fileCryptoEncryptAttrs =
            new FileCryptoEncryptAttributes(fixedKeyAttrs, mutableKeyAttrs);

            // Initialize generic file cipher object.
            GenericFileCipher cipher = new GenericFileCipher(agent);

            // Encrypt
            try
            {
                Console.WriteLine("Encrypting file {0} and saving in cipher file {1}", fileOriginal, fileCipherText);
                cipher.Encrypt(fileOriginal, fileCipherText, ref fileCryptoEncryptAttrs);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Generic file cipher encrypt error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }
   
            // Decrypt
            try
            {
                Console.WriteLine("Decrypting file {0} and saving in plaintext file {1}", fileCipherText, filePlainText);
                cipher.Decrypt(fileCipherText, filePlainText);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Generic file cipher decrypt error: " + sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Read the files for comparison.
            string message = File.ReadAllText(fileOriginal);
            string plainText = File.ReadAllText(filePlainText);

            // Verify encrypt and decrypt worked.
            if (message != plainText)
            {
                Console.WriteLine("Encryption/Decrption does not match!");
                Console.WriteLine("Message: {0} - PlainText: {1}", message, plainText);
                WaitForInput();
                Environment.Exit(1);
            }

            WaitForInput();
            return 0;
        }
    }
}

