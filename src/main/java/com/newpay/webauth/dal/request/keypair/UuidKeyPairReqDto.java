/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午3:27:04
 */
package com.newpay.webauth.dal.request.keypair;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class UuidKeyPairReqDto {
	@NotEmpty
	@Length(min = 24, max = 40)
	private String uuid;
	@NotEmpty
	private String keyType;
	private String rasPublicKey;

}
