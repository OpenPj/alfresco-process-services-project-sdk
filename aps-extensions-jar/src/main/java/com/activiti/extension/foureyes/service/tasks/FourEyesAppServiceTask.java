package com.activiti.extension.foureyes.service.tasks;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FourEyesAppServiceTask implements JavaDelegate {

	private final Logger log = LoggerFactory.getLogger(FourEyesAppServiceTask.class);
	
	@Override
	public void execute(DelegateExecution execution) {
		log.debug("Four Eyes App - Hello world from the class delegate");
        execution.setVariable("helloWorld", "Four Eyes App - Hello world from the class delegate");
	}

}
