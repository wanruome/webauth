/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午4:43:11
 */
package com.newpay.webauth.services;

import com.newpay.webauth.dal.request.UuidKeyPairReqDto;

public interface UuidKeyPairService {
	public Object getPublicKeyByUuid(UuidKeyPairReqDto uuidKeyPairReqDto);
}
