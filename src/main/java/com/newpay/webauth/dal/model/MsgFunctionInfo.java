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
	@Column(name = "MSG_FUNCTION")
	private String msgFunction;
	@Column(name = "MSG_AUTH_TYPE")
	private Integer msgAuthType;
	@Column(name = "MSG_MAPPING")
	private String msgMapping;
	@Column(name = "MSG_TEMPLATE")
	private String msgTemplate;
	@Column(name = "STATUS")
	private Integer status;

}
