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
	public static String CompanyName = null;
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
	public static Integer VerfiyCodeLength = null;
	public static Long VerfiyCodeValidTime = null;
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
			VerfiyCodeLength = null;
			configProperty.forceLoadProperty();
			CompanyName = configProperty.getValString("CompanyName", "浙江盛炬支付");
			KeyPairPublicKeyValidTime = configProperty.getValLongTime("keypair.publickey_valid_time");
			KeyPairPublicKeyGetSkipTime = configProperty.getValLongTime("keypair.publickey_get_skip_time");
			UserPwdEncryptMethod = configProperty.getValString("user.pwd_encrypt_method");
			UserPwdEncryptDefault = configProperty.getValString("user.pwd_encrypt_default");
			UserPwdMinLength = configProperty.getValInteger("user.pwd_min_length");
			UserPwdMaxLength = configProperty.getValInteger("user.pwd_max_length", 24);
			UserPwdMinRule = configProperty.getValInteger("user.pwd_min_rule");
			VerfiyCodeLength = configProperty.getValInteger("msg.verify_code_length", 6);
			if (VerfiyCodeLength < 4) {
				VerfiyCodeLength = 4;
			}
			if (VerfiyCodeLength > 10) {
				VerfiyCodeLength = 10;
			}
			VerfiyCodeValidTime = configProperty.getValLongTime("msg.verify_code_valid_time", 15 * 60 * 1000l);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static final String REQUEST_FIELD_SIGN_INFO = "signInfo";
	public static final String REQUEST_FIELD_APP_ID = "appId";
	public static final String REQUEST_FIELD_UUID = "uuid";
	public static final String REQUEST_FIELD_USER_ID = "userId";
	public static final String REQUEST_FIELD_TOKEN_ID = "tokenId";
	public static final String REQUEST_FIELD_VERIFY_CODE = "msgVerifyCode";
	public static SimpleDateFormat SDF_DB_DATE = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat SDF_DB_VERSION = new SimpleDateFormat("yyyyMMddHHmmss");
	public static String PWD_ENCRYPT_NONE = "NONE";
	public static String PWD_ENCRYPT_MD5 = "MD5";
	public static String PWD_ENCRYPT_RSA = "RSA";
	public static String PWD_ENCRYPT_RSAMD5 = "RSAMD5";
	public static String PWD_ENCRYPT_3DES = "3DES";
	public static String PWD_ENCRYPT_3DESMD5 = "3DESMD5";
	public static String ACCOUNT_TYPE_MOBILE = "1";
	public static String ACCOUNT_TYPE_EMAIL = "2";
	public static String ACCOUNT_TYPE_NAME = "3";
	public static String ACCOUNT_TYPE_USERID = "4";
	public static String TERM_TYPE_ANDROID = "1";
	public static String TERM_TYPE_IPHONE = "2";
	public static String TERM_TYPE_WEB = "3";
	public static String TERM_TYPE_ALL = "4";
	public static String PWD_ERROR_PARSE = "密码解析有误，请求参数错误";

	// public static int getUpdateVersion(int version) {
	// if (version <= 0) {
	// return 5;
	// }
	//
	// if (version % 5 == 0) {
	// return version + 5;
	// }
	// else {
	// return version - version % 5 + 5;
	// }
	// }

}
