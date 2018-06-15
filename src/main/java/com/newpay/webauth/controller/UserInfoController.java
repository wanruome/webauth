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

import com.newpay.webauth.config.PropertyUtil;
import com.newpay.webauth.dal.core.RequestPwdParse;
import com.newpay.webauth.dal.request.userinfo.UserInfoLoginReqDto;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyEmail;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyMobie;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyName;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyPwd;
import com.newpay.webauth.dal.request.userinfo.UserInfoRegisterReqDto;
import com.newpay.webauth.dal.response.BaseReturn;
import com.newpay.webauth.services.PwdService;
import com.newpay.webauth.services.UserInfoService;
import com.ruomm.base.tools.BaseWebUtils;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/app/userInfo")

public class UserInfoController {
	@Autowired
	UserInfoService userInfoService;
	@Autowired
	PwdService pwdService;

	@ApiOperation("用户注册")
	@PostMapping("/doRegister")
	public Object doRegister(@Valid @RequestBody UserInfoRegisterReqDto userInfoRegister, BindingResult bindingResult) {
		System.out.println(PropertyUtil.getProperty("pwd_encrypt_limit"));
		if (null == bindingResult || bindingResult.hasErrors()) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		// if (StringUtils.isBlank(userInfoRegister.getPwdEncrypt())) {
		// userInfoRegister.setPwdEncrypt(AppConfig.UserPwdEncryptDefault);
		// }
		// 验证在parseRequsetPwd里面进行了
		// if (StringUtils.isBlank(userInfoRegister.getPwd()) ||
		// StringUtils.isBlank(userInfoRegister.getUuid())) {
		// return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		//
		// }

		RequestPwdParse pwdParse = pwdService.parseRequsetPwd(userInfoRegister.getPwd(),
				userInfoRegister.getPwdEncrypt(), userInfoRegister.getUuid(), true);
		if (!pwdParse.isValid()) {
			return pwdParse.getReturnResp();
		}
		userInfoRegister.setPwd(pwdParse.getPwdParse());
		return userInfoService.doRegister(userInfoRegister);

	}

	@ApiOperation("用户注册")
	@PostMapping("/doLogin")
	public Object doLogin(@RequestBody UserInfoLoginReqDto userInfoLoginReqDto, BindingResult bindingResult) {
		if (null == bindingResult || bindingResult.hasErrors()) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		BaseWebUtils.getClassesRoot();
		BaseWebUtils.getWwwroot();
		RequestPwdParse pwdParse = pwdService.parseRequsetPwd(userInfoLoginReqDto.getPwd(),
				userInfoLoginReqDto.getPwdEncrypt(), userInfoLoginReqDto.getUuid(), false);
		if (!pwdParse.isValid()) {
			return pwdParse.getReturnResp();
		}
		userInfoLoginReqDto.setPwd(pwdParse.getPwdParse());
		return userInfoService.doLogin(userInfoLoginReqDto);
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
		RequestPwdParse oldPwdParse = pwdService.parseRequsetPwd(userInfoModifyPwd.getOldPwd(),
				userInfoModifyPwd.getOldPwdEncrypt(), userInfoModifyPwd.getUuid(), false);
		RequestPwdParse newPwdParse = pwdService.parseRequsetPwd(userInfoModifyPwd.getNewPwd(),
				userInfoModifyPwd.getNewPwdEncrypt(), userInfoModifyPwd.getUuid(), true);

		if (!newPwdParse.isValid()) {
			return newPwdParse.getReturnResp();
		}
		if (!oldPwdParse.isValid()) {
			return newPwdParse.getReturnResp();
		}
		userInfoModifyPwd.setNewPwd(newPwdParse.getPwdParse());
		userInfoModifyPwd.setOldPwd(oldPwdParse.getPwdParse());
		// 验证密码解密
		return userInfoService.doModifyPwd(userInfoModifyPwd);
	}

	@ApiOperation("用户注册")
	@PostMapping("/doFindPwd")
	public Object doFindPwd(@RequestBody UserInfoRegisterReqDto userInfoRegister) {
		return null;
	}

	@ApiOperation("用户注册")
	@PostMapping("/doModifyMobie")
	public Object doModifyMobie(@Valid @RequestBody UserInfoModifyMobie userInfoModifyMobie,
			BindingResult bindingResult) {
		if (null == bindingResult || bindingResult.hasErrors()) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		return userInfoService.doModifyMobie(userInfoModifyMobie);
	}

	@ApiOperation("用户注册")
	@PostMapping("/doModifyEmail")
	public Object doModifyEmail(@Valid @RequestBody UserInfoModifyEmail userInfoModifyEmail,
			BindingResult bindingResult) {
		if (null == bindingResult || bindingResult.hasErrors()) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		return userInfoService.doModifyEmail(userInfoModifyEmail);
	}

	@ApiOperation("用户注册")
	@PostMapping("/doModifyName")
	public Object doModifyName(@Valid @RequestBody UserInfoModifyName userInfoModifyName, BindingResult bindingResult) {
		if (null == bindingResult || bindingResult.hasErrors()) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		return userInfoService.doModifyName(userInfoModifyName);
	}

}
