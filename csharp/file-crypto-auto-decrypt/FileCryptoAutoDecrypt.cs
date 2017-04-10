/*
 * (c) 2016-2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using IonicSecurity.SDK;

namespace FileCryptoAutoDecrypt
{
    class Program
    {
        static int Main(string[] args)
        {
            // Set these however you would like
            string pathToEncryptedFile = @"example_file_encrypted.docx";
            string pathToDecryptedFile = @"example_file_decrypted.docx";

            Agent agent = new Agent();
            agent.Initialize();

            AutoFileCipher fileCipher = new AutoFileCipher(agent);

            // Decrypt
            try
            {
                FileCryptoDecryptAttributes decryptAttributes = new FileCryptoDecryptAttributes();
                fileCipher.Decrypt(pathToEncryptedFile, pathToDecryptedFile, ref decryptAttributes);
            }
            catch (SdkException e)
            {
                Console.WriteLine("Error decrypting {0}: {1}", pathToEncryptedFile, e.Message);
                Console.ReadKey();
                return -2;
            }

            Console.WriteLine("Decrypted file {0} to {1}.", pathToEncryptedFile, pathToDecryptedFile);
            Console.ReadKey();
            return 0;
        }
    }
}
