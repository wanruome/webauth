/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午3:24:25
 */
package com.newpay.webauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newpay.webauth.dal.request.UuidKeyPairReqDto;
import com.newpay.webauth.services.UuidKeyPairService;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/ras")
public class RsaController {
	@Autowired
	UuidKeyPairService uuidKeyPairService;

	@ApiOperation("密码获取")
	@PostMapping("/callRsaByUUID")
	public Object getPublicKey(@RequestBody UuidKeyPairReqDto rsaInfoReqDto) {
		return uuidKeyPairService.getPublicKeyByUUID(rsaInfoReqDto);

	}
}
