/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月10日 下午3:11:31
 */
package com.newpay.webauth.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.abel533.entity.Example;
import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.dal.mapper.LoginUserInfoMapper;
import com.newpay.webauth.dal.model.LoginUserInfo;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyEmail;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyMobie;
import com.newpay.webauth.dal.request.userinfo.UserInfoModifyName;
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
	boolean VERIFY_IN_DB = true;

	@DataSource("mysql")
	@Transactional(rollbackFor = Exception.class)

	@Override
	public String doLogin() {
		return dbSeqService.getLoginUserNewPK() + "";
	}

	@Override
	public Object doRegister(UserInfoRegisterReqDto loginUserReqDto) {
		// TODO Auto-generated method stub

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
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_CORE, "注册失败：手机号、用户名、邮箱等重复");
		}
	}

	@Override
	public Object doModifyPwd(UserInfoModifyPwd userInfoModifyPwd) {
		LoginUserInfo dbLoginUserInfo = queryLoginUserInfo(Long.valueOf(userInfoModifyPwd.getUserId()));
		if (null == dbLoginUserInfo) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_CORE, "用户不存在");
		}
		if (!userInfoModifyPwd.getOldPwd().equals(dbLoginUserInfo.getLoginPwd())) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_CORE, "旧的密码不正确");
		}
		LoginUserInfo updateUserInfo = new LoginUserInfo();
		updateUserInfo.setLoginPwd(userInfoModifyPwd.getNewPwd());
		boolean dbFlag = updateLoginUserInfo(dbLoginUserInfo, updateUserInfo);
		if (dbFlag) {
			Map<String, String> mapResult = new HashMap<String, String>();
			mapResult.put("version", updateUserInfo.getVersion() + "");
			return BaseReturn.toSUCESS(mapResult);
		}
		else {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_SYSTEM);
		}
	}

	@Override
	public Object doModifyMobie(UserInfoModifyMobie userInfoModifyMobie) {
		// TODO Auto-generated method stub
		if (VERIFY_IN_DB) {
			LoginUserInfo queryUserInfo = new LoginUserInfo();
			queryUserInfo.setLoginMobie(userInfoModifyMobie.getNewMobile());
			List<LoginUserInfo> lstResult = loginUserInfoMapper.select(queryUserInfo);
			if (null != lstResult && lstResult.size() > 0) {
				return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_CORE, "手机号已经被注册了");
			}
		}
		LoginUserInfo dbLoginUserInfo = queryLoginUserInfo(Long.valueOf(userInfoModifyMobie.getUserId()));
		if (null == dbLoginUserInfo) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_CORE, "用户不存在");
		}
		// 验证authToken是否有效
		LoginUserInfo updateUserInfo = new LoginUserInfo();
		updateUserInfo.setLoginMobie(userInfoModifyMobie.getNewMobile());
		boolean dbFlag = updateLoginUserInfo(dbLoginUserInfo, updateUserInfo);
		if (dbFlag) {
			Map<String, String> mapResult = new HashMap<String, String>();
			mapResult.put("version", updateUserInfo.getVersion() + "");
			return BaseReturn.toSUCESS(mapResult);
		}
		else {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_SYSTEM);
		}
	}

	@Override
	public Object doModifyEmail(UserInfoModifyEmail userInfoModifyEmail) {
		// TODO Auto-generated method stub
		if (VERIFY_IN_DB) {
			LoginUserInfo queryUserInfo = new LoginUserInfo();
			queryUserInfo.setLoginEmail(userInfoModifyEmail.getNewEmail());
			List<LoginUserInfo> lstResult = loginUserInfoMapper.select(queryUserInfo);
			if (null != lstResult && lstResult.size() > 0) {
				return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_CORE, "邮箱已经被注册了");
			}
		}
		LoginUserInfo dbLoginUserInfo = queryLoginUserInfo(Long.valueOf(userInfoModifyEmail.getUserId()));
		if (null == dbLoginUserInfo) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_CORE, "用户不存在");
		}
		// 验证authToken是否有效
		LoginUserInfo updateUserInfo = new LoginUserInfo();
		updateUserInfo.setLoginEmail(userInfoModifyEmail.getNewEmail());
		boolean dbFlag = updateLoginUserInfo(dbLoginUserInfo, updateUserInfo);
		if (dbFlag) {
			Map<String, String> mapResult = new HashMap<String, String>();
			mapResult.put("version", updateUserInfo.getVersion() + "");
			return BaseReturn.toSUCESS(mapResult);
		}
		else {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_SYSTEM);
		}
	}

	@Override
	public Object doModifyName(UserInfoModifyName userInfoModifyName) {
		// TODO Auto-generated method stub
		if (VERIFY_IN_DB) {
			LoginUserInfo queryUserInfo = new LoginUserInfo();
			queryUserInfo.setLoginName(userInfoModifyName.getNewName());
			List<LoginUserInfo> lstResult = loginUserInfoMapper.select(queryUserInfo);
			if (null != lstResult && lstResult.size() > 0) {
				return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_CORE, "用户名已经被注册了");
			}
		}
		LoginUserInfo dbLoginUserInfo = queryLoginUserInfo(Long.valueOf(userInfoModifyName.getUserId()));
		if (null == dbLoginUserInfo) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_CORE, "用户不存在");
		}
		// 验证authToken是否有效
		LoginUserInfo updateUserInfo = new LoginUserInfo();
		updateUserInfo.setLoginName(userInfoModifyName.getNewName());
		boolean dbFlag = updateLoginUserInfo(dbLoginUserInfo, updateUserInfo);
		if (dbFlag) {
			Map<String, String> mapResult = new HashMap<String, String>();
			mapResult.put("version", updateUserInfo.getVersion() + "");
			return BaseReturn.toSUCESS(mapResult);
		}
		else {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_SYSTEM);
		}
	}

	public LoginUserInfo queryLoginUserInfo(long userId) {
		LoginUserInfo queryUserInfo = new LoginUserInfo();
		queryUserInfo.setLoginId(userId);

		return loginUserInfoMapper.selectByPrimaryKey(queryUserInfo);
	}

	public boolean updateLoginUserInfo(LoginUserInfo dbLoginUserInfo, LoginUserInfo updateUserInfo) {
		updateUserInfo.setLoginId(dbLoginUserInfo.getLoginId());
		updateUserInfo.setVersion(AppConfig.getUpdateVersion(dbLoginUserInfo.getVersion()));
		// 创建Example
		Example example = new Example(LoginUserInfo.class);
		// 创建Criteria
		Example.Criteria criteria = example.createCriteria();
		// 添加条件
		criteria.andEqualTo("loginId", dbLoginUserInfo.getLoginId());
		criteria.andEqualTo("version", dbLoginUserInfo.getVersion());

		int dbResult = loginUserInfoMapper.updateByExampleSelective(updateUserInfo, example);
		return dbResult > 0 ? true : false;
	}

}
