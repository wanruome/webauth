/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月20日 下午2:49:53
 */
package com.newpay.webauth.services;

import com.newpay.webauth.dal.core.SysLogBean;

public interface SystemLogService {
	public void writeLogs(SysLogBean sysLogBean, String code, String msg);

	// public boolean writeLogs(String functionId, String functionName, String userId, String uuid,
	// String appId,
	// JSONObject jsonObject);
}
