/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月11日 上午12:04:43
 */
package com.newpay.webauth.dal.request;

import java.util.Map;

import javax.validation.constraints.NotEmpty;

import com.ruomm.base.spring.RuommParamClass;

import lombok.Data;

@Data
@RuommParamClass(rootBody = "", rootHeaders = "", rootParams = "", renameParams = "userId=loginId,userName=loginName", renameBodys = "loginUser=loginId", renameHeaders = "host=MyHost,content-length=contentSiz", forceArrayFields = "MyHost,user-agent", isParseHeader = true)
public class LoginUserReqDto2 {
	@NotEmpty
	private String loginId;
	private String loginName;
	private String loginMobie;
	private String loginEmail;
	private String loginPwd;
	private Map<String, Object> requestParams;
	private Map<String, Object> requestHeaders;

}
