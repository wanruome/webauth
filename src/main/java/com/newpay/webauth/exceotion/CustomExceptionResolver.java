/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午1:42:22
 */
package com.newpay.webauth.exceotion;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.newpay.webauth.aop.SystemLogThreadLocal;
import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.config.listener.SpringContextHolder;
import com.newpay.webauth.dal.core.SysLogBean;
import com.newpay.webauth.dal.core.SystemLogThread;
import com.newpay.webauth.dal.response.ResultFactory;
import com.newpay.webauth.services.SystemLogService;
import com.ruomm.base.tools.BaseWebUtils;
import com.ruomm.base.tools.StringUtils;

public class CustomExceptionResolver implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {

		ex.printStackTrace();
		boolean isApp = false;
		try {
			String[] realUriArray = BaseWebUtils.getRealUriToArray(request.getRequestURI());
			if (realUriArray[1].equals("app")) {
				isApp = true;
			}
			else {
				isApp = false;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		String contentType = StringUtils.nullStrToEmpty(request.getHeader("content-type")).toLowerCase();
		if (isApp) {
			// 解析写入日志
			// 写入日志
			SysLogBean sysLogBean = null;
			try {
				sysLogBean = SystemLogThreadLocal.get();

			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				SystemLogThreadLocal.cleanSysLogBean();
			}
			try {
				if (null != sysLogBean) {
					if (AppConfig.SYSTEMLOG_ASYNC) {
						new SystemLogThread(sysLogBean, ResultFactory.ERR_UNKNOWN, "系统异常").start();
					}
					else {
						SystemLogService systemLogService = SpringContextHolder.getBean(SystemLogService.class);
						systemLogService.writeLogs(sysLogBean, ResultFactory.ERR_UNKNOWN, "系统异常");
					}

				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 解析JSON请求
		if (isApp && (contentType.contains("application/json") || contentType.contains("")
				|| contentType.contains("text/plain"))) {

			JSONObject baseResponse = ResultFactory.toNack(ResultFactory.ERR_UNKNOWN, null);
			Writer writer = null;
			try {
				writer = response.getWriter();
				writer.write(baseResponse.toJSONString());
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				if (null != writer) {
					try {
						writer.close();
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return null;
		}
		else {
			// 向前台返回错误信息
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("message", ex.getMessage());
			modelAndView.setViewName("/error");

			return modelAndView;
		}

	}
}
