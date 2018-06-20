/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月20日 下午2:50:20
 */
package com.newpay.webauth.services.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.dal.mapper.SystemLogMapper;
import com.newpay.webauth.dal.model.SystemLog;
import com.newpay.webauth.dal.response.ResultFactory;
import com.newpay.webauth.services.DbSeqService;
import com.newpay.webauth.services.SystemLogService;

@Service
public class SystemLogServiceImpl implements SystemLogService {
	@Autowired
	DbSeqService dbSeqService;
	@Autowired
	SystemLogMapper systemLogMapper;

	@Override
	public boolean writeLogs(String functionId, String functionName, String userId, String uuid, String appId,
			JSONObject jsonObject) {
		// TODO Auto-generated method stub
		String code = null;
		String msg = null;
		try {
			code = jsonObject.getString(ResultFactory.RESPONSE_COED_TAG);
			msg = jsonObject.getString(ResultFactory.RESPONSE_MSG_TAG);
		}
		catch (Exception e) {
			e.printStackTrace();
			code = ResultFactory.ERR_UNKNOWN;
			msg = "系统异常";
		}
		SystemLog systemLog = new SystemLog();
		systemLog.setLogId(dbSeqService.getSystemLogNewPk());
		systemLog.setLogId(userId);
		systemLog.setUuid(uuid);
		systemLog.setAppId(appId);
		systemLog.setFunctionId(functionId);
		systemLog.setFunctionName(functionName);
		systemLog.setResultCode(code);
		systemLog.setResultMsg(msg);
		Date date = new Date();
		systemLog.setCreateDate(AppConfig.SDF_DB_DATE.format(date));
		systemLog.setCreateTime(AppConfig.SDF_DB_VERSION.format(date));
		return false;
	}

}
