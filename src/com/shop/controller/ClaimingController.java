package com.shop.controller;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.shiro.web.session.HttpServletSession;
import org.hibernate.validator.internal.engine.ValueContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.pojo.ActiveUser;
import com.shop.pojo.Baoxiaobill;
import com.shop.pojo.Employee;
import com.shop.pojo.Leavebill;
import com.shop.pojo.MenuTree;
import com.shop.pojo.SysPermission;
import com.shop.pojo.SysRole;
import com.shop.pojo.SysUserRole;
import com.shop.pojo.SysUserRoleExample;
import com.shop.service.BaoxiaoService;
import com.shop.service.EmployeeService;
import com.shop.service.SysService;
import com.shop.service.WorkFlowService;
import com.shop.utils.Constants;

import oracle.net.aso.e;

@Controller
public class ClaimingController {
	@Autowired
	private WorkFlowService workFlowService;
	@Autowired
	private BaoxiaoService baoxiaoService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private SysService SysService;
	//保存流程定义
	@RequestMapping("/saveStartBaoxiao")
	public String saveStartBaoxiao(Baoxiaobill baoxiao,HttpSession session){
		baoxiao.setCreatdate(new Date());
		baoxiao.setState(1);
		ActiveUser au = (ActiveUser) session.getAttribute(Constants.GLOBLE_USER_SESSION);
		baoxiao.setUserId(au.getId());
		baoxiaoService.saveStartBaoxiao(baoxiao);
		workFlowService.saveStartBaoxiao(baoxiao.getId(), au.getUsername());
		System.out.println(baoxiao.getId());
		return "redirect:/myTaskList2";
	}
	@RequestMapping("/myTaskList2")
	public String getTaskList(Model model,HttpSession session) {
		String name = ((ActiveUser)session.getAttribute(Constants.GLOBLE_USER_SESSION)).getUsername();
		List<Task> list=workFlowService.findTaskListByName(name);
		model.addAttribute("taskList", list);
		return "workflow_task2";
		
	}
	//根据当前任务id 去查询当前请假单信息  +  查询出历史批注信息
//	@RequestMapping(value="/viewTaskForm",method=RequestMethod.GET)
//	public String findBaoxiaoBillByTaskId(String taskId,Model model){
//		//跳转页面approve_leave.jsp
//		Baoxiaobill baoxiaobill = workFlowService.findBaoxiaoBillByTaskId(taskId);
//		//根据taskId 去查询历史批注表
//		List<Comment> commentList = this.workFlowService.findCommentListByTaskId(taskId);
//		List<String> list=new ArrayList<>();
//		list.add("驳回");
//		list.add("金额大于5000");
//		list.add("金额少于等于5000");
//		list.add("不同意");
//		list.add("同意");
//	    model.addAttribute("outcomeList", list);
//		model.addAttribute("baoxiaoBill", baoxiaobill);
//		model.addAttribute("commentList", commentList);
//		model.addAttribute("taskId", taskId);
//		return "approve_baoxiao";
//	}
	
	//根据当前任务id 去查询当前请假单信息  +  查询出历史批注信息
	@RequestMapping(value="/viewTaskForm",method=RequestMethod.GET)
	public String findBaoxiaoBillByTaskId(String taskId,Model model){
		// 跳转页面approve_leave.jsp
		Baoxiaobill baoxiaobill = workFlowService.findBaoxiaoBillByTaskId(taskId);
		// 根据taskId 去查询历史批注表
		List<Comment> commentList = this.workFlowService.findCommentListByTaskId(taskId);
		model.addAttribute("baoxiaoBill", baoxiaobill);
		model.addAttribute("commentList", commentList);
		model.addAttribute("taskId", taskId);
		List<String> outcomeList = workFlowService.findOutComeListByTaskId(taskId);
		model.addAttribute("outcomeList", outcomeList);
		return "approve_baoxiao";
	}
	
