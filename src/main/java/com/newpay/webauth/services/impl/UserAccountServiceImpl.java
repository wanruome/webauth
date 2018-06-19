/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月10日 下午3:11:31
 */
package com.newpay.webauth.services.impl;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.dal.core.TokenResponseParse;
import com.newpay.webauth.dal.mapper.LoginAppInfoMapper;
import com.newpay.webauth.dal.mapper.LoginUserAccountMapper;
import com.newpay.webauth.dal.model.LoginAppInfo;
import com.newpay.webauth.dal.model.LoginUserAccount;
import com.newpay.webauth.dal.model.LoginUserToken;
import com.newpay.webauth.dal.request.userinfo.UserInfoFindPwd;
import com.newpay.webauth.dal.request.userinfo.UserInfoLoginReqDto;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyEmail;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyMobie;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyName;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyPwd;
import com.newpay.webauth.dal.request.userinfo.UserInfoRegisterReqDto;
import com.newpay.webauth.dal.response.ResultFactory;
import com.newpay.webauth.services.DbSeqService;
import com.newpay.webauth.services.UserAccountService;
import com.newpay.webauth.services.UserTokenInfoService;
import com.ruomm.base.http.HttpConfig;
import com.ruomm.base.http.ResponseData;
import com.ruomm.base.http.okhttp.DataOKHttp;
import com.ruomm.base.tools.Base64;
import com.ruomm.base.tools.EncryptUtils;
import com.ruomm.base.tools.RSAUtils;
import com.ruomm.base.tools.RegexUtil;
import com.ruomm.base.tools.StringUtils;

@Component
@Service
public class UserAccountServiceImpl implements UserAccountService {
	@Autowired
	LoginAppInfoMapper loginAppInfoMapper;
	@Autowired
	UserTokenInfoService userTokenInfoService;
	@Autowired
	DbSeqService dbSeqService;
	@Autowired
	LoginUserAccountMapper loginUserAccountMapper;
	boolean VERIFY_IN_DB = true;

