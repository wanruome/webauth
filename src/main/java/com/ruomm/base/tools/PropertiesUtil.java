/**
 *	@copyright wanruome-2017
 * 	@author wanruome
 * 	@create 2017年4月1日 上午10:36:44
 */
package com.ruomm.base.tools;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
	public static Properties loadProperties(String path) {
		Properties p = null;
		try {
			p = new Properties();
			InputStream in = new FileInputStream(path);
			p.load(in);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	public static String getProperty(Properties props, String key) {
		if (null == props) {
			return null;
		}
		return props.getProperty(key);
	}

	public static String getProperty(Properties props, String key, String defaultValue) {
		if (null == props) {
			return defaultValue;
		}
		return props.getProperty(key, defaultValue);
	}

	public static boolean setProperty(Properties props, String key, String value) {
		if (null == props) {
			return false;
		}
		try {
			props.setProperty(key, value);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
