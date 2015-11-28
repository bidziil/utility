package org.bidziil.configuration;

import com.google.common.base.Strings;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;


public class AppConfiguration {

	private static Logger logger = Logger.getLogger(AppConfiguration.class);

	public static String UTF_ENCODING = "UTF-8";

	protected Properties properties;
	private   String     key;
	private   boolean    initialized;

	public static final String DEVELOPER_MODE     = "DEVELOPER_MODE";
	public static final String HOST_PAGE_BASE_URL = "HOST_PAGE_BASE_URL";

	public static final String WEBSERVICE_LOG_QCF_JNDI = "jms/WebServiceLogQueueConnectionFactory";
	public static final String WEBSERVICE_LOG_Q_JNDI   = "jms/WebServiceLogQueue";
	public static final String AUDITLOG_QCF_JNDI       = "jms/AuditLogQueueConnectionFactory";
	public static final String AUDITLOG_Q_JNDI         = "jms/AuditLogQueue";

	public AppConfiguration(String key) {
		this.key = key;
		this.initialized = false;
	}

	public void init() throws FileNotFoundException {

		logger.debug("AppConfiguration meghivva: " + key);

		String path = System.getProperty(key);
		if ( Strings.isNullOrEmpty(path) ) {
			throw new FileNotFoundException("App configuration error: system property not found - " + key);
		}
		File f = new File(path);
		InputStreamReader isr = null;
		if ( f.exists() ) {
			try {
				this.properties = new Properties();
				isr = new InputStreamReader(new FileInputStream(f), UTF_ENCODING);
				this.properties.load(isr);
				this.initialized = true;

			} catch ( Exception e ) {
				this.initialized = false;
				this.properties = null;
				logger.error("==> AppConfiguration : ", e);
			} finally {
				if ( isr != null ) {
					try {
						isr.close();
					} catch ( IOException e ) {

					}
				}
			}

		} else {
			throw new FileNotFoundException("App configuration error: file not found - " + path);
		}
	}

	public boolean isInitialized() {
		return initialized;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getProperty(String propertyName) {
		return this.properties.getProperty(propertyName);
	}

	public String getProperty(String propertyName, String defaultValue) {
		return this.properties.getProperty(propertyName, defaultValue);
	}

	public String getTrimmedProperty(String propertyName) {
		String prop = this.properties.getProperty(propertyName);
		return prop == null ? prop : prop.trim();
	}

	public String getTrimmedProperty(String propertyName, String defaultValue) {
		String prop = this.properties.getProperty(propertyName, defaultValue);
		return prop == null ? prop : prop.trim();
	}

	public boolean getBooleanProperty(String propertyName, boolean defaultvalue) {
		String v = this.properties.getProperty(propertyName);
		if ( Strings.isNullOrEmpty(v) ) {
			return defaultvalue;
		}
		return "true".equalsIgnoreCase(v);
	}

	public boolean isDeveloperMode() {
		String value = getProperty(DEVELOPER_MODE);
		return ( value != null ) && value.toLowerCase().equals("true");
	}
}
