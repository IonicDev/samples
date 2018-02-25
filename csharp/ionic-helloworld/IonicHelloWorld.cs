/*
 * (c) 2016-2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using IonicSecurity.SDK;

namespace IonicHelloWorld
{
    class Program
    {
        static int Main(string[] args)
        {
            // Set input string
            string input = "Hello World!";

            // Setup an agent object to talk to Ionic
            Agent agent = new Agent();
            agent.Initialize();

            // Check if there are profiles.
            if (!agent.HasAnyProfiles)
            {
                Console.WriteLine("There are no device profiles on this device.");
                Console.WriteLine("Register a device before continuing.");
                return -1;
            }

            // Setup a Chunk Crypto object to handle Ionic encryption
            ChunkCipherAuto chunkCrypto = new ChunkCipherAuto(agent);

            // Encrypt the string using an Ionic-managed key, and validate the response
            string encryptedText = null;
            try
            {
                chunkCrypto.Encrypt(input, ref encryptedText);
            }
            catch (SdkException e)
            {
                Console.WriteLine("Error encrypting: {0}", e.Message);
                Console.ReadKey();
                return -2;
            }

            Console.WriteLine("Plain Text: {0}", input);
            Console.WriteLine("Ionic Chunk Encrypted Text: {0}", encryptedText);

            Console.WriteLine("Press return to exit.");
            Console.ReadKey();
            return 0;
        }
    }
}