	//4. 流程的推进
	@RequestMapping("/submitTask2")
	public String subTask(String id,String taskId,String comment,HttpSession session,String outcome){
		ActiveUser au = (ActiveUser) session.getAttribute(Constants.GLOBLE_USER_SESSION);
		workFlowService.submitBaoxiaoTask(id, taskId, comment,outcome, au.getUsername());
		return "redirect:/myTaskList2";
		
	}	
	//我的报销模块	
	@RequestMapping("/myBaoxiaoBill")
	public String getTaskList2(Model model,HttpSession session) {
		ActiveUser au = (ActiveUser) session.getAttribute(Constants.GLOBLE_USER_SESSION);
		List<Baoxiaobill> list=workFlowService.findBaoxiaoListById(au.getId());
		model.addAttribute("baoxiaoList", list);
		return "baoxiaobill";
		
	}
    //查看流程
	@RequestMapping("/processDefinitionList")
	public String getprocessDeinition(Model model,HttpSession session) {
		List<ProcessDefinition> definit = workFlowService.findProcessDefinitionList();
		List<Deployment> findDeployment = workFlowService.findDeployment();
		model.addAttribute("depList", findDeployment);
		model.addAttribute("pdList", definit);
		return "workflow_list";
	}
	//查看流程定义图
	@RequestMapping("/viewImage1")
	public void getViewimage(ServletResponse response,String deploymentId,String imageName) {
	    InputStream in = workFlowService.findView(deploymentId, imageName);
	//	HttpServletResponse resp = ServletActionContext.getResponse();
		try {
		  OutputStream out = response.getOutputStream();
		  // 把图片的输入流程写入resp的输出流中
		  byte[] b = new byte[1024];
		  for (int len = -1; (len= in.read(b))!=-1; ) {
			  out.write(b, 0, len);
		  }
		  // 关闭流
		  out.close();
		  in.close();
		} catch (IOException e) {
		  e.printStackTrace();
		}
	}
	//用户列表
	@RequestMapping("/findUserList")
	public String getUserlist(Model model,HttpSession session) {
		List<Employee> list=new ArrayList<>();
		List<Employee> emp = employeeService.findEmployee();
		for(Employee emp1:emp) {
			Employee findEmployeeManager = employeeService.findEmployeeManager(emp1.getManagerId());
			if(findEmployeeManager!=null) {
				if(findEmployeeManager.getManagerId()!=null&&!findEmployeeManager.getManagerId().equals("")) {
					list.add(findEmployeeManager);
				}
			}
		}
		List<SysRole> findRole = employeeService.findRole();
		model.addAttribute("list", list);
		model.addAttribute("allRoles", findRole);
		model.addAttribute("userList", emp);
	
	
	
		return "userlist";
	}
	//查询用户权限
	@RequestMapping("/viewPermissionByUser")
	@ResponseBody
	public SysRole getUserPermissions(Model model,String userName) {
		
		//SysUserRole selectUser = SysService.selectdByUserId(userName);
		//List<SysPermission> findPer = SysService.findPermissionsByRoleId(selectUser.getSysRoleId());
	    SysRole sysRole = SysService.findRolesAndPermissionsByUserId(userName);
//		JSONObject json = new JSONObject();
//		json.put("sysRole", sysRole);
		return sysRole;
	}
	
	//删除流程部署
	@RequestMapping("/delDeployment")
	public String delDeployment(Model model,HttpSession session,String deploymentId) {
		workFlowService.deleteDeployment(deploymentId);
		
		return "redirect:/processDefinitionList";
	}
	//添加角色
	@RequestMapping("/toAddRole")
	public String addJuese(Model model,HttpSession session,String deploymentId) {
		 List<MenuTree> loadMenuTree = SysService.loadMenuTree();
		model.addAttribute("allPermissions", loadMenuTree);
		model.addAttribute("menuTypes", loadMenuTree);
		return "rolelist";
	}
	//角色列表
	@RequestMapping("/showjuese")
	public String showJuese(Model model,HttpSession session,String deploymentId) {
		List<SysRole> findAllRoles = SysService.findAllRoles();
		List<MenuTree> allMenuAndPermissions = SysService.loadMenuTree();	
		model.addAttribute("allMenuAndPermissions", allMenuAndPermissions);
		model.addAttribute("allRoles", findAllRoles);
		return "permissionlist";
	}
	//新建权限
	@RequestMapping("/saveSubmitPermission")
	public String saveSubmitPermission(SysPermission permission) {
		SysService.addSysPermission(permission);
		return "redirect:/toAddRole";
	}
	@RequestMapping("/loadMyPermissions")
	@ResponseBody
	public List<SysPermission> editJuese(String roleId,Model model) {
		List<SysPermission> permissionList = SysService.findPermissionsByRoleId(roleId);
		return  permissionList;
	}
	
	//重新分配角色
	@RequestMapping("/assignRole")
	@ResponseBody
	public String declareJuese(Model model,String roleId,String userId ) {
		SysUserRole d=new SysUserRole();
		
		d.setSysRoleId(roleId);
		d.setSysUserId(userId);
        SysService.updateByUserId(d);
        JSONObject result = new JSONObject();
        result.put("msg", "更改成功");
		return result.toString();
	}
	
