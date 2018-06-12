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

import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.config.PropertyUtil;
import com.newpay.webauth.dal.request.LoginUserReqDto;
import com.newpay.webauth.dal.request.LoginUserReqDto2;
import com.newpay.webauth.dal.response.BaseReturn;
import com.newpay.webauth.services.LoginService;
import com.newpay.webauth.services.PwdService;
import com.ruomm.base.spring.RuommParam;
import com.ruomm.base.tools.StringUtils;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/login")

public class LoginUserInfoController {
	@Autowired
	LoginService loginService;
	@Autowired
	PwdService pwdService;

	@ApiOperation("用户注册")
	@PostMapping("/doRegister")
	public Object doRegister(@RequestBody LoginUserReqDto loginUserReqDto) {
		System.out.println(PropertyUtil.getProperty("pwd_encrypt_limit"));
		if (null == loginUserReqDto) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		if (StringUtils.isBlank(loginUserReqDto.getLoginMobie()) && StringUtils.isBlank(loginUserReqDto.getLoginEmail())
				&& StringUtils.isBlank(loginUserReqDto.getLoginName())) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		if (StringUtils.isBlank(loginUserReqDto.getLoginPwd()) || StringUtils.isBlank(loginUserReqDto.getUuid())) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);

		}
		if (StringUtils.isBlank(loginUserReqDto.getPwdEncrypt())) {
			// return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
			loginUserReqDto.setPwdEncrypt(AppConfig.getUserPwdEncryptDefault());
		}
		if (!pwdService.isEncryptTypeOk(loginUserReqDto.getPwdEncrypt())) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		String pwd = pwdService.getRealPassWord(loginUserReqDto.getLoginPwd(), loginUserReqDto.getPwdEncrypt(),
				loginUserReqDto.getUuid(), loginUserReqDto.getLoginMobie());
		if (StringUtils.isBlank(pwd)) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM, AppConfig.PWD_ERROR_PARSE);
		}
		if (StringUtils.getLength(pwd) != StringUtils.getLengthByChar(pwd)) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM, pwd);
		}
		loginUserReqDto.setLoginPwd(pwd);
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
