/*
 * (c) 2016-2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using IonicSecurity.SDK;

namespace logging
{
   class Logging
    {
        static int Main(string[] args)
        {
            LogBase logger = LogFactory.GetInstance.CreateSimple("sample.log", false, LogSeverity.SEV_DEBUG);
            Log.SetSingleton(logger);

            Agent agent = new Agent();
            agent.Initialize();

            string input = "Hello World";

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

            chunkCrypto.Encrypt(input, ref encryptedText);
            Console.WriteLine("Plain Text: {0}", input);
            Console.WriteLine("Ionic Chunk Encrypted Text: {0}", encryptedText);
            Console.ReadKey();
            return 0;
        }
    }
}