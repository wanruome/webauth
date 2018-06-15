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

@Table(name = "TBL_USER_TOKEN")
public class UserTokeInfo {
	@Id
	@Column(name = "USER_ID")
	private String userId;
	@Id
	@Column(name = "APP_ID")
	private String appId;
	@Id
	@Column(name = "TERM_TYPE")
	private String termType;
	@Column(name = "TOKENS")
	private String tokens;
	@Column(name = "VALID_TIME")
	private String validTime;
	@Column(name = "CREATE_TIME")
	private String createTime;
	@Version
	@Column(name = "VERSION")
	private Integer version;
}
