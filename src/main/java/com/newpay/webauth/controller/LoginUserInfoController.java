/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月10日 下午3:09:24
 */
package com.newpay.webauth.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newpay.webauth.dal.request.LoginUserReqDto;
import com.newpay.webauth.dal.request.LoginUserReqDto2;
import com.newpay.webauth.services.LoginService;
import com.ruomm.base.spring.RuommParam;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/login")

public class LoginUserInfoController {
	@Autowired
	LoginService loginService;

	@ApiOperation("用户注册")
	@PostMapping("/doRegister")
	public Object doRegister(@Valid @RequestBody LoginUserReqDto loginUserReqDto) {
		System.out.println(loginUserReqDto.toString());
		return loginService.doRegister(loginUserReqDto);

	}

	@ApiOperation("用户登录")
	@PostMapping("/postLogin")
	public String postLogin(@RuommParam LoginUserReqDto2 mapString) {
		// System.out.println(loginUserReqDto.toString());
		System.out.println(mapString.getLoginEmail().toString());
		return "adsfdsa";
	}

	@ApiOperation("用户登录")
	@GetMapping("/getLogin")
	public String getLogin(@Valid LoginUserReqDto2 mapString) {
		// System.out.println(loginUserReqDto.toString());
		System.out.println(mapString.getLoginName().toString());
		return "adsfdsa";
	}
}
