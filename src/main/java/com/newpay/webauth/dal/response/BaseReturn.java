/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午3:29:42
 */
package com.newpay.webauth.dal.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruomm.base.tools.StringUtils;

public class BaseReturn {
	public static String ERROR_CODE_PRARM = "e400";
	public static String ERROR_CODE_CORE = "e500";
	public static String ERROR_CODE_DB = "e400";
	public static String ERROR_CODE_SYSTEM = "400";
	public static String SUCESS_CODE = "0000";

	public static JSONObject toFAIL(String code) {
		return createResponse(code, null, null, true);
	}

	public static JSONObject toFAIL(String code, String msg) {
		return createResponse(code, msg, null, true);
	}

	public static JSONObject toSUCESS(Object data) {
		return createResponse(SUCESS_CODE, null, data, true);
	}

	public static JSONObject toSUCESS(String msg, Object data) {
		return createResponse(SUCESS_CODE, msg, data, true);
	}

	private static JSONObject createResponse(String code, String msg, Object data, boolean isOneRoot) {
		String realCode = StringUtils.isBlank(code) ? ERROR_CODE_SYSTEM : code;
		String realMsg = null;
		if (StringUtils.isBlank(msg)) {
			if (realCode.equals(SUCESS_CODE)) {
				realMsg = "OK";
			}
			else {
				if (realCode.equals(ERROR_CODE_PRARM)) {
					realMsg = "提交的信息不合法";
				}
				else if (realCode.equals(ERROR_CODE_DB)) {
					realMsg = "数据处理错误";
				}
				else if (realCode.equals(ERROR_CODE_CORE)) {
					realMsg = "逻辑处理错误";
				}
				else if (realCode.equals(ERROR_CODE_SYSTEM)) {
					realMsg = "未知错误，请求失败，请重试";
				}
				else {
					realMsg = "FAIL";
				}
			}
		}
		else {
			realMsg = msg;
		}
		JSONObject resultJson = null;
		if (null == data) {
			resultJson = new JSONObject();
			resultJson.put("repCode", realCode);
			resultJson.put("repMsg", realMsg);

		}
		else {
			if (isOneRoot) {
				String json = JSON.toJSONString(data);
				resultJson = JSON.parseObject(json);
				if (null == resultJson) {
					resultJson = new JSONObject();
				}
				resultJson.put("repCode", realCode);
				resultJson.put("repMsg", realMsg);
			}
			else {
				resultJson = new JSONObject();
				resultJson.put("repCode", realCode);
				resultJson.put("repMsg", realMsg);
				resultJson.put("data", data);
			}

		}
		return resultJson;

	}

}
