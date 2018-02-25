/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionicsecurity.examples;

import com.ionicsecurity.examples.LogConfig.FilterLevel;
import com.ionicsecurity.sdk.Agent;
import com.ionicsecurity.sdk.AgentSdk;
import com.ionicsecurity.sdk.AutoFileCipher;
import com.ionicsecurity.sdk.DeviceProfilePersistorPlainText;
import com.ionicsecurity.sdk.FileCryptoEncryptAttributes;
import com.ionicsecurity.sdk.KeyAttributesMap;
import com.ionicsecurity.sdk.Log;
import com.ionicsecurity.sdk.LogBase;
import com.ionicsecurity.sdk.LogFactory;
import com.ionicsecurity.sdk.SdkException;

import java.util.List;
import java.util.ArrayList;

public class FileCryptoAutoEncrypt {
	static final FilterLevel LogLevel = FilterLevel.TRACE;
	static final int LoopCnt = 2;
	
	/**
	 * Initializes the SDK logging.  We set it to log output to CONSOLE, we log messages from all available channels in the SDK,
	 * and set the minimum priority level of messages that are actually logged to the value of LogLevel
	 * @see LogLevel
	 */
	private static void initLog()
	{
		/* The log configuration can be read from a file or a String.  We build it in-line for this example */
		LogConfig logConfig = new LogConfig();

		LogConfig.LogFilter logFilter = logConfig.new LogFilter(LogConfig.FilterType.SEVERITY, LogLevel);
		LogConfig.LogSink logSink =  logConfig.new LogSink();
		logSink.setFilter(logFilter);
	
		logSink.addChannel(LogConfig.Channel.ISFILECOVERPAGE);

		// The default writer is to Console
		// You can write to a file via the following syntax:
		// LogConfig.LogWriter logWriter = logConfig.new LogWriter("/Users/myusername/coverpage.log", logFilter);
		LogConfig.LogWriter logWriter = logConfig.new LogWriter();
		logSink.addWriter(logWriter);
		logConfig.addSink(logSink);

		String logConfigJson = logConfig.toJson();
		LogBase logger = LogFactory.getInstance().createFromConfig(logConfigJson);
		Log.setSingleton(logger);
	}
	
	/*
	 * usage: <plaintext file> <ciphertext output base file name>
	 * 
	 * The will take a plaintext file and encrypt it based on its type (OpenXML, PDF, text, ...) and produce
	 * an Ionic protected output file with the appropriate format.  If custom cover pages exist they will be 
	 * downloaded and cached for future operations.  The code creates <LoopCnt> output files using the 
	 * <ciphertext output base file name> in order to demonstrate coverpage caching.
	 */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage args: <pathToPlaintext> <pathToTargetCiphertext>");
            return;
        }
        String pathToPlainFile = args[0];
        String pathToEncryptedFile = args[1];

        initLog();
        AgentSdk.initialize(null);
        Agent agent = new Agent();
        if (System.getProperty("os.name").startsWith("Linux")) {
            DeviceProfilePersistorPlainText ptPersistor = new DeviceProfilePersistorPlainText();
            String sProfilePath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pt";
            ptPersistor.setFilePath(sProfilePath);
            agent.initialize(ptPersistor);
        } else { // other OSes have a default persistor in the SDK, so we will use that
            agent.initialize();
       }

        // only do this the first time creating the cache.
        FileCryptoCoverPageImpl.cacheInit();

        // Loop for cache testing
        for (int i = 0; i < LoopCnt; i++ ) {
        	AutoFileCipher fileCipher = new AutoFileCipher(agent, new FileCryptoCoverPageImpl(agent));
  
        	FileCryptoEncryptAttributes encryptAttributes = new FileCryptoEncryptAttributes();
        	KeyAttributesMap attrMap = new KeyAttributesMap();
        	List<String> values = new ArrayList<String>();
        	values.add("secret"); // make sure you have access to keys marked as
                              // secret before you do add this key attribute
        	attrMap.set("classification", values);
        	encryptAttributes.setKeyAttributes(attrMap);

        	try {
        	    int extensionIndex = pathToEncryptedFile.lastIndexOf('.');
        	    String outputFilePath;
        	    if (extensionIndex > 0) {
        	      outputFilePath =  pathToEncryptedFile.substring(0, extensionIndex) + i + 
        				pathToEncryptedFile.substring(extensionIndex);
        	    } else {
        	       outputFilePath = pathToEncryptedFile + i;
        	    }
        		fileCipher.encrypt(pathToPlainFile, outputFilePath, encryptAttributes);
        	} catch (SdkException e) {
        		System.out.println("SDK Exception: " + e.getMessage() + ' ' +  e.getReturnCode());
        	}
        }
    }
}
