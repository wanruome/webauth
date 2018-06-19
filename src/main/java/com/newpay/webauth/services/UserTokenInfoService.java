/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月15日 下午10:54:54
 */
package com.newpay.webauth.services;

import com.alibaba.fastjson.JSONObject;
import com.newpay.webauth.dal.core.TokenResponseParse;

public interface UserTokenInfoService {
	public TokenResponseParse createTokenForLogin(String userId, String appId, String termType, String uuid);

	public JSONObject distoryTokenForLogout(String userId, String appId, String termType);

	public String getTokenById(String tokenId, String userId, String appId);
}
