package com.partdb.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.partdb.entity.base.Applicant;
import com.partdb.repository.base.ApplicantRepository;
import com.partdb.repository.base.ProjectRepository;

@RestController
public class HireProcessRestController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ApplicantRepository applicantRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private IdentityService identityService;
    
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/start-hire-process", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void startHireProcess(@RequestBody Map<String, String> data) {

        Applicant applicant = new Applicant(data.get("name"), data.get("email"), data.get("phoneNumber"));
        applicantRepository.save(applicant);

        Map<String, Object> vars = Collections.<String, Object>singletonMap("applicant", applicant);
        runtimeService.startProcessInstanceByKey("hireProcessWithJpa", vars);
    }
    
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/start-faas-process", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void startFaasProcess() {
    	HashMap<String, Object> variables = new HashMap<String, Object>();
    	variables.put("userId", 123123);
        runtimeService.startProcessInstanceByKey("myProcess", variables);
    }
    
    @RequestMapping(value = "/start-project-process", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void startProjectSaveProcess(@RequestBody Map<String, String> data) {
    	
    	Applicant applicant = new Applicant(data.get("name"), data.get("email"), data.get("phoneNumber"));
    	applicantRepository.save(applicant);
    	
    	Map<String, Object> variables = new HashMap<String, Object>();
    	    	
    	variables.put("userId", "1");
    	
    	ProcessInstance pi = runtimeService.startProcessInstanceByKey("myProcess", variables);
    	variables.put("applicant", applicant);
    	variables.put("xxx", "1234");
    	runtimeService.setVariablesLocal(pi.getId(), variables);
    }
    
    @RequestMapping(value = "/start-project-process2", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void startProjectSaveProcess2(@RequestBody Map<String, String> data) {
    	
    	Applicant applicant = new Applicant(data.get("name"), data.get("email"), data.get("phoneNumber"));
    	applicantRepository.save(applicant);
    			
    	Map<String, Object> variables = new HashMap<String, Object>();
    	Map<String, Object> variables2 = new HashMap<String, Object>();
    	
    	variables.put("groupId", "11");
    	
    	Task task = taskService.createTaskQuery().processInstanceId(data.get("pid")).singleResult();
    	
    	variables2.put("applicant", applicant);    	
    	variables2.put("xxx", "5678");
    	taskService.setVariablesLocal(task.getId(), variables2);
    	
    	taskService.complete(task.getId(), variables);
    }
    
}