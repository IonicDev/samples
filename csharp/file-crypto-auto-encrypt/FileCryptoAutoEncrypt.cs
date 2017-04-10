/*
 * (c) 2016-2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using IonicSecurity.SDK;

namespace FileCryptoAutoEncrypt
{
    class Program
    {
        static int Main(string[] args)
        {
            // Set these however you would like
            string pathToPlainFile = @"example_file.docx";
            string pathToEncryptedFile = @"example_file_encrypted.docx";

            Agent agent = new Agent();
            agent.Initialize();
          
            AutoFileCipher fileCipher = new AutoFileCipher(agent);
            // Create a classification
            FileCryptoEncryptAttributes encryptAttributes = new FileCryptoEncryptAttributes();
            encryptAttributes.KeyAttributes["classification"].Add("Restricted");

            // Encrypt the file using the classification, and validate the response
            try
            {
                // We will encrypt file cipher by path overload; see documentation to encrypt byte stream
                fileCipher.Encrypt(pathToPlainFile, pathToEncryptedFile, ref encryptAttributes);
            }
            catch (SdkException e)
            {
                Console.WriteLine("Error encrypting {0}: {1}", pathToPlainFile, e.Message);
                Console.ReadKey();
                return -2;
            }

            Console.WriteLine("Encrypted file {0} to {1}.", pathToPlainFile, pathToEncryptedFile);
            Console.ReadKey();
            return 0;
        }
    }
}