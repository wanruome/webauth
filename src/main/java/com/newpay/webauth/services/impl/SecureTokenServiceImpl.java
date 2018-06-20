/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月19日 下午10:54:26
 */
package com.newpay.webauth.services.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.dal.core.TokenResponseParse;
import com.newpay.webauth.dal.mapper.LoginUserTokenMapper;
import com.newpay.webauth.dal.model.LoginAppInfo;
import com.newpay.webauth.dal.model.LoginUserAccount;
import com.newpay.webauth.dal.model.LoginUserToken;
import com.newpay.webauth.dal.request.userinfo.UserInfoLoginReqDto;
import com.newpay.webauth.dal.response.ResultFactory;
import com.newpay.webauth.services.DbSeqService;
import com.newpay.webauth.services.SecureTokenService;
import com.ruomm.base.tools.ListUtils;
import com.ruomm.base.tools.StringUtils;
import com.ruomm.base.tools.TimeUtils;
import com.ruomm.base.tools.TokenUtil;

@Service
public class SecureTokenServiceImpl implements SecureTokenService {
	@Autowired
	DbSeqService dbSeqService;
	@Autowired
	LoginUserTokenMapper loginUserTokenMapper;

	@Override
	public TokenResponseParse createTokenForLogin(UserInfoLoginReqDto userInfoLoginReqDto,
			LoginUserAccount resultLoginUserAccount, LoginAppInfo resultLoginAppInfo) {
		// TODO Auto-generated method stub
		String appId = resultLoginAppInfo.getAppId();
		String userId = resultLoginUserAccount.getLoginId();
		String termType = userInfoLoginReqDto.getTermType();
		String uuidTemp = userInfoLoginReqDto.getUuid();
		String realUUID = appId + "_" + userId + "_" + uuidTemp;
		TokenResponseParse tokenResponseParse = new TokenResponseParse();
		tokenResponseParse.setValid(false);
		tokenResponseParse.setNeedVerifyCode(false);
		int termTypeLimit = 0;
		if (termType.equals(AppConfig.TERM_TYPE_ANDROID)) {
			if (resultLoginAppInfo.getTermAndroidLimit() <= 0) {
				tokenResponseParse.setReturnResp(ResultFactory.toNackCORE("该设备无权登录"));
				return tokenResponseParse;
			}
			termTypeLimit = resultLoginAppInfo.getTermAndroidLimit();
		}
		else if (termType.equals(AppConfig.TERM_TYPE_IPHONE)) {
			if (resultLoginAppInfo.getTermIphoneLimit() <= 0) {
				tokenResponseParse.setReturnResp(ResultFactory.toNackCORE("该设备无权登录"));
				return tokenResponseParse;
			}
			termTypeLimit = resultLoginAppInfo.getTermIphoneLimit();
		}
		else if (termType.equals(AppConfig.TERM_TYPE_WEB)) {
			if (resultLoginAppInfo.getTermWebLimit() <= 0) {
				tokenResponseParse.setReturnResp(ResultFactory.toNackCORE("该设备无权登录"));
				return tokenResponseParse;
			}
			termTypeLimit = resultLoginAppInfo.getTermWebLimit();
		}
		else {
			tokenResponseParse.setReturnResp(ResultFactory.toNackCORE("该设备无权登录"));
			return tokenResponseParse;
		}
		LoginUserToken queryUUIDToken = new LoginUserToken();
		queryUUIDToken.setAppId(appId);
		queryUUIDToken.setUserId(userId);
		queryUUIDToken.setUuid(realUUID);
		LoginUserToken resultUUIDToken = loginUserTokenMapper.selectOne(queryUUIDToken);
		// 查找有没有该UUID下面的设备，有的话不需要验证码登录
		if (StringUtils.isEmpty(userInfoLoginReqDto.getMsgVerifyCode())) {
			// 查看设备授权状态
			JSONObject jsonResult = ResultFactory.toNack(ResultFactory.ERR_NEED_VERIFYCODE, "需要验证码登录");
			if (null == resultUUIDToken) {
				tokenResponseParse.setReturnResp(jsonResult);
				return tokenResponseParse;
			}
			else {
				try {
					long timeLastValidTime = AppConfig.SDF_DB_VERSION.parse(resultUUIDToken.getValidTime()).getTime();
					long timeSkip = new Date().getTime() - timeLastValidTime;
					if (timeSkip < 0 || timeSkip > AppConfig.UserToken_DeleteTime) {
						tokenResponseParse.setReturnResp(jsonResult);
						return tokenResponseParse;
					}
				}
				catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					tokenResponseParse.setReturnResp(jsonResult);
					return tokenResponseParse;
				}

			}
			// 查看业务时间段
		}

