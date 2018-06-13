/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午4:55:51
 */
package com.newpay.webauth.config;

import java.text.SimpleDateFormat;

import com.ruomm.base.tools.PropertyReader;

public class AppConfig {
	// 从WebApp.properties读取的配置属性值
	public static PropertyReader configProperty = new PropertyReader("config/app/WebApp.properties");
	public static Long KeyPairPublicKeyValidTime = null;
	public static Long KeyPairPublicKeyGetSkipTime = null;
	public static String UserPwdEncryptMethod = null;
	public static String UserPwdEncryptDefault = null;
	public static Integer UserPwdMinLength = null;
	public static Integer UserPwdMaxLength = null;
	/**
	 * * 数字、大写字母、小写字母、特殊符号 密码强度，0不限制，1不能为纯数字，2为至少2种组合，3为至少3种组合，4为4种组合 //
	 */
	public static Integer UserPwdMinRule = null;
	static {
		forceLoadProperty();
	}

	public synchronized static void forceLoadProperty() {
		try {
			KeyPairPublicKeyValidTime = null;
			KeyPairPublicKeyGetSkipTime = null;
			UserPwdEncryptMethod = null;
			UserPwdEncryptDefault = null;
			UserPwdMinLength = null;
			UserPwdMaxLength = null;
			UserPwdMinRule = null;
			configProperty.forceLoadProperty();
			KeyPairPublicKeyValidTime = configProperty.getValLongTime("keypair.publickey_valid_time");
			KeyPairPublicKeyGetSkipTime = configProperty.getValLongTime("keypair.publickey_get_skip_time");
			UserPwdEncryptMethod = configProperty.getValString("user.pwd_encrypt_method");
			UserPwdEncryptDefault = configProperty.getValString("user.pwd_encrypt_default");
			UserPwdMinLength = configProperty.getValInteger("user.pwd_min_length");
			UserPwdMaxLength = configProperty.getValInteger("user.pwd_max_length", 24);
			UserPwdMinRule = configProperty.getValInteger("user.pwd_min_rule");
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static SimpleDateFormat SDF_DB_VERSION = new SimpleDateFormat("yyyyMMddHHmmss");
	public static String PWD_ENCRYPT_NONE = "NONE";
	public static String PWD_ENCRYPT_MD5 = "MD5";
	public static String PWD_ENCRYPT_RSA = "RSA";
	public static String PWD_ENCRYPT_RSAMD5 = "RSAMD5";
	public static String PWD_ENCRYPT_3DES = "3DES";
	public static String PWD_ENCRYPT_3DESMD5 = "3DESMD5";

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
