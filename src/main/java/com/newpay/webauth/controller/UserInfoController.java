/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月10日 下午3:09:24
 */
package com.newpay.webauth.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.config.PropertyUtil;
import com.newpay.webauth.dal.core.RequestPwdParse;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyPwd;
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
		// 验证在parseRequsetPwd里面进行了
		// if (StringUtils.isBlank(userInfoRegister.getPwd()) ||
		// StringUtils.isBlank(userInfoRegister.getUuid())) {
		// return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		//
		// }
		if (StringUtils.isBlank(userInfoRegister.getPwdEncrypt())) {
			// return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
			userInfoRegister.setPwdEncrypt(AppConfig.UserPwdEncryptDefault);
		}
		RequestPwdParse pwdParse = pwdService.parseRequsetPwd(userInfoRegister.getPwd(),
				userInfoRegister.getPwdEncrypt(), userInfoRegister.getUuid());
		if (!pwdParse.isValid()) {
			return pwdParse.getReturnResp();
		}
		userInfoRegister.setPwd(pwdParse.getPwdParse());
		return userInfoService.doRegister(userInfoRegister);

	}

	@ApiOperation("用户注册")
	@PostMapping("/doModifyPwd")
	public Object doModifyPwd(@Valid @RequestBody UserInfoModifyPwd userInfoModifyPwd, BindingResult bindingResult) {
		if (null == bindingResult || bindingResult.hasErrors()) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		// if (!pwdService.isEncryptTypeOk(userInfoModifyPwd.getPwdEncrypt())) {
		// return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		// }
		// 验证密码解密
		return null;
	}

	@ApiOperation("用户注册")
	@PostMapping("/doFindPwd")
	public Object doFindPwd(@RequestBody UserInfoRegisterReqDto userInfoRegister) {
		return null;
	}

	@ApiOperation("用户注册")
	@PostMapping("/doModifyPhone")
	public Object doModifyPhone(@RequestBody UserInfoRegisterReqDto userInfoRegister) {
		return null;
	}

	@ApiOperation("用户注册")
	@PostMapping("/doModifyEmail")
	public Object doModifyEmail(@RequestBody UserInfoRegisterReqDto userInfoRegister) {
		return null;
	}

	@ApiOperation("用户注册")
	@PostMapping("/doModifyName")
	public Object doModifyName(@RequestBody UserInfoRegisterReqDto userInfoRegister) {
		return null;
	}

}
