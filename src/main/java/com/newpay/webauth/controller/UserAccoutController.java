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
import com.newpay.webauth.dal.core.PwdRequestParse;
import com.newpay.webauth.dal.request.userinfo.UserInfoFindPwd;
import com.newpay.webauth.dal.request.userinfo.UserInfoLoginReqDto;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyEmail;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyMobie;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyName;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyPwd;
import com.newpay.webauth.dal.request.userinfo.UserInfoRegisterReqDto;
import com.newpay.webauth.dal.response.ResultFactory;
import com.newpay.webauth.services.PwdService;
import com.newpay.webauth.services.UserAccountService;
import com.ruomm.base.tools.BaseWebUtils;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/app/userAccount")

public class UserAccoutController {
	@Autowired
	UserAccountService userAccountService;
	@Autowired
	PwdService pwdService;

	@ApiOperation("用户注册")
	@PostMapping("/doRegister")
	public Object doRegister(@Valid @RequestBody UserInfoRegisterReqDto userInfoRegister, BindingResult bindingResult) {
		System.out.println(PropertyUtil.getProperty("pwd_encrypt_limit"));
		if (null == bindingResult || bindingResult.hasErrors()) {
			return ResultFactory.toNackPARAM();
		}
		// if (StringUtils.isBlank(userInfoRegister.getPwdEncrypt())) {
		// userInfoRegister.setPwdEncrypt(AppConfig.UserPwdEncryptDefault);
		// }
		PwdRequestParse pwdParse = pwdService.parseRequsetPwd(userInfoRegister.getPwd(),
				userInfoRegister.getPwdEncrypt(), userInfoRegister.getUuid(), true);
		if (!pwdParse.isValid()) {
			return pwdParse.getReturnResp();
		}
		userInfoRegister.setPwd(pwdParse.getPwdParse());
		return userAccountService.doRegister(userInfoRegister);

	}

	@ApiOperation("用户登录")
	@PostMapping("/doLogin")
	public Object doLogin(@Valid @RequestBody UserInfoLoginReqDto userInfoLoginReqDto, BindingResult bindingResult) {
		if (null == bindingResult || bindingResult.hasErrors()) {
			return ResultFactory.toNackPARAM();
		}
		BaseWebUtils.getClassesRoot();
		BaseWebUtils.getWwwroot();
		PwdRequestParse pwdParse = pwdService.parseRequsetPwd(userInfoLoginReqDto.getPwd(),
				userInfoLoginReqDto.getPwdEncrypt(), userInfoLoginReqDto.getUuid(), false);
		if (!pwdParse.isValid()) {
			return pwdParse.getReturnResp();
		}
		userInfoLoginReqDto.setPwd(pwdParse.getPwdParse());
		return userAccountService.doLogin(userInfoLoginReqDto);
	}

	@ApiOperation("修改密码")
	@PostMapping("/doModifyPwd")
	public Object doModifyPwd(@Valid @RequestBody UserInfoModifyPwd userInfoModifyPwd, BindingResult bindingResult) {
		if (null == bindingResult || bindingResult.hasErrors()) {
			return ResultFactory.toNackPARAM();
		}
		PwdRequestParse oldPwdParse = pwdService.parseRequsetPwd(userInfoModifyPwd.getOldPwd(),
				userInfoModifyPwd.getOldPwdEncrypt(), userInfoModifyPwd.getUuid(), false);
		PwdRequestParse newPwdParse = pwdService.parseRequsetPwd(userInfoModifyPwd.getNewPwd(),
				userInfoModifyPwd.getNewPwdEncrypt(), userInfoModifyPwd.getUuid(), true);

		if (!newPwdParse.isValid()) {
			return newPwdParse.getReturnResp();
		}
		if (!oldPwdParse.isValid()) {
			return newPwdParse.getReturnResp();
		}
		if (newPwdParse.getPwdParse().equals(oldPwdParse.getPwdParse())) {
			return ResultFactory.toNackCORE("新密码不能和旧密码一致");
		}
		userInfoModifyPwd.setNewPwd(newPwdParse.getPwdParse());
		userInfoModifyPwd.setOldPwd(oldPwdParse.getPwdParse());
		// 验证密码解密
		return userAccountService.doModifyPwd(userInfoModifyPwd);
	}

	@ApiOperation("找回密码")
	@PostMapping("/doFindPwd")
	public Object doFindPwd(@Valid @RequestBody UserInfoFindPwd userInfoFindPwd, BindingResult bindingResult) {
		if (null == bindingResult || bindingResult.hasErrors()) {
			return ResultFactory.toNackPARAM();
		}
		PwdRequestParse oldPwdParse = pwdService.parseRequsetPwd(userInfoFindPwd.getNewPwd(),
				userInfoFindPwd.getNewPwdEncrypt(), userInfoFindPwd.getUuid(), true);
		if (!oldPwdParse.isValid()) {
			return oldPwdParse.getReturnResp();
		}
		userInfoFindPwd.setNewPwd(oldPwdParse.getPwdParse());
		return userAccountService.doFindPwd(userInfoFindPwd);
	}

	@ApiOperation("修改手机号")
	@PostMapping("/doModifyMobile")
	public Object doModifyMobile(@Valid @RequestBody UserInfoModifyMobie userInfoModifyMobie,
			BindingResult bindingResult) {
		if (null == bindingResult || bindingResult.hasErrors()) {
			return ResultFactory.toNackPARAM();
		}
		return userAccountService.doModifyMobile(userInfoModifyMobie);
	}

	@ApiOperation("修改邮箱号码")
	@PostMapping("/doModifyEmail")
	public Object doModifyEmail(@Valid @RequestBody UserInfoModifyEmail userInfoModifyEmail,
			BindingResult bindingResult) {
		if (null == bindingResult || bindingResult.hasErrors()) {
			return ResultFactory.toNackPARAM();
		}
		return userAccountService.doModifyEmail(userInfoModifyEmail);
	}

	@ApiOperation("修改登录名")
	@PostMapping("/doModifyName")
	public Object doModifyName(@Valid @RequestBody UserInfoModifyName userInfoModifyName, BindingResult bindingResult) {
		if (null == bindingResult || bindingResult.hasErrors()) {
			return ResultFactory.toNackPARAM();
		}
		return userAccountService.doModifyName(userInfoModifyName);
	}

}
