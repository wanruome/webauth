/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月20日 下午9:56:40
 */
package com.newpay.webauth.dal.core;

import com.newpay.webauth.config.listener.SpringContextHolder;
import com.newpay.webauth.services.SystemLogService;

public class SystemLogThread extends Thread {
	private SysLogBean sysLogBean;
	private String code;
	private String msg;

	public SystemLogThread(SysLogBean sysLogBean, String code, String msg) {
		super();
		this.sysLogBean = sysLogBean;
		this.code = code;
		this.msg = msg;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			SystemLogService systemLogService = SpringContextHolder.getBean(SystemLogService.class);
			systemLogService.writeLogs(sysLogBean, code, msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
