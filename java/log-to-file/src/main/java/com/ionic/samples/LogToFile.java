/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * Developed with Ionic Java SDK 2.1.0
 */

package com.ionic.samples;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.io.IOException;

public class LogToFile
{
    public static void main(String[] args)
    {
        String sLogChannel = "com.ionic.sdk.sampleapp";
        String sLogPath = "./sample-log.txt";

        // initialize logger
        FileHandler fh = null;
        try {
            fh = new FileHandler(sLogPath);
        } catch (IOException e) {
            System.out.println("Failed to open file handle to: " + sLogPath);
            System.exit(1);
        }
        final Logger logger = Logger.getLogger(sLogChannel);
        logger.addHandler(fh);
        logger.setLevel(Level.INFO);

        // write log 
        logger.info("Sample log entry");
    }
}
