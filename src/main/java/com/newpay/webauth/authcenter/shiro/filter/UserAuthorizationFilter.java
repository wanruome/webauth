/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月14日 下午9:23:58
 */
package com.newpay.webauth.authcenter.shiro.filter;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.config.MsgFunctionConfig;
import com.newpay.webauth.config.SystemLogFunctionConfig;
import com.newpay.webauth.config.sign.SignTools;
import com.newpay.webauth.dal.mapper.LoginUserAccountMapper;
import com.newpay.webauth.dal.mapper.LoginUserTokenMapper;
import com.newpay.webauth.dal.mapper.MsgAuthInfoMapper;
import com.newpay.webauth.dal.model.LoginUserAccount;
import com.newpay.webauth.dal.model.LoginUserToken;
import com.newpay.webauth.dal.model.MsgAuthInfo;
import com.newpay.webauth.dal.model.MsgFunctionInfo;
import com.newpay.webauth.dal.model.SystemLogFunction;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyMobie;
import com.newpay.webauth.dal.response.ResultFactory;
import com.ruomm.base.tools.FastJsonTools;
import com.ruomm.base.tools.StringUtils;

public class UserAuthorizationFilter extends AuthorizationFilter {
	@Autowired
	LoginUserTokenMapper loginUserTokenMapper;
	@Autowired
	LoginUserAccountMapper loginUserAccountMapper;
	@Autowired
	MsgAuthInfoMapper msgAuthInfoMapper;

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		return processAccessDeniedFastJson(request, response);

	}

	private boolean processAccessDeniedFastJson(ServletRequest request, ServletResponse response) throws IOException {
		JSONObject jsonObject = null;
		try {
			jsonObject = JSON.parseObject(IOUtils.toString(request.getInputStream(), "UTF-8"));
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		Map<String, String> jsonKeyMap = FastJsonTools.parseJsonKeyMap(jsonObject);
		if (null == jsonKeyMap) {
			throwException(response, ResultFactory.ERR_PARSE_REQUEST);
			return false;

		}
		// 日志节点加入

		SystemLogFunction systemLogFunction = SystemLogFunctionConfig.getSystemLogFuntionInfoByURI(request);
		System.out.println(systemLogFunction);
		// 进行短信验证码验证流程
		String verifyCode = FastJsonTools.getStringByKey(jsonObject, jsonKeyMap, AppConfig.REQUEST_FIELD_VERIFY_CODE);
		UserInfoModifyMobie userInfoModifyMobie = jsonObject.toJavaObject(UserInfoModifyMobie.class);
		System.out.println(userInfoModifyMobie.toString());
		if (!StringUtils.isEmpty(verifyCode)) {
			MsgFunctionInfo msgFunctionInfo = MsgFunctionConfig.getMsgFuntionInfoByURI(request);
			if (null == msgFunctionInfo) {
				throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "此功能不需要短信验证码");
				return false;
			}
			String msgUUID = null;
			if (msgFunctionInfo.getAuthType() == 0) {
				msgUUID = FastJsonTools.getStringByKey(jsonObject, jsonKeyMap, AppConfig.REQUEST_FIELD_UUID);
			}
			else {
				msgUUID = FastJsonTools.getStringByKey(jsonObject, jsonKeyMap, AppConfig.REQUEST_FIELD_USER_ID);
			}
			if (StringUtils.isEmpty(msgUUID)) {
				throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "请求参数错误");
			}
			String nowDateStr = AppConfig.SDF_DB_VERSION.format(new Date());
			MsgAuthInfo msgAuthInfo = new MsgAuthInfo();
			msgAuthInfo.setUuid(msgUUID);
			msgAuthInfo.setFunctionId(msgFunctionInfo.getFunctionId());
			MsgAuthInfo resultAuthInfo = msgAuthInfoMapper.selectByPrimaryKey(msgAuthInfo);
			if (null == resultAuthInfo || nowDateStr.compareTo(resultAuthInfo.getMsgValidTime()) >= 0) {
				throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "你还没有获取短信验证码");
				return false;
			}
			else if (resultAuthInfo.getMsgStatus() != 1) {
				throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "短信验证码已经失效，请重新获取");
				return false;
			}
			else {
				if (msgFunctionInfo.getAuthType() != 2) {
					msgAuthInfo.setMsgStatus(0);
					msgAuthInfo.setVersion(resultAuthInfo.getVersion());
					int dbResult = msgAuthInfoMapper.updateByPrimaryKeySelective(msgAuthInfo);
					if (dbResult <= 0) {
						throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "短信验证码已经失效，请重新获取");
						return false;
					}
				}
				if (!verifyCode.equals(resultAuthInfo.getMsgCode())) {
					throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "短信验证码不正确");
					return false;
				}
			}
			if (StringUtils.isEmpty(msgFunctionInfo.getVerfifyFieldName())) {
				String userId = FastJsonTools.getStringByKey(jsonObject, jsonKeyMap, AppConfig.REQUEST_FIELD_USER_ID);
				LoginUserAccount queryAccount = new LoginUserAccount();
				queryAccount.setLoginId(userId);
				LoginUserAccount resultAccount = loginUserAccountMapper.selectByPrimaryKey(queryAccount);
				if (null == resultAccount) {
					throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "短信验证码不匹配");
					return false;
				}
				if (!resultAuthInfo.getMsgAddr().equals(resultAccount.getLoginMobile())
						&& !resultAuthInfo.getMsgAddr().equals(resultAccount.getLoginEmail())) {
					throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "短信验证码不匹配");
					return false;
				}
			}
			else {
				String msgAddr = FastJsonTools.getStringByKey(jsonObject, jsonKeyMap,
						msgFunctionInfo.getVerfifyFieldName());
				if (!resultAuthInfo.getMsgAddr().equals(msgAddr)) {
					throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "短信验证码不匹配");
					return false;
				}
			}
		}
		// 进行签名验证流程
		String signInfo = FastJsonTools.getStringByKey(jsonObject, jsonKeyMap, AppConfig.REQUEST_FIELD_SIGN_INFO);
		if (StringUtils.isEmpty(signInfo)) {
			return true;
		}
		else {
			String userId = FastJsonTools.getStringByKey(jsonObject, jsonKeyMap, AppConfig.REQUEST_FIELD_USER_ID);
			String appId = FastJsonTools.getStringByKey(jsonObject, jsonKeyMap, AppConfig.REQUEST_FIELD_APP_ID);
			String tokenId = FastJsonTools.getStringByKey(jsonObject, jsonKeyMap, AppConfig.REQUEST_FIELD_TOKEN_ID);
			String token = getTokenById(tokenId, userId, appId);
			if (SignTools.verifySign(jsonObject, token)) {
				return true;
			}
			{
				throwException(response, ResultFactory.ERR_TOKEN_INVALID);
				return false;
			}

		}
	}

	private boolean processAccessDeniedGson(ServletRequest request, ServletResponse response) throws IOException {
		JSONObject jsonObject = null;
		try {
			jsonObject = JSON.parseObject(IOUtils.toString(request.getInputStream(), "UTF-8"));
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if (null == jsonObject) {
			throwException(response, ResultFactory.ERR_PARSE_REQUEST);
			return false;
		}
		// 进行短信验证码验证流程
		String verifyCode = jsonObject.getString(AppConfig.REQUEST_FIELD_VERIFY_CODE);
		UserInfoModifyMobie userInfoModifyMobie = jsonObject.toJavaObject(UserInfoModifyMobie.class);
		System.out.println(userInfoModifyMobie.toString());
		if (!StringUtils.isEmpty(verifyCode)) {
			MsgFunctionInfo msgFunctionInfo = MsgFunctionConfig.getMsgFuntionInfoByURI(request);
			if (null == msgFunctionInfo) {
				throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "此功能不需要短信验证码");
				return false;
			}
			String msgUUID = null;
			if (msgFunctionInfo.getAuthType() == 0) {
				msgUUID = jsonObject.getString(AppConfig.REQUEST_FIELD_UUID);
			}
			else {
				msgUUID = jsonObject.getString(AppConfig.REQUEST_FIELD_USER_ID);
			}
			if (StringUtils.isEmpty(msgUUID)) {
				throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "请求参数错误");
			}
			String nowDateStr = AppConfig.SDF_DB_VERSION.format(new Date());
			MsgAuthInfo msgAuthInfo = new MsgAuthInfo();
			msgAuthInfo.setUuid(msgUUID);
			msgAuthInfo.setFunctionId(msgFunctionInfo.getFunctionId());
			MsgAuthInfo resultAuthInfo = msgAuthInfoMapper.selectByPrimaryKey(msgAuthInfo);
			if (null == resultAuthInfo || nowDateStr.compareTo(resultAuthInfo.getMsgValidTime()) >= 0) {
				throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "你还没有获取短信验证码");
				return false;
			}
			else if (resultAuthInfo.getMsgStatus() != 1) {
				throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "短信验证码已经失效，请重新获取");
				return false;
			}
			else {
				if (msgFunctionInfo.getAuthType() != 2) {
					msgAuthInfo.setMsgStatus(0);
					msgAuthInfo.setVersion(resultAuthInfo.getVersion());
					int dbResult = msgAuthInfoMapper.updateByPrimaryKeySelective(msgAuthInfo);
					if (dbResult <= 0) {
						throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "短信验证码已经失效，请重新获取");
						return false;
					}
				}
				if (!verifyCode.equals(resultAuthInfo.getMsgCode())) {
					throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "短信验证码不正确");
					return false;
				}
			}
			if (StringUtils.isEmpty(msgFunctionInfo.getVerfifyFieldName())) {
				String userId = jsonObject.getString(AppConfig.REQUEST_FIELD_USER_ID);
				LoginUserAccount queryAccount = new LoginUserAccount();
				queryAccount.setLoginId(userId);
				LoginUserAccount resultAccount = loginUserAccountMapper.selectByPrimaryKey(queryAccount);
				if (null == resultAccount) {
					throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "短信验证码不匹配");
					return false;
				}
				if (!resultAuthInfo.getMsgAddr().equals(resultAccount.getLoginMobile())
						&& !resultAuthInfo.getMsgAddr().equals(resultAccount.getLoginEmail())) {
					throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "短信验证码不匹配");
					return false;
				}
			}
			else {
				String msgAddr = jsonObject.getString(msgFunctionInfo.getVerfifyFieldName());
				if (!resultAuthInfo.getMsgAddr().equals(msgAddr)) {
					throwException(response, ResultFactory.ERR_MSGCODE_INVALID, "短信验证码不匹配");
					return false;
				}
			}
		}
		// 进行签名验证流程
		String signInfo = jsonObject.getString(AppConfig.REQUEST_FIELD_SIGN_INFO);
		if (StringUtils.isEmpty(signInfo)) {
			return true;
		}
		else {
			String userId = jsonObject.getString(AppConfig.REQUEST_FIELD_USER_ID);
			String appId = jsonObject.getString(AppConfig.REQUEST_FIELD_APP_ID);
			String tokenId = jsonObject.getString(AppConfig.REQUEST_FIELD_TOKEN_ID);
			String token = getTokenById(tokenId, userId, appId);
			if (SignTools.verifySign(jsonObject, token)) {
				return true;
			}
			{
				throwException(response, ResultFactory.ERR_TOKEN_INVALID);
				return false;
			}

		}
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		System.out.println("isAccessAllowed");
		return false;

		// HttpServletRequest req = (HttpServletRequest) request;
		// User user = (User) SecurityUtils.getSubject().getPrincipal();
		// String userId = user.getUserId();
		// String token = user.getUserToken();
		// String requestToken = req.getHeader("token");
		// if (token.equals(requestToken)) {
		// return true;
		// }
		// else {
		// return false;
		// }
		// TODO Auto-generated method stub

	}

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

	private void throwException(ServletResponse response, String errCode) throws IOException {
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
		httpServletResponse.setHeader("Cache-Control", "no-cache, must-revalidate");
		JSONObject resultJson = ResultFactory.toNack(errCode, null);
		httpServletResponse.getWriter().write(resultJson.toJSONString());
	}

	private void throwException(ServletResponse response, String errCode, String errMsg) throws IOException {
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
		httpServletResponse.setHeader("Cache-Control", "no-cache, must-revalidate");
		JSONObject resultJson = ResultFactory.toNack(errCode, errMsg);
		httpServletResponse.getWriter().write(resultJson.toJSONString());
	}

}
