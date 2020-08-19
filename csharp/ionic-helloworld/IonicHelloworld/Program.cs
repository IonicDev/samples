/*
 * (c) 2018-2020-2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using IonicSecurity.SDK;


namespace Samples
{
    class IonicHelloworld
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

            // Create a password persistor for agent initialization.
            String persistorPath = homePath + "\\.ionicsecurity\\profiles.pw"
            DeviceProfilePersistorPassword persistor = new DeviceProfilePersistorPassword();
            persistor.FilePath = persistorPath;
            persistor.Password = persistorPassword;

            try
            {
                // Create an agent object to talk to Ionic.
                Agent agent = new Agent();
                agent.SetMetadata(Agent.MetaApplicationName, "IonicHelloWorld Sample");
                agent.Initialize(persistor);
            }
            catch (SdkException sdkExp)
            {
                Console.WriteLine("Failed to initialize agent from password persistor ({0})", persistorPath);
                Console.WriteLine(sdkExp.Message);
                WaitForInput();
                Environment.Exit(1);
            }

            // Setup the Chunk Crypto object.
            ChunkCipherAuto chunkCrypto = new ChunkCipherAuto(agent);

            string clearText = "Hello, World!";
            string encryptedText = null;

            // Define data markings
            AttributesDictionary dataMarkings = new AttributesDictionary();
            dataMarkings.Add("clearance-level", new List<string> { "secret" });

            // Encrypt the string using an Ionic-managed key.
            chunkCrypto.Encrypt(clearText, ref encryptedText, dataMarkings);

            string decryptedText = null;

            // Note: Decryption only works if the policy allows it.
            chunkCrypto.Decrypt(encryptedText, decryptedText);

            Console.WriteLine("Plain Text: {0}", clearText);
            Console.WriteLine("Ionic Chunk Encrypted Text: {0}", encryptedText);
            Console.WriteLine("Decrypted text: {0}", decryptedText);

            WaitForInput();
        }
    }
}
