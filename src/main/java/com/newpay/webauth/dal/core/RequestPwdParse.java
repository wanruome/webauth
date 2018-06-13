/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 上午11:39:43
 */
package com.newpay.webauth.dal.core;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;

@Data
public class RequestPwdParse {
	boolean isValid = false;
	JSONObject returnResp;
	String pwdParse;
}
