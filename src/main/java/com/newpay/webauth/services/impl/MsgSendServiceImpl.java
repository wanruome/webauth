/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 下午8:24:37
 */
package com.newpay.webauth.services.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.config.MsgFunctionConfig;
import com.newpay.webauth.dal.mapper.LoginUserAccountMapper;
import com.newpay.webauth.dal.mapper.MsgAuthInfoMapper;
import com.newpay.webauth.dal.mapper.MsgSendInfoMapper;
import com.newpay.webauth.dal.model.LoginUserAccount;
import com.newpay.webauth.dal.model.MsgAuthInfo;
import com.newpay.webauth.dal.model.MsgFunctionInfo;
import com.newpay.webauth.dal.model.MsgSendInfo;
import com.newpay.webauth.dal.request.functionmsg.MsgSendReqDto;
import com.newpay.webauth.dal.request.functionmsg.MsgTokenGetReqDto;
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
	@Autowired
	LoginUserAccountMapper loginUserAccountMapper;

	@Override
	public Object doSendMsg(MsgSendReqDto msgSendReqDto) {
		// TODO Auto-generated method stub
		MsgFunctionInfo msgFunctionInfo = MsgFunctionConfig.getMsgFuntionInfo(msgSendReqDto.getMsgFunction());
		if (null == msgFunctionInfo) {
			return ResultFactory.toNackPARAM();
		}
		if (msgFunctionInfo.getAuthType() == 0) {
			return doSendMsgAuthType0(msgSendReqDto, msgFunctionInfo);
		}
		else if (msgFunctionInfo.getAuthType() == 1) {
			return doSendMsgAuthType1(msgSendReqDto, msgFunctionInfo);
		}
		else if (msgFunctionInfo.getAuthType() == 2) {
			return doSendMsgAuthType2(msgSendReqDto, msgFunctionInfo);
		}
		else {
			return ResultFactory.toNackPARAM();
		}

	}

	public int getRemindCount(MsgSendReqDto msgSendReqDto, MsgFunctionInfo msgFunctionInfo, boolean isMobile) {
		int limit = 1000;
		String dateStr = AppConfig.SDF_DB_DATE.format(new Date());
		if (AppConfig.MSGSEND_LIMITCOUNT_MOBILE > 0 & isMobile) {

			MsgSendInfo queryMsgInfo = new MsgSendInfo();
			queryMsgInfo.setMsgAddr(msgSendReqDto.getMsgAddr());
			queryMsgInfo.setCreateDate(dateStr);
			int countSend = msgSendInfoMapper.selectCount(queryMsgInfo);
			int countRemind = AppConfig.MSGSEND_LIMITCOUNT_MOBILE - countSend;
			if (limit > countRemind) {
				limit = countRemind;
			}
		}
		if (limit < 1) {
			return limit;
		}
		if (AppConfig.MSGSEND_LIMITCOUNT_EMAIL > 0 & !isMobile) {
			MsgSendInfo queryMsgInfo = new MsgSendInfo();
			queryMsgInfo.setMsgAddr(msgSendReqDto.getMsgAddr());
			queryMsgInfo.setCreateDate(dateStr);
			int countSend = msgSendInfoMapper.selectCount(queryMsgInfo);
			int countRemind = AppConfig.MSGSEND_LIMITCOUNT_EMAIL - countSend;
			if (limit > countRemind) {
				limit = countRemind;
			}
		}
		if (limit < 1) {
			return limit;
		}
		if (AppConfig.MSGSEND_LIMITCOUNT_UUID > 0) {
			MsgSendInfo queryMsgInfo = new MsgSendInfo();
			queryMsgInfo.setUuid(msgSendReqDto.getUuid());
			queryMsgInfo.setCreateDate(dateStr);
			int countSend = msgSendInfoMapper.selectCount(queryMsgInfo);
			int countRemind = AppConfig.MSGSEND_LIMITCOUNT_UUID - countSend;
			if (limit > countRemind) {
				limit = countRemind;
			}
		}
		if (limit < 1) {
			return limit;
		}
		if (AppConfig.MSGSEND_LIMITCOUNT_USER > 0
				&& (msgFunctionInfo.getAuthType() == 1 || msgFunctionInfo.getAuthType() == 2)) {
			MsgSendInfo queryMsgInfo = new MsgSendInfo();
			queryMsgInfo.setUserId(msgSendReqDto.getUserId());
			queryMsgInfo.setCreateDate(dateStr);
			int countSend = msgSendInfoMapper.selectCount(queryMsgInfo);
			int countRemind = AppConfig.MSGSEND_LIMITCOUNT_USER - countSend;
			if (limit > countRemind) {
				limit = countRemind;
			}
		}
		return limit;
	}

	public Object doSendMsgAuthType2(MsgSendReqDto msgSendReqDto, MsgFunctionInfo msgFunctionInfo) {

		boolean isEmail = RegexUtil.doRegex(msgSendReqDto.getMsgAddr(), RegexUtil.EMAILS);
		boolean isMobile = RegexUtil.doRegex(msgSendReqDto.getMsgAddr(), RegexUtil.MOBILE_NUM);
		if (!isEmail && !isMobile) {
			return ResultFactory.toNackPARAM();
		}

		if (StringUtils.isEmpty(msgSendReqDto.getUserId()) || StringUtils.isEmpty(msgSendReqDto.getTokenId())
				|| StringUtils.isEmpty(msgSendReqDto.getMsgFunctionTo())
				|| StringUtils.isEmpty(msgSendReqDto.getSignInfo())) {
			return ResultFactory.toNackPARAM();
		}
		if (!StringUtils.isEmpty(msgSendReqDto.getMsgToken())) {
			return ResultFactory.toNackPARAM();
		}
		MsgFunctionInfo msgFunctionInfoTo = MsgFunctionConfig.getMsgFuntionInfo(msgSendReqDto.getMsgFunctionTo());
		if (null == msgFunctionInfoTo || msgFunctionInfoTo.getAuthType() != 1) {
			return ResultFactory.toNackPARAM();
		}
		String msgUUID = msgSendReqDto.getUserId();
		boolean isSameToUserInfoAddr = false;
		// 若是手机或邮箱和用户的手机号邮箱不同，需要先获取对应功能的msgToken才能往不同的手机或邮箱发送验证码
		LoginUserAccount queryUserAccount = new LoginUserAccount();
		queryUserAccount.setLoginId(msgSendReqDto.getUserId());
		queryUserAccount.setStatus(1);
		LoginUserAccount resultUserAccout = loginUserAccountMapper.selectOne(queryUserAccount);
		if (null == resultUserAccout) {
			return ResultFactory.toNackCORE("用户不存在");
		}
		if (isEmail) {
			isSameToUserInfoAddr = msgSendReqDto.getMsgAddr().equals(resultUserAccout.getLoginEmail());
		}
		if (isMobile) {
			isSameToUserInfoAddr = msgSendReqDto.getMsgAddr().equals(resultUserAccout.getLoginMobile());
		}
		if (!isSameToUserInfoAddr) {
			return ResultFactory.toNackCORE("只能往登录的账户上面发送验证码");
		}
		// 校验验证码使用次数
		int limitCount = getRemindCount(msgSendReqDto, msgFunctionInfo, isMobile);
		if (limitCount < 1) {
			if (isMobile) {
				return ResultFactory.toNackCORE("今日短信验证码次数已经用完");
			}
			else {
				return ResultFactory.toNackCORE("今日邮箱验证码次数已经用完");
			}
		}
		// 写入记录表
		String msgId = dbSeqService.getMsgInfoNewPk();
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
		msgSendInfo.setUserId(msgSendReqDto.getUserId());
		msgSendInfo.setUuid(msgSendReqDto.getUuid());
		msgSendInfo.setFunctionId(functionId);
		msgSendInfo.setMsgType(msgType);
		if (isSameToUserInfoAddr && msgFunctionInfo.getAuthType() == 2) {
			msgSendInfo.setMsgToken(msgToken);
		}
		msgSendInfo.setMsgAddr(msgAddr);
		msgSendInfo.setMsgCode(msgCode);
		msgSendInfo.setMsgStatus(0);
		msgSendInfo.setMsgContent(msgContent);
		msgSendInfo.setMsgValidTime(msgValidTime);
		msgSendInfo.setCreateDate(createDate);
		msgSendInfo.setCreateTime(createTime);
		int dbResult = msgSendInfoMapper.insertSelective(msgSendInfo);
		if (dbResult <= 0) {
			return ResultFactory.toNackDB();
		}

		MsgAuthInfo msgAuthInfo = new MsgAuthInfo();
		msgAuthInfo.setUuid(msgUUID);
		msgAuthInfo.setMsgAddr(msgAddr);
		msgAuthInfo.setFunctionId(functionId);
		msgAuthInfo.setSessionTokenId(msgSendReqDto.getTokenId());
		msgAuthInfo.setMsgCode(msgCode);
		msgAuthInfo.setMsgValidTime(msgValidTime);
		msgAuthInfo.setMsgStatus(1);
		msgAuthInfo.setFunctionToId(msgSendReqDto.getMsgFunctionTo());
		msgAuthInfo.setMsgToken(msgToken);
		MsgAuthInfo resultMsgAuthInfo = msgAuthInfoMapper.selectByPrimaryKey(msgAuthInfo);
		if (null == resultMsgAuthInfo) {
			// 写入认证表
			msgAuthInfo.setVersion(0);
			dbResult = msgAuthInfoMapper.insertSelective(msgAuthInfo);
		}
		else {
			msgAuthInfo.setVersion(resultMsgAuthInfo.getVersion());
			dbResult = msgAuthInfoMapper.updateByPrimaryKey(msgAuthInfo);
		}

		if (dbResult <= 0) {
			return ResultFactory.toNackDB();
		}
		else {
			Map<String, String> resultData = new HashMap<>();
			resultData.put("limitCount", limitCount - 1 + "");
			return ResultFactory.toAck(resultData);
		}
	}

	public Object doSendMsgAuthType1(MsgSendReqDto msgSendReqDto, MsgFunctionInfo msgFunctionInfo) {
		boolean isEmail = RegexUtil.doRegex(msgSendReqDto.getMsgAddr(), RegexUtil.EMAILS);
		boolean isMobile = RegexUtil.doRegex(msgSendReqDto.getMsgAddr(), RegexUtil.MOBILE_NUM);
		if (!isEmail && !isMobile) {
			return ResultFactory.toNackPARAM();
		}

		if (StringUtils.isEmpty(msgSendReqDto.getUserId()) || StringUtils.isEmpty(msgSendReqDto.getTokenId())
				|| StringUtils.isEmpty(msgSendReqDto.getSignInfo())) {
			return ResultFactory.toNackPARAM();
		}
		if (!StringUtils.isEmpty(msgSendReqDto.getMsgFunctionTo())) {
			return ResultFactory.toNackPARAM();
		}
		MsgAuthInfo tokenMsgAuthInfo = null;
		String msgUUID = msgSendReqDto.getUserId();
		boolean isSameToUserInfoAddr = false;
		// 若是手机或邮箱和用户的手机号邮箱不同，需要先获取对应功能的msgToken才能往不同的手机或邮箱发送验证码
		LoginUserAccount queryUserAccount = new LoginUserAccount();
		queryUserAccount.setLoginId(msgSendReqDto.getUserId());
		queryUserAccount.setStatus(1);
		LoginUserAccount resultUserAccout = loginUserAccountMapper.selectOne(queryUserAccount);
		if (null == resultUserAccout) {
			return ResultFactory.toNackCORE("用户不存在");
		}
		if (isEmail) {
			isSameToUserInfoAddr = msgSendReqDto.getMsgAddr().equals(resultUserAccout.getLoginEmail());
		}
		if (isMobile) {
			isSameToUserInfoAddr = msgSendReqDto.getMsgAddr().equals(resultUserAccout.getLoginMobile());
		}
		if (!isSameToUserInfoAddr && msgFunctionInfo.getNeedMsgToken() == 1) {

			if (StringUtils.isEmpty(msgSendReqDto.getMsgToken())) {
				return ResultFactory.toNackCORE("无权往其他账户上面发送验证码");
			}
			String nowDateStr = AppConfig.SDF_DB_VERSION.format(new Date());
			MsgAuthInfo queryMsgAuthInfo = new MsgAuthInfo();
			queryMsgAuthInfo.setUuid(msgUUID);
			queryMsgAuthInfo.setMsgToken(msgSendReqDto.getMsgToken());
			queryMsgAuthInfo.setFunctionToId(msgSendReqDto.getMsgFunction());
			tokenMsgAuthInfo = msgAuthInfoMapper.selectOne(queryMsgAuthInfo);
			if (null == tokenMsgAuthInfo || nowDateStr.compareTo(tokenMsgAuthInfo.getMsgValidTime()) >= 0) {
				return ResultFactory.toNack(ResultFactory.ERR_MSGCODE_INVALID, "你还没有获取短信验证码");
			}
			else if (tokenMsgAuthInfo.getMsgStatus() != 1) {
				return ResultFactory.toNack(ResultFactory.ERR_MSGCODE_INVALID, "短信验证码已经失效，请重新获取");
			}
			else {
				tokenMsgAuthInfo.setMsgStatus(0);
				int dbResult = msgAuthInfoMapper.updateByPrimaryKeySelective(tokenMsgAuthInfo);
				if (dbResult <= 0) {
					return ResultFactory.toNack(ResultFactory.ERR_MSGCODE_INVALID, "短信验证码已经失效，请重新获取");
				}
			}
		}
		// 校验验证码使用次数
		int limitCount = getRemindCount(msgSendReqDto, msgFunctionInfo, isMobile);
		if (limitCount < 1) {
			if (isMobile) {
				return ResultFactory.toNackCORE("今日短信验证码次数已经用完");
			}
			else {
				return ResultFactory.toNackCORE("今日邮箱验证码次数已经用完");
			}
		}
		// 写入记录表
		String msgId = dbSeqService.getMsgInfoNewPk();
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
		msgSendInfo.setUserId(msgSendReqDto.getUserId());
		msgSendInfo.setUuid(msgSendReqDto.getUuid());
		msgSendInfo.setFunctionId(functionId);
		msgSendInfo.setMsgType(msgType);
		if (isSameToUserInfoAddr && msgFunctionInfo.getAuthType() == 2) {
			msgSendInfo.setMsgToken(msgToken);
		}
		msgSendInfo.setMsgAddr(msgAddr);
		msgSendInfo.setMsgCode(msgCode);
		msgSendInfo.setMsgStatus(0);
		msgSendInfo.setMsgContent(msgContent);
		msgSendInfo.setMsgValidTime(msgValidTime);
		msgSendInfo.setCreateDate(createDate);
		msgSendInfo.setCreateTime(createTime);
		int dbResult = msgSendInfoMapper.insertSelective(msgSendInfo);
		if (dbResult <= 0) {
			return ResultFactory.toNackDB();
		}

		MsgAuthInfo msgAuthInfo = new MsgAuthInfo();
		msgAuthInfo.setUuid(msgUUID);
		msgAuthInfo.setMsgAddr(msgAddr);
		msgAuthInfo.setFunctionId(functionId);
		msgAuthInfo.setSessionTokenId(msgSendReqDto.getTokenId());
		msgAuthInfo.setMsgCode(msgCode);
		msgAuthInfo.setMsgValidTime(msgValidTime);
		msgAuthInfo.setMsgStatus(1);
		// msgAuthInfo.setFunctionToId(null);
		// msgAuthInfo.setMsgToken(null);
		MsgAuthInfo resultMsgAuthInfo = msgAuthInfoMapper.selectByPrimaryKey(msgAuthInfo);
		if (null == resultMsgAuthInfo) {
			// 写入认证表
			msgAuthInfo.setVersion(0);
			dbResult = msgAuthInfoMapper.insertSelective(msgAuthInfo);
		}
		else {
			msgAuthInfo.setVersion(resultMsgAuthInfo.getVersion());
			dbResult = msgAuthInfoMapper.updateByPrimaryKey(msgAuthInfo);
		}

		if (dbResult <= 0) {
			return ResultFactory.toNackDB();
		}
		else {
			Map<String, String> resultData = new HashMap<>();
			resultData.put("limitCount", limitCount - 1 + "");
			return ResultFactory.toAck(resultData);
		}
	}

	public Object doSendMsgAuthType0(MsgSendReqDto msgSendReqDto, MsgFunctionInfo msgFunctionInfo) {
		boolean isEmail = RegexUtil.doRegex(msgSendReqDto.getMsgAddr(), RegexUtil.EMAILS);
		boolean isMobile = RegexUtil.doRegex(msgSendReqDto.getMsgAddr(), RegexUtil.MOBILE_NUM);
		if (!isEmail && !isMobile) {
			return ResultFactory.toNackPARAM();
		}
		if (!StringUtils.isEmpty(msgSendReqDto.getMsgFunctionTo()) || !StringUtils.isEmpty(msgSendReqDto.getMsgToken())
				|| !StringUtils.isEmpty(msgSendReqDto.getUserId())
				|| !StringUtils.isEmpty(msgSendReqDto.getTokenId())) {
			return ResultFactory.toNackPARAM();
		}
		String msgUUID = msgSendReqDto.getUuid();
		// 校验验证码使用次数
		int limitCount = getRemindCount(msgSendReqDto, msgFunctionInfo, isMobile);
		if (limitCount < 1) {
			if (isMobile) {
				return ResultFactory.toNackCORE("今日短信验证码次数已经用完");
			}
			else {
				return ResultFactory.toNackCORE("今日邮箱验证码次数已经用完");
			}
		}
		// 写入记录表
		String msgId = dbSeqService.getMsgInfoNewPk();
		String userId = msgSendReqDto.getUserId();
		String functionId = msgFunctionInfo.getFunctionId();
		int msgType = isMobile ? 1 : 2;
		String msgCode = MsgFunctionConfig.generateVerifyCode();
		String msgContent = MsgFunctionConfig.generateVerifyContent(msgFunctionInfo, msgCode, isMobile, isEmail);
		String msgAddr = msgSendReqDto.getMsgAddr();
		Date nowDate = new Date();
		String msgValidTime = TimeUtils.formatTime(nowDate.getTime() + AppConfig.VerfiyCodeValidTime,
				AppConfig.SDF_DB_VERSION);
		String createDate = AppConfig.SDF_DB_DATE.format(nowDate);
		String createTime = AppConfig.SDF_DB_VERSION.format(nowDate);
		MsgSendInfo msgSendInfo = new MsgSendInfo();
		msgSendInfo.setMsgId(msgId);
		msgSendInfo.setUserId(msgSendReqDto.getUserId());
		msgSendInfo.setUuid(msgSendReqDto.getUuid());
		msgSendInfo.setFunctionId(functionId);
		msgSendInfo.setMsgType(msgType);
		msgSendInfo.setMsgAddr(msgAddr);
		msgSendInfo.setMsgCode(msgCode);
		msgSendInfo.setMsgStatus(0);
		msgSendInfo.setMsgContent(msgContent);
		msgSendInfo.setMsgValidTime(msgValidTime);
		msgSendInfo.setCreateDate(createDate);
		msgSendInfo.setCreateTime(createTime);
		int dbResult = msgSendInfoMapper.insertSelective(msgSendInfo);
		if (dbResult <= 0) {
			return ResultFactory.toNackDB();
		}

		MsgAuthInfo msgAuthInfo = new MsgAuthInfo();
		msgAuthInfo.setUuid(msgUUID);
		msgAuthInfo.setMsgAddr(msgAddr);
		msgAuthInfo.setFunctionId(functionId);
		msgAuthInfo.setSessionTokenId(msgSendReqDto.getTokenId());
		msgAuthInfo.setMsgCode(msgCode);
		msgAuthInfo.setMsgValidTime(msgValidTime);
		msgAuthInfo.setMsgStatus(1);
		if (msgFunctionInfo.getAuthType() == 2) {
			msgAuthInfo.setFunctionToId(msgSendReqDto.getMsgFunctionTo());
		}
		MsgAuthInfo resultMsgAuthInfo = msgAuthInfoMapper.selectByPrimaryKey(msgAuthInfo);
		if (null == resultMsgAuthInfo) {
			// 写入认证表
			msgAuthInfo.setVersion(0);
			dbResult = msgAuthInfoMapper.insertSelective(msgAuthInfo);
		}
		else {
			msgAuthInfo.setVersion(resultMsgAuthInfo.getVersion());
			dbResult = msgAuthInfoMapper.updateByPrimaryKey(msgAuthInfo);
		}

		if (dbResult <= 0) {
			return ResultFactory.toNackDB();
		}
		else {
			Map<String, String> resultData = new HashMap<>();
			resultData.put("limitCount", limitCount - 1 + "");
			return ResultFactory.toAck(resultData);
		}
	}

	@Override
	public Object doGetMsgToken(MsgTokenGetReqDto msgTokenGetReqDto) {
		// TODO Auto-generated method stub
		String nowTime = AppConfig.SDF_DB_VERSION.format(new Date());
		MsgAuthInfo queryAuthInfo = new MsgAuthInfo();
		queryAuthInfo.setUuid(msgTokenGetReqDto.getUserId());
		queryAuthInfo.setFunctionId(msgTokenGetReqDto.getMsgFunction());
		queryAuthInfo.setMsgAddr(msgTokenGetReqDto.getMsgAddr());
		queryAuthInfo.setMsgCode(msgTokenGetReqDto.getMsgVerifyCode());
		MsgAuthInfo resultMsgAuthInfo = msgAuthInfoMapper.selectOne(queryAuthInfo);
		if (null == resultMsgAuthInfo || nowTime.compareTo(resultMsgAuthInfo.getMsgValidTime()) >= 0) {
			return ResultFactory.toNackCORE("没有获取验证码");
		}
		else if (resultMsgAuthInfo.getMsgStatus() != 1) {
			return ResultFactory.toNackCORE("改验证码已被使用");
		}
		else {
			Map<String, String> dataResult = new HashMap<>();
			dataResult.put("msgToken", resultMsgAuthInfo.getMsgToken());
			dataResult.put("functionTo", resultMsgAuthInfo.getFunctionToId());
			return ResultFactory.toAck(dataResult);
		}
	}

}
