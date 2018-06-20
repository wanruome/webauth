/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 下午10:16:45
 */
package com.newpay.webauth.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.newpay.webauth.config.listener.SpringContextHolder;
import com.newpay.webauth.dal.model.SystemLogFunction;
import com.newpay.webauth.services.SystemLogFunctionService;
import com.ruomm.base.tools.BaseWebUtils;

public class SystemLogFunctionConfig {
	private static Map<String, SystemLogFunction> systemLogFunctionMap = null;

	public static SystemLogFunction getSystemLogFunction(String msgFunction) {
		if (null == systemLogFunctionMap) {
			getAllSystemLogFunction();
		}
		return systemLogFunctionMap.get(msgFunction);
	}

	public static SystemLogFunction getSystemLogFuntionInfoByURI(ServletRequest servletRequest) {
		try {
			String uri = ((HttpServletRequest) servletRequest).getRequestURI();
			String realUri = BaseWebUtils.getRealUri(uri);
			if (null == systemLogFunctionMap) {
				getAllSystemLogFunction();
			}
			SystemLogFunction msgFunction = null;
			for (String key : systemLogFunctionMap.keySet()) {
				if (realUri.endsWith(systemLogFunctionMap.get(key).getMapping())) {
					msgFunction = systemLogFunctionMap.get(key);
				}
			}
			return msgFunction;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void reload() {
		systemLogFunctionMap = null;
	}

	private static synchronized void getAllSystemLogFunction() {
		try {
			SystemLogFunctionService systemLogFunctionInfoService = SpringContextHolder
					.getBean(SystemLogFunctionService.class);
			systemLogFunctionMap = systemLogFunctionInfoService.getAllEnableSystemLogFunction();
			System.out.println(systemLogFunctionMap);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (null == systemLogFunctionMap) {
			systemLogFunctionMap = new HashMap<String, SystemLogFunction>();
		}
	}

}
