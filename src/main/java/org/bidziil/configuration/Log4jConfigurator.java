package org.bidziil.configuration;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Level;

import org.apache.log4j.Appender;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

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
			String file = tryToGetLog4jConfiguration();

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

	private String tryToGetLog4jConfiguration() {
		String file = tryToGetFileFromClassLoader();
		if ( file == null ) {
			file = tryToGetFileFromJvm();
		}
		if ( file == null ) {
			throw new NullPointerException("There is no log4j config file!");
		}
		return file;
	}

	public long getDelay() {
		String refreshIntervalParam = System.getProperty(LOG4J_REFRESH_INTERVAL);
		long delay = DEFAULT_INTERVAL;

		// Check for refreshIntervalParam parameter
		if ( refreshIntervalParam != null && !refreshIntervalParam.isEmpty() ) {
			try {
				delay = Long.parseLong(refreshIntervalParam);
			} catch ( NumberFormatException nfe ) {
				// Can't really log the error since we haven't initialized Log4J will use the default value
				delay = DEFAULT_INTERVAL;
			}
		}
		System.out.println("delay=" + refreshIntervalParam + " -> " + delay);
		return delay;
	}
	
	public String tryToGetFileFromClassLoader() {
		try {
			return getFileFromClassLoader("/WEB-INF/classes/log4j.xml");
		} catch (Exception e) {
			System.err.println(String.format("%s occurred while load log4j from classloader due to %s", e.getClass().getSimpleName(), ( e.getMessage() != null ) ? e.getMessage().toLowerCase() : "-"));
		}
		return null;
	}
	
	public String getFileFromClassLoader(String resourceName) throws URISyntaxException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL resource = classLoader.getResource(resourceName);
		if ( resource == null ) {
			throw new URISyntaxException("/WEB-INF/classes/log4j.xml", "log4j.xml is missing");
		}

		File file = new File(resource.toURI());
		
		if ( !( file.exists() ) ) {
			String message = String.format("Invalid '%s' parameter value '%s'.", getPropertyName(), file);
			throw new IllegalArgumentException(message);
		}
		
		System.out.println("log4j file= -> " + file.getAbsolutePath());
		return file.getAbsolutePath();
	}
	
	public String tryToGetFileFromJvm() {
		try {
			return getFileFromJvm();
		} catch (Exception e) {
			System.err.println(String.format("%s occurred while load log4j from jvm due to %s", e.getClass().getSimpleName(), ( e.getMessage() != null ) ? e.getMessage().toLowerCase() : "-"));
		}
		return null;
	}

	public String getFileFromJvm() {
		String configurationParam = System.getProperty(getPropertyName());
		String file = configurationParam;

		if ( file == null || file.isEmpty() ) {
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
		System.out.println("log4j file=" + configurationParam + " -> " + file);
		return file;
	}

	public String getPropertyName() {
		return propertyName;
	}

	@SuppressWarnings("unchecked")
	public boolean isConfigured() {
		org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
		Enumeration<Appender> appenders = rootLogger.getAllAppenders();

		boolean configured = false;
		while (appenders.hasMoreElements()) {
			Appender appender = appenders.nextElement();
			LOGGER.log(Level.INFO, "Log4j appender found " + appender.getName());
			if ( !configured ) {
				configured = true;
			}
		}
		
		if ( !configured ) {
			LOGGER.log(Level.INFO, "Log4j config file is missing");
		}
		return configured;
	}
	
}
