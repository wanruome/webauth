/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月17日 下午9:02:57
 */
package com.ruomm.base.tools;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class FastJsonTools {
	public static String getStringByKey(JSONObject jsonObject, Map<String, String> jsonKeyMap, String key) {
		try {
			if (null == key || key.length() == 0 || null == jsonKeyMap || null == jsonObject) {
				return null;
			}
			String realKey = jsonKeyMap.get(key.toLowerCase());
			return jsonObject.getString(realKey);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Map<String, String> parseJsonKeyMap(JSONObject jsonObject) {
		try {
			if (null == jsonObject) {
				return null;
			}

			Map<String, String> keyMap = new HashMap<>();
			for (String key : jsonObject.keySet()) {
				if (keyMap.containsKey(key.toLowerCase())) {
					keyMap = null;
					break;
				}
				else {
					keyMap.put(key.toLowerCase(), key);
				}
			}
			return keyMap;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
