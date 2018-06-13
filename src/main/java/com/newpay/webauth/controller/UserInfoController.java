/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月10日 下午3:09:24
 */
package com.newpay.webauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.config.PropertyUtil;
import com.newpay.webauth.dal.request.userinfo.UserInfoRegisterReqDto;
import com.newpay.webauth.dal.response.BaseReturn;
import com.newpay.webauth.services.PwdService;
import com.newpay.webauth.services.UserInfoService;
import com.ruomm.base.tools.StringUtils;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/userInfo")

public class UserInfoController {
	@Autowired
	UserInfoService userInfoService;
	@Autowired
	PwdService pwdService;

	@ApiOperation("用户注册")
	@PostMapping("/doRegister")
	public Object doRegister(@RequestBody UserInfoRegisterReqDto userInfoRegister) {
		System.out.println(PropertyUtil.getProperty("pwd_encrypt_limit"));
		if (null == userInfoRegister) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		if (StringUtils.isBlank(userInfoRegister.getMobie()) && StringUtils.isBlank(userInfoRegister.getEmail())
				&& StringUtils.isBlank(userInfoRegister.getName())) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		if (StringUtils.isBlank(userInfoRegister.getPwd()) || StringUtils.isBlank(userInfoRegister.getUuid())) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);

		}
		if (StringUtils.isBlank(userInfoRegister.getPwdEncrypt())) {
			// return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
			userInfoRegister.setPwdEncrypt(AppConfig.getUserPwdEncryptDefault());
		}
		if (!pwdService.isEncryptTypeOk(userInfoRegister.getPwdEncrypt())) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		String pwd = pwdService.getRealPassWord(userInfoRegister.getPwd(), userInfoRegister.getPwdEncrypt(),
				userInfoRegister.getUuid(), userInfoRegister.getMobie());
		if (StringUtils.isBlank(pwd)) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM, AppConfig.PWD_ERROR_PARSE);
		}
		if (StringUtils.getLength(pwd) != StringUtils.getLengthByChar(pwd)) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM, pwd);
		}
		userInfoRegister.setPwd(pwd);
		return userInfoService.doRegister(userInfoRegister);

	}

}
