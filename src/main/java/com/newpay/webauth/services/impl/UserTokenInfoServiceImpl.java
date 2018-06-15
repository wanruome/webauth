/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月15日 下午10:58:49
 */
package com.newpay.webauth.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newpay.webauth.dal.mapper.LoginAppInfoMapper;
import com.newpay.webauth.dal.mapper.LoginUserTokeMapper;
import com.newpay.webauth.dal.model.LoginAppInfo;
import com.newpay.webauth.dal.response.ResultFactory;
import com.newpay.webauth.services.UserTokenInfoService;

@Service
public class UserTokenInfoServiceImpl implements UserTokenInfoService {
	@Autowired
	LoginAppInfoMapper loginAppInfoMapper;
	@Autowired
	LoginUserTokeMapper loginUserTokeMapper;

	@Override
	public ResultFactory createTokenForLogin(String userId, String appId, String termType) {
		LoginAppInfo queryLoginAppInfo = new LoginAppInfo();
		queryLoginAppInfo.setAppId(appId);
		queryLoginAppInfo.setStatus(1);
		LoginAppInfo resultLoginAppInfo = loginAppInfoMapper.selectOne(queryLoginAppInfo);
		if (null == resultLoginAppInfo) {

		}
		return null;
	}

	@Override
	public ResultFactory distoryTokenForLogout(String userId, String appId, String termType) {
		// TODO Auto-generated method stub
		return null;
	}

}
