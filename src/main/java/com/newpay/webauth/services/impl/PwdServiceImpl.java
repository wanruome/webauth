/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午8:28:21
 */
package com.newpay.webauth.services.impl;

import java.security.PrivateKey;
import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.dal.mapper.UuidKeyPairMapper;
import com.newpay.webauth.dal.model.UuidKeyPair;
import com.newpay.webauth.services.PwdService;
import com.ruomm.base.tools.Base64;
import com.ruomm.base.tools.DesUtil;
import com.ruomm.base.tools.EncryptUtils;
import com.ruomm.base.tools.RSAUtils;
import com.ruomm.base.tools.StringUtils;

@Service
public class PwdServiceImpl implements PwdService {

	@Autowired
	UuidKeyPairMapper uuidKeyPairMapper;

	@Override
	public String getRealPassWord(String pwdRequest, String pwdEncrypt, String pwdUuid, String phone) {
		// TODO Auto-generated method stub
		if (pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_NONE)) {
			if (isPwdRuleOK(pwdRequest)) {
				return EncryptUtils.encodingMD5(EncryptUtils.encodingMD5(pwdRequest));
			}
			else {
				return parsePwdRuleToString(AppConfig.getUserPwdMinLength(), AppConfig.getUserPwdMinRule());
			}
		}
		else if (pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_MD5)) {
			return EncryptUtils.encodingMD5(pwdRequest);
		}
		else if (pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_RSAMD5) || pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_3DESMD5)) {
			String pwd = getPwdByRsaOr3Des(pwdRequest, pwdEncrypt, pwdUuid, phone);
			if (StringUtils.isBlank(pwd)) {
				return null;
			}
			else {
				return EncryptUtils.encodingMD5(pwd);
			}
		}
		else if (pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_RSA) || pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_3DES)) {
			String pwd = getPwdByRsaOr3Des(pwdRequest, pwdEncrypt, pwdUuid, phone);
			if (StringUtils.isBlank(pwd)) {
				return null;
			}
			else {
				if (isPwdRuleOK(pwd)) {
					return EncryptUtils.encodingMD5(EncryptUtils.encodingMD5(pwd));
				}
				else {
					return parsePwdRuleToString(AppConfig.getUserPwdMinLength(), AppConfig.getUserPwdMinRule());
				}
			}
		}
		return null;
	}

	public String parsePwdRuleToString(int length, int ruleNumber) {
		StringBuilder sb = new StringBuilder();
		if (length > 0) {
			sb.append("密码至少" + length + "位");
			if (ruleNumber > 0) {
				sb.append("且");
			}
		}
		else {
			sb.append("密码");
		}

		if (ruleNumber == 1) {
			sb.append("不能为纯数字");
		}
		else if (ruleNumber >= 2) {
			int tmp = ruleNumber > 4 ? 4 : ruleNumber;
			sb.append("需要大写、小写、数字、特殊符号" + tmp + "种组合");
		}
		return sb.toString();
	}

	@Override
	public boolean isEncryptTypeOk(String pwdEncrypt) {

		return AppConfig.getUserPwdEncryptMethod().contains("|" + pwdEncrypt + "|");
	}

	@Override
	public boolean isPwdRuleOK(String pwd) {

		if (StringUtils.isBlank(pwd)) {
			return false;
		}
		boolean isBig = false;
		boolean isNumber = false;
		boolean isSmall = false;
		boolean isOther = false;
		int minLength = AppConfig.getUserPwdMinLength();
		int minRule = AppConfig.getUserPwdMinRule();
		if (StringUtils.getLength(pwd) < minLength) {
			return false;
		}
		if (minRule <= 0) {
			return true;
		}
		for (int i = 0; i < pwd.length(); i++) {
			char tmp = pwd.charAt(i);
			if (tmp >= 'a' && tmp <= 'z') {
				isSmall = true;
			}
			else if (tmp >= 'A' && tmp <= 'Z') {
				isBig = true;
			}
			else if (tmp >= '0' && tmp <= '9') {
				isNumber = true;
			}
			else {
				isOther = true;
			}
		}
		int i = 0;
		if (isBig) {
			i++;
		}
		if (isSmall) {
			i++;
		}
		if (isOther) {
			i++;
		}
		if (isNumber) {
			i++;
		}
		if (minRule == 1) {
			if (i == 1 && isNumber) {
				return false;
			}
			else {
				return true;
			}
		}
		else if (minRule >= 4) {
			if (i >= 4) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if (i >= minRule) {
				return true;
			}
			else {
				return false;
			}
		}

	}

	private String getPwdByRsaOr3Des(String pwdRequest, String pwdEncrypt, String uuid, String phone) {
		if (StringUtils.isBlank(uuid)) {
			return null;
		}
		// if (StringUtils.getLength(uuidKeyPairReqDto.getUuid()) < 32) {
		// return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		// }
		UuidKeyPair queryUuidKeyPair = new UuidKeyPair();
		queryUuidKeyPair.setUuid(uuid);
		if (pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_RSA) || pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_RSAMD5)) {
			queryUuidKeyPair.setKeyType(AppConfig.PWD_ENCRYPT_RSA);
		}
		else if (pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_3DES) || pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_3DESMD5)) {
			queryUuidKeyPair.setKeyType(AppConfig.PWD_ENCRYPT_3DES);
		}

		// 获取UUID设置项目
		UuidKeyPair resultUuidKeyPair = uuidKeyPairMapper.selectByPrimaryKey(queryUuidKeyPair);
		if (null == resultUuidKeyPair) {
			return null;
		}
		String keyVersion = resultUuidKeyPair.getKeyVersion();
		long timeSkip = -1000l;
		try {
			timeSkip = Math.abs(new Date().getTime() - AppConfig.SDF_DB_VERSION.parse(keyVersion).getTime());
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			timeSkip = -1000l;
		}
		if (timeSkip > AppConfig.getKeyPairPublicKeyValidTime() || timeSkip < 0) {
			return null;
		}
		try {
			String pwd = null;
			if (resultUuidKeyPair.getKeyType().equals(AppConfig.PWD_ENCRYPT_RSA)) {
				PrivateKey privateKey = RSAUtils.loadPrivateKey(resultUuidKeyPair.getPrivateKey());
				pwd = new String(RSAUtils.decryptData(Base64.decode(pwdRequest), privateKey), "UTF-8");
			}
			else if (resultUuidKeyPair.getKeyType().equals(AppConfig.PWD_ENCRYPT_3DES)) {
				pwd = DesUtil.decryptString(pwdRequest, resultUuidKeyPair.getPrivateKey());
			}
			return pwd;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
