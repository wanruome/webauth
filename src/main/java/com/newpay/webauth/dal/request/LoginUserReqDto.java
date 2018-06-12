/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月11日 上午12:04:43
 */
package com.newpay.webauth.dal.request;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class LoginUserReqDto {
	@NotEmpty
	private String loginId;
	private String loginName;
	private String loginMobie;
	private String loginEmail;
	private String loginPwd;
	private String loginType;

}
