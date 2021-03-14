package com.shop.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import com.shop.pojo.Baoxiaobill;
import com.shop.pojo.Leavebill;

public interface WorkFlowService {

	//部署流程
	void  saveNewDeploy(InputStream in,String fileName);
	
	//查询出定义流程
	List<ProcessDefinition> findProcessDefinitionList();
	//查询部署
	List<Deployment> findDeployment();
	// 启动流程
	void saveStartLeave(Long leaveId, String name);
	//删除流程部署
	void deleteDeployment(String deploymentId);
	
	void saveStartBaoxiao(Long leaveId, String name);
	
	List<Task> findTaskListByName(String name);
    //查看流程定义图
	InputStream findView(String deploymentId,String imageName);

	Leavebill findLeaveBillByTaskId(String taskId);

	Baoxiaobill findBaoxiaoBillByTaskId(String taskId);

	List<Comment> findCommentListByTaskId(String taskId);


	void submitTask(String id, String taskId, String comment, String username);



	Map<String, Object> findCoordingByTask(String taskId);

	InputStream findImageInputStream(String deploymentId, String imageName);

	ProcessDefinition findProcessDefinitionByTaskId(String taskId);

	List<Baoxiaobill> findBaoxiaoListById(long id);
	
	void submitBaoxiaoTask(String id, String taskId, String comment,String outcome, String username);
	
	List<Comment> findCommentListById(String id);
	
	ProcessDefinition findProcess(String baoxioaId);
	
	Task findTaskbyBill(String billId);
	
	Baoxiaobill findBaoxiaoListBykey(long id);

	List<String> findOutComeListByTaskId(String taskId);
}
