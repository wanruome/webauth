/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 上午11:27:35
 */
package com.newpay.webauth.dal.request.userinfo;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class UserInfoModifyPwd {
	@NotEmpty
	private String uuid;
	@NotEmpty
	private String userId;
	@NotEmpty
	private String appId;
	@NotEmpty
	private String tokenId;
	@NotEmpty
	private String signInfo;
	private String msgVerifyCode;
	@NotEmpty
	private String newPwd;
	@NotEmpty
	private String oldPwd;
	private String newPwdEncrypt;
	private String oldPwdEncrypt;
}
