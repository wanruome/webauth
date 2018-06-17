/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月17日 上午2:58:43
 */
package com.newpay.webauth.dal.request.functionmsg;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class MsgTokenGetReqDto {
	@NotEmpty
	private String userId;
	@NotEmpty
	private String appId;
	@NotEmpty
	private String tokenId;
	@NotEmpty
	private String signInfo;
	@NotEmpty
	private String msgAddr;
	@NotEmpty
	private String msgFunction;
	@NotEmpty
	private String msgVerifyCode;
}
