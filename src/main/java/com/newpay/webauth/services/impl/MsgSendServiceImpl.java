/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 下午8:24:37
 */
package com.newpay.webauth.services.impl;

import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.config.MsgFunctionConfig;
import com.newpay.webauth.dal.mapper.MsgSendInfoMapper;
import com.newpay.webauth.dal.model.MsgFunctionInfo;
import com.newpay.webauth.dal.model.MsgSendInfo;
import com.newpay.webauth.dal.request.functionmsg.MsgSendReqDto;
import com.newpay.webauth.dal.response.BaseReturn;
import com.newpay.webauth.services.MsgSendService;
import com.ruomm.base.tools.RegexUtil;
import com.ruomm.base.tools.StringUtils;
import com.ruomm.base.tools.TimeUtils;

@Service
public class MsgSendServiceImpl implements MsgSendService {
	@Autowired
	MsgSendInfoMapper msgFuntionInfoMapper;
	@Autowired
	MsgSendInfoMapper msgSendInfoMapper;

	@Override
	public Object doSendMsg(MsgSendReqDto msgSendReqDto) {
		// TODO Auto-generated method stub
		MsgFunctionInfo msgFunctionInfo = MsgFunctionConfig.getMsgFuntionInfo(msgSendReqDto.getMsgFunction());
		if (null == msgFunctionInfo) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		boolean isEmail = RegexUtil.doRegex(msgSendReqDto.getMsgAddr(), RegexUtil.EMAILS);
		boolean isMobile = RegexUtil.doRegex(msgSendReqDto.getMsgAddr(), RegexUtil.MOBILE_NUM);
		if (!isEmail && !isMobile) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		if (msgFunctionInfo.getMsgAuthType() == 1 || msgFunctionInfo.getMsgAuthType() == 2) {
			if (StringUtils.isBlank(msgSendReqDto.getUserId()) || StringUtils.isEmpty(msgSendReqDto.getToken())) {
				return BaseReturn.toFAIL(BaseReturn.ERROR_TOKEN_MISS);
			}
		}
		String msgCode = MsgFunctionConfig.generateVerifyCode();
		String msgContent = MsgFunctionConfig.generateVerifyContent(msgFunctionInfo, msgCode, isMobile, isEmail);
		MsgSendInfo msgSendInfo = new MsgSendInfo();
		msgSendInfo.setMsgId(new Random().nextInt(1000) + "");
		msgSendInfo.setUserId(msgSendReqDto.getUserId());
		msgSendInfo.setMsgType(isMobile ? 1 : 2);
		msgSendInfo.setMsgToken("66778899");
		msgSendInfo.setMsgFunction(msgFunctionInfo.getMsgFunction());
		msgSendInfo.setMsgAddr(msgSendReqDto.getMsgAddr());
		msgSendInfo.setMsgCode(msgCode);
		msgSendInfo.setMsgStatus(0);
		msgSendInfo.setMsgContent(msgContent);
		Date nowDate = new Date();
		msgSendInfo.setMsgValidTime(
				TimeUtils.formatTime(nowDate.getTime() + AppConfig.VerfiyCodeValidTime, AppConfig.SDF_DB_VERSION));
		msgSendInfo.setCreateDate(AppConfig.SDF_DB_DATE.format(nowDate));
		msgSendInfo.setCreateTime(AppConfig.SDF_DB_VERSION.format(nowDate));
		int dbResult = msgSendInfoMapper.insert(msgSendInfo);
		if (dbResult > 0) {
			return BaseReturn.toSUCESS(null);
		}
		else {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_DB);
		}
	}

}
