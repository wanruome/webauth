/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月14日 下午10:15:41
 */
package com.newpay.webauth.dal.request.userinfo;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class UserInfoLoginReqDto {
	@NotEmpty
	private String account;
	@NotEmpty
	private String accountType;
	@NotEmpty
	private String pwd;
	@NotEmpty
	private String uuid;
	@NotEmpty
	private String pwdEncrypt;
}
