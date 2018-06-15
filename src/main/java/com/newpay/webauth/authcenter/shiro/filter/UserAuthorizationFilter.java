/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月14日 下午9:23:58
 */
package com.newpay.webauth.authcenter.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import com.newpay.webauth.authcenter.shiro.model.User;

public class UserAuthorizationFilter extends AuthorizationFilter {

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		System.out.println("isAccessAllowed");
		HttpServletRequest req = (HttpServletRequest) request;
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		String userId = user.getUserId();
		String token = user.getUserToken();
		String requestToken = req.getHeader("token");
		if (token.equals(requestToken)) {
			return true;
		}
		else {
			return false;
		}
		// TODO Auto-generated method stub

	}

}
