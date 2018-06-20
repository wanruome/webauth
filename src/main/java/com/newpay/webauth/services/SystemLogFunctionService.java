/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月20日 下午5:13:18
 */
package com.newpay.webauth.services;

import java.util.Map;

import com.newpay.webauth.dal.model.SystemLogFunction;

public interface SystemLogFunctionService {
	public Map<String, SystemLogFunction> getAllEnableSystemLogFunction();
}
