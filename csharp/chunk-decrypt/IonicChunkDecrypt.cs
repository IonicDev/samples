/*
 * (c) 2016-2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using IonicSecurity.SDK;

namespace IonicChunkDecrypt
{
    class Program
    {
        static void Main(string[] args)
        {
            string decryptedText = null;
            string encryptedText = "{SET_ENCRYPTED_TEXT_HERE}";
            if (encryptedText == "{SET_ENCRYPTED_TEXT_HERE}")
            {
                Console.WriteLine("You MUST set the output from IonicHelloWorld (or similar to decrypt).");
                Console.ReadKey();
            }
            Agent agent = new Agent();
            agent.Initialize();

            ChunkCipherAuto chunkCrypto = new ChunkCipherAuto(agent);

            chunkCrypto.Decrypt(encryptedText, ref decryptedText);
            Console.WriteLine("Chunk-Encrypted String: ", encryptedText);
            Console.WriteLine("Decrypted String: ", decryptedText);
            Console.ReadKey();

        }
    }
}
