/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * Developed with Ionic Java SDK 2.1.0
 */

package com.ionic.samples;

import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.error.IonicException;

public class InitializeAgentNoPersistor
{
    public static void main(String[] args)
    {
        // initialize agent with no persistor
        Agent agent = new Agent();
        try {
            agent.initializeWithoutProfiles();
        } catch(IonicException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
