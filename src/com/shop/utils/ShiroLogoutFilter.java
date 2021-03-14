package com.shop.utils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.web.filter.authc.LogoutFilter;

public class ShiroLogoutFilter extends LogoutFilter {
	@Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        //清除HTTPSession的用户信息
        HttpServletRequest httpServletRequest=(HttpServletRequest) request;
        HttpSession session = httpServletRequest.getSession();

        if (session.getAttribute(Constants.GLOBLE_USER_SESSION)!=null) {
            session.removeAttribute(Constants.GLOBLE_USER_SESSION);
        }
        System.out.println("=HTTPSession用户数据被清空了=");
        
        return super.preHandle(httpServletRequest, response);
    }

}
