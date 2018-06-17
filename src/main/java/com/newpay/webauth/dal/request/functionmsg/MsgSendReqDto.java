/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 下午8:17:56
 */
package com.newpay.webauth.dal.request.functionmsg;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class MsgSendReqDto {
	private String appId;
	private String userId;
	private String tokenId;
	private String signInfo;
	@NotEmpty
	private String uuid;
	@NotEmpty
	private String msgFunction;
	@NotEmpty
	private String msgAddr;
	private String msgFunctionTo;
	private String msgToken;
}
