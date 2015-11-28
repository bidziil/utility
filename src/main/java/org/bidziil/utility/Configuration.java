package org.bidziil.utility;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

public class Configuration {

	public static final String	PROPERTY_FILE_NAME					= "brokerportal.properties";
	private static Properties	props								= null;

	public static final String	JOB_DIM_REFRESH						= "job.DimRefresh";
	public static final String	OFFLINE_CALC_EXCEL_LIFE_DIR_PATH	= "offline.calc.excel.life.dir.path";
	public static final String	OFFLINE_CALC_EXCEL_OTHER_DIR_PATH	= "offline.calc.excel.other.dir.path";

	public static final String	WS_TIMEOUT							= "brokerportal.WebServiceTimeOut";

	private static Logger		logger								= Logger.getLogger(Configuration.class);

	public static Properties getProperties() throws IOException {
		if (props == null) {
			synchronized (Configuration.class) {
				props = new Properties();
				Properties sys = System.getProperties();

				for (Enumeration<?> keys = sys.propertyNames(); keys.hasMoreElements();) {
					String nk = (String) keys.nextElement();

					if (nk.startsWith(PROPERTY_FILE_NAME)) {
						String value = sys.getProperty(nk);
						InputStream in = null;

						try {
							in = new FileInputStream(value);
							props.load(in);
						} finally { if (in != null) { try { in.close(); } catch (Exception e) { } } }
					}
				}
			}
		}
		return props;
	}

	public static String getProperty(String propertyName) {
		try {
			return getProperties().getProperty(propertyName);
		} catch (IOException e) {
			logger.fatal(e.getMessage(), e);
			e.printStackTrace();
			return null;
		}
	}

	public static String getTrimmedProperty(String propertyName) {
		try {
			String prop = getProperties().getProperty(propertyName);
			return prop == null ? prop : prop.trim();
		} catch (IOException e) {
			logger.fatal(e.getMessage(), e);
			return null;
		}
	}

	public static Properties loadProperties(Map<String, String> returned) throws IOException {
		Properties pr = new Properties();
		Properties sys = System.getProperties();

		for (Enumeration<?> keys = sys.propertyNames(); keys.hasMoreElements();) {
			String nk = (String) keys.nextElement();

			if (nk.startsWith(PROPERTY_FILE_NAME)) {
				String value = sys.getProperty(nk);

				returned.put(nk, value);
				InputStream in = null;

				try {
					in = new FileInputStream(value);
					pr.load(in);
				} finally { if (in != null) { try { in.close(); } catch (Exception e) { } } }
			}
		}
		return pr;
	}

	public static Properties loadProperties(String basename) throws IOException {
		Properties pr = new Properties();
		try {
			Properties sys = System.getProperties();

			for (Enumeration<?> keys = sys.propertyNames(); keys.hasMoreElements();) {
				String nk = (String) keys.nextElement();
				if (nk.startsWith(basename)) {
					String value = sys.getProperty(nk);
					InputStream in = null;

					try {
						in = new FileInputStream(value);
						pr.load(in);
					} finally { if (in != null) { try { in.close(); } catch (Exception e) { } } }
				}
			}
		} catch (IOException ioe) {
			throw ioe;
		}

		return pr;
	}

	private Configuration() {
		// non-instantiable
		throw new AssertionError();
	}
}
