package com.partdb.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.transaction.TransactionManager;
import javax.transaction.Transactional;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.partdb.entity.base.Project;
import com.partdb.service.ProjectService;

@Controller
@RequestMapping("/project")
public class ActivitiProjectController {
	
	@Autowired
	ProjectService projectService;
	
	@Autowired
	HistoryService historyService;
	
	@RequestMapping(value="/list", method = RequestMethod.GET) 
	public ModelAndView list(Authentication auth) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("name", auth.getName());
		
		mav.addObject("projects", projectService.projectList(auth.getName()));
		
		mav.setViewName("project/project-list");
		
		return mav;
	}
		
	@RequestMapping(value="/create", method = RequestMethod.GET) 
	public ModelAndView create(Authentication auth) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("name", auth.getName());
		mav.setViewName("project/project-create");
		
		return mav;
	}
	
	@RequestMapping(value="/create", method = RequestMethod.POST) 
	public ModelAndView create(Project project, Authentication auth) {
		
		projectService.createProject(auth.getName(), project);
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("name", auth.getName());
		mav.addObject("projects", projectService.projectList(auth.getName()));
		
		mav.setViewName("redirect:/project/list");
		
		return mav;
	}
	
	@RequestMapping(value="/allocate/{taskId}", method = RequestMethod.POST)
	public ModelAndView allocateModelEngineer(@PathVariable String taskId, @RequestParam("assignee") String assignee) {
		ModelAndView mav = new ModelAndView();
		
		projectService.taskComplete(taskId, assignee);
		
		mav.setViewName("redirect:/project/list");
		return mav;
		
	}
	
	@RequestMapping(value="/detail/{taskId}", method = RequestMethod.GET)
	public ModelAndView modelEngineer(@PathVariable String taskId, Authentication auth) {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("name", auth.getName());
		mav.addAllObjects(projectService.projectListByTaskId(taskId));
		mav.setViewName("project/project-detail");
		return mav;
		
	}
	
	@RequestMapping(value="/history/{processId}", method = RequestMethod.GET)
	@ResponseBody public List<HistoricActivityInstance> history(@PathVariable("processId") String processId) {
		return historyService.createHistoricActivityInstanceQuery()
    			.finished().processInstanceId(processId)
    			.orderByHistoricActivityInstanceStartTime().asc().list();
	}
	
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public void upload(MultipartHttpServletRequest request, Project project) {
		System.out.println("upload");
        Iterator<String> itr =  request.getFileNames();
        if(itr.hasNext()) {
            MultipartFile mpf = request.getFile(itr.next());
            System.out.println(mpf.getOriginalFilename() +" uploaded!");
            try {
            	System.out.println("file length : " + mpf.getBytes().length);
                System.out.println("file name : " + mpf.getOriginalFilename());
                
                // Get the filename and build the local file path (be sure that the 
                // application have write permissions on such directory)
                String filename = mpf.getOriginalFilename();
                String directory = "d:/";
                String filepath = Paths.get(directory, filename).toString();
                
                // Save the file locally
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
                stream.write(mpf.getBytes());
                stream.close();
                
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        } else {
        	System.out.println("false");
        }
	}
}
