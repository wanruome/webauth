/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月15日 下午10:58:49
 */
package com.newpay.webauth.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.dal.core.TokenResponseParse;
import com.newpay.webauth.dal.mapper.LoginAppInfoMapper;
import com.newpay.webauth.dal.mapper.LoginUserTokenMapper;
import com.newpay.webauth.dal.model.LoginAppInfo;
import com.newpay.webauth.dal.model.LoginUserToken;
import com.newpay.webauth.dal.response.ResultFactory;
import com.newpay.webauth.services.DbSeqService;
import com.newpay.webauth.services.UserTokenInfoService;
import com.ruomm.base.tools.ListUtils;
import com.ruomm.base.tools.TimeUtils;
import com.ruomm.base.tools.TokenUtil;

@Service
public class UserTokenInfoServiceImpl implements UserTokenInfoService {
	@Autowired
	DbSeqService dbSeqService;
	@Autowired
	LoginAppInfoMapper loginAppInfoMapper;
	@Autowired
	LoginUserTokenMapper loginUserTokenMapper;

	@Override
	public TokenResponseParse createTokenForLogin(String userId, String appId, String termType) {
		TokenResponseParse tokenResponseParse = new TokenResponseParse();
		tokenResponseParse.setValid(false);
		LoginAppInfo queryLoginAppInfo = new LoginAppInfo();
		queryLoginAppInfo.setAppId(appId);
		queryLoginAppInfo.setStatus(1);
		LoginAppInfo resultLoginAppInfo = loginAppInfoMapper.selectOne(queryLoginAppInfo);

		if (null == resultLoginAppInfo) {
			tokenResponseParse.setReturnResp(ResultFactory.toNackPARAM());
			return tokenResponseParse;
		}
		if (resultLoginAppInfo.getStatus() != 1) {
			tokenResponseParse.setReturnResp(ResultFactory.toNackCORE("该应用已被停用，无法登录"));
			return tokenResponseParse;
		}
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
		Date nowTime = new Date();
		String nowTimeStr = AppConfig.SDF_DB_VERSION.format(nowTime);
		String validTimeString = TimeUtils.formatTime(nowTime.getTime() + TimeUtils.VALUE_DAYTimeMillis,
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

		LoginUserToken loginUserToken = new LoginUserToken();
		loginUserToken.setTokenId(dbSeqService.getLoginTokenNewPk());
		loginUserToken.setAppId(appId);
		loginUserToken.setUserId(userId);
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

	@Override
	public String getTokenById(String tokenId, String userId, String appId) {
		LoginUserToken loginUserToken = new LoginUserToken();
		loginUserToken.setTokenId(tokenId);
		// loginUserToken.setAppId(appId);
		// loginUserToken.setTokenId(tokenId);
		LoginUserToken resultUserToken = loginUserTokenMapper.selectByPrimaryKey(loginUserToken);
		if (null == resultUserToken || resultUserToken.getLoginStatus() != 1) {
			return null;
		}
		else if (!resultUserToken.getUserId().equals(userId)) {
			return null;
		}
		else if (!resultUserToken.getAppId().equals(appId)) {
			return null;
		}
		String nowTimeStr = AppConfig.SDF_DB_VERSION.format(new Date());
		if (nowTimeStr.compareTo(resultUserToken.getValidTime()) > 0) {
			return null;
		}
		else {
			return resultUserToken.getToken();
		}
	}

	@Override
	public JSONObject distoryTokenForLogout(String userId, String appId, String termType) {
		// TODO Auto-generated method stub
		return null;
	}

}