		Date nowTime = new Date();
		String nowTimeStr = AppConfig.SDF_DB_VERSION.format(nowTime);
		String validTimeString = TimeUtils.formatTime(nowTime.getTime() + AppConfig.UserToken_ValidTime,
				AppConfig.SDF_DB_VERSION);
		LoginUserToken outUserToken = new LoginUserToken();
		outUserToken.setAppId(appId);
		outUserToken.setUserId(userId);
		outUserToken.setValidTime(nowTimeStr);
		loginUserTokenMapper.logoutAllInValid(outUserToken);
		LoginUserToken queryUserToken = new LoginUserToken();
		queryUserToken.setAppId(appId);
		queryUserToken.setUserId(userId);
		queryUserToken.setTermType(Integer.valueOf(termType));
		List<LoginUserToken> resultTermTokenLst = loginUserTokenMapper.selectLoginTokens(queryUserToken);
		System.out.println(resultTermTokenLst);
		int loginTermSize = ListUtils.getSize(resultTermTokenLst);
		int deleteTermSize = loginTermSize >= termTypeLimit ? loginTermSize - termTypeLimit + 1 : 0;
		if (deleteTermSize > loginTermSize) {
			deleteTermSize = loginTermSize;
		}
		for (int i = 0; i < deleteTermSize; i++) {
			resultTermTokenLst.get(i).setLoginStatus(0);
			if (loginUserTokenMapper.updateByPrimaryKeySelective(resultTermTokenLst.get(i)) <= 0) {
				tokenResponseParse.setReturnResp(ResultFactory.toNackDB("无法登录"));
				return tokenResponseParse;
			}
		}
		queryUserToken.setTermType(null);
		List<LoginUserToken> resultTotalTokenLst = loginUserTokenMapper.selectLoginTokens(queryUserToken);
		System.out.println(resultTotalTokenLst);
		int loginTotalSize = ListUtils.getSize(resultTotalTokenLst);
		int deleteTotalSize = loginTotalSize >= resultLoginAppInfo.getTermLimit()
				? loginTotalSize - resultLoginAppInfo.getTermLimit() + 1 : 0;
		if (deleteTotalSize > loginTotalSize) {
			deleteTotalSize = loginTotalSize;
		}
		for (int i = 0; i < deleteTotalSize; i++) {
			resultTotalTokenLst.get(i).setLoginStatus(0);
			if (loginUserTokenMapper.updateByPrimaryKeySelective(resultTotalTokenLst.get(i)) <= 0) {
				tokenResponseParse.setReturnResp(ResultFactory.toNackDB("无法登录"));
				return tokenResponseParse;
			}
		}

		if (null == resultUUIDToken) {
			LoginUserToken loginUserToken = new LoginUserToken();
			loginUserToken.setTokenId(dbSeqService.getLoginTokenNewPk());
			loginUserToken.setAppId(appId);
			loginUserToken.setUserId(userId);
			loginUserToken.setUuid(realUUID);
			loginUserToken.setTermType(Integer.valueOf(termType));
			loginUserToken.setToken(TokenUtil.generateToken());
			loginUserToken.setLoginStatus(1);
			loginUserToken.setValidTime(validTimeString);
			loginUserToken.setCreateTime(nowTimeStr);
			loginUserToken.setVersion(1);
			int dbResult = loginUserTokenMapper.insert(loginUserToken);
			if (dbResult > 0) {
				List<LoginUserToken> userTokenListAll = new ArrayList<>();
				for (int i = 0; i < loginTotalSize; i++) {
					if (resultTotalTokenLst.get(i).getLoginStatus() == 1) {
						userTokenListAll.add(resultTotalTokenLst.get(i));
					}
				}
				userTokenListAll.add(loginUserToken);
				tokenResponseParse.setLoginUserToken(loginUserToken);
				tokenResponseParse.setTokenList(userTokenListAll);
				tokenResponseParse.setValid(true);
				return tokenResponseParse;
			}
			else {
				tokenResponseParse.setReturnResp(ResultFactory.toNackDB("无法登录"));
				return tokenResponseParse;
			}
		}
		else {
			LoginUserToken loginUserToken = new LoginUserToken();
			loginUserToken.setTokenId(resultUUIDToken.getTokenId());
			loginUserToken.setTermType(Integer.valueOf(termType));
			loginUserToken.setToken(TokenUtil.generateToken());
			loginUserToken.setLoginStatus(1);
			loginUserToken.setValidTime(validTimeString);
			loginUserToken.setVersion(resultUUIDToken.getVersion());
			int dbResult = loginUserTokenMapper.updateByPrimaryKeySelective(loginUserToken);
			loginUserToken.setAppId(appId);
			loginUserToken.setUserId(userId);
			loginUserToken.setUuid(realUUID);
			loginUserToken.setCreateTime(resultUUIDToken.getCreateTime());
			loginUserToken.setVersion(resultUUIDToken.getVersion() + 1);

			if (dbResult > 0) {
				List<LoginUserToken> userTokenListAll = new ArrayList<>();
				for (int i = 0; i < loginTotalSize; i++) {
					if (resultTotalTokenLst.get(i).getLoginStatus() == 1) {
						userTokenListAll.add(resultTotalTokenLst.get(i));
					}
				}
				userTokenListAll.add(loginUserToken);
				tokenResponseParse.setLoginUserToken(loginUserToken);
				tokenResponseParse.setTokenList(userTokenListAll);
				tokenResponseParse.setValid(true);
				return tokenResponseParse;
			}
			else {
				tokenResponseParse.setReturnResp(ResultFactory.toNackDB("无法登录"));
				return tokenResponseParse;
			}
		}
	}

}
