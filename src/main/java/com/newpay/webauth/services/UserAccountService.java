/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月10日 下午3:10:53
 */
package com.newpay.webauth.services;

import com.newpay.webauth.dal.request.userinfo.UserInfoLoginReqDto;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyEmail;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyMobie;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyName;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyPwd;
import com.newpay.webauth.dal.request.userinfo.UserInfoRegisterReqDto;

public interface UserAccountService {
	public Object doLogin(UserInfoLoginReqDto userInfoLoginReqDto);

	public Object doRegister(UserInfoRegisterReqDto loginUserReqDto);

	public Object doModifyPwd(UserInfoModifyPwd userInfoModifyPwd);

	public Object doModifyMobile(UserInfoModifyMobie userInfoModifyMobie);

	public Object doModifyEmail(UserInfoModifyEmail userInfoModifyEmail);

	public Object doModifyName(UserInfoModifyName userInfoModifyName);
}
