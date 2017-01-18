package com.partdb.event;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import com.partdb.service.ProjectService;

public class ActivitiDelegateListener implements JavaDelegate {
	
	@Autowired
	ProjectService projectService;
	

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> variables = execution.getVariables();
		
		System.out.println(variables);
	}

}
