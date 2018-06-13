/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 上午12:00:04
 */
package com.ruomm.base.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertyReader {
	private Properties props = null;
	private String propertyPath = null;

	public PropertyReader(String propertyPath) {
		super();
		this.propertyPath = propertyPath;
	}

	public String getPropertyPath() {
		return propertyPath;
	}

	public void setPropertyPath(String propertyPath) {
		this.propertyPath = propertyPath;
	}

	public void forceLoadProperty() {
		props = null;
		loadProperty();
	}

	private synchronized void loadProperty() {
		if (null != props) {
			return;
		}
		props = new Properties();
		InputStream in = null;
		try {
			// config/app/WebApp.properties
			// in = PropertyConfig.class.getClassLoader().getResourceAsStream(propertyPath);
			in = PropertyReader.class.getResourceAsStream("/" + propertyPath);
			props.load(in);
		}
		catch (Exception e) {
			e.printStackTrace();
			props = null;
		}

		finally {
			try {
				if (null != in) {
					in.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		log.info("加载'" + propertyPath + "'配置文件内容完成...........");
		log.info("properties文件内容：" + props);
	};

	public String getValString(String key) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		// loadProperty();
		return props.getProperty(key);
	}

	public String getValString(String key, String defaultVal) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		// loadProperty();
		String value = props.getProperty(key);
		if (StringUtils.isEmpty(value)) {
			return defaultVal;
		}
		else {
			return value;
		}
	}

	public Integer getValInteger(String key) {
		return getValInteger(key, null);
	}

	public Integer getValInteger(String key, Integer defaultVal) {
		String valueString = getValString(key);
		if (StringUtils.isEmpty(valueString)) {
			return defaultVal;
		}
		Integer value = null;
		try {
			value = Integer.valueOf(valueString);
		}
		catch (Exception e) {
			e.printStackTrace();
			value = null;
		}
		if (value == null) {
			return defaultVal;
		}
		else {
			return value;
		}
	}

	public Long getValLong(String key) {
		return getValLong(key, null);
	}

	public Long getValLong(String key, Long defaultVal) {
		String valueString = getValString(key);
		if (StringUtils.isEmpty(valueString)) {
			return defaultVal;
		}
		Long value = null;
		try {
			value = Long.valueOf(valueString);
		}
		catch (Exception e) {
			e.printStackTrace();
			value = null;
		}
		if (value == null) {
			return defaultVal;
		}
		else {
			return value;
		}
	}

	public Long getValLongTime(String key) {
		return getValLongTime(key, null);
	}

	public Long getValLongTime(String key, Long defaultVal) {
		String valueString = getValString(key);
		if (StringUtils.isEmpty(valueString)) {
			return defaultVal;
		}
		Long value = null;
		try {
			if (valueString.toLowerCase().endsWith("s")) {
				value = Long.valueOf(valueString.substring(0, valueString.length() - 1)) * 1000;
			}
			else if (valueString.toLowerCase().endsWith("m")) {
				value = Long.valueOf(valueString.substring(0, valueString.length() - 1)) * 1000 * 60;
			}
			else if (valueString.toLowerCase().endsWith("h")) {
				value = Long.valueOf(valueString.substring(0, valueString.length() - 1)) * 1000 * 3600;
			}
			else if (valueString.toLowerCase().endsWith("d")) {
				value = Long.valueOf(valueString.substring(0, valueString.length() - 1)) * 1000 * 3600 * 24;
			}
			else if (valueString.toLowerCase().endsWith("w")) {
				value = Long.valueOf(valueString.substring(0, valueString.length() - 1)) * 1000 * 3600 * 24 * 7;
			}
			else if (valueString.toLowerCase().endsWith("mon")) {
				value = Long.valueOf(valueString.substring(0, valueString.length() - 3)) * 1000 * 3600 * 24 * 30;
			}
			else if (valueString.toLowerCase().endsWith("y")) {
				value = Long.valueOf(valueString.substring(0, valueString.length() - 1)) * 1000 * 3600 * 365;
			}
			else {
				value = Long.valueOf(valueString.substring(0, valueString.length() - 1)) * 1000;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			value = null;
		}
		if (value == null) {
			return defaultVal;
		}
		else {
			return value;
		}

	}

	public Float getValFloat(String key) {
		return getValFloat(key, null);
	}

	public Float getValFloat(String key, Float defaultVal) {
		String valueString = getValString(key);
		if (StringUtils.isEmpty(valueString)) {
			return defaultVal;
		}
		Float value = null;
		try {
			value = Float.valueOf(valueString);
		}
		catch (Exception e) {
			e.printStackTrace();
			value = null;
		}
		if (value == null) {
			return defaultVal;
		}
		else {
			return value;
		}
	}

	public Double getValDouble(String key) {
		return getValDouble(key, null);
	}

	public Double getValDouble(String key, Double defaultVal) {
		String valueString = getValString(key);
		if (StringUtils.isEmpty(valueString)) {
			return defaultVal;
		}
		Double value = null;
		try {
			value = Double.valueOf(valueString);
		}
		catch (Exception e) {
			e.printStackTrace();
			value = null;
		}
		if (value == null) {
			return defaultVal;
		}
		else {
			return value;
		}
	}

	public Boolean getValBoolean(String key) {
		return getValBoolean(key, false);

	}

	public Boolean getValBoolean(String key, boolean defaultVal) {
		String value = getValString(key);
		if (StringUtils.isEmpty(value)) {
			return defaultVal;
		}
		else if ("1".equals(value) || "true".equals(value.toLowerCase())) {
			return true;
		}
		else if ("0".equals(value) || "false".equals(value.toLowerCase())) {
			return false;
		}
		else {
			return defaultVal;
		}

	}
}
