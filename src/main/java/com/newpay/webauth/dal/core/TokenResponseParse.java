/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月16日 下午1:23:51
 */
package com.newpay.webauth.dal.core;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.newpay.webauth.dal.model.LoginUserToken;

import lombok.Data;

@Data
public class TokenResponseParse {
	private boolean isValid;
	private boolean isNeedVerifyCode;
	private JSONObject returnResp;
	private LoginUserToken loginUserToken;
	private List<LoginUserToken> tokenList;
}
