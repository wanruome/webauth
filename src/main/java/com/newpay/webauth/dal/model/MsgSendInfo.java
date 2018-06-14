/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 下午7:59:07
 */
package com.newpay.webauth.dal.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "TBL_MSG_SEND_INFO")
public class MsgSendInfo {
	@Id
	@Column(name = "MSG_ID")
	private String msgId;
	@Column(name = "USER_ID")
	private String userId;
	@Column(name = "FUNCTION_ID")
	private String functionId;
	@Column(name = "MSG_TYPE")
	private Integer msgType;
	@Column(name = "MSG_TOKEN")
	private String msgToken;

	@Column(name = "MSG_ADDR")
	private String msgAddr;
	@Column(name = "MSG_CODE")
	private String msgCode;
	@Column(name = "MSG_CONTENT")
	private String msgContent;
	@Column(name = "MSG_VALID_TIME")
	private String msgValidTime;
	@Column(name = "MSG_STATUS")
	private Integer msgStatus;
	@Column(name = "CREATE_DATE")
	private String createDate;
	@Column(name = "CREATE_TIME")
	private String createTime;
}
