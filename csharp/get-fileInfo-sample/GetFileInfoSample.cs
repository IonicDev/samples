/*
 * (c) 2018 Ionic Security Inc.
 *
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html) and the
 * Privacy Policy (https://www.ionic.com/privacy-notice/).
 *
*/

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using IonicSecurity.SDK;

namespace GetFileInfoSample
{
    class Program
    {
        static void Main(string[] args)
        {
            string filepath = null;
            FileInfo fileinfo;

            Console.Write("Please enter the path to the file: ");
            filepath = Console.ReadLine();

            try
            {
                fileinfo = FileCrypto.GetFileInfo(filepath);
                if (fileinfo.Encrypted)
                {
                    Console.WriteLine("The file \"" + filepath + "\" is encrypted using key " + fileinfo.KeyId + ".");
                }
                else
                {
                    Console.WriteLine("The file \"" + filepath + "\" is not encrypted.");
                }
            }
            catch (SdkException e)
            {
                Console.WriteLine(e);
            }

            Console.Write("\nPress Enter to continue...");
            Console.ReadKey();
        }
    }
}
