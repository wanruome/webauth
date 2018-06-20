/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月20日 下午4:12:09
 */
package com.newpay.webauth.aop;

import com.newpay.webauth.dal.core.SysLogBean;

public class SystemLogThreadLocal {
	private static final ThreadLocal<SysLogBean> dataSourceKey = new ThreadLocal<SysLogBean>();

	public static void setSysLogBean(SysLogBean sysLogBean) {
		dataSourceKey.set(sysLogBean);
	}

	public static SysLogBean get() {
		return dataSourceKey.get();
	}

	public static void reset() {
		dataSourceKey.set(null);
	}

	public static void cleanSysLogBean() {
		dataSourceKey.remove();
	}

	public static void setUserId(String userId) {
		SysLogBean sysLogBean = null;
		try {
			sysLogBean = get();
			if (null != sysLogBean) {
				sysLogBean.setUserId(userId);
			}
			else {
				cleanSysLogBean();
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}
