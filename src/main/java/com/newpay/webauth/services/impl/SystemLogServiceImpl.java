/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月20日 下午2:50:20
 */
package com.newpay.webauth.services.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.dal.core.SysLogBean;
import com.newpay.webauth.dal.mapper.SystemLogMapper;
import com.newpay.webauth.dal.mapper.SystemLogMatterMapper;
import com.newpay.webauth.dal.model.SystemLog;
import com.newpay.webauth.dal.model.SystemLogMatter;
import com.newpay.webauth.dal.response.ResultFactory;
import com.newpay.webauth.services.DbSeqService;
import com.newpay.webauth.services.SystemLogService;
import com.ruomm.base.tools.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SystemLogServiceImpl implements SystemLogService {
	@Autowired
	DbSeqService dbSeqService;
	@Autowired
	SystemLogMapper systemLogMapper;
	@Autowired
	SystemLogMatterMapper systemLogPwdErrMapper;

	@Override
	public void writeLogs(SysLogBean sysLogBean, String code, String msg) {
		try {
			String resultCode = StringUtils.isEmpty(code) ? ResultFactory.ERR_UNKNOWN : code;
			String resultMsg = StringUtils.isEmpty(msg) ? "系统异常" : msg;
			SystemLog systemLog = new SystemLog();
			systemLog.setLogId(dbSeqService.getSystemLogNewPk());
			systemLog.setUserId(sysLogBean.getUserId());
			systemLog.setLogKeyValue(sysLogBean.getLogKeyValue());
			systemLog.setUuid(sysLogBean.getUuid());
			systemLog.setAppId(sysLogBean.getAppId());
			systemLog.setFunctionId(sysLogBean.getFunctionId());
			systemLog.setFunctionName(sysLogBean.getFunctionName());
			systemLog.setMapping(sysLogBean.getMapping());
			systemLog.setRequestInfo(sysLogBean.getRequestInfo());
			systemLog.setResultCode(resultCode);
			systemLog.setResultMsg(resultMsg);

			Date date = new Date();
			systemLog.setCreateDate(AppConfig.SDF_DB_DATE.format(date));
			systemLog.setCreateTime(AppConfig.SDF_DB_VERSION.format(date));
			String excuteTime = null;
			try {
				long timeExe = date.getTime() - sysLogBean.getStartTime();
				excuteTime = timeExe + "";
			}
			catch (Exception e) {
				log.error(e.getMessage());
				excuteTime = null;
			}
			systemLog.setExcuteTime(excuteTime);
			systemLogMapper.insert(systemLog);
			// 插入重要的检索信息表，提高关键日志检索效率
			if (ResultFactory.ERR_PWD_WRONG.equals(code)
					|| "app/userAccount/doRegister".equals(sysLogBean.getMapping())) {
				SystemLogMatter systemLogMatter = new SystemLogMatter();
				systemLogMatter.setLogId(systemLog.getLogId());
				systemLogMatter.setUserId(systemLog.getUserId());
				systemLogMatter.setLogKeyValue(systemLog.getLogKeyValue());
				systemLogMatter.setUuid(systemLog.getUuid());
				systemLogMatter.setAppId(systemLog.getAppId());
				systemLogMatter.setFunctionId(systemLog.getFunctionId());
				systemLogMatter.setFunctionName(systemLog.getFunctionName());
				systemLogMatter.setMapping(systemLog.getMapping());
				systemLogMatter.setResultCode(resultCode);
				systemLogMatter.setResultMsg(resultMsg);
				systemLogMatter.setCreateDate(systemLog.getCreateDate());
				systemLogMatter.setCreateTime(systemLog.getCreateTime());
				systemLogMatter.setExcuteTime(excuteTime);
				systemLogPwdErrMapper.insert(systemLogMatter);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			log.error(e.getMessage());
		}
		// TODO Auto-generated method stub

	}

}
