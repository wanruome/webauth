/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月11日 上午12:04:43
 */
package com.newpay.webauth.dal.request.userinfo;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class UserInfoFindPwd {
	@NotEmpty
	private String uuid;
	@NotEmpty
	private String account;
	@NotEmpty
	private String accountType;
	@NotEmpty
	private String newPwd;
	@NotEmpty
	private String newPwdEncrypt;

	@NotEmpty
	private String msgVerifyCode;
}
