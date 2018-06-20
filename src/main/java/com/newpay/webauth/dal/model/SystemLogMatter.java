/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月20日 下午2:44:54
 */
package com.newpay.webauth.dal.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "TBL_SYSTEM_LOG_MATTER")
public class SystemLogMatter {
	@Id
	@Column(name = "LOG_ID")
	private String logId;
	@Column(name = "USER_ID")
	private String userId;
	@Column(name = "LOG_KEY_VALUE")
	private String logKeyValue;
	@Column(name = "UUID")
	private String uuid;
	@Column(name = "APP_ID")
	private String appId;
	@Column(name = "FUNCTION_ID")
	private String functionId;
	@Column(name = "FUNCTION_NAME")
	private String functionName;
	@Column(name = "REQUEST_INFO")
	private String requestInfo;
	@Column(name = "RESULT_CODE")
	private String resultCode;
	@Column(name = "RESULT_MSG")
	private String resultMsg;
	@Column(name = "MAPPING")
	private String mapping;
	@Column(name = "EXCUTE_TIME")
	private String excuteTime;
	@Column(name = "CREATE_DATE")
	private String createDate;
	@Column(name = "CREATE_TIME")
	private String createTime;
}
