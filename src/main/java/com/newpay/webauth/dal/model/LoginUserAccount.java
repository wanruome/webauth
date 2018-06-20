/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月10日 下午2:51:33
 */
package com.newpay.webauth.dal.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import tk.mybatis.mapper.annotation.Version;

@Data
@Table(name = "TBL_LOGIN_USER_ACCOUNT")
public class LoginUserAccount {
	@Id
	@Column(name = "LOGIN_ID")
	private String loginId;
	@Column(name = "LOGIN_NAME")
	private String loginName;
	@Column(name = "LOGIN_MOBILE")
	private String loginMobile;
	@Column(name = "LOGIN_EMAIL")
	private String loginEmail;
	@Column(name = "LOGIN_PWD")
	private String loginPwd;
	@Column(name = "STATUS")
	private Integer status;
	@Column(name = "PWD_ERR_COUNT")
	private Integer pwdErrCount;
	@Column(name = "LAST_AUTH_UUID")
	private String lastAuthUuid;
	@Column(name = "LAST_AUTH_TIME")
	private String lastAuthTime;
	@Column(name = "REGISTER_TIME")
	private String registerTime;
	@Column(name = "UPDATE_TIME")
	private String updateTime;

	@Version
	@Column(name = "VERSION")
	private Integer version;
}
