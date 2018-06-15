/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月14日 下午4:53:25
 */
package com.newpay.webauth.authcenter.shiro.realm;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import com.newpay.webauth.authcenter.shiro.model.User;

public class UserAuthorizingRealm extends AuthorizingRealm {

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// TODO Auto-generated method stub
		System.out.println("权限认证方法：MyShiroRealm.doGetAuthorizationInfo()");
		String currentUsername = (String) super.getAvailablePrincipal(principals);
		SimpleAuthorizationInfo simpleAuthorInfo = new SimpleAuthorizationInfo();
		// 实际中可能会像上面注释的那样从数据库取得
		if (null != currentUsername && "papio".equals(currentUsername)) {
			// 添加一个角色,不是配置意义上的添加,而是证明该用户拥有admin角色
			simpleAuthorInfo.addRole("admin");
			// 添加权限
			simpleAuthorInfo.addStringPermission("admin:manage");
			System.out.println("已为用户[papio]赋予了[admin]角色和[admin:manage]权限");
			return simpleAuthorInfo;
		}
		else if (null != currentUsername && "big".equals(currentUsername)) {
			System.out.println("当前用户[big]无授权");
			return simpleAuthorInfo;
		}
		// 若该方法什么都不做直接返回null的话,就会导致任何用户访问/admin/listUser.jsp时都会自动跳转到unauthorizedUrl指定的地址
		// 详见applicationContext.xml中的<bean id="shiroFilter">的配置
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
			throws AuthenticationException {
		// TODO Auto-generated method stub
		System.out.println("权限认证方法：doGetAuthenticationInfo()");
		// 获取基于用户名和密码的令牌
		// 实际上这个authcToken是从LoginController里面currentUser.login(token)传过来的
		// 两个token的引用都是一样的,本例中是org.apache.shiro.authc.UsernamePasswordToken@33799a1e
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		System.out.println(
				"验证当前Subject时获取到token为" + ReflectionToStringBuilder.toString(token, ToStringStyle.MULTI_LINE_STYLE));
		User user = new User();
		user.setUserId(token.getUsername());
		user.setUserToken(String.valueOf(token.getPassword()));
		System.out.println(user);
		return new SimpleAuthenticationInfo(user, user.getUserToken(), user.getUserId());
	}

	/**
	 * 将一些数据放到ShiroSession中,以便于其它地方使用
	 *
	 * @see 比如Controller,使用时直接用HttpSession.getAttribute(key)就可以取到
	 */
	private void setSession(Object key, Object value) {
		Subject currentUser = SecurityUtils.getSubject();
		if (null != currentUser) {
			Session session = currentUser.getSession();
			System.out.println("Session默认超时时间为[" + session.getTimeout() + "]毫秒");
			if (null != session) {
				session.setAttribute(key, value);
			}
		}
	}

}
