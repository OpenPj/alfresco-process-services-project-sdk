package com.activiti.extension.foureyes.listeners;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class FourEyesAppLoggingListener implements ExecutionListener {

	private final Logger logger = LoggerFactory.getLogger(FourEyesAppLoggingListener.class);

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		
		//Initiator
		String initiator = StringUtils.EMPTY;
		Object initiatorObject = execution.getVariable("initiator");
		
		if(initiatorObject instanceof String) {
			initiator = (String) initiatorObject;
		} else {
			Long initiatorLong = (Long)initiatorObject;
			initiator = String.valueOf(initiatorLong);
		}
		
		
		logger.info("Four Eyes - Initiator of the process has user ID = " + initiator);
		
		//Process variables 
		logger.info("--- Process variables:");
		Map<String, Object> procVars = execution.getVariables();
		for (Map.Entry<String, Object> procVar : procVars.entrySet()) {
		 logger.info(" [" + procVar.getKey() + " = " + procVar.getValue() + "]");
		}
	}

}