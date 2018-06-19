/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月15日 下午9:25:32
 */
package com.newpay.webauth.dal.request.appinfo;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class AppInfoAddReqDto {
	@NotEmpty
	@Length(max = 32)
	private String appName;
	@NotEmpty
	@Length(min = 32, max = 32)
	private String appPwd;
	@NotEmpty
	private String termLimit;
	@NotEmpty
	private String termAndroidLimit;
	@NotEmpty
	private String termIphoneLimit;
	@NotEmpty
	private String termWebLimit;
	@NotEmpty
	private String newKillOut;
	@Length(max = 256)
	@NotEmpty
	private String publicKey;
	private String notifyUrl;

}
