/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 下午8:24:37
 */
package com.newpay.webauth.services.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.config.MsgFunctionConfig;
import com.newpay.webauth.dal.mapper.MsgAuthInfoMapper;
import com.newpay.webauth.dal.mapper.MsgSendInfoMapper;
import com.newpay.webauth.dal.model.MsgAuthInfo;
import com.newpay.webauth.dal.model.MsgFunctionInfo;
import com.newpay.webauth.dal.model.MsgSendInfo;
import com.newpay.webauth.dal.request.functionmsg.MsgSendReqDto;
import com.newpay.webauth.dal.response.ResultFactory;
import com.newpay.webauth.services.DbSeqService;
import com.newpay.webauth.services.MsgSendService;
import com.ruomm.base.tools.RegexUtil;
import com.ruomm.base.tools.StringUtils;
import com.ruomm.base.tools.TimeUtils;
import com.ruomm.base.tools.TokenUtil;

@Service
public class MsgSendServiceImpl implements MsgSendService {
	@Autowired
	MsgSendInfoMapper msgSendInfoMapper;
	@Autowired
	MsgAuthInfoMapper msgAuthInfoMapper;
	@Autowired
	DbSeqService dbSeqService;

	@Override
	public Object doSendMsg(MsgSendReqDto msgSendReqDto) {
		// TODO Auto-generated method stub
		MsgFunctionInfo msgFunctionInfo = MsgFunctionConfig.getMsgFuntionInfo(msgSendReqDto.getMsgFunction());
		if (null == msgFunctionInfo) {
			return ResultFactory.toNack(ResultFactory.ERR_PRARM);
		}
		boolean isEmail = RegexUtil.doRegex(msgSendReqDto.getMsgAddr(), RegexUtil.EMAILS);
		boolean isMobile = RegexUtil.doRegex(msgSendReqDto.getMsgAddr(), RegexUtil.MOBILE_NUM);
		if (!isEmail && !isMobile) {
			return ResultFactory.toNack(ResultFactory.ERR_PRARM);
		}
		if (msgFunctionInfo.getAuthType() == 1 || msgFunctionInfo.getAuthType() == 2) {
			if (StringUtils.isBlank(msgSendReqDto.getUserId()) || StringUtils.isEmpty(msgSendReqDto.getToken())) {
				return ResultFactory.toNack(ResultFactory.ERR_PRARM);
			}
		}
		// 写入记录表
		String msgId = dbSeqService.getMsgInfoNewPk();
		String userId = msgSendReqDto.getUserId();
		String functionId = msgFunctionInfo.getFunctionId();
		int msgType = isMobile ? 1 : 2;
		String msgCode = MsgFunctionConfig.generateVerifyCode();
		String msgToken = TokenUtil.generateVerifyToken();
		String msgContent = MsgFunctionConfig.generateVerifyContent(msgFunctionInfo, msgCode, isMobile, isEmail);
		String msgAddr = msgSendReqDto.getMsgAddr();
		Date nowDate = new Date();
		String msgValidTime = TimeUtils.formatTime(nowDate.getTime() + AppConfig.VerfiyCodeValidTime,
				AppConfig.SDF_DB_VERSION);
		String createDate = AppConfig.SDF_DB_DATE.format(nowDate);
		String createTime = AppConfig.SDF_DB_VERSION.format(nowDate);
		MsgSendInfo msgSendInfo = new MsgSendInfo();
		msgSendInfo.setMsgId(msgId);
		msgSendInfo.setUserId(userId);
		msgSendInfo.setFunctionId(functionId);
		msgSendInfo.setMsgType(msgType);
		msgSendInfo.setMsgToken(msgToken);
		msgSendInfo.setMsgAddr(msgAddr);
		msgSendInfo.setMsgCode(msgCode);
		msgSendInfo.setMsgStatus(0);
		msgSendInfo.setMsgContent(msgContent);
		msgSendInfo.setMsgValidTime(msgValidTime);
		msgSendInfo.setCreateDate(createDate);
		msgSendInfo.setCreateTime(createTime);
		int dbResult = msgSendInfoMapper.insertSelective(msgSendInfo);
		if (dbResult <= 0) {
			return ResultFactory.toNack(ResultFactory.ERR_DB);
		}

		MsgAuthInfo msgAuthInfo = new MsgAuthInfo();
		msgAuthInfo.setUuid(msgSendReqDto.getUuid());
		msgAuthInfo.setMsgAddr(msgAddr);
		msgAuthInfo.setFunctionId(functionId);
		msgAuthInfo.setSessionToken(msgSendReqDto.getToken());
		msgAuthInfo.setMsgCode(msgCode);
		msgAuthInfo.setMsgValidTime(msgValidTime);
		msgAuthInfo.setMsgToken(msgToken);
		MsgAuthInfo resultMsgAuthInfo = msgAuthInfoMapper.selectByPrimaryKey(msgAuthInfo);
		if (null == resultMsgAuthInfo) {
			// 写入认证表
			// msgAuthInfo.setVersion(AppConfig.getUpdateVersion(0));
			dbResult = msgAuthInfoMapper.insert(msgAuthInfo);
		}
		else {
			msgAuthInfo.setVersion(resultMsgAuthInfo.getVersion());
			dbResult = msgAuthInfoMapper.updateByPrimaryKey(msgAuthInfo);

			// Example example = new Example(MsgAuthInfo.class);
			// // 创建Criteria
			// Example.Criteria criteria = example.createCriteria();
			// // 添加条件
			// criteria.andEqualTo("uuid", msgAuthInfo.getUuid());
			// criteria.andEqualTo("functionId", msgAuthInfo.getFunctionId());
			// criteria.andEqualTo("msgAddr", msgAuthInfo.getMsgAddr());
			// criteria.andEqualTo("version", msgAuthInfo.getVersion());
			// dbResult = msgAuthInfoMapper.updateByExample(msgAuthInfo, example);
		}
		if (dbResult <= 0) {
			return ResultFactory.toNack(ResultFactory.ERR_DB);
		}
		else {
			return ResultFactory.toAck(null);
		}
	}

}
