package com.partdb.event;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

@Component
public class MyDelegate implements TaskListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegateTask) {
		//delegateTask.setAssignee("ksh85@partdb.com");
		//delegateTask.setOwner("ksh85@partdb.com");
		//delegateTask.addCandidateUser("ksh85@partdb.com");
		
		//delegateTask.setOwner(delegateTask.getVariable("userId").toString());
		//delegateTask.addCandidateGroup("experts");
		//System.out.println("DelegateTask" + delegateTask.getId());
	}

}