	@Override
	public Object doLogin(UserInfoLoginReqDto userInfoLoginReqDto) {
		LoginUserAccount queryUserAccount = new LoginUserAccount();
		if (AppConfig.ACCOUNT_TYPE_MOBILE.equals(userInfoLoginReqDto.getAccountType())) {
			queryUserAccount.setLoginMobile(userInfoLoginReqDto.getAccount());
		}
		else if (AppConfig.ACCOUNT_TYPE_EMAIL.equals(userInfoLoginReqDto.getAccountType())) {
			queryUserAccount.setLoginEmail(userInfoLoginReqDto.getAccount());
		}
		else if (AppConfig.ACCOUNT_TYPE_NAME.equals(userInfoLoginReqDto.getAccountType())) {
			queryUserAccount.setLoginName(userInfoLoginReqDto.getAccount());
		}
		else if (AppConfig.ACCOUNT_TYPE_USERID.equals(userInfoLoginReqDto.getAccountType())) {
			queryUserAccount.setLoginId(userInfoLoginReqDto.getAccount());
		}
		else {
			return ResultFactory.toNackPARAM();
		}
		LoginAppInfo queryLoginAppinfo = new LoginAppInfo();
		queryLoginAppinfo.setAppId(userInfoLoginReqDto.getAppId());
		LoginAppInfo resultLoginAppinfo = loginAppInfoMapper.selectByPrimaryKey(queryLoginAppinfo);
		if (null == resultLoginAppinfo || resultLoginAppinfo.getStatus() != 1) {
			return ResultFactory.toNackCORE("无法登陆，应用授权失败");
		}

		LoginUserAccount resultLoginUserAccount = loginUserAccountMapper.selectOne(queryUserAccount);
		if (null == resultLoginUserAccount) {
			return ResultFactory.toNackCORE("用户不存在");
		}
		else if (!resultLoginUserAccount.getLoginPwd().equals(userInfoLoginReqDto.getPwd())) {
			return ResultFactory.toNackCORE("密码错误");
		}
		// String token = EncryptUtils.encodingMD5(TokenUtil.generateToken());
		// UsernamePasswordToken shiroToken = new
		// UsernamePasswordToken(resultLoginUserAccount.getLoginId(),
		// TokenUtil.generateToken(), false);
		// SecurityUtils.getSubject().login(shiroToken);

		TokenResponseParse tokenResponseParse = userTokenInfoService.createTokenForLogin(
				resultLoginUserAccount.getLoginId(), userInfoLoginReqDto.getAppId(), userInfoLoginReqDto.getTermType(),
				userInfoLoginReqDto.getUuid());
		if (!tokenResponseParse.isValid()) {
			return tokenResponseParse.getReturnResp();
		}
		// 发送数据到第三方服务器上
		if (!StringUtils.isEmpty(resultLoginAppinfo.getNotifyUrl())
				&& resultLoginAppinfo.getNotifyUrl().toLowerCase().startsWith("http")) {
			List<JSONObject> lstTokenJsons = new ArrayList<JSONObject>();
			List<LoginUserToken> lstToken = tokenResponseParse.getTokenList();
			StringBuilder sb = new StringBuilder();
			for (LoginUserToken tmp : lstToken) {
				sb.append(tmp.getTokenId()).append(tmp.getToken());
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("userId", tmp.getUserId());
				jsonObject.put("tokenId", tmp.getTokenId());
				jsonObject.put("token", tmp.getToken());
				jsonObject.put("termType", tmp.getTermType());
				jsonObject.put("validTime", tmp.getValidTime());
				lstTokenJsons.add(jsonObject);
			}
			String tokenSignMd5 = EncryptUtils.encodingMD5(sb.toString());
			PublicKey tokenKey = RSAUtils.getPublicKey(Base64.decode(resultLoginAppinfo.getPublicKey()));
			byte[] tokeSignData = RSAUtils.encryptData(tokenSignMd5.getBytes(), tokenKey);
			if (null == tokeSignData) {
				return ResultFactory.toNackCORE("应用授权登录失败，应用秘钥不正确");
			}
			String tokenSignRSAMD5 = Base64.encode(tokeSignData);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("signInfo", tokenSignRSAMD5);
			jsonObject.put("tokenList", lstTokenJsons);
			jsonObject.put("token", tokenResponseParse.getTokenList());
			JSONObject json = ResultFactory.toAck(jsonObject);
			ResponseData responseData = new DataOKHttp().setUrl(resultLoginAppinfo.getNotifyUrl())
					.setRequestBody(json.toJSONString()).setRequestBody("fads").doHttp(String.class);
			if (null == responseData || responseData.getStatus() != HttpConfig.Code_Success) {
				return ResultFactory.toNackCORE("应用授权登录失败，第三方服务器无响应");
			}
		}
		Map<String, String> resultData = new HashMap<>();
		resultData.put("tokenId", tokenResponseParse.getLoginUserToken().getTokenId());
		resultData.put("token", tokenResponseParse.getLoginUserToken().getToken());
		resultData.put("validTime", tokenResponseParse.getLoginUserToken().getValidTime());
		resultData.put("termType", tokenResponseParse.getLoginUserToken().getTermType() + "");
		resultData.put("version", tokenResponseParse.getLoginUserToken().getVersion() + "");
		resultData.put("userId", resultLoginUserAccount.getLoginId());
		resultData.put("appId", tokenResponseParse.getLoginUserToken().getAppId());
		resultData.put("email", resultLoginUserAccount.getLoginEmail());
		resultData.put("name", resultLoginUserAccount.getLoginName());
		resultData.put("mobile", resultLoginUserAccount.getLoginMobile());
		return ResultFactory.toAck(resultData);
	}

