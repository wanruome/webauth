/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月15日 下午9:25:32
 */
package com.newpay.webauth.dal.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;

@Data
@Table(name = "TBL_LOGIN_APP_INFO")
public class LoginAppInfo {
	@Id
	@Column(name = "APP_ID")
	private String appId;
	@Column(name = "APP_NAME")
	private String appName;
	@Column(name = "PUBIC_KEY")
	private String publicKey;
	@Column(name = "TERM_LIMIT")
	private Integer termLimit;
	@Column(name = "TERM_ANDROID_LIMIT")
	private Integer termAndroidLimit;
	@Column(name = "TERM_IPHONE_LIMIT")
	private Integer termIphoneLimit;
	@Column(name = "TERM_WEB_LIMIT")
	private Integer termWebLimit;
	@Column(name = "NEW_KILL_OUT")
	private Integer newKillOut;
	@Column(name = "STATUS")
	private Integer status;
	@Column(name = "CREATE_TIME")
	private String createTime;
	@Column(name = "VERSION")
	@Version
	private Integer version;

}
