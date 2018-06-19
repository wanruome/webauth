/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月15日 下午9:25:32
 */
package com.newpay.webauth.dal.request.appinfo;

import java.security.PublicKey;

import javax.validation.constraints.NotEmpty;

import com.ruomm.base.tools.Base64;
import com.ruomm.base.tools.EncryptUtils;
import com.ruomm.base.tools.RSAUtils;
import com.ruomm.base.tools.StringUtils;

import lombok.Data;

@Data
public class AppInfoModify {
	@NotEmpty

	private String appId;
	@NotEmpty
	private String appPwd;
	private String appName;
	private String appNewPwd;
	private String termLimit;
	private String termAndroidLimit;
	private String termIphoneLimit;
	private String termWebLimit;
	private String newKillOut;
	private String publicKey;
	private String notifyUrl;
	private String rsaSignInfo;

	public boolean doVerifySignInfo(String appPublicKey) {
		try {
			PublicKey appKey = RSAUtils.getPublicKey(Base64.decode(appPublicKey));
			String signInfoMd5 = new String(RSAUtils.decryptData(Base64.decode(rsaSignInfo), appKey));
			if (doGetSignInfo().equals(signInfoMd5)) {
				return true;
			}
			else {
				return false;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public String doGetSignInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append(appId);
		sb.append(appPwd);
		if (!StringUtils.isEmpty(appNewPwd)) {
			sb.append(appNewPwd);
		}
		if (!StringUtils.isEmpty(appName)) {
			sb.append(appName);
		}
		if (!StringUtils.isEmpty(termLimit)) {
			sb.append(termLimit);
		}
		if (!StringUtils.isEmpty(termAndroidLimit)) {
			sb.append(termAndroidLimit);
		}
		if (!StringUtils.isEmpty(termIphoneLimit)) {
			sb.append(termIphoneLimit);
		}
		if (!StringUtils.isEmpty(termWebLimit)) {
			sb.append(termWebLimit);
		}
		if (!StringUtils.isEmpty(newKillOut)) {
			sb.append(newKillOut);
		}
		if (!StringUtils.isEmpty(publicKey)) {
			sb.append(publicKey);
		}
		if (!StringUtils.isEmpty(notifyUrl)) {
			sb.append(notifyUrl);
		}

		return EncryptUtils.encodingMD5(sb.toString());
	}

}
