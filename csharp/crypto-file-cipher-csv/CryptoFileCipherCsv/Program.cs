/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using System.IO;
using System.Collections.Generic;
using IonicSecurity.SDK;

namespace Samples
{
    class CryptoFileCipherCsv
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


        // Prints a message, waits for any input, and exits.
        // This is for exiting with an error.
        static void ErrorExitWithMessage(String message)
        {
            Console.WriteLine(message);
            WaitForInput();
            Environment.Exit(1);
        }


        static int Main(string[] args)
        {
            // The files to encrypt from and decrypt to.
            string fileOriginal   = "../../../../../../sample-data/files/info.csv";
            string fileCipherText = "./info-protected.csv";
            string filePlainText  = "./info.csv";

            // Get the user's home path and password persistor from the environment.
            String homePath = Environment.GetEnvironmentVariable("USERPROFILE");

            String persistorPassword = Environment.GetEnvironmentVariable("IONIC_PERSISTOR_PASSWORD");
            if (persistorPassword == null || persistorPassword.Length == 0)
            {
                ErrorExitWithMessage("Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD");
            }

            // Create an agent object to talk to Ionic.
            Agent agent = new Agent();

            // Create a password persistor for agent initialization.
            try
            {         
                DeviceProfilePersistorPassword persistor = new DeviceProfilePersistorPassword();
                persistor.FilePath = homePath + "\\.ionicsecurity\\profiles.pw";
                persistor.Password = persistorPassword;

                agent.SetMetadata(Agent.MetaApplicationName, "CryptoFileCipherCsv Sample");
                agent.SetMetadata(Agent.MetaClientVersion, "1.0.0");
                agent.Initialize(persistor);
            }
            catch (SdkException sdkExp)
            {
                ErrorExitWithMessage("Agent initialization error: " + sdkExp.Message);
            }

            // Create single key without attributes.
            CreateKeysResponse.Key key = null;
            try
            {
                key = agent.CreateKey().Keys[0];
            }
            catch (SdkException sdkExp)
            {
                ErrorExitWithMessage("Key creation error: " + sdkExp.Message);
            }

            // Define mutable attributes and empty fixed attributes.
            AttributesDictionary mutableKeyAttrs = new AttributesDictionary();
            AttributesDictionary fixedKeyAttrs = new AttributesDictionary();
            mutableKeyAttrs.Add("classification", new List<string> { "Restricted" });
            FileCryptoEncryptAttributes fileCryptoEncryptAttrs =
            new FileCryptoEncryptAttributes(fixedKeyAttrs, mutableKeyAttrs);

            // Initialize OpenXML file cipher object.
            CsvFileCipher cipher = new CsvFileCipher(agent);

            // Encrypt
            try
            {
                Console.WriteLine("Encrypting file {0} and saving in cipher file {1}", fileOriginal, fileCipherText);
                cipher.Encrypt(fileOriginal, fileCipherText, ref fileCryptoEncryptAttrs);
            }
            catch (SdkException sdkExp)
            {
                ErrorExitWithMessage("CSV file cipher encrypt error: " + sdkExp.Message);
            }
   
            // Decrypt
            try
            {
                Console.WriteLine("Decrypting file {0} and saving in plaintext file {1}", fileCipherText, filePlainText);
                cipher.Decrypt(fileCipherText, filePlainText);
            }
            catch (SdkException sdkExp)
            {
                ErrorExitWithMessage("CSV file cipher decrypt error: " + sdkExp.Message);
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

