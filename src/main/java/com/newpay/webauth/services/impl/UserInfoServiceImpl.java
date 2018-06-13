/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月10日 下午3:11:31
 */
package com.newpay.webauth.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newpay.webauth.dal.mapper.LoginUserInfoMapper;
import com.newpay.webauth.dal.model.LoginUserInfo;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyPwd;
import com.newpay.webauth.dal.request.userinfo.UserInfoRegisterReqDto;
import com.newpay.webauth.dal.response.BaseReturn;
import com.newpay.webauth.services.DbSeqService;
import com.newpay.webauth.services.UserInfoService;
import com.ruomm.base.datasource.DataSource;
import com.ruomm.base.tools.RegexUtil;
import com.ruomm.base.tools.StringUtils;

@Component
@Service
public class UserInfoServiceImpl implements UserInfoService {
	@Autowired
	DbSeqService dbSeqService;
	@Autowired
	LoginUserInfoMapper loginUserInfoMapper;

	@DataSource("mysql")
	@Transactional(rollbackFor = Exception.class)

	@Override
	public String doLogin() {
		return dbSeqService.getLoginUserNewPK() + "";
	}

	@Override
	public Object doRegister(UserInfoRegisterReqDto loginUserReqDto) {
		// TODO Auto-generated method stub
		boolean VERIFY_IN_DB = true;
		LoginUserInfo insertUserInfo = new LoginUserInfo();
		// 验证手机号是否有效
		if (!StringUtils.isBlank(loginUserReqDto.getMobie())) {
			if (!RegexUtil.doRegex(loginUserReqDto.getMobie(), RegexUtil.MOBILE_NUM)) {
				return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
			}
			if (VERIFY_IN_DB) {
				LoginUserInfo queryUserInfo = new LoginUserInfo();
				queryUserInfo.setLoginMobie(loginUserReqDto.getMobie());
				List<LoginUserInfo> lstResult = loginUserInfoMapper.select(queryUserInfo);
				if (null != lstResult && lstResult.size() > 0) {
					return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_CORE, "手机号已经被注册了");
				}
			}
			insertUserInfo.setLoginMobie(loginUserReqDto.getMobie());
		}
		// 验证邮箱是否有效
		if (!StringUtils.isBlank(loginUserReqDto.getEmail())) {
			if (!RegexUtil.doRegex(loginUserReqDto.getEmail(), RegexUtil.EMAILS)) {
				return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
			}
			if (VERIFY_IN_DB) {
				LoginUserInfo queryUserInfo = new LoginUserInfo();
				queryUserInfo.setLoginEmail(loginUserReqDto.getEmail());
				List<LoginUserInfo> lstResult = loginUserInfoMapper.select(queryUserInfo);
				if (null != lstResult && lstResult.size() > 0) {
					return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_CORE, "邮箱已经被注册了");
				}
			}
			insertUserInfo.setLoginEmail(loginUserReqDto.getEmail());
		}
		// 验证用户名是否有效
		if (!StringUtils.isBlank(loginUserReqDto.getName())) {
			if (!RegexUtil.doRegex(loginUserReqDto.getName(), RegexUtil.APP_LOGIN_NAME)) {
				return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
			}
			if (VERIFY_IN_DB) {
				LoginUserInfo queryUserInfo = new LoginUserInfo();
				queryUserInfo.setLoginName(loginUserReqDto.getName());
				List<LoginUserInfo> lstResult = loginUserInfoMapper.select(queryUserInfo);
				if (null != lstResult && lstResult.size() > 0) {
					return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_CORE, "用户名已经被注册了");
				}
			}
			insertUserInfo.setLoginName(loginUserReqDto.getName());
		}
		insertUserInfo.setLoginPwd(loginUserReqDto.getPwd());
		// 插入记录
		insertUserInfo.setLoginId(dbSeqService.getLoginUserNewPK());
		insertUserInfo.setVersion(1);
		insertUserInfo.setStatus(1);
		int dbResult = loginUserInfoMapper.insertSelective(insertUserInfo);
		if (dbResult > 0) {
			return BaseReturn.toSUCESS("注册成功", null);
		}
		else {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_DB, "注册失败：手机号、用户名、邮箱等重复");
		}
	}

	@Override
	public Object doModifyPwd(UserInfoModifyPwd userInfoModifyPwd) {
		// TODO Auto-generated method stub
		return null;
	}

}
