/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.examples;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * A class to define a Log Configuration that can be used to generate a JSON representation for
 * configuration of a Ionic SDK Log instance.
 */
public class LogConfig {
	/**
	 *  Filter levels
	 *  <li>{@link #TRACE}</li>
	 *  <li>{@link #DEBUG}</li>
	 *  <li>{@link #INFO}</li>
	 *  <li>{@link #WARN}</li>
	 *  <li>{@link #ERROR}</li>
	 *  <li>{@link #FATAL}</li>
	 */
	public enum FilterLevel {
        @SerializedName("TRACE") TRACE,
        @SerializedName("DEBUG") DEBUG,
        @SerializedName("INFO") INFO,
        @SerializedName("WARN") WARN,
        @SerializedName("ERROR") ERROR,
        @SerializedName("FATAL") FATAL      
	}
	
	/**
	 *  Filter types
	 *  <li>{@link #SEVERITY}</li>
	 */
	public enum FilterType {
        @SerializedName("Severity") SEVERITY
	}	
	
	/**
	 *  Writer types
	 *  <li>{@link #CONSOLE}</li>
	 *  <li>{@link #FILE}</li>
	 *  <li>{@link #ROTATING_FILE}</li>
	 */	
	public enum WriterType {
		/**
		 * Log to console
		 */
        @SerializedName("Console") CONSOLE,
        /**
         * Log to a file
         */
        @SerializedName("File") FILE,
        /**
         * Log to a rotating file scheme
         * @see RotationSchedule
         */
        @SerializedName("RotatingFile") ROTATING_FILE
	}
	
	/**
	 *  Schedule for rotating file log
	 *  <li>{@link #NONE}</li>
	 *  <li>{@link #DAILY}</li>
	 *  <li>{@link #HOURLY}</li>
	 *  <li>{@link #MINUTELY}</li>
	 */	
	public enum RotationSchedule {
		@SerializedName("NONE") NONE,
		@SerializedName("DAILY") DAILY,
		@SerializedName("HOURLY") HOURLY,
		@SerializedName("MINUTELY") MINUTELY		    
	}	
	
	/**
	 * Log channel to listen to
	 *  <li>{@link #ALL}</li>
	 *  <li>{@link #ISHTTP}</li>
	 *  <li>{@link #ISAGENT</li>
	 */
	public enum Channel {
		/**
		 * grab all messages 
		 */
		@SerializedName("*") ALL,
		/** 
		 * grab messages from ISHTTP
		 */
		@SerializedName("ISHTTP") ISHTTP,
		/** 
		 * grab messages from ISAgent
		 */
		@SerializedName("ISAgent") ISAGENT		    
	}			
	
	/**
	 * A class to define a LogFilter.
	 *
	 * @see FilterType
	 * @see LevelType
	 */
	class LogFilter {					
		private FilterType type;
		private FilterLevel level;
		
		public LogFilter(FilterType type, FilterLevel level) {
			this.type = type;
			this.level = level;
		}
		
		/**
		 * set the filter type
		 * 
		 * @param type
		 * @see FilterType
		 */
		public void setType(FilterType type) {
			this.type = type;
		}
		
		/**
		 * get the filter type
		 * 
		 * @return the filter type
		 * @see FilterType
		 */
		public FilterType getType() {
			return this.type;
		}
		
		/**
		 * Set the filter level
		 * @param level
		 * @see FilterLevel
		 */
		public void setLevel(FilterLevel level) {
			this.level = level;
		}
		
		/**
		 *  get the filter level
		 * @return the filter level
		 * @see FilterLevel
		 */
		public FilterLevel getLevel() {
			return this.level;
		}
	}
	
	/**
	 * A class to define a log writer
	 */
	class LogWriter {
		
		private WriterType type;
		private String filePattern;
		private LogFilter filter;
		private RotationSchedule rotationSchedule;
		private String rotationSize;

				
		private void initLogWriter (WriterType type, String filePattern, LogFilter filter, RotationSchedule rotationSchedule, String rotationSize) {
			this.type = type;
			this.filePattern = filePattern;
			this.filter = filter;
			this.rotationSchedule = rotationSchedule;
			this.rotationSize = rotationSize;		
		}
		
		/***
		 * @param type
		 * 
		 * @param filePattern
		 * <p>%Y - Four digit year (e.g. 2014)
		 * <p>%m - Two digit month with range 01 - 12 (e.g. 05)
		 * <p>%d - Two digit day with range 01 - 31 (e.g. 05)
		 * <p>%H - Two digit hour with range 00 - 11 (e.g. 05)
		 * <p>%M - Two digit minute with range 00 - 59 (e.g. 05)
		 * <p>%S - Two digit second 00 - 59 (e.g. 05)
	 	 * <p>
	 	 *<p>example:
	 	 *<p>"ionic_global.log"
		 *<p>"ionic_global_%Y-%m-%d.log" 
		 *<p>"ionic_debug_%Y-%m-%d_%H-%M-%S.log"
		 *
		 * @param filter
		 * @param rotationSchedule
		 * @param RotationSize:
		 * The size-based rotation threshold for the file. This value can be expressed in bytes, kilobytes, or megabytes. 
		 * The special value of "-1" will disable size-based rotation.
		 *	<p>Bytes - Byte count only (e.g. "1024", "51200", "8000")
		 *	<p>Kilobytes - Use "kb" postfix (e.g. "20kb", "100kb")
		 *	<p>Megabytes - Use "mb" postfix (e.g. "50mb", "100mb")
		 *
		 * @see LogFilter
		 * @see RotationSchedule
		 * @see WriterType
		 */	
		public LogWriter(String filePattern, LogFilter filter, RotationSchedule rotationSchedule, String rotationSize) {
			initLogWriter(WriterType.ROTATING_FILE, filePattern, filter, rotationSchedule, rotationSize);
		}
		
