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
	// public static String UserPwdEncryptDefault = null;

	public static Integer UserPwdMinLength = null;
	public static Integer UserPwdMaxLength = null;
	/**
	 * * 数字、大写字母、小写字母、特殊符号 密码强度，0不限制，1不能为纯数字，2为至少2种组合，3为至少3种组合，4为4种组合 //
	 */
	public static Integer UserPwdMinRule = null;
	public static Integer UserPwdErrLimit = null;
	public static Long UserUuidAuthTime = null;
	public static Long UserToken_ValidTime = null;
	public static Long UserToken_DeleteTime = null;
	public static Integer VerfiyCodeLength = null;
	public static Long VerfiyCodeValidTime = null;
	public static Integer MSGSEND_LIMITCOUNT_EMAIL = null;
	public static Integer MSGSEND_LIMITCOUNT_MOBILE = null;
	public static Integer MSGSEND_LIMITCOUNT_UUID = null;
	public static Integer MSGSEND_LIMITCOUNT_USER = null;
	public static Integer APPINFO_MODIFY_LIMIT_ONE = null;
	public static Boolean SYSTEMLOG_ASYNC = null;

	static {
		forceLoadProperty();
	}

	public static void resetPropertyData() {
		CompanyName = null;
		KeyPairPublicKeyValidTime = null;
		KeyPairPublicKeyGetSkipTime = null;
		UserPwdEncryptMethod = null;
		// UserPwdEncryptDefault = null;
		UserPwdMinLength = null;
		UserPwdMaxLength = null;
		UserPwdMinRule = null;
		UserPwdErrLimit = null;
		UserUuidAuthTime = null;
		UserToken_ValidTime = null;
		UserToken_DeleteTime = null;
		VerfiyCodeLength = null;
		VerfiyCodeValidTime = null;
		MSGSEND_LIMITCOUNT_EMAIL = null;
		MSGSEND_LIMITCOUNT_MOBILE = null;
		MSGSEND_LIMITCOUNT_UUID = null;
		MSGSEND_LIMITCOUNT_USER = null;
		APPINFO_MODIFY_LIMIT_ONE = null;
		SYSTEMLOG_ASYNC = null;
	}

	public synchronized static void forceLoadProperty() {
		try {
			resetPropertyData();
			configProperty.forceLoadProperty();
			CompanyName = configProperty.getValString("CompanyName", "浙江盛炬支付");
			KeyPairPublicKeyValidTime = configProperty.getValLongTime("keypair.publickey_valid_time");
			KeyPairPublicKeyGetSkipTime = configProperty.getValLongTime("keypair.publickey_get_skip_time");
			UserPwdEncryptMethod = configProperty.getValString("user.pwd_encrypt_method");
			// UserPwdEncryptDefault = configProperty.getValString("user.pwd_encrypt_default");
			UserPwdMinLength = configProperty.getValInteger("user.pwd_min_length");
			UserPwdMaxLength = configProperty.getValInteger("user.pwd_max_length", 24);
			UserPwdMinRule = configProperty.getValInteger("user.pwd_min_rule");
			UserPwdErrLimit = configProperty.getValInteger("user.pwd_err_limit");
			UserUuidAuthTime = configProperty.getValLongTime("user.uuid_authtime");
			UserToken_ValidTime = configProperty.getValLongTime("usertoken.validtime");
			UserToken_DeleteTime = configProperty.getValLongTime("usertoken.deletetime");
			VerfiyCodeLength = configProperty.getValInteger("msg.verify_code_length", 6);
			if (VerfiyCodeLength < 4) {
				VerfiyCodeLength = 4;
			}
			if (VerfiyCodeLength > 10) {
				VerfiyCodeLength = 10;
			}
			VerfiyCodeValidTime = configProperty.getValLongTime("msg.verify_code_valid_time", 15 * 60 * 1000l);
			MSGSEND_LIMITCOUNT_EMAIL = configProperty.getValInteger("msgsend.limitcount_email");
			MSGSEND_LIMITCOUNT_MOBILE = configProperty.getValInteger("msgsend.limitcount_mobile");
			MSGSEND_LIMITCOUNT_UUID = configProperty.getValInteger("msgsend.limitcount_uuid");
			MSGSEND_LIMITCOUNT_USER = configProperty.getValInteger("msgsend.limitcount_user");
			APPINFO_MODIFY_LIMIT_ONE = configProperty.getValInteger("appinfo.modify.limitone");
			SYSTEMLOG_ASYNC = configProperty.getValBoolean("systemlog.async");
		}
		catch (Exception e) {
			e.printStackTrace();
			resetPropertyData();
		}

	}

	public static final Integer OKHTTP_CONNECT_TIMEOUT = 5;
	public static final Integer OKHTTP_WRITE_TIMEOUT = 15;
	public static final Integer OKHTTP_READ_TIMEOUT = 15;
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

}
