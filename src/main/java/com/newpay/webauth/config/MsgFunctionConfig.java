/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 下午10:16:45
 */
package com.newpay.webauth.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.newpay.webauth.config.listener.SpringContextHolder;
import com.newpay.webauth.dal.model.MsgFunctionInfo;
import com.newpay.webauth.services.MsgFunctionInfoService;

public class MsgFunctionConfig {
	private static Map<String, MsgFunctionInfo> msgFunctionMap = null;

	public static MsgFunctionInfo getMsgFuntionInfo(String msgFunction) {
		if (null == msgFunctionMap) {
			getAllMsgFunction();
		}
		return msgFunctionMap.get(msgFunction);
	}

	public static void reload() {
		msgFunctionMap = null;
	}

	private static synchronized void getAllMsgFunction() {
		try {
			MsgFunctionInfoService msgFunctionInfoService = SpringContextHolder.getBean(MsgFunctionInfoService.class);
			msgFunctionMap = msgFunctionInfoService.queryAllEnableMsgFunctionInfos();
			System.out.println(msgFunctionMap);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (null == msgFunctionMap) {
			msgFunctionMap = new HashMap<String, MsgFunctionInfo>();
		}
	}

	public static String generateVerifyToken() {
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < AppConfig.VerfiyCodeLength; i++) {
			sb.append(random.nextInt(10));
		}
		return sb.toString();
	}

	public static String generateVerifyCode() {
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < AppConfig.VerfiyCodeLength; i++) {
			sb.append(random.nextInt(10));
		}
		return sb.toString();
	}

	public static String generateVerifyContent(MsgFunctionInfo msgFunctionInfo, String msgCode, boolean isMobile,
			boolean isEmail) {
		String verifyTime = MsgFunctionConfig.parseValidTimeToString(AppConfig.VerfiyCodeValidTime);
		String appName = AppConfig.CompanyName;
		String msgTypeNormal = isEmail ? "通过邮箱" : "通过手机";
		String msgTypeEmail = isEmail ? "通过邮箱" : "";
		String msgTypeMobile = isMobile ? "通过手机" : "";
		String msgFunctionName = msgFunctionInfo.getMapping();

		// String msgTemplate = new String(msgFunctionInfo.getMsgTemplate());
		// msgTemplate = msgTemplate.replaceAll("{msgCode}", code);
		// msgTemplate = msgTemplate.replaceAll("{msgValidTime}", verifyTime);
		// msgTemplate = msgTemplate.replaceAll("{appName}", appName);
		// msgTemplate = msgTemplate.replaceAll("{msgType}", msgTypeNormal);
		// msgTemplate = msgTemplate.replaceAll("{msgTypeMobile}", msgTypeMobile);
		// msgTemplate = msgTemplate.replaceAll("{msgTypeEmail}", msgTypeEmail);
		// msgTemplate = msgTemplate.replaceAll("{msgFunction}", msgFunctionName);
		// return msgTemplate;
		String msgTemplate = new String(msgFunctionInfo.getTemplate()).replaceAll("\\{msgCode\\}", msgCode)
				.replaceAll("\\{msgValidTime\\}", verifyTime).replaceAll("\\{appName\\}", appName)
				.replaceAll("\\{msgType\\}", msgTypeNormal).replaceAll("\\{msgTypeMobile\\}", msgTypeMobile)
				.replaceAll("\\{msgTypeEmail\\}", msgTypeEmail).replaceAll("\\{msgFunction\\}", msgFunctionName);
		return msgTemplate;
	}

	public static String parseValidTimeToString(long validTime) {
		long timeMin = validTime / (1000l * 60);
		if (timeMin < 60l) {
			return timeMin + "分钟";
		}
		else if (timeMin % 60 == 0) {
			return timeMin / 60 + "小时";
		}
		else {
			return timeMin / 60 + "小时" + timeMin % 60 + "分钟";
		}
	}

}
