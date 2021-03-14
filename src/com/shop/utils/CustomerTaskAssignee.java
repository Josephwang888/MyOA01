package com.shop.utils;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.shop.pojo.ActiveUser;
import com.shop.pojo.Employee;
import com.shop.service.EmployeeService;

public class CustomerTaskAssignee implements TaskListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegateTask) {

		
		// 调用业务类查询出当前待办人的上级 
		
		
		WebApplicationContext webApplicationContext = ContextLoader
		.getCurrentWebApplicationContext();
		
	EmployeeService employeeService = 	(EmployeeService) webApplicationContext
			.getBean("employeeService");
	
	
	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
			.getRequest();
	
  
	ActiveUser employee =   (ActiveUser) request.getSession().getAttribute(Constants.GLOBLE_USER_SESSION);
	
	
		  
		  
	
	
	long  manageId = employee.getManagerId();
	// 调用业务类的方法
	Employee manager = employeeService.findEmployeeManager(manageId);
	
	
	delegateTask.setAssignee(manager.getName());
	
	
	
	
	
		
		
		
		
		
		
		
		
		
		
		
	}

}
