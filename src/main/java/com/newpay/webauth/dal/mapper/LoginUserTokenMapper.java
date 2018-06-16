/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月15日 下午8:58:20
 */
package com.newpay.webauth.dal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.newpay.webauth.dal.model.LoginUserToken;

import tk.mybatis.mapper.common.Mapper;

public interface LoginUserTokenMapper extends Mapper<LoginUserToken> {
	public Integer logoutAllInValid(@Param("loginUserToken") LoginUserToken loginUserToken);

	public List<LoginUserToken> selectLoginTokens(@Param("loginUserToken") LoginUserToken loginUserToken);

}
