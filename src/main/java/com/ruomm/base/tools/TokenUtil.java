/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月14日 上午10:57:58
 */
package com.ruomm.base.tools;

import java.util.Random;

public class TokenUtil {
	public static final String CHARSFORTOKEN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	public static final int CHARSFORTOKENSIZE = 52;
	public static final int DEFAULT_TOKEN_SIZE = 16;
	public static final int DEFAULT_VERIFY_TOKEN_SIZE = 8;

	public static String generateToken() {
		return generateToken(DEFAULT_TOKEN_SIZE);
	}

	public static String generateToken(int length) {
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(random.nextInt(CHARSFORTOKENSIZE));
		}
		return sb.toString();
	}

	public static String generateVerifyToken() {
		return generateToken(DEFAULT_VERIFY_TOKEN_SIZE);
	}

}
