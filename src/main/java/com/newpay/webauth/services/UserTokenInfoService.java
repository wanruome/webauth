/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月15日 下午10:54:54
 */
package com.newpay.webauth.services;

import com.newpay.webauth.dal.response.ResultFactory;

public interface UserTokenInfoService {
	public ResultFactory createTokenForLogin(String userId, String appId, String termType);

	public ResultFactory distoryTokenForLogout(String userId, String appId, String termType);
}
