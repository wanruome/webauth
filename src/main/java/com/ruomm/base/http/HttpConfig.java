package com.ruomm.base.http;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruomm.base.tools.StringUtils;

public class HttpConfig {
	public final static int Code_Success = 200;
	public final static int Code_NoSend = -200;
	public final static int Code_SendError = -400;
	public final static int Code_FileCreateError = -409;

	private final static String ParseString = "parseString";
	private final static String ParseByte = "parseByte";

	public static Object parseResponseData(byte[] resourceByte, Class<?> cls, String charsetName) {
		if (null == cls) {
			return resourceByte;
		}
		if (String.class.getName().equals(cls.getName())) {
			return byteToString(resourceByte, charsetName);
		}
		if (JSONObject.class.getName().equals(cls.getName())) {
			return parseToJSONObject(getRealJsonString(byteToString(resourceByte, charsetName)));
		}
		if (JSONArray.class.getName().equals(cls.getName())) {
			return parseToJSONArray(getRealJsonString(byteToString(resourceByte, charsetName)));
		}
		Method method = null;
		{
			if (null != cls) {
				try {
					method = cls.getDeclaredMethod(ParseByte, byte[].class);
				}
				catch (Exception e) {
					method = null;
				}
			}
		}
		if (null == method) {
			// 默认解析为JSON对象
			return parseTextToJson(getRealJsonString(byteToString(resourceByte, charsetName)), cls);
		}
		else {
			Object objinstance = null;
			try {
				Constructor<?> constructor = cls.getDeclaredConstructor();
				if (null != constructor) {
					constructor.setAccessible(true);
					objinstance = constructor.newInstance();
				}
				method.setAccessible(true);
				method.invoke(objinstance, resourceByte);
			}
			catch (Exception e) {
				objinstance = null;
			}

			return objinstance;
		}

	}

	public static Object parseResponseText(String resourceString, Class<?> cls) {
		if (null == cls) {
			return resourceString;
		}
		if (String.class.getName().equals(cls.getName())) {
			return resourceString;
		}
		if (JSONObject.class.getName().equals(cls.getName())) {
			return parseToJSONObject(resourceString);
		}
		if (JSONArray.class.getName().equals(cls.getName())) {
			return parseToJSONArray(resourceString);
		}
		Method method = null;
		try {
			method = cls.getDeclaredMethod(ParseString, String.class);
		}
		catch (Exception e) {
			method = null;
		}
		if (null == method) {
			return parseTextToJson(resourceString, cls);
		}
		else {
			Object objinstance = null;
			try {
				Constructor<?> constructor = cls.getDeclaredConstructor();
				if (null != constructor) {
					constructor.setAccessible(true);
					objinstance = constructor.newInstance();
				}
				method.setAccessible(true);
				method.invoke(objinstance, resourceString);
			}
			catch (Exception e) {
				objinstance = null;
			}

			return objinstance;
		}
	}

	private static String byteToString(byte[] resourceByte, String charsetName) {
		String temp = null;
		try {
			if (null == charsetName || charsetName.length() <= 0) {
				temp = new String(resourceByte);
			}
			else {
				temp = new String(resourceByte, Charset.forName(charsetName));
			}
		}
		catch (Exception e) {
			temp = null;
		}
		return temp;
	}

	private static String getRealJsonString(String responseString) {
		String jsonString = null;
		if (!StringUtils.isEmpty(responseString) && !responseString.startsWith("\"")
				&& !responseString.endsWith("\"")) {
			jsonString = responseString;
		}
		else if (!StringUtils.isEmpty(responseString) && responseString.startsWith("\"")
				&& responseString.endsWith("\"")) {
			try {
				jsonString = responseString.substring(1, responseString.length() - 1);
			}
			catch (Exception e) {
				jsonString = null;
			}

		}
		else if (!StringUtils.isEmpty(responseString)) {
			jsonString = responseString;
		}
		else {
			jsonString = null;
		}
		return jsonString;
	}

	private static JSONObject parseToJSONObject(String responseString) {
		String jsonString = getRealJsonString(responseString);
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}
		JSONObject object = null;
		try {
			object = JSON.parseObject(jsonString);
		}
		catch (Exception e) {
			object = null;
		}
		return object;
	}

	private static JSONArray parseToJSONArray(String responseString) {
		String jsonString = getRealJsonString(responseString);
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}
		JSONArray object = null;
		try {
			object = JSON.parseArray(jsonString);
		}
		catch (Exception e) {
			object = null;
		}
		return object;
	}

	private static Object parseTextToJson(String responseString, Class<?> cls) {
		String jsonString = getRealJsonString(responseString);
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}
		Object object = null;
		try {
			if (jsonString.startsWith("[")) {
				object = JSON.parseArray(jsonString, cls);
			}
			else {
				object = JSON.parseObject(jsonString, cls);
			}
		}
		catch (Exception e) {
			object = null;
		}
		return object;
	}

}
