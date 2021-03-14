package com.shop.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;
import org.apache.commons.lang.StringUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.mapper.BaoxiaobillMapper;
import com.shop.mapper.LeavebillMapper;
import com.shop.pojo.Baoxiaobill;
import com.shop.pojo.BaoxiaobillExample;
import com.shop.pojo.Leavebill;
import com.shop.service.WorkFlowService;
import com.shop.utils.Constants;

@Service("workFlowService")
public class WorkFlowServiceImpl implements WorkFlowService {
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private LeavebillMapper leavebillMapper;
	@Autowired
	private BaoxiaobillMapper baoxiaobillMapper;
	/**
	 * 部署流程定义
	 */

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	@Override
	public void saveNewDeploy(InputStream in, String fileName) {
		try {
			ZipInputStream zip = new ZipInputStream(in);
			repositoryService.createDeployment()// 创建部署对象
					.name(fileName) // 添加部署名称
					.addZipInputStream(zip).deploy();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
   //查看流程定义
	@Override
	public List<ProcessDefinition> findProcessDefinitionList() {
		List<ProcessDefinition> list = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
                .createProcessDefinitionQuery()//创建一个流程定义查询
                /*指定查询条件,where条件*/
                //.deploymentId(deploymentId)//使用部署对象ID查询
                //.processDefinitionId(processDefinitionId)//使用流程定义ID查询
                //.processDefinitionKey(processDefinitionKey)//使用流程定义的KEY查询
                //.processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询
                
                /*排序*/
                .orderByProcessDefinitionVersion().asc()//按照版本的升序排列
                //.orderByProcessDefinitionName().desc()//按照流程定义的名称降序排列
                
                .list();//返回一个集合列表，封装流程定义
                //.singleResult();//返回唯一结果集
                //.count();//返回结果集数量
                //.listPage(firstResult, maxResults)//分页查询
		return list;
	}

	@Override
	public void saveStartLeave(Long leaveId, String name) {
		// 获取到要执行的key
		String key = Constants.Leave_KEY;
		/*
		 * 从session中获取到的当前任务待办人,使用流程变量设置下一个任务的待办人
		 */
		Map<String, Object> map = new HashMap<>();
		map.put("userName", name);
		// 设置Bussiness_key的规则
		String BUSSINESS_KEY = key + "." + leaveId;

		map.put("objId", BUSSINESS_KEY);

		System.out.println("BUSSINESS_KEY=============================" + BUSSINESS_KEY);

		this.runtimeService.startProcessInstanceByKey(key, BUSSINESS_KEY, map);

	}

	@Override
	public List<Task> findTaskListByName(String name) {
		List<Task> list = taskService.createTaskQuery().taskAssignee(name).orderByTaskCreateTime().asc().list();
		return list;
	}

	@Override
	public Leavebill findLeaveBillByTaskId(String taskId) {
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		// 然后根据流程实例ID 在获取到ProcessInstance
		// act_hi_procinst
		ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();

		// 再从processInstance 获取Business_key+"."+leaveId
		String businessKey = processInstance.getBusinessKey();
		String leaveId = "";
		if (businessKey != null && !"".equals(businessKey)) {
			leaveId = businessKey.split("\\.")[1];
		}
		// 根据leaveid去查询出请假单信息
		Leavebill leavebill = leavebillMapper.selectByPrimaryKey(Long.parseLong(leaveId));
		return leavebill;
	}

	@Override
	public List<Comment> findCommentListByTaskId(String taskId) {
		// 获取到任务对象
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		// 然后根据流程实例ID 在获取到ProcessInstancId
		String processInstanceId = task.getProcessInstanceId();
		return taskService.getProcessInstanceComments(processInstanceId);
	}

	@Override
	public void submitTask(String id, String taskId, String comment, String username) {
		// 获取到任务对象
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		// 然后根据流程实例ID 在获取到ProcessInstancId
		String processInstanceId = task.getProcessInstanceId();
		// 设置批注人
		Authentication.setAuthenticatedUserId(username);
		// 添加批注
		this.taskService.addComment(taskId, processInstanceId, comment);
		// 完成任务
		this.taskService.complete(taskId);

		// 修改请假单的状态
		ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery()
											.processInstanceId(task.getProcessInstanceId()).singleResult();
		if(processInstance==null){
			//更新状态
			Leavebill leavebill = leavebillMapper.selectByPrimaryKey(Long.parseLong(id));
			leavebill.setState(2);
			//重新更新数据库
			this.leavebillMapper.updateByPrimaryKey(leavebill);
		}

	}

	@Override
	public Map<String, Object> findCoordingByTask(String taskId) {
		//存放坐标
				Map<String, Object> map = new HashMap<String,Object>();
				//使用任务ID，查询任务对象
				Task task = taskService.createTaskQuery()//
							.taskId(taskId)//使用任务ID查询
							.singleResult();
				//获取流程定义的ID
				String processDefinitionId = task.getProcessDefinitionId();
				//获取流程定义的实体对象（对应.bpmn文件中的数据）
				ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);
				//流程实例ID
				String processInstanceId = task.getProcessInstanceId();
				//使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
				ProcessInstance pi = runtimeService.createProcessInstanceQuery()//创建流程实例查询
													.processInstanceId(processInstanceId)//使用流程实例ID查询
													.singleResult();
				//获取当前活动的ID
				String activityId = pi.getActivityId();
				//获取当前活动对象
				ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);//活动ID
				//获取坐标
				map.put("x", activityImpl.getX());
				map.put("y", activityImpl.getY());
				map.put("width", activityImpl.getWidth());
				map.put("height", activityImpl.getHeight());
				return map;
	}

