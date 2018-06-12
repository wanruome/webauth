/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午4:55:51
 */
package com.newpay.webauth.config;

public class AppConfig {
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
