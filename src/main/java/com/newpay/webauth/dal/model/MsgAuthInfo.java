/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月14日 上午10:26:19
 */
package com.newpay.webauth.dal.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import tk.mybatis.mapper.annotation.Version;

@Data
@Table(name = "TBL_MSG_AUTH_INFO")
public class MsgAuthInfo {
	@Id
	@Column(name = "UUID")
	private String uuid;
	@Column(name = "MSG_ADDR")
	private String msgAddr;
	@Column(name = "FUNCTION_ID")
	private String functionId;
	@Column(name = "SESSION_TOKEN")
	private String sessionToken;
	@Column(name = "MSG_CODE")
	private String msgCode;
	@Column(name = "MSG_VALID_TIME")
	private String msgValidTime;
	@Column(name = "MSG_TOKEN")
	private String msgToken;
	@Version
	@Column(name = "VERISON")
	private Integer version;
}
