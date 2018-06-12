/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月10日 下午3:10:53
 */
package com.newpay.webauth.services;

import com.newpay.webauth.dal.request.LoginUserReqDto;

public interface LoginService {
	public String doLogin();

	public Object doRegister(LoginUserReqDto loginUserReqDto);
}