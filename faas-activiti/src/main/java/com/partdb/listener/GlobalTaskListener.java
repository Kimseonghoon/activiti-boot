package com.partdb.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GlobalTaskListener implements TaskListener {

	private static final long serialVersionUID = 1L;
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void notify(DelegateTask delegateTask) {
		logger.debug("Task Listener : , pid={}, tid={}, event={}", new Object[]{
                delegateTask.getProcessInstanceId(), delegateTask.getId(), delegateTask.getEventName()
        });
	}

}
