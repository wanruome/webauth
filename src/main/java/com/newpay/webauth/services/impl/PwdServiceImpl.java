/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午8:28:21
 */
package com.newpay.webauth.services.impl;

import java.security.PrivateKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.dal.core.PwdRequestParse;
import com.newpay.webauth.dal.mapper.UuidKeyPairMapper;
import com.newpay.webauth.dal.model.UuidKeyPair;
import com.newpay.webauth.dal.response.ResultFactory;
import com.newpay.webauth.services.PwdService;
import com.ruomm.base.tools.Base64;
import com.ruomm.base.tools.DesUtil;
import com.ruomm.base.tools.EncryptUtils;
import com.ruomm.base.tools.RSAUtils;
import com.ruomm.base.tools.StringUtils;
import com.ruomm.base.tools.TimeUtils;

@Service
public class PwdServiceImpl implements PwdService {

	@Autowired
	UuidKeyPairMapper uuidKeyPairMapper;

	private boolean isEncryptTypeOk(String pwdEncrypt) {
		if (StringUtils.isEmpty(AppConfig.UserPwdEncryptMethod)) {
			return false;
		}
		return AppConfig.UserPwdEncryptMethod.contains("|" + pwdEncrypt + "|");
	}

	private boolean isPwdRuleOK(String pwd, int minLength, int maxLength, int minRule, boolean isRuleCheck) {
		if (!isRuleCheck) {
			return true;
		}

		if (StringUtils.isBlank(pwd)) {
			return false;
		}
		if (pwd.length() > maxLength) {
			return false;
		}
		if (StringUtils.getLength(pwd) < minLength) {
			return false;
		}

		boolean isBig = false;
		boolean isNumber = false;
		boolean isSmall = false;
		boolean isOther = false;
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

	private String parsePwdRuleToString(int minLength, int maxLength, int ruleNumber) {

		StringBuilder sb = new StringBuilder();
		if (minLength > 0 && maxLength > 0) {
			sb.append("密码需要" + minLength + "-" + maxLength + "位");
			if (ruleNumber > 0) {
				sb.append("且");
			}
		}
		else if (minLength > 0) {
			sb.append("密码至少" + minLength + "位");
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

	private String getPwdByRsaOr3Des(String pwdRequest, String pwdEncrypt, String uuid) {
		// if (StringUtils.isBlank(uuid)) {
		// return null;
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
		boolean versionCacheFlag = TimeUtils.isCacheOk(keyVersion, AppConfig.SDF_DB_VERSION,
				AppConfig.KeyPairPublicKeyValidTime);
		if (!versionCacheFlag) {
			return null;
		}
		// long timeSkip = -1000l;
		// try {
		// timeSkip = Math.abs(new Date().getTime() -
		// AppConfig.SDF_DB_VERSION.parse(keyVersion).getTime());
		// }
		// catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// timeSkip = -1000l;
		// }
		// if (timeSkip > AppConfig.KeyPairPublicKeyValidTime || timeSkip < 0) {
		// return null;
		// }
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

	/***
	 * 解析请求的密码，返回解析是否成功和解析好的密码字段
	 */
	@Override
	public PwdRequestParse parseRequsetPwd(String pwdRequest, String pwdEncrypt, String pwdUuid, boolean isRuleCheck) {
		// TODO Auto-generated method stub
		PwdRequestParse pwdParse = new PwdRequestParse();
		pwdParse.setValid(false);
		if (StringUtils.isBlank(pwdRequest) || StringUtils.isEmpty(pwdUuid)) {
			pwdParse.setValid(false);
			pwdParse.setReturnResp(ResultFactory.toNackPARAM());
			return pwdParse;
		}
		if (!isEncryptTypeOk(pwdEncrypt)) {
			pwdParse.setValid(false);
			pwdParse.setReturnResp(ResultFactory.toNackPARAM());
			return pwdParse;
		}
		if (pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_NONE)) {
			if (isPwdRuleOK(pwdRequest, AppConfig.UserPwdMinLength, AppConfig.UserPwdMaxLength,
					AppConfig.UserPwdMinRule, isRuleCheck)) {
				String pwdResult = EncryptUtils.encodingMD5(EncryptUtils.encodingMD5(pwdRequest));
				pwdParse.setPwdParse(pwdResult);
				pwdParse.setValid(true);
				pwdParse.setReturnResp(null);
				return pwdParse;
			}
			else {
				String msg = parsePwdRuleToString(AppConfig.UserPwdMinLength, AppConfig.UserPwdMaxLength,
						AppConfig.UserPwdMinRule);
				pwdParse.setValid(false);
				pwdParse.setReturnResp(ResultFactory.toNackPARAM(msg));
				return pwdParse;

			}
		}
		else if (pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_MD5)) {
			if (pwdRequest.length() != 32) {
				pwdParse.setValid(false);
				pwdParse.setReturnResp(ResultFactory.toNackPARAM());
				return pwdParse;
			}
			String pwdResult = EncryptUtils.encodingMD5(pwdRequest);
			pwdParse.setPwdParse(pwdResult);
			pwdParse.setValid(true);
			pwdParse.setReturnResp(null);
			return pwdParse;
		}
		else if (pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_RSAMD5) || pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_3DESMD5)) {
			String pwd = getPwdByRsaOr3Des(pwdRequest, pwdEncrypt, pwdUuid);
			if (StringUtils.isBlank(pwd)) {
				pwdParse.setValid(false);
				pwdParse.setReturnResp(ResultFactory.toNackPARAM(AppConfig.PWD_ERROR_PARSE));
				return pwdParse;
			}
			else if (pwd.length() != 32) {
				pwdParse.setValid(false);
				pwdParse.setReturnResp(ResultFactory.toNackPARAM());
				return pwdParse;
			}
			else {
				String pwdResult = EncryptUtils.encodingMD5(pwd);
				pwdParse.setPwdParse(pwdResult);
				pwdParse.setValid(true);
				pwdParse.setReturnResp(null);
				return pwdParse;
			}
		}
		else if (pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_RSA) || pwdEncrypt.equals(AppConfig.PWD_ENCRYPT_3DES)) {
			String pwd = getPwdByRsaOr3Des(pwdRequest, pwdEncrypt, pwdUuid);
			if (StringUtils.isBlank(pwd)) {
				pwdParse.setValid(false);
				pwdParse.setReturnResp(ResultFactory.toNackPARAM(AppConfig.PWD_ERROR_PARSE));
				return pwdParse;
			}
			else {
				if (isPwdRuleOK(pwd, AppConfig.UserPwdMinLength, AppConfig.UserPwdMaxLength, AppConfig.UserPwdMinRule,
						isRuleCheck)) {
					String pwdResult = EncryptUtils.encodingMD5(EncryptUtils.encodingMD5(pwd));
					pwdParse.setPwdParse(pwdResult);
					pwdParse.setValid(true);
					pwdParse.setReturnResp(null);
					return pwdParse;
				}
				else {
					String msg = parsePwdRuleToString(AppConfig.UserPwdMinLength, AppConfig.UserPwdMaxLength,
							AppConfig.UserPwdMinRule);
					pwdParse.setValid(false);
					pwdParse.setReturnResp(ResultFactory.toNackPARAM(msg));
					return pwdParse;

				}
			}
		}
		else {
			pwdParse.setValid(false);
			pwdParse.setReturnResp(ResultFactory.toNackPARAM(AppConfig.PWD_ERROR_PARSE));
			return pwdParse;
		}
	}

}
