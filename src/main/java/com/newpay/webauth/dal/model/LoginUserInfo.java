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
@Table(name = "TBL_LOGIN_USER")
public class LoginUserInfo {
	@Id
	@Column(name = "LOGIN_ID")
	private String loginId;
	@Column(name = "LOGIN_NAME")
	private String loginName;
	@Column(name = "LOGIN_MOBILE")
	private String loginMobie;
	@Column(name = "LOGIN_EMAIL")
	private String loginEmail;
	@Column(name = "LOGIN_PWD")
	private String loginPwd;
	@Column(name = "STATUS")
	private Integer status;
	@Version
	@Column(name = "VERSION")
	private Integer version;
}
