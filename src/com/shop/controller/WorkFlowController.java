package com.shop.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.shop.pojo.ActiveUser;
import com.shop.pojo.Employee;
import com.shop.pojo.Leavebill;
import com.shop.service.LeavebillService;
import com.shop.service.WorkFlowService;
import com.shop.utils.Constants;

@Controller
public class WorkFlowController {

	@Autowired
	private WorkFlowService workFlowService;
	@Autowired
	private LeavebillService leavebillService;
	//部署流程
	@RequestMapping("/deployProcess")
	public String deployProcess(String processName,MultipartFile fileName){
		try {
			workFlowService.saveNewDeploy(fileName.getInputStream(), processName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "add_process";
	}
	
	@RequestMapping("/saveStartLeave")
	public String saveStartLeave(Leavebill leavebill,HttpSession session){
		//设置当前时间
		leavebill.setLeavedate(new Date());
		//设置状态
		leavebill.setState(1);
		//获取到当前用户
		ActiveUser au = (ActiveUser) session.getAttribute(Constants.GLOBLE_USER_SESSION);
		leavebill.setUserId(au.getId());
		leavebillService.saveLeaveBill(leavebill);
		workFlowService.saveStartLeave(leavebill.getId(), au.getUsername());
		return "redirect:/myTaskList";
	}
	
	@RequestMapping("/myTaskList")
	public String getTaskList(Model model,HttpSession session){
		String name = ((ActiveUser)session.getAttribute(Constants.GLOBLE_USER_SESSION)).getUsername();
		List<Task> list = workFlowService.findTaskListByName(name);	
		model.addAttribute("taskList", list);
		return "workflow_task";
	}
	
	//根据当前任务id 去查询当前请假单信息  +  查询出历史批注信息
	@RequestMapping(value="/viewTaskForm2",method=RequestMethod.GET)
	public String findLeaveBillByTaskId(String taskId,Model model){
		//跳转页面approve_leave.jsp
		Leavebill leaveBill = workFlowService.findLeaveBillByTaskId(taskId);
		//根据taskId 去查询历史批注表
		List<Comment> commentList = this.workFlowService.findCommentListByTaskId(taskId);
		model.addAttribute("bill", leaveBill);
		model.addAttribute("commentList", commentList);
		model.addAttribute("taskId", taskId);
		return "approve_leave";
	}
	//4. 流程的推进
	@RequestMapping("/submitTask")
	public String subTask(String id,String taskId,String comment,HttpSession session){
		ActiveUser au = (ActiveUser) session.getAttribute(Constants.GLOBLE_USER_SESSION);
		workFlowService.submitTask(id, taskId, comment, au.getUsername());
		return "redirect:/myTaskList";
		
	}
}
