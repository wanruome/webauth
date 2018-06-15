/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 下午5:24:31
 */
package com.newpay.webauth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newpay.webauth.dal.request.userinfo.UserInfoRegisterReqDto;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/app/token")
public class TokenController {
	@ApiOperation("用户注册")
	@PostMapping("/doGetOprateToke")
	public Object doGetOprateToke(@RequestBody UserInfoRegisterReqDto userInfoRegister) {
		return null;
	}
}
