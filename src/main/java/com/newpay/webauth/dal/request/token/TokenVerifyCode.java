/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 下午5:26:57
 */
package com.newpay.webauth.dal.request.token;

import lombok.Data;

@Data
public class TokenVerifyCode {
	private String userId;
	private String codeType;
}
