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
    class LogToFile
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
            string sLogChannel = "ionic-csharp-sample";
            string logFilePath = "../../../../../../sample-data/logs/sample.log";

            // Log servity debug and lower to a file.
            String sConfigJson =
               "{ \"sinks\":  [ {\n" +
               "              \"channels\": [ \"" + sLogChannel + "\" ],\n" +
               "             \n" +
               "              \"filter\": { \"type\": \"Severity\",\n" +
               "                          \"level\": \"Debug\" },\n" +
               "                         \n" +
               "              \"writers\": [\n" +
               "                           { \"type\": \"File\",\n" +
               "                           \"filePattern\": \"sample-%Y.log\"},\n" +
               "                         ]\n" +
               "                }\n" +
               "              ]\n" +
               "}";
        
            Console.WriteLine(sConfigJson);
            Console.ReadKey();

            // Initialize logger
            LogBase logger = LogFactory.Instance.CreateFromConfig(sConfigJson);
            Log.SetSingleton(logger);

            Log.LogDebug(sLogChannel, "Sample log entry");

            WaitForInput();
            
        }
    }
}