	@Override
	public Object doRegister(UserInfoRegisterReqDto loginUserReqDto) {
		// TODO Auto-generated method stub

		LoginUserAccount insertUserAccount = new LoginUserAccount();
		// 验证手机号是否有效
		if (AppConfig.ACCOUNT_TYPE_MOBILE.equals(loginUserReqDto.getAccountType())) {
			loginUserReqDto.setMobie(loginUserReqDto.getAccount());
		}
		else if (AppConfig.ACCOUNT_TYPE_EMAIL.equals(loginUserReqDto.getAccountType())) {
			loginUserReqDto.setEmail(loginUserReqDto.getAccount());
		}
		else if (AppConfig.ACCOUNT_TYPE_NAME.equals(loginUserReqDto.getAccountType())) {
			loginUserReqDto.setName(loginUserReqDto.getAccount());
		}
		else {
			return ResultFactory.toNackPARAM();
		}
		if (!StringUtils.isEmpty(loginUserReqDto.getMobie())) {
			if (!RegexUtil.doRegex(loginUserReqDto.getMobie(), RegexUtil.MOBILE_NUM)) {
				return ResultFactory.toNackPARAM();
			}
			if (VERIFY_IN_DB) {
				LoginUserAccount queryUserInfo = new LoginUserAccount();
				queryUserInfo.setLoginMobile(loginUserReqDto.getMobie());
				List<LoginUserAccount> lstResult = loginUserAccountMapper.select(queryUserInfo);
				if (null != lstResult && lstResult.size() > 0) {
					return ResultFactory.toNackCORE("手机号已经被注册了");
				}
			}
			insertUserAccount.setLoginMobile(loginUserReqDto.getMobie());
		}
		// 验证邮箱是否有效
		if (!StringUtils.isEmpty(loginUserReqDto.getEmail())) {
			if (!RegexUtil.doRegex(loginUserReqDto.getEmail(), RegexUtil.EMAILS)) {
				return ResultFactory.toNackPARAM();
			}
			if (VERIFY_IN_DB) {
				LoginUserAccount queryUserAccount = new LoginUserAccount();
				queryUserAccount.setLoginEmail(loginUserReqDto.getEmail());
				List<LoginUserAccount> lstResult = loginUserAccountMapper.select(queryUserAccount);
				if (null != lstResult && lstResult.size() > 0) {
					return ResultFactory.toNackCORE("邮箱已经被注册了");
				}
			}
			insertUserAccount.setLoginEmail(loginUserReqDto.getEmail());
		}
		// 验证用户名是否有效
		if (!StringUtils.isEmpty(loginUserReqDto.getName())) {
			if (!RegexUtil.doRegex(loginUserReqDto.getName(), RegexUtil.APP_LOGIN_NAME)) {
				return ResultFactory.toNackPARAM();
			}
			if (VERIFY_IN_DB) {
				LoginUserAccount queryUserAccount = new LoginUserAccount();
				queryUserAccount.setLoginName(loginUserReqDto.getName());
				List<LoginUserAccount> lstResult = loginUserAccountMapper.select(queryUserAccount);
				if (null != lstResult && lstResult.size() > 0) {
					return ResultFactory.toNackCORE("用户名已经被注册了");
				}
			}
			insertUserAccount.setLoginName(loginUserReqDto.getName());
		}
		insertUserAccount.setLoginPwd(loginUserReqDto.getPwd());
		// 插入记录
		insertUserAccount.setLoginId(dbSeqService.getLoginUserNewPK());
		insertUserAccount.setVersion(1);
		insertUserAccount.setStatus(1);
		int dbResult = loginUserAccountMapper.insertSelective(insertUserAccount);
		if (dbResult > 0) {
			return ResultFactory.toAck(null);
		}
		else {
			return ResultFactory.toNackCORE("注册失败：手机号、用户名、邮箱等重复");
		}
	}

	@Override
	public Object doModifyPwd(UserInfoModifyPwd userInfoModifyPwd) {
		LoginAppInfo queryLoginAppinfo = new LoginAppInfo();
		queryLoginAppinfo.setAppId(userInfoModifyPwd.getAppId());
		LoginAppInfo resultLoginAppinfo = loginAppInfoMapper.selectByPrimaryKey(queryLoginAppinfo);
		if (null == resultLoginAppinfo || resultLoginAppinfo.getStatus() != 1) {
			return ResultFactory.toNackCORE("无法修改密码，应用授权失败");
		}
		LoginUserAccount dbLoginUserAccount = queryLoginUserAccount(userInfoModifyPwd.getUserId());
		if (null == dbLoginUserAccount) {
			return ResultFactory.toNackCORE("用户不存在");
		}
		if (!userInfoModifyPwd.getOldPwd().equals(dbLoginUserAccount.getLoginPwd())) {
			return ResultFactory.toNackCORE("旧的密码不正确");
		}
		LoginUserAccount updateUserAccount = new LoginUserAccount();
		updateUserAccount.setLoginId(userInfoModifyPwd.getUserId());
		updateUserAccount.setLoginPwd(userInfoModifyPwd.getNewPwd());
		boolean dbFlag = updateLoginUserAccount(dbLoginUserAccount, updateUserAccount);
		if (dbFlag) {
			Map<String, String> mapResult = new HashMap<String, String>();
			mapResult.put("version", updateUserAccount.getVersion() + "");
			return ResultFactory.toAck(mapResult);
		}
		else {
			return ResultFactory.toNackDB();
		}
	}

