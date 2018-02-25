/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.examples;

import com.ionic.examples.LogConfig;
import com.ionic.examples.ProfilePersistorFactory;
import com.ionic.examples.LogConfig.FilterLevel;
import com.ionicsecurity.sdk.AgentSdk;
import com.ionicsecurity.sdk.Agent;
import com.ionicsecurity.sdk.DeviceProfilePersistorBase;
import com.ionicsecurity.sdk.Log;
import com.ionicsecurity.sdk.LogBase;
import com.ionicsecurity.sdk.LogFactory;

public class IonicAgentFactory {
	static boolean isInitialized = false;
	//NOTE: For production this level should be increased, likely to INFO or WARN:
	static FilterLevel LogLevel = FilterLevel.TRACE;

	protected IonicAgentFactory() {
		
	}
	
	/**
	 * Configure and return an Agent. We use an Agent per thread. Most of the
	 * SDK is not thread-safe and requires this model.
	 * 
	 * @return an Ionic SDK Agent
	 */
	public static Agent getAgent() {
		if (!isInitialized) {
			AgentSdk.initialize(null);
			initLog();
		}

		// This is done so an Agent can be constructed with no requirement for a pre-existing
		// SEP to be available when the agent is initialized.
		DeviceProfilePersistorBase<?> profilePersistor = ProfilePersistorFactory.getPersistor();

		// Construct an Agent from that configuration
		Agent agent = new Agent();
		agent.initialize(profilePersistor);

		return agent;
	}

	/**
	 * Initializes the SDK logging. We set it to log output to CONSOLE, we log
	 * messages from all available channels in the SDK, and set the minimum
	 * priority level of messages that are actually logged to the value of
	 * LogLevel
	 * 
	 * @see LogLevel
	 */
	private static void initLog() {
		/*
		 * The log configuration can be read from a file or a String.
		 * In this example, we use a utility class, LogConfig, to build up the string programatically.
		 */
		LogConfig logConfig = new LogConfig();

		LogConfig.LogFilter logFilter = logConfig.new LogFilter(LogConfig.FilterType.SEVERITY, LogLevel);
		LogConfig.LogSink logSink = logConfig.new LogSink();
		logSink.setFilter(logFilter);

		logSink.addChannel(LogConfig.Channel.ALL);

		// The default writer is to Console
		LogConfig.LogWriter logWriter = logConfig.new LogWriter();
		logSink.addWriter(logWriter);
		logConfig.addSink(logSink);

		// This serializes the JSON that the Ionic SDK expects as configuration input:
		String logConfigJson = logConfig.toJson();
		// Tell the SDK to use this logger created above using the LogConfig class:
		LogBase logger = LogFactory.getInstance().createFromConfig(logConfigJson);
		Log.setSingleton(logger);
	}

}
