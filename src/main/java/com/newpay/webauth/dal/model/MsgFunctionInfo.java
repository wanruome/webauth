/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 下午8:45:42
 */
package com.newpay.webauth.dal.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "TBL_MSG_FUNTION_INFO")
public class MsgFunctionInfo {
	@Id
	@Column(name = "FUNCTION_ID")
	private String functionId;
	@Column(name = "FUNCTION_NAME")
	private String functionName;
	@Column(name = "AUTH_TYPE")
	private Integer authType;
	@Column(name = "NEED_MSGTOKEN")
	private Integer needMsgToken;
	@Column(name = "VERIFY_FIELD_NAME")
	private String verfifyFieldName;
	@Column(name = "MAPPING")
	private String mapping;
	@Column(name = "TEMPLATE")
	private String template;
	@Column(name = "STATUS")
	private Integer status;

}
