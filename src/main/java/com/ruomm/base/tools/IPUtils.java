/**
 *	@copyright wanruome-2017
 * 	@author wanruome
 * 	@create 2017年3月30日 上午9:26:35
 */
package com.ruomm.base.tools;

import javax.servlet.http.HttpServletRequest;

public class IPUtils {
	public static String getRequestIP(HttpServletRequest request) {
		String ip = null;

		try {
			ip = request.getHeader("x-forwarded-for");
			String localIP = "127.0.0.1";
			if ((ip == null) || (ip.length() == 0) || (ip.equalsIgnoreCase(localIP))
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}

			if ((ip == null) || (ip.length() == 0) || (ip.equalsIgnoreCase(localIP))
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}

			if ((ip == null) || (ip.length() == 0) || (ip.equalsIgnoreCase(localIP))
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			ip = null;
		}

		return ip;
	}

}
