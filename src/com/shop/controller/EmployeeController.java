package com.shop.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shop.pojo.ActiveUser;
import com.shop.service.EmployeeService;
import com.shop.utils.Constants;

@Controller
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@RequestMapping("/login")
	public String login(HttpServletRequest request, Model model) {
		String exceptionName=(String) request.getAttribute("shiroLoginFailure");
		if(UnknownAccountException.class.getName().equals(exceptionName)) {
			model.addAttribute("errorMsg","用户账户不存在");
		}else if(IncorrectCredentialsException.class.getName().equals(exceptionName)) {
			model.addAttribute("errorMsg", "密码不正确");
		}else {
			model.addAttribute("errorMsg", "未知错误");
		}
		
		
		return "login";
		
		
	}
	
	@RequestMapping("/main")
	public String main(ModelMap model,HttpSession session) {
		ActiveUser activeUser=(ActiveUser) SecurityUtils.getSubject().getPrincipal();
		session.setAttribute(Constants.GLOBLE_USER_SESSION, activeUser);
		model.addAttribute("activeUser",activeUser);
		return "index";
		
	}
	
	/*
	 * @RequestMapping(value="/logout") public String logout(HttpSession session) {
	 * // 清除session session.invalidate(); // 重定向到login.jsp return
	 * "redirect:/login.jsp";
	 * 
	 * }
	 */
	/*
	 * @RequestMapping(value="/logout",method=RequestMethod.GET) public String
	 * logout(RedirectAttributes redirectAttributes ){ //使用权限管理工具进行用户的退出，跳出登录，给出提示信息
	 * Subject subject = SecurityUtils.getSubject(); if (subject.isAuthenticated())
	 * { subject.logout(); } return "redirect:/login.jsp"; }
	 */
}