	@Override
	public Object doFindPwd(UserInfoFindPwd userInfoFindPwd) {
		// TODO Auto-generated method stub
		LoginUserAccount queryUserAccount = new LoginUserAccount();
		if (AppConfig.ACCOUNT_TYPE_MOBILE.equals(userInfoFindPwd.getAccountType())) {
			queryUserAccount.setLoginMobile(userInfoFindPwd.getAccount());
		}
		else if (AppConfig.ACCOUNT_TYPE_EMAIL.equals(userInfoFindPwd.getAccountType())) {
			queryUserAccount.setLoginEmail(userInfoFindPwd.getAccount());
		}
		else {
			return ResultFactory.toNackPARAM();
		}
		LoginUserAccount dbLoginUserAccount = loginUserAccountMapper.selectOne(queryUserAccount);
		if (null == dbLoginUserAccount || dbLoginUserAccount.getStatus() != 1) {
			return ResultFactory.toNackCORE("账户不存在");
		}
		if (dbLoginUserAccount.getStatus() != 1) {
			return ResultFactory.toNackCORE("账户已停用");
		}
		LoginUserAccount updateUserAccount = new LoginUserAccount();
		updateUserAccount.setLoginId(dbLoginUserAccount.getLoginId());
		updateUserAccount.setLoginPwd(userInfoFindPwd.getNewPwd());
		boolean dbFlag = updateLoginUserAccount(dbLoginUserAccount, updateUserAccount);
		if (dbFlag) {
			return ResultFactory.toAck(null);
		}
		else {
			return ResultFactory.toNackDB();
		}
	}

	@Override
	public Object doModifyMobile(UserInfoModifyMobie userInfoModifyMobie) {
		// TODO Auto-generated method stub
		LoginAppInfo queryLoginAppinfo = new LoginAppInfo();
		queryLoginAppinfo.setAppId(userInfoModifyMobie.getAppId());
		LoginAppInfo resultLoginAppinfo = loginAppInfoMapper.selectByPrimaryKey(queryLoginAppinfo);
		if (null == resultLoginAppinfo || resultLoginAppinfo.getStatus() != 1) {
			return ResultFactory.toNackCORE("无法修改手机号，应用授权失败");
		}
		if (VERIFY_IN_DB) {
			LoginUserAccount queryUserAccount = new LoginUserAccount();
			queryUserAccount.setLoginMobile(userInfoModifyMobie.getNewMobile());
			List<LoginUserAccount> lstResult = loginUserAccountMapper.select(queryUserAccount);
			if (null != lstResult && lstResult.size() > 0) {
				if (userInfoModifyMobie.getUserId().equals(lstResult.get(0).getLoginId())) {
					return ResultFactory.toNackCORE("新手机号不能和旧手机号相同");
				}
				else {
					return ResultFactory.toNackCORE("手机号已经被注册了");
				}
			}
		}
		LoginUserAccount dbLoginUserAccount = queryLoginUserAccount(userInfoModifyMobie.getUserId());
		if (null == dbLoginUserAccount) {
			return ResultFactory.toNackCORE("用户不存在");
		}
		LoginUserAccount updateUserAccount = new LoginUserAccount();
		updateUserAccount.setLoginId(userInfoModifyMobie.getUserId());
		updateUserAccount.setLoginMobile(userInfoModifyMobie.getNewMobile());
		boolean dbFlag = updateLoginUserAccount(dbLoginUserAccount, updateUserAccount);
		if (dbFlag) {
			Map<String, String> mapResult = new HashMap<String, String>();
			mapResult.put("version", updateUserAccount.getVersion() + "");
			return ResultFactory.toAck(mapResult);
		}
		else {
			return ResultFactory.toNackDB();
		}
	}

	@Override
	public Object doModifyEmail(UserInfoModifyEmail userInfoModifyEmail) {
		// TODO Auto-generated method stub
		LoginAppInfo queryLoginAppinfo = new LoginAppInfo();
		queryLoginAppinfo.setAppId(userInfoModifyEmail.getAppId());
		LoginAppInfo resultLoginAppinfo = loginAppInfoMapper.selectByPrimaryKey(queryLoginAppinfo);
		if (null == resultLoginAppinfo || resultLoginAppinfo.getStatus() != 1) {
			return ResultFactory.toNackCORE("无法修改邮箱，应用授权失败");
		}
		if (VERIFY_IN_DB) {
			LoginUserAccount queryUserAccount = new LoginUserAccount();
			queryUserAccount.setLoginEmail(userInfoModifyEmail.getNewEmail());
			List<LoginUserAccount> lstResult = loginUserAccountMapper.select(queryUserAccount);
			if (null != lstResult && lstResult.size() > 0) {
				if (userInfoModifyEmail.getUserId().equals(lstResult.get(0).getLoginId())) {
					return ResultFactory.toNackCORE("新邮箱不能和旧邮箱相同");
				}
				else {
					return ResultFactory.toNackCORE("邮箱已经被注册了");
				}
			}
		}
		LoginUserAccount dbLoginUserAccount = queryLoginUserAccount(userInfoModifyEmail.getUserId());
		if (null == dbLoginUserAccount) {
			return ResultFactory.toNackCORE("用户不存在");
		}
		// 验证authToken是否有效
		LoginUserAccount updateUserAccount = new LoginUserAccount();
		updateUserAccount.setLoginId(userInfoModifyEmail.getUserId());
		updateUserAccount.setLoginEmail(userInfoModifyEmail.getNewEmail());
		boolean dbFlag = updateLoginUserAccount(dbLoginUserAccount, updateUserAccount);
		if (dbFlag) {
			Map<String, String> mapResult = new HashMap<String, String>();
			mapResult.put("version", updateUserAccount.getVersion() + "");
			return ResultFactory.toAck(mapResult);
		}
		else {
			return ResultFactory.toNackDB();
		}
	}

