/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月15日 上午10:52:48
 */
package com.newpay.webauth.authcenter.shiro.subjectfactory;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.web.util.WebUtils;

import com.ruomm.base.tools.BaseWebUtils;

public class StatelessDefaultSubjectFactory extends DefaultWebSubjectFactory {
	@Override
	public Subject createSubject(SubjectContext context) {
		// 不创建session
		// context.setSessionCreationEnabled(false);
		// DefaultWebSubjectContext webSubjectContext = (DefaultWebSubjectContext) context;
		// context.getSubject(
		// ServletRequest request = ((WebSubject) SecurityUtils.getSubject()).getServletRequest();
		// ServletRequest request = ((WebSubject) context.getSubject()).getServletRequest();
		// HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletRequest httpRequest = WebUtils.getHttpRequest(context);
		if (null != httpRequest) {
			String uri = BaseWebUtils.getRealUri(httpRequest.getRequestURI());
			// System.out.println(httpRequest.getRequestURI());
			// String tmp = httpRequest.getSession().toString();
			// System.out.println(tmp);
			if (uri.startsWith("/webauth/app/")) {

				context.setSessionCreationEnabled(false);
			}
			else {
				context.setSessionCreationEnabled(true);
			}
		}
		else {

		}
		return super.createSubject(context);
	}
}