	//根据当前id 去查询当前报销单信息  +  查询出历史批注信息
		@RequestMapping(value="/viewHisComment",method=RequestMethod.GET)
		public String findLeaveBillByTaskId(String id,Model model){
		    
			Baoxiaobill findBaoxiao= workFlowService.findBaoxiaoListBykey(Long.parseLong(id));
		    
		//	Baoxiaobill baoxiaobill = workFlowService.findBaoxiaoBillByTaskId(id);
			
			//根据taskId 去查询历史批注表
			List<Comment> commentList = this.workFlowService.findCommentListById(id);
			model.addAttribute("baoxiaoBill", findBaoxiao);
			model.addAttribute("commentList", commentList);
			model.addAttribute("taskId", id);
			return "workflow_commentlist";
		}
		//删除我的报销记录
		@RequestMapping("/leaveBillAction_delete")
		public String delBaoxiaohistory(String id,Model model) {			
			baoxiaoService.deleteByPrimaryKey(Long.parseLong(id));
			return "redirect:/myBaoxiaoBill";
		}
		
		//查询上级主管
		@RequestMapping("/findNextManager")
		@ResponseBody
		public List<Employee> findNextManager(String level) {
			Employee managerList1 = employeeService.findEmployeeManager(Long.parseLong(level));
			Employee manager = employeeService.findEmployeeManager(managerList1.getManagerId());
			List<Employee> managerList=new ArrayList<>();
			managerList.add(manager);
			return managerList;
		}
		
		//保存新用户
		@RequestMapping("/saveUser")
		public String saveEmployee(Model model,Employee emp,String manager_id,String role) {
			SysRole role1 = SysService.selectByPrimaryKey(emp.getRole());
			List<SysUserRole> list = SysService.findUserRoles();
			int i=Integer.parseInt(list.get(list.size()-1).getId())+1;
			
			SysUserRole userrole=new SysUserRole();
			userrole.setId(String.valueOf(i));
			userrole.setSysRoleId(role);
			userrole.setSysUserId(emp.getName());
			SysService.insertUserrole(userrole);
		//	Employee findManager = employeeService.findEmployeeManager(Long.parseLong(manager_id));
			if(role1!=null) {
				emp.setManagerId(Long.parseLong(manager_id));
				emp.setRole(role1.getName());
				employeeService.Saveemployee(emp);
			}
			return "redirect:/findUserList";
		}
		
		//保存角色和权限
		@RequestMapping("/saveRoleAndPermissions")
		public String saveRoleandper(String name,int []permissionIds) {
			SysRole sys=new SysRole();
			sys.setId(UUID.randomUUID().toString());
			sys.setAvailable("1");
			sys.setName(name);
			SysService.addRoleAndPermissions(sys, permissionIds);
			return "redirect:/toAddRole";
			
		}
		//编辑权限
		@RequestMapping("/updateRoleAndPermission")
		public String editorPermission(String roleId,Model model,int []permissionIds) {
			SysService.updateRoleAndPermissions(roleId, permissionIds);
			return "redirect:/showjuese";
		}
		//删除角色
		@RequestMapping("/deleteRole")
		public String deleteRoleandpers(Model model,String roleId) {
			SysService.deleteRoled(roleId);
			return "redirect:/showjuese";
		}
		
		//查看当前流程图
		@RequestMapping("/viewCurrentImage")
		public String showCurrentpri(Model model,String taskId) {
			ProcessDefinition pd = workFlowService.findProcessDefinitionByTaskId(taskId);
	        model.addAttribute("deploymentId", pd.getDeploymentId());
	        model.addAttribute("imageName", pd.getDiagramResourceName());
			/**二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中*/
			Map<String, Object> map = workFlowService.findCoordingByTask(taskId);
//			System.out.println("x: " + map.get("x"));
//			System.out.println("y: " + map.get("y"));
//			System.out.println("width: " + map.get("width"));
//			System.out.println("height: " + map.get("height"));
			model.addAttribute("acs", map);
			return "viewimage";
		}
		@RequestMapping("/viewImage")
		public void dff(String deploymentId, String imageName,ServletResponse response) {	
			InputStream in = workFlowService.findImageInputStream(deploymentId, imageName);
			try {
				  OutputStream out = response.getOutputStream();
				  // 把图片的输入流程写入resp的输出流中
				  byte[] b = new byte[1024];
				  for (int len = -1; (len= in.read(b))!=-1; ) {
					  out.write(b, 0, len);
				  }
				  // 关闭流
				  out.close();
				  in.close();
				} catch (IOException e) {
				  e.printStackTrace();
				}
		}
		@RequestMapping("/viewCurrentImageByBill")
		public String viewCurrentimge(String billId,Model model) {
			ProcessDefinition pd = workFlowService.findProcess(billId);
			model.addAttribute("deploymentId", pd.getDeploymentId());
	        model.addAttribute("imageName", pd.getDiagramResourceName());
	        Task task = workFlowService.findTaskbyBill(billId);
	        Map<String, Object> map = workFlowService.findCoordingByTask(task.getId());
	        model.addAttribute("acs", map);
			return "viewimage";
		}
		
}
