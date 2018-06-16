/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月14日 下午9:23:58
 */
package com.newpay.webauth.authcenter.shiro.filter;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.config.sign.SignTools;
import com.newpay.webauth.dal.mapper.LoginUserTokenMapper;
import com.newpay.webauth.dal.model.LoginUserToken;
import com.newpay.webauth.dal.response.ResultFactory;
import com.ruomm.base.tools.StringUtils;

public class UserAuthorizationFilter extends AuthorizationFilter {
	@Autowired
	LoginUserTokenMapper loginUserTokenMapper;

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		// return true;
		JSONObject jsonObject = null;
		try {
			jsonObject = JSON.parseObject(IOUtils.toString(request.getInputStream(), "UTF-8"));
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if (null == jsonObject) {
			throwException(ResultFactory.ERR_PARSE_REQUEST, response);
			return false;
		}
		else {
			String signInfo = jsonObject.getString(AppConfig.SIGN_FIELD_NAME);
			if (StringUtils.isEmpty(signInfo)) {
				return true;
			}
			else {
				String userId = jsonObject.getString(AppConfig.SIGN_USER_ID);
				String appId = jsonObject.getString(AppConfig.SIGN_APP_ID);
				String tokenId = jsonObject.getString(AppConfig.SIGN_TOKEN_ID);
				String token = getTokenById(tokenId, userId, appId);
				if (SignTools.verifySign(jsonObject, token)) {
					return true;
				}
				{
					throwException(ResultFactory.ERR_TOKEN_INVALID, response);
					return false;
				}

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

	private void throwException(String errCode, ServletResponse response) throws IOException {
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
		httpServletResponse.setHeader("Cache-Control", "no-cache, must-revalidate");
		JSONObject resultJson = ResultFactory.toNack(errCode, null);
		httpServletResponse.getWriter().write(resultJson.toJSONString());
	}

}
