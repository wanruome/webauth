/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月19日 上午10:24:01
 */
package com.newpay.webauth.services.impl;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.dal.mapper.LoginAppInfoMapper;
import com.newpay.webauth.dal.model.LoginAppInfo;
import com.newpay.webauth.dal.request.appinfo.AppInfoAddReqDto;
import com.newpay.webauth.dal.request.appinfo.AppInfoModify;
import com.newpay.webauth.dal.response.ResultFactory;
import com.newpay.webauth.services.AppInfoService;
import com.ruomm.base.tools.EncryptUtils;
import com.ruomm.base.tools.RSAUtils;
import com.ruomm.base.tools.StringUtils;

@Service
public class AppInfoServiceImpl implements AppInfoService {
	@Autowired
	DbSeqServiceImpl dbSeqServiceImpl;
	@Autowired
	LoginAppInfoMapper loginAppInfoMapper;

	@Override
	public Object doAddAppInfo(AppInfoAddReqDto appInfoAddReqDto) {
		LoginAppInfo loginAppInfo = new LoginAppInfo();
		try {
			loginAppInfo.setAppName(appInfoAddReqDto.getAppName());
			loginAppInfo.setAppPwd(EncryptUtils.encodingMD5(appInfoAddReqDto.getAppPwd()));
			loginAppInfo.setTermLimit(Integer.valueOf(appInfoAddReqDto.getTermLimit()));
			loginAppInfo.setTermAndroidLimit(Integer.valueOf(appInfoAddReqDto.getTermAndroidLimit()));
			loginAppInfo.setTermIphoneLimit(Integer.valueOf(appInfoAddReqDto.getTermIphoneLimit()));
			loginAppInfo.setTermWebLimit(Integer.valueOf(appInfoAddReqDto.getTermWebLimit()));
			loginAppInfo.setNewKillOut(Integer.valueOf(appInfoAddReqDto.getNewKillOut()));
			loginAppInfo.setPublicKey(appInfoAddReqDto.getPublicKey());
			loginAppInfo.setNotifyUrl(appInfoAddReqDto.getNotifyUrl());
			if (!RSAUtils.isRealRsa1024(appInfoAddReqDto.getPublicKey())) {
				return ResultFactory.toNackPARAM();
			}
			if (loginAppInfo.getTermLimit() == null || loginAppInfo.getTermLimit() > 100
					|| loginAppInfo.getTermLimit() < 0) {
				return ResultFactory.toNackPARAM();
			}
			if (loginAppInfo.getTermAndroidLimit() == null || loginAppInfo.getTermAndroidLimit() > 100
					|| loginAppInfo.getTermAndroidLimit() < 0) {
				return ResultFactory.toNackPARAM();
			}
			if (loginAppInfo.getTermIphoneLimit() == null || loginAppInfo.getTermIphoneLimit() > 100
					|| loginAppInfo.getTermIphoneLimit() < 0) {
				return ResultFactory.toNackPARAM();
			}
			if (loginAppInfo.getTermWebLimit() == null || loginAppInfo.getTermWebLimit() > 100
					|| loginAppInfo.getTermWebLimit() < 0) {
				return ResultFactory.toNackPARAM();
			}
			if (loginAppInfo.getNewKillOut() == null
					|| (loginAppInfo.getNewKillOut() != 0 && loginAppInfo.getNewKillOut() != 1)) {
				return ResultFactory.toNackPARAM();
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			return ResultFactory.toNackPARAM();
		}
		loginAppInfo.setAppId(dbSeqServiceImpl.getLoginAppInfoNewPk());
		loginAppInfo.setCreateTime(AppConfig.SDF_DB_VERSION.format(new Date()));
		loginAppInfo.setStatus(2);
		loginAppInfo.setVersion(1);
		int dbResult = loginAppInfoMapper.insertSelective(loginAppInfo);
		// int dbResult = loginAppInfoMapper.insert(loginAppInfo);
		if (dbResult > 0) {
			HashMap<String, String> resultData = new HashMap<>();
			resultData.put("appId", loginAppInfo.getAppId());
			resultData.put("status", loginAppInfo.getStatus() + "");
			return ResultFactory.toAck(resultData);
		}
		else {
			return ResultFactory.toNackDB();
		}
	}

	@Override
	public Object doModifyAppInfo(AppInfoModify appInfoModify) {
		// TODO Auto-generated method stub
		LoginAppInfo queryAppInfo = new LoginAppInfo();
		queryAppInfo.setAppId(appInfoModify.getAppId());
		LoginAppInfo resultAppInfo = loginAppInfoMapper.selectByPrimaryKey(queryAppInfo);
		if (null == resultAppInfo) {
			return ResultFactory.toNackCORE("没有此应用或应用已停用");
		}
		if (resultAppInfo.getStatus() == 2) {
			return ResultFactory.toNackCORE("正在审核中，无法修改");
		}
		if (resultAppInfo.getStatus() != 1) {
			return ResultFactory.toNackCORE("应用已停用，无法修改");
		}
		if (!EncryptUtils.encodingMD5(appInfoModify.getAppPwd()).equals(resultAppInfo.getAppPwd())) {
			return ResultFactory.toNackCORE("密码不正确，无法进行修改");
		}
		boolean isTermLimitModify = false;
		int modifyCount = 0;
		LoginAppInfo loginAppInfo = new LoginAppInfo();
		loginAppInfo.setAppId(appInfoModify.getAppId());
		loginAppInfo.setVersion(resultAppInfo.getVersion());
		if (!StringUtils.isEmpty(appInfoModify.getAppNewPwd())) {
			if (appInfoModify.getAppNewPwd().length() != 32) {
				return ResultFactory.toNackPARAM();
			}
			String pwdTmp = EncryptUtils.encodingMD5(appInfoModify.getAppNewPwd());
			if (pwdTmp.equals(resultAppInfo.getAppPwd())) {
				return ResultFactory.toNackCORE("新密码不能和旧的密码相同");
			}
			loginAppInfo.setAppPwd(EncryptUtils.encodingMD5(appInfoModify.getAppNewPwd()));
			modifyCount++;
		}
		if (!StringUtils.isEmpty(appInfoModify.getAppName())) {
			loginAppInfo.setAppName(appInfoModify.getAppName());
			modifyCount++;
		}
		if (!StringUtils.isEmpty(appInfoModify.getTermLimit())) {
			loginAppInfo.setTermLimit(Integer.valueOf(appInfoModify.getTermLimit()));
			if (loginAppInfo.getTermLimit() == null || loginAppInfo.getTermLimit() > 100
					|| loginAppInfo.getTermLimit() < 0) {
				return ResultFactory.toNackPARAM();
			}
			if (!isTermLimitModify) {
				isTermLimitModify = true;
				modifyCount++;
			}
		}
		if (!StringUtils.isEmpty(appInfoModify.getTermAndroidLimit())) {
			loginAppInfo.setTermAndroidLimit(Integer.valueOf(appInfoModify.getTermAndroidLimit()));
			if (loginAppInfo.getTermAndroidLimit() == null || loginAppInfo.getTermAndroidLimit() > 100
					|| loginAppInfo.getTermAndroidLimit() < 0) {
				return ResultFactory.toNackPARAM();
			}
			if (!isTermLimitModify) {
				isTermLimitModify = true;
				modifyCount++;
			}
		}
		if (!StringUtils.isEmpty(appInfoModify.getTermIphoneLimit())) {
			loginAppInfo.setTermIphoneLimit(Integer.valueOf(appInfoModify.getTermIphoneLimit()));
			if (loginAppInfo.getTermIphoneLimit() == null || loginAppInfo.getTermIphoneLimit() > 100
					|| loginAppInfo.getTermIphoneLimit() < 0) {
				return ResultFactory.toNackPARAM();
			}
			if (!isTermLimitModify) {
				isTermLimitModify = true;
				modifyCount++;
			}
		}
		if (!StringUtils.isEmpty(appInfoModify.getTermWebLimit())) {
			loginAppInfo.setTermWebLimit(Integer.valueOf(appInfoModify.getTermWebLimit()));
			if (loginAppInfo.getTermWebLimit() == null || loginAppInfo.getTermWebLimit() > 100
					|| loginAppInfo.getTermWebLimit() < 0) {
				return ResultFactory.toNackPARAM();
			}
			if (!isTermLimitModify) {
				isTermLimitModify = true;
				modifyCount++;
			}
		}
		if (!StringUtils.isEmpty(appInfoModify.getNewKillOut())) {
			loginAppInfo.setNewKillOut(Integer.valueOf(appInfoModify.getNewKillOut()));
			if (loginAppInfo.getNewKillOut() == null
					|| (loginAppInfo.getNewKillOut() != 0 && loginAppInfo.getNewKillOut() != 1)) {
				return ResultFactory.toNackPARAM();
			}
			modifyCount++;
		}

		if (!StringUtils.isEmpty(appInfoModify.getPublicKey())) {
			if (!RSAUtils.isRealRsa1024(appInfoModify.getPublicKey())) {
				return ResultFactory.toNackPARAM();
			}
			loginAppInfo.setPublicKey(appInfoModify.getPublicKey());
			modifyCount++;
		}
		if (!StringUtils.isEmpty(appInfoModify.getNotifyUrl())) {
			loginAppInfo.setNotifyUrl(appInfoModify.getNotifyUrl());
			modifyCount++;
		}

		if (modifyCount <= 0) {
			return ResultFactory.toNackPARAM();
		}
		if (AppConfig.APPINFO_MODIFY_LIMIT_ONE > 0 && modifyCount > 1) {
			return ResultFactory.toNackCORE("只能逐项修改应用信息");
		}
		boolean verifyFlag = appInfoModify.doVerifySignInfo(resultAppInfo.getPublicKey());
		if (!verifyFlag) {
			return ResultFactory.toNackCORE("应用签名信息不正确");
		}

		int dbResult = loginAppInfoMapper.updateByPrimaryKeySelective(loginAppInfo);
		if (dbResult > 0) {
			return ResultFactory.toAck(null);
		}
		else {
			return ResultFactory.toNackDB();
		}
	}

}