		/**
		 * Define a file writer
		 * @param filePattern
		 * @param filter
		 * @see LogWriter#LogWriter(WriterType, String, LogFilter, RotationSchedule, String)
		 */
		public LogWriter(String filePattern, LogFilter filter) {
			initLogWriter(WriterType.FILE, filePattern, filter, null, null);
		}
		
		/**
		 * Define a console writer that has a filter
		 * @param filter
		 * @see LogWriter#LogWriter(WriterType, String, LogFilter, RotationSchedule, String)
		 */
		public LogWriter(LogFilter filter) {
			initLogWriter(WriterType.CONSOLE, null, filter, null, null);
		}
		
		/**
		 * Define a console writer with no filter
		 */
		public LogWriter() {
			this(null);
		}
		
		/**
		 * Set the writer type
		 * @param type
		 * @see WriterType
		 */
		public void setType(WriterType type) {
			this.type = type;
		}
		
		
		/**
		 * Get the writer type
		 * @return the write type
		 * @see WriterType
		 */
		public WriterType getType() {
			return this.type;
		}
		
		/** 
		 * Set the file pattern
		 * 
		 * @param filePattern
		 * @see LogWriter#LogWriter(WriterType, String, LogFilter, RotationSchedule, String)
		 */
		public void setFilePattern(String filePattern) {
			this.filePattern = filePattern;
		}
		
		/**
		 *  Get the file pattern
		 *  
		 * @return the file pattern
		 * @see LogWriter#LogWriter(WriterType, String, LogFilter, RotationSchedule, String)
		 */
		public String getFilePattern() {
			return this.filePattern;
		}
		
		/**
		 * Set the log filter
		 * @param filter
		 * @see LogFilter
		 */
		public void setFilter(LogFilter filter) {
			this.filter = filter;
		}
		
		/**
		 * Get the log filter
		 * @return the log filter
		 */
		public LogFilter getFilter() {
			return this.filter;
		}
		
		/**
		 * Set the rotation schedule for a rotation file log
		 * @param rotationSchedule
		 * @see RoatationSchedule
		 */
		public void setRotationSchedule(RotationSchedule rotationSchedule) {
			this.rotationSchedule = rotationSchedule;
		}
		
		/**
		 * Get the rotation schedule for a rotation file log
		 * @return the rotation schedule
		 * @see RotationSchedule
		 */
		public RotationSchedule getRotationSchedule() {
			return this.rotationSchedule;
		}
		
		/**
		 * Set the size of the file that will trigger a new file for a rotation log writer
		 * @param rotationSize
		 * @see LogWriter#LogWriter(WriterType, String, LogFilter, RotationSchedule, String)
		 */
		public void setRotationSize(String rotationSize) {
			this.rotationSize = rotationSize;
		}
		
		/**
		 * Get the size of the file that will trigger a new file for a rotation log writer
		 * @return the rotationSize
		 * @see LogWriter#LogWriter(WriterType, String, LogFilter, RotationSchedule, String)
		 */		
		public String getRotationSize() {
			return this.rotationSize;
		}
	}
	
	/**
	 * A class to define a Log Sink.  You can have multiple of these in a LogConfig
	 * A LogSink has a list of Channels a filter and a list of writers
	 * @see Channel
	 * @see LogFilter
	 * @see LogWriter
	 */
	public class LogSink {
		private ArrayList<Channel> channels;
		private LogFilter filter;
		private ArrayList<LogWriter> writers;
		
		public void addWriter(LogWriter writer) {
			writers.add(writer);
		}
		
		public void setFilter(LogFilter filter) {
			this.filter = filter;
		}
		
		public LogFilter getFilter() {
			return this.filter;
		}
		
		/**
		 * @param channel
		 * @see Channel
		 */
		public void addChannel(Channel channel) {
			if (!channels.contains(channel))
				channels.add(channel);
		}
		
		/**
		 * Construct a LogSink with no channels
		 */
		public LogSink() {
			channels = new ArrayList<Channel>();
			writers = new ArrayList<LogWriter>();
		}
	}
	
	private ArrayList<LogSink> sinks;
	
	public LogConfig() {
		sinks = new ArrayList<LogSink>();
	}
	
	public void addSink(LogSink sink) {
		sinks.add(sink);
	}
	
	/**
	 * Get a JSON string representation of the LogConfig object that can be used to config a Logger.
	 * Producing this output is the point of this class.
	 * 
	 * @return A JSON String 
	 */
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
