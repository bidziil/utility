package org.bidziil.configuration;

import com.google.common.base.Strings;
import org.apache.log4j.Appender;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;
import java.util.Enumeration;
import java.util.logging.Level;

public class Log4jConfigurator {

	protected static final java.util.logging.Logger LOGGER                 = java.util.logging.Logger.getLogger("Log4jConfigurator");
	protected static       String                   LOG4J_CONFIGURATION    = "log4j.configuration";
	protected static       String                   LOG4J_REFRESH_INTERVAL = "log4j.refresh.interval";
	protected static       long                     DEFAULT_INTERVAL       = 300000; // Default delay is 5 minutes

	protected String propertyName;

	public Log4jConfigurator() {
		this(LOG4J_CONFIGURATION);
	}

	public Log4jConfigurator(String propertyName) {
		this.propertyName = propertyName;
	}

	public void init() {
		if ( isConfigured() ) return;

		try {
			String file = getFile();
			if ( file.toLowerCase().endsWith(".xml") ) {
				DOMConfigurator.configureAndWatch(file, getDelay());
			} else {
				PropertyConfigurator.configureAndWatch(file, getDelay());
			}
		} catch ( Exception e ) {
			String message = String.format("%s occurred while init log4j due to %s", e.getClass().getSimpleName(), ( e.getMessage() != null ) ? e.getMessage().toLowerCase() : "-");
			System.err.printf(message);
			e.printStackTrace(System.err);
			throw new IllegalStateException(message, e);
		}
	}

	public long getDelay() {
		String refreshIntervalParam = System.getProperty(LOG4J_REFRESH_INTERVAL);
		long delay = DEFAULT_INTERVAL;

		// Check for refreshIntervalParam parameter
		if ( !Strings.isNullOrEmpty(refreshIntervalParam) ) {
			try {
				delay = Long.parseLong(refreshIntervalParam);
			} catch ( NumberFormatException nfe ) {
				// Can't really log the error since we haven't initialized Log4J will use the default value
				delay = DEFAULT_INTERVAL;
			}
		}
		LOGGER.log(Level.INFO, "delay=" + refreshIntervalParam + " -> " + delay);
		return delay;
	}

	public String getFile() {
		String configurationParam = System.getProperty(getPropertyName());
		String file = configurationParam;

		if ( Strings.isNullOrEmpty(file) ) {
			String message = String.format("Invalid '%s' parameter value '%s'.", getPropertyName(), file);
			throw new IllegalArgumentException(message);
		}

		String prefix = "file:";
		if ( file.startsWith(prefix) ) {
			file = file.substring(prefix.length());
		}

		if ( !( new File(file).exists() ) ) {
			String message = String.format("Invalid '%s' parameter value '%s'.", getPropertyName(), file);
			throw new IllegalArgumentException(message);
		}
		LOGGER.log(Level.INFO, "file=" + configurationParam + " -> " + file);
		return file;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public boolean isConfigured() {
		org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
		Enumeration appenders = rootLogger.getAllAppenders();
		if ( !appenders.hasMoreElements() ) {
			LOGGER.log(Level.INFO, "Log4j config file is missing");
			return false;
		} else {
			LOGGER.log(Level.INFO, "Log4j appender found " + ( ( Appender ) appenders.nextElement() ).getName());
			return true;
		}
	}
}
