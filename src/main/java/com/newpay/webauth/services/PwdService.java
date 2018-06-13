/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午8:28:08
 */
package com.newpay.webauth.services;

import com.newpay.webauth.dal.core.RequestPwdParse;

public interface PwdService {
	// public String getRealPassWord(String pwdRequest, String pwdEncrypt, String pwdUuid, String
	// phone);

	public RequestPwdParse parseRequsetPwd(String pwdRequest, String pwdEncrypt, String pwdUuid);

	// public boolean isEncryptTypeOk(String pwdEncrypt);
	//
	// public boolean isPwdRuleOK(String pwd);
}
