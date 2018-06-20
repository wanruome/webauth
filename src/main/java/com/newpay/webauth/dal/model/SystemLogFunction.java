/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月20日 下午4:52:16
 */
package com.newpay.webauth.dal.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "TBL_SYSTEM_LOG_FUNCTION")
public class SystemLogFunction {
	@Id
	@Column(name = "FUNCTION_ID")
	private String functionId;
	@Column(name = "FUNCTION_NAME")
	private String functionName;
	@Column(name = "MAPPING")
	private String mapping;
	@Column(name = "STATUS")
	private Integer status;
}
