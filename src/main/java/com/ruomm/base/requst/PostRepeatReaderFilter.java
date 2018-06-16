/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月16日 下午3:17:02
 */
package com.ruomm.base.requst;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class PostRepeatReaderFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		ServletRequest requestWrapper = null;
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			if ("POST".equals(httpServletRequest.getMethod().toUpperCase())) {
				requestWrapper = new PostRepeatReaderRequestWrapper((HttpServletRequest) request);
			}
		}
		if (requestWrapper == null) {
			chain.doFilter(request, response);
		}
		else {
			chain.doFilter(requestWrapper, response);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	@Override
	public void destroy() {

	}

}
