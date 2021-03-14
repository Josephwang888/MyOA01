package com.shop.utils;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;

public class ManagerTaskHandler implements TaskListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegateTask) {

		//WebApplicationContext context=ContextLoader.getCurrentWebApplicationContext().
		//HttpServletRequest request=RequestContextHolder.getRequestAttributes();
		delegateTask.setAssignee("mike");
	}

}
