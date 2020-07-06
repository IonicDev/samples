/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * Developed with Ionic Java SDK 2.1.0
 */

package com.ionic.samples;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;

public class LogToConsole
{
    public static void main(String[] args)
    {
        String sLogChannel = "com.ionic.sdk.sampleapp";

        // initialize logger
        final Logger logger = Logger.getLogger(sLogChannel);
        ConsoleHandler ch = new ConsoleHandler();
        logger.addHandler(ch);
        logger.setLevel(Level.INFO);

        // write log 
        logger.info("Sample log entry");
    }
}
