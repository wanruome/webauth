/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 上午11:27:35
 */
package com.newpay.webauth.dal.request.userinfo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.ruomm.base.tools.RegexUtil;

import lombok.Data;

@Data
public class UserInfoModifyName {
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
	@Pattern(regexp = RegexUtil.APP_LOGIN_NAME)
	private String newName;
}
