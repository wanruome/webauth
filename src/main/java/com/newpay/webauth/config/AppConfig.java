/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午4:55:51
 */
package com.newpay.webauth.config;

import java.text.SimpleDateFormat;

import com.ruomm.base.tools.PropertyReader;
import com.ruomm.base.tools.StringUtils;

public class AppConfig {
	public static PropertyReader configProperty = new PropertyReader("config/app/WebApp.properties");

	public static long getKeyPairPublicKeyValidTime() {
		return configProperty.getValLongTime("keypair.publickey_valid_time");
	};

	public static long getKeyPairPublicKeyGetSkipTime() {
		return configProperty.getValLongTime("keypair.publickey_get_skip_time");
	}

	public static String getUserPwdEncryptMethod() {
		String tmp = configProperty.getValString("user.pwd_encrypt_method");
		if (StringUtils.isEmpty(tmp)) {
			return null;
		}
		return "|" + tmp + "|";
	}

	public static String getUserPwdEncryptDefault() {
		return configProperty.getValString("user.pwd_encrypt_default");
	}

	public static int getUserPwdMinLength() {
		return configProperty.getValInteger("user.pwd_min_length");
	}

	public static int getUserPwdMinRule() {
		return configProperty.getValInteger("user.pwd_min_rule");
	}

	public static SimpleDateFormat SDF_DB_VERSION = new SimpleDateFormat("yyyyMMddHHmmss");
	// public static long PUBLIC_KEY_VALID_TIME = TimeUtils.VALUE_DAYTimeMillis * 3;
	// public static long PUBLIC_KEY_GET_SKIP_TIME = TimeUtils.VALUE_DAYTimeMillis /
	// TimeUtils.VALUE_DAYTimeMillis;
	public static String PWD_ENCRYPT_NONE = "NONE";
	public static String PWD_ENCRYPT_MD5 = "MD5";
	public static String PWD_ENCRYPT_RSA = "RSA";
	public static String PWD_ENCRYPT_RSAMD5 = "RSAMD5";
	// public static String PWD_ENCRYPT_DEFAULT = PWD_ENCRYPT_RSA;
	/**
	 * 数字、大写字母、小写字母、特殊符号 密码强度，0不限制，1不能为纯数字，2为至少2种组合，3为至少3种组合，4为4种组合
	 */
	// public static int PWD_STRONG_RULE = 2;
	// public static int PWD_STRONG_LENG = 6;
	public static String PWD_ERROR_PARSE = "密码解析有误，请求参数错误";
	public static String PWD_ERROT_RULE = "密码不符合复杂性要求";

	public static int getUpdateVersion(int version) {
		if (version <= 0) {
			return 5;
		}

		if (version % 5 == 0) {
			return version + 5;
		}
		else {
			return version - version % 5 + 5;
		}
	}

}
