/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月19日 下午10:52:24
 */
package com.newpay.webauth.services;

import com.newpay.webauth.dal.core.TokenResponseParse;
import com.newpay.webauth.dal.model.LoginAppInfo;
import com.newpay.webauth.dal.model.LoginUserAccount;
import com.newpay.webauth.dal.request.userinfo.UserInfoLoginReqDto;

public interface SecureTokenService {
	public TokenResponseParse createTokenForLogin(UserInfoLoginReqDto userInfoLoginReqDto,
			LoginUserAccount resultLoginUserAccount, LoginAppInfo resultLoginAppInfo);
}
