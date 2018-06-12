/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午3:27:04
 */
package com.newpay.webauth.dal.request;

import lombok.Data;

@Data
public class UuidKeyPairReqDto {
	private String uuid;
	private String loginId;

}
