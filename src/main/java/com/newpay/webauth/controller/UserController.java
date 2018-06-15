/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月14日 下午7:32:41
 */
package com.newpay.webauth.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("mydemo")
public class UserController {
	@RequestMapping(value = "/getUserInfo")
	public String getUserInfo(HttpServletRequest request) {
		String currentUser = (String) request.getSession().getAttribute("currentUser");
		System.out.println("当前登录的用户为[" + currentUser + "]");
		request.setAttribute("currUser", currentUser);
		return "/user/info";
	}
}
