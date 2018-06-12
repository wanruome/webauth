/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午11:45:02
 */
package com.newpay.webauth.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertyUtil {
	private static Properties props;
	static {
		loadProps();
	}

	synchronized static private void loadProps() {
		props = null;
		props = new Properties();
		InputStream in = null;
		try {
			// in =
			// PropertyConfig.class.getClassLoader().getResourceAsStream("config/app/WebApp.properties");
			in = PropertyUtil.class.getResourceAsStream("/config/app/WebApp.properties");
			props.load(in);
		}
		catch (Exception e) {
			e.printStackTrace();
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
		log.info("加载properties文件内容完成...........");
		log.info("properties文件内容：" + props);
	}

	public static String getProperty(String key) {
		if (null == props) {
			loadProps();
		}
		return props.getProperty(key);
	}

	public static String getProperty(String key, String defaultValue) {
		if (null == props) {
			loadProps();
		}
		return props.getProperty(key, defaultValue);
	}
}
