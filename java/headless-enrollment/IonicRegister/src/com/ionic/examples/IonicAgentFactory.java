package com.ionic.examples;

import com.ionic.examples.IonicApplicationMetadata;
import com.ionic.examples.LogConfig;
import com.ionic.examples.ProfilePersistorFactory;
import com.ionic.examples.LogConfig.FilterLevel;
import com.ionicsecurity.sdk.Agent;
import com.ionicsecurity.sdk.AgentConfig;
import com.ionicsecurity.sdk.AgentSdk;
import com.ionicsecurity.sdk.DeviceProfilePersistorBase;
import com.ionicsecurity.sdk.Log;
import com.ionicsecurity.sdk.LogBase;
import com.ionicsecurity.sdk.LogFactory;

public class IonicAgentFactory {
	static boolean isInitialized = false;
	static FilterLevel LogLevel = FilterLevel.TRACE;

	protected IonicAgentFactory() {
		
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
		 * The log configuration can be read from a file or a String. We build
		 * it in-line for this example
		 */
		LogConfig logConfig = new LogConfig();

		LogConfig.LogFilter logFilter = logConfig.new LogFilter(LogConfig.FilterType.SEVERITY, LogLevel);
		LogConfig.LogSink logSink = logConfig.new LogSink();
		logSink.setFilter(logFilter);

		logSink.addChannel(LogConfig.Channel.ALL);

		// the default writer is to Console
		// LogConfig.LogWriter logWriter = logConfig.new
		// LogWriter("/Users/bill/ionicnmhsdk.log", logFilter);
		LogConfig.LogWriter logWriter = logConfig.new LogWriter();
		logSink.addWriter(logWriter);
		logConfig.addSink(logSink);

		String logConfigJson = logConfig.toJson();
		LogBase logger = LogFactory.getInstance().createFromConfig(logConfigJson);
		Log.setSingleton(logger);
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

		DeviceProfilePersistorBase<?> profilePersistor = ProfilePersistorFactory.getPersistor();

		AgentConfig config = new AgentConfig();

		// Construct an Agent from that configuration
		Agent agent = new Agent();

		// We set the standard metadata for the application
		agent.setMetadata(IonicApplicationMetadata.getMetadataMap());
		agent.initialize(config, profilePersistor);

		return agent;
	}

}
