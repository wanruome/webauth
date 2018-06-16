/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月15日 下午8:54:56
 */
package com.newpay.webauth.dal.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;

@Data
@Table(name = "TBL_LOGIN_USER_TOKEN")
public class LoginUserToken {
	@Id
	@Column(name = "TOKEN_ID")
	private String tokenId;
	@Column(name = "USER_ID")
	private String userId;
	@Column(name = "APP_ID")
	private String appId;
	@Column(name = "TERM_TYPE")
	private Integer termType;
	@Column(name = "TOKEN")
	private String token;
	@Column(name = "VALID_TIME")
	private String validTime;
	@Column(name = "LOGIN_STATUS")
	private Integer loginStatus;
	@Column(name = "CREATE_TIME")
	private String createTime;

	@Version
	@Column(name = "VERSION")
	private Integer version;
}