	@Override
	public InputStream findImageInputStream(String deploymentId, String imageName) {
		// TODO Auto-generated method stub
		return this.repositoryService.getResourceAsStream(deploymentId, imageName);
	}

	@Override
	public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
		  //使用任务ID，查询任务对象
        Task task = taskService.createTaskQuery()//
                    .taskId(taskId)//使用任务ID查询
                    .singleResult();
        //获取流程定义ID
        String processDefinitionId = task.getProcessDefinitionId();
        //查询流程定义的对象
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()//创建流程定义查询对象，对应表act_re_procdef 
                    .processDefinitionId(processDefinitionId)//使用流程定义ID查询
                    .singleResult();
        return pd;
	}

	@Override
	public Baoxiaobill findBaoxiaoBillByTaskId(String taskId) {
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		// 然后根据流程实例ID 在获取到ProcessInstance
		// act_hi_procinst
		ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();

		// 再从processInstance 获取Business_key+"."+leaveId
		String businessKey = processInstance.getBusinessKey();
		String leaveId = "";
		if (businessKey != null && !"".equals(businessKey)) {
			leaveId = businessKey.split("\\.")[1];
		}
		System.out.println(leaveId);
		// 根据leaveid去查询出请假单信息
		Baoxiaobill baoxiaobill = baoxiaobillMapper.selectByPrimaryKey(Long.parseLong(leaveId));
		return baoxiaobill;
	}

	@Override
	public void saveStartBaoxiao(Long leaveId, String name) {
		// 获取到要执行的key
				String key = Constants.Baoxiao_KEY;
				/*
				 * 从session中获取到的当前任务待办人,使用流程变量设置下一个任务的待办人
				 */
				Map<String, Object> map = new HashMap<>();
				map.put("inputUser", name);
				// 设置Bussiness_key的规则
				String BUSSINESS_KEY = key + "." + leaveId;

				map.put("objId", BUSSINESS_KEY);

				System.out.println("BUSSINESS_KEY=============================" + BUSSINESS_KEY);

				this.runtimeService.startProcessInstanceByKey(key, BUSSINESS_KEY, map);
		
	}

	@Override
	public List<Baoxiaobill>  findBaoxiaoListById(long id) {
		BaoxiaobillExample ue=new BaoxiaobillExample();
		BaoxiaobillExample.Criteria cri=ue.createCriteria();
		cri.andUserIdEqualTo((int) id);
		List<Baoxiaobill> list=baoxiaobillMapper.selectByExample(ue);
		if(list.size()>0) {
		return list;
		}
		return null;
	}
	@Override
	public List<Deployment> findDeployment() {
		 ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
         List<Deployment> dList = processEngine.getRepositoryService()
         .createDeploymentQuery()//创建一个部署查询
         .list();
		return dList;
	}
    public InputStream findView(String deploymentId,String imageName) {
    	RepositoryService repositoryService = processEngine.getRepositoryService();
    	InputStream in=repositoryService.getResourceAsStream(deploymentId,imageName);
    	
		return in;
    	
    }
	@Override
	public void deleteDeployment(String deploymentId) {
		processEngine.getRepositoryService().deleteDeployment(deploymentId, true);//强制删除
		
	}
	@Override
	public void submitBaoxiaoTask(String id, String taskId, String comment, String outcome, String username) {
				Map<String,Object> map=new HashMap<>();
				map.put("message", outcome);
				
		        // 获取到任务对象
				
				String pizhu=comment+outcome;
				
				Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
				// 然后根据流程实例ID 在获取到ProcessInstancId
				String processInstanceId = task.getProcessInstanceId();
				// 设置批注人
				Authentication.setAuthenticatedUserId(username);
				// 添加批注
				this.taskService.addComment(taskId, processInstanceId, pizhu);
				// 完成任务
				this.taskService.complete(taskId,map);

				// 修改请假单的状态
				ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery()
													.processInstanceId(task.getProcessInstanceId()).singleResult();
				if(processInstance==null){
					//更新状态
					 Baoxiaobill baoxiaobill = baoxiaobillMapper.selectByPrimaryKey(Long.parseLong(id));
					 baoxiaobill.setState(2);
					//重新更新数据库
					this.baoxiaobillMapper.updateByPrimaryKey(baoxiaobill);
				}
		
	}
	@Override
	public List<Comment> findCommentListById(String id) {
		String key = Constants.Baoxiao_KEY;
		// 设置Bussiness_key的规则
		String BUSSINESS_KEY = key + "." + id;
		HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
				.processInstanceBusinessKey(BUSSINESS_KEY).orderByHistoricTaskInstanceStartTime().desc().list().get(0);
		List<Comment> list = taskService.getProcessInstanceComments(historicTaskInstance.getProcessInstanceId());
		return list;
	}
	@Override
	public ProcessDefinition findProcess(String baoxioaId) {
        String bussinessKey = Constants.Baoxiao_KEY + "." + baoxioaId; // baoxiao.13
		
		Task task = taskService.createTaskQuery().processInstanceBusinessKey(bussinessKey).singleResult();
		
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
								.processDefinitionId(task.getProcessDefinitionId()).singleResult();
		

		return pd;
	}
	@Override
	public Task findTaskbyBill(String billId) {
		String bussinessKey = Constants.Baoxiao_KEY + "." + billId; // baoxiao.13
		
		Task task = taskService.createTaskQuery().processInstanceBusinessKey(bussinessKey).singleResult();

		return task;
	}
	@Override
	public Baoxiaobill findBaoxiaoListBykey(long id) {
		Baoxiaobill selectByPrimaryKey = baoxiaobillMapper.selectByPrimaryKey(id);
		return selectByPrimaryKey;
	}
	@Override
	public List<String> findOutComeListByTaskId(String taskId) {
		// TODO Auto-generated method stub
		// 返回存放连线的名称集合
				List<String> list = new ArrayList<String>();
				// 1:使用任务ID，查询任务对象
				Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
				// 2:获取流程定义ID
				String processDefinitionId = task.getProcessDefinitionId();
				// 3:查询ProcessDefinitionEntity对象
				ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
						.getProcessDefinition(processDefinitionId);

				// 获取流程实例ID
				String processInstanceId = task.getProcessInstanceId();
				// 获取流程实例
				ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
						.singleResult();
				// 获取当前活动ID
				String activityId = pi.getActivityId();

				// 4：获取当前的活动
				ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);

				// 5:获取当前活动完成之后连线的名称
				List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
				if (pvmList != null && pvmList.size() > 0) {
					for (PvmTransition pvm : pvmList) {
						String name = (String) pvm.getProperty("name");
						if (StringUtils.isNotBlank(name)) {
							list.add(name);
						} else {
							list.add("提交");
						}
					}
				}
				return list;

	}
	
}
