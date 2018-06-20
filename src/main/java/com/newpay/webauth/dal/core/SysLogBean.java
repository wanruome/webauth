/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月20日 下午4:12:53
 */
package com.newpay.webauth.dal.core;

import lombok.Data;

@Data
public class SysLogBean {
	private String appId;
	private String userId;
	private String uuid;
	private String functionId;
	private String functionName;
}
