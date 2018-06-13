/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 下午8:50:12
 */
package com.newpay.webauth.services;

import java.util.Map;

import com.newpay.webauth.dal.model.MsgFunctionInfo;

public interface MsgFunctionInfoService {
	Map<String, MsgFunctionInfo> queryAllEnableMsgFunctionInfos();
}
