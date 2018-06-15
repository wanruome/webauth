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
import com.newpay.webauth.dal.response.ResultFactory;

public class CustomExceptionResolver implements HandlerExceptionResolver {
	private static String ERR_UNKNOWN = "e499";

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {

		ex.printStackTrace();
		String contentType = request.getHeader("content-type");
		// 解析JSON请求
		if (null != contentType && contentType.toLowerCase().contains("application/json")) {

			JSONObject baseResponse = ResultFactory.toNack(ERR_UNKNOWN);
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