	@Override
	public Object doModifyName(UserInfoModifyName userInfoModifyName) {
		// TODO Auto-generated method stub
		LoginAppInfo queryLoginAppinfo = new LoginAppInfo();
		queryLoginAppinfo.setAppId(userInfoModifyName.getAppId());
		LoginAppInfo resultLoginAppinfo = loginAppInfoMapper.selectByPrimaryKey(queryLoginAppinfo);
		if (null == resultLoginAppinfo || resultLoginAppinfo.getStatus() != 1) {
			return ResultFactory.toNackCORE("无法修改用户名，应用授权失败");
		}
		if (VERIFY_IN_DB) {
			LoginUserAccount queryUserAccount = new LoginUserAccount();
			queryUserAccount.setLoginName(userInfoModifyName.getNewName());
			List<LoginUserAccount> lstResult = loginUserAccountMapper.select(queryUserAccount);
			if (null != lstResult && lstResult.size() > 0) {
				if (userInfoModifyName.getUserId().equals(lstResult.get(0).getLoginId())) {
					return ResultFactory.toNackCORE("新用户名不能和旧用户名相同");
				}
				else {
					return ResultFactory.toNackCORE("用户名已经被注册了");
				}
			}
		}
		LoginUserAccount dbLoginUserAccount = queryLoginUserAccount(userInfoModifyName.getUserId());
		if (null == dbLoginUserAccount) {
			return ResultFactory.toNackCORE("用户不存在");
		}
		// 验证authToken是否有效
		LoginUserAccount updateUserAccount = new LoginUserAccount();
		updateUserAccount.setLoginId(userInfoModifyName.getUserId());
		updateUserAccount.setLoginName(userInfoModifyName.getNewName());
		boolean dbFlag = updateLoginUserAccount(dbLoginUserAccount, updateUserAccount);
		if (dbFlag) {
			Map<String, String> mapResult = new HashMap<String, String>();
			mapResult.put("version", updateUserAccount.getVersion() + "");
			return ResultFactory.toAck(mapResult);
		}
		else {
			return ResultFactory.toNackDB();
		}
	}

	public LoginUserAccount queryLoginUserAccount(String userId) {
		LoginUserAccount queryUserAccount = new LoginUserAccount();
		queryUserAccount.setLoginId(userId);

		return loginUserAccountMapper.selectByPrimaryKey(queryUserAccount);
	}

	public boolean updateLoginUserAccount(LoginUserAccount dbLoginUserAccount, LoginUserAccount updateUserAccount) {
		updateUserAccount.setVersion(dbLoginUserAccount.getVersion());
		int dbResult = loginUserAccountMapper.updateByPrimaryKeySelective(updateUserAccount);
		return dbResult > 0 ? true : false;
	}
	// public boolean updateLoginUserInfo(LoginUserAccount dbLoginUserAccount, LoginUserAccount
	// updateUserAccount) {
	// updateUserAccount.setLoginId(dbLoginUserAccount.getLoginId());
	// updateUserAccount.setVersion(AppConfig.getUpdateVersion(dbLoginUserAccount.getVersion()));
	// // 创建Example
	// Example example = new Example(LoginUserAccount.class);
	// // 创建Criteria
	// Example.Criteria criteria = example.createCriteria();
	// // 添加条件
	// criteria.andEqualTo("loginId", dbLoginUserAccount.getLoginId());
	// criteria.andEqualTo("version", dbLoginUserAccount.getVersion());
	//
	// int dbResult = loginUserInfoMapper.updateByExampleSelective(updateUserAccount, example);
	// return dbResult > 0 ? true : false;
	// }

}
