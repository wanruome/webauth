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
	private static String RESPONSE_COED_TAG = "code";
	private static String RESPONSE_MSG_TAG = "msg";
	private static String RESPONSE_DATA_TAG = "data";
	private static String ERR_PRARM = "e401";
	private static String ERR_CORE = "e402";
	private static String ERR_DB = "e403";
	public static String ERR_UNKNOWN = "e499";
	public static String ERR_PARSE_REQUEST = "e410";
	public static String ERR_TOKEN_INVALID = "e411";
	public static String ERR_MSGCODE_INVALID = "e412";

	// public static String ERROR_TOKEN_MISS = "e001";
	// public static String ERROR_TOKEN_INVALID = "e000";
	private static String SUCESS_CODE = "0000";

	public static JSONObject toNackPARAM() {
		return createResponse(ERR_PRARM, null, null, false);
	}

	public static JSONObject toNackPARAM(String msg) {
		return createResponse(ERR_PRARM, msg, null, false);
	}

	public static JSONObject toNackCORE() {
		return createResponse(ERR_CORE, null, null, false);
	}

	public static JSONObject toNackCORE(String msg) {
		return createResponse(ERR_CORE, msg, null, false);
	}

	public static JSONObject toNackDB() {
		return createResponse(ERR_DB, null, null, false);
	}

	public static JSONObject toNackDB(String msg) {
		return createResponse(ERR_DB, msg, null, false);
	}

	public static JSONObject toNack(String code, String msg) {
		return createResponse(code, msg, null, false);
	}

	public static JSONObject toAck(Object data) {
		return createResponse(SUCESS_CODE, null, data, false);
	}

	public static boolean isAck(JSONObject jsonObject) {
		if (null == jsonObject || !jsonObject.containsKey(RESPONSE_COED_TAG)) {
			return false;
		}
		return SUCESS_CODE.equals(jsonObject.getString(RESPONSE_COED_TAG));
	}

	// private static JSONObject createResponse(ResultData<?> resultData, boolean isOneRoot) {
	// String realCode = StringUtils.isBlank(resultData.getCode()) ? ERR_UNKNOWN :
	// resultData.getCode();
	// String realMsg = null;
	// if (StringUtils.isBlank(resultData.getMsg())) {
	// if (realCode.equals(SUCESS_CODE)) {
	// realMsg = "OK";
	// }
	// else {
	// if (realCode.equals(ERR_PRARM)) {
	// realMsg = "请求参数错误";
	// }
	// else if (realCode.equals(ERR_CORE)) {
	// realMsg = "业务处理异常";
	// }
	// else if (realCode.equals(ERR_DB)) {
	// realMsg = "数据处理错误";
	// }
	// else if (realCode.equals(ERR_UNKNOWN)) {
	// realMsg = "网络请求失败";
	// }
	// // else if (realCode.equals(ERROR_CODE_SYSTEM)) {
	// // realMsg = "未知错误，请求失败，请重试";
	// // }
	// // else if (realCode.equalsIgnoreCase(ERROR_TOKEN_MISS)) {
	// // realMsg = "验证信息丢失";
	// // }
	// // else if (realCode.equalsIgnoreCase(ERROR_TOKEN_INVALID)) {
	// // realMsg = "验证信息失效";
	// // }
	// else {
	// realMsg = "发生未知错误";
	// }
	// }
	// }
	// else {
	// realMsg = resultData.getMsg();
	// }
	// JSONObject resultJson = null;
	// if (null == resultData.getData()) {
	// resultJson = new JSONObject();
	// resultJson.put(RESPONSE_COED_TAG, realCode);
	// resultJson.put(RESPONSE_MSG_TAG, realMsg);
	//
	// }
	// else {
	// if (isOneRoot) {
	// String json = JSON.toJSONString(resultData.getData());
	// resultJson = JSON.parseObject(json);
	// if (null == resultJson) {
	// resultJson = new JSONObject();
	// }
	// resultJson.put(RESPONSE_COED_TAG, realCode);
	// resultJson.put(RESPONSE_MSG_TAG, realMsg);
	// }
	// else {
	// resultJson = new JSONObject();
	// resultJson.put(RESPONSE_COED_TAG, realCode);
	// resultJson.put(RESPONSE_MSG_TAG, realMsg);
	// resultJson.put(RESPONSE_DATA_TAG, resultData.getData());
	// }
	//
	// }
	// return resultJson;
	//
	// }

	private static JSONObject createResponse(String code, String msg, Object data, boolean isOneRoot) {
		String realCode = StringUtils.isEmpty(code) ? ERR_UNKNOWN : code;
		String realMsg = null;
		if (StringUtils.isEmpty(msg)) {
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
				else if (realCode.equals(ERR_TOKEN_INVALID)) {
					realMsg = "签名信息不正确";
				}
				else if (realCode.equals(ERR_PARSE_REQUEST)) {
					realMsg = "无效的请求信息";
				}
				else if (realCode.equals(ERR_MSGCODE_INVALID)) {
					realMsg = "短信验证码不正确";
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
			resultJson.put(RESPONSE_COED_TAG, realCode);
			resultJson.put(RESPONSE_MSG_TAG, realMsg);

		}
		else {
			if (isOneRoot) {
				String json = JSON.toJSONString(data);
				resultJson = JSON.parseObject(json);
				if (null == resultJson) {
					resultJson = new JSONObject();
				}
				resultJson.put(RESPONSE_COED_TAG, realCode);
				resultJson.put(RESPONSE_MSG_TAG, realMsg);
			}
			else {
				resultJson = new JSONObject();
				resultJson.put(RESPONSE_COED_TAG, realCode);
				resultJson.put(RESPONSE_MSG_TAG, realMsg);
				resultJson.put(RESPONSE_DATA_TAG, data);
			}

		}
		return resultJson;

	}

}
