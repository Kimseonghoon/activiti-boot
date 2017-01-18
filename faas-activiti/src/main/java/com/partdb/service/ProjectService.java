package com.partdb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.IdentityLinkType;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.partdb.entity.base.Project;
import com.partdb.repository.base.ProjectRepository;

@Service
public class ProjectService {
	@Autowired
	PersonService personService;
	
	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	RuntimeService runtimeService;

	@Autowired
	TaskService taskService;

	@Autowired
	ProcessEngine processEngine;
		
	@Autowired
	IdentityService identityService;

    @Autowired
    RepositoryService repositoryService;
    
	// @Transactional("transactionManager")
	public int createProject(String userId, Project project) {

		ProcessInstance processInstance = null;
		
		try {
			
			identityService.setAuthenticatedUserId(userId);
			processInstance = runtimeService.startProcessInstanceByKey("FaaSProcess");
			
		} finally {
			identityService.setAuthenticatedUserId(null);
		}
		
		Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
		
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("userId", userId);

		//taskService.setOwner(task.getId(), userId);
		taskService.setVariablesLocal(task.getId(), variables);
		taskService.complete(task.getId());

		
		task = taskService.createTaskQuery()
			.processInstanceId(processInstance.getId()).singleResult();
		
		HashMap<String, Object> variables2 = new HashMap<String, Object>(); 
		projectRepository.save(project); 
		variables2.put("projectId", project);
		
		//taskService.setOwner(task.getId(), userId);
		taskService.setAssignee(task.getId(), "project-engineer@faas.com");
		taskService.setVariableLocal(task.getId(), "projectId", variables2);
		taskService.setVariables(task.getId(), variables2);
		taskService.setVariablesLocal(task.getId(), variables2);
		///////////////////////////////////////

		/*
		 * task = taskService.createTaskQuery()
		 * .processInstanceId(processInstance.getId()).singleResult();
		 * 
		 * taskService.complete(task.getId());
		 * 
		 * projectRepository.save(project); HashMap<String, Object> variables2 =
		 * new HashMap<String, Object>(); variables2.put("projectId", project);
		 * variables2.put("projectEngineerId", "project-engineer@faas.com");
		 * taskService.setVariablesLocal(task.getId(), variables2);
		 */

		return project.getIdx();
	}

	public void taskComplete(String taskId, String assignee) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		//String owner = task.getOwner();
		String processInstanceId = task.getProcessInstanceId();
		Map<String,Object> variables = new HashMap<String, Object>();
		Project project = taskService.getVariable(taskId, "projectId", Project.class);
		variables.put("projectId", project);
		
		taskService.complete(taskId);
		
		task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
		//taskService.setOwner(task.getId(), owner);
		taskService.setAssignee(task.getId(), assignee);
		taskService.setVariableLocal(task.getId(), "projectId", project);
		
	}
	
	public IdentityLink getIdentityLInkByTaskId(String taskId) {
		
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		List<IdentityLink> ils = runtimeService.getIdentityLinksForProcessInstance(task.getProcessInstanceId());
		
		for(IdentityLink il : ils) {
			System.out.println(il.getType());
			if(il.getType().equalsIgnoreCase("starter" )) {
				return il;
			}
		}
		return null;
	}
	
	public Execution getExecutionBytaskId(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		
		return runtimeService.createExecutionQuery().executionId(task.getProcessInstanceId()).singleResult();
	}
	
	public Map<String,Object> projectListByTaskId(String taskId) {
	
		System.out.println(getExecutionBytaskId(taskId).getActivityId());

		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(taskService.createTaskQuery().taskId(taskId).singleResult().getProcessDefinitionId());
        List<ActivityImpl> activitiList = processDefinition.getActivities();//获得当前任务的所有节点
		
		
		
		
		
		Map<String,Object> map = new HashMap<String, Object>();

		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
				
		Project project = taskService.getVariable(taskId, "projectId", Project.class);
		
		project.setState(task.getName());
		
		map.put("project", project);
		map.put("task", task);
		
		return map;
	}
	
	public List<Map<String,Object>> projectList(String userId) {
		String role = personService.findByEmail(userId).getRole();
		if(role.equalsIgnoreCase("user")) {
			List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
			
			
			
			List<Project> projects = new ArrayList<Project>();

			List<Task> tasks = taskService.createTaskQuery().taskOwner(userId).list();
			
			for(Task task : tasks) {			
				
				Map<String,Object> hm = new HashMap<String, Object>();
				
				Project project = taskService.getVariable(task.getId(), "projectId", Project.class);
				project.setState(task.getName());
				
				System.out.println(task.getName() + " , " + task.getId() + " , " + project.getName() + " , " + task.getOwner());
				
				
				hm.put("project", project);
				hm.put("task", task);
				
				mapList.add(hm);
				
			}
			
			return mapList;
		} else if(role.equalsIgnoreCase("expert")) {
			
			
			
			/*		
					List<Task> tasks = taskService.createTaskQuery().taskOwner(userId).list();
					
					for(Task task : tasks) {
						Map<String,Object> maps = task.getTaskLocalVariables();
						System.out.println(maps.get("projectId"));
					}
					*/
					List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
					
					
					
					List<Project> projects = new ArrayList<Project>();

					List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).list();
					
					for(Task task : tasks) {			
						
						Map<String,Object> hm = new HashMap<String, Object>();
						
						Project project = taskService.getVariable(task.getId(), "projectId", Project.class);
						project.setState(task.getName());
						
						System.out.println(task.getName() + " , " + task.getId() + " , " + project.getName() + " , " + task.getOwner());
						
						
						hm.put("project", project);
						hm.put("task", task);
						
						mapList.add(hm);
						
					}
					
					return mapList;
					
					
					
			/*		List<Project> projects = new ArrayList<Project>();
					List<Execution> executions = runtimeService.createExecutionQuery().processDefinitionKey("FaaSProcess").list();

					for (Execution ex : executions) {
						System.out.println(ex.getProcessInstanceId());
					}

					List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
					for (Task task : tasks) {
						// Map<String,Object> variables = task.getTaskLocalVariables();

						Map<String, Object> vs = runtimeService.getVariables(task.getProcessInstanceId());

						Project project = (Project) vs.get("userId");
						if (project != null) {
							// projects.add(project);
						}
					}

					return projects;*/
		} else {
			return null;
		}
		

	}

}
