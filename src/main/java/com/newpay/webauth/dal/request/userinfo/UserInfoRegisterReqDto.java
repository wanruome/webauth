/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月11日 上午12:04:43
 */
package com.newpay.webauth.dal.request.userinfo;

import lombok.Data;

@Data
public class UserInfoRegisterReqDto {

	private String userId;
	private String name;
	private String mobie;
	private String email;
	private String pwd;
	private String pwdEncrypt;
	private String uuid;

}
