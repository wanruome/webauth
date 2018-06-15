/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午3:29:42
 */
package com.newpay.webauth.dal.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruomm.base.tools.StringUtils;

public class ResultFactory {
	public static String ERR_PRARM = "e401";
	public static String ERR_CORE = "e402";
	public static String ERR_DB = "e403";
	private static String ERR_UNKNOWN = "e499";
	// public static String ERROR_TOKEN_MISS = "e001";
	// public static String ERROR_TOKEN_INVALID = "e000";
	public static String SUCESS_CODE = "0000";

	public static JSONObject toNack(String code) {
		return createResponse(code, null, null, false);
	}

	public static JSONObject toNack(String code, String msg) {
		return createResponse(code, msg, null, false);
	}

	public static JSONObject toAck(Object data) {
		return createResponse(SUCESS_CODE, null, data, false);
	}

	// public static JSONObject toAck(String msg, Object data) {
	// return createResponse(SUCESS_CODE, msg, data, false);
	// }

	private static JSONObject createResponse(String code, String msg, Object data, boolean isOneRoot) {
		String realCode = StringUtils.isBlank(code) ? ERR_UNKNOWN : code;
		String realMsg = null;
		if (StringUtils.isBlank(msg)) {
			if (realCode.equals(SUCESS_CODE)) {
				realMsg = "OK";
			}
			else {
				if (realCode.equals(ERR_PRARM)) {
					realMsg = "请求参数错误";
				}
				else if (realCode.equals(ERR_CORE)) {
					realMsg = "业务处理异常";
				}
				else if (realCode.equals(ERR_DB)) {
					realMsg = "数据处理错误";
				}
				else if (realCode.equals(ERR_UNKNOWN)) {
					realMsg = "网络请求失败";
				}
				// else if (realCode.equals(ERROR_CODE_SYSTEM)) {
				// realMsg = "未知错误，请求失败，请重试";
				// }
				// else if (realCode.equalsIgnoreCase(ERROR_TOKEN_MISS)) {
				// realMsg = "验证信息丢失";
				// }
				// else if (realCode.equalsIgnoreCase(ERROR_TOKEN_INVALID)) {
				// realMsg = "验证信息失效";
				// }
				else {
					realMsg = "发生未知错误";
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
