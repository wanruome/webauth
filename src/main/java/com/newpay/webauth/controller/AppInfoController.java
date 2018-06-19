/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月15日 下午10:54:00
 */
package com.newpay.webauth.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newpay.webauth.dal.request.appinfo.AppInfoAddReqDto;
import com.newpay.webauth.dal.request.appinfo.AppInfoModify;
import com.newpay.webauth.dal.response.ResultFactory;
import com.newpay.webauth.services.AppInfoService;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("app/appInfo")
public class AppInfoController {
	@Autowired
	AppInfoService appInfoService;

	@ApiOperation("用户注册")
	@PostMapping("/doTest")
	public Object doAddAppInfo() {
		return "成功";
	}

	@ApiOperation("用户注册")
	@PostMapping("/doAddAppInfo")
	public Object doAddAppInfo(@Valid @RequestBody AppInfoAddReqDto appInfoAddReqDto, BindingResult bindingResult) {
		if (null == bindingResult || bindingResult.hasErrors()) {
			return ResultFactory.toNackPARAM();
		}
		return appInfoService.doAddAppInfo(appInfoAddReqDto);
	}

	@ApiOperation("用户注册")
	@PostMapping("/doModifyAppInfo")
	public Object doModifyAppInfo(@Valid @RequestBody AppInfoModify appInfoModify, BindingResult bindingResult) {
		if (null == bindingResult || bindingResult.hasErrors()) {
			return ResultFactory.toNackPARAM();
		}
		return appInfoService.doModifyAppInfo(appInfoModify);
	}
}
