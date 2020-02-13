package com.activiti.extension.bean;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.asyncexecutor.AsyncExecutor;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkflowUtils {

	private final Logger log = LoggerFactory.getLogger(WorkflowUtils.class);

	private static final String FORM_PREFIX = "form";
	private static final String OUTCOME_SUFFIX = "outcome";
	public static final String ADMIN_USER_EMAIL = "admin@app.activiti.com";
	public static final String RESULT_OK = "OK";
	public static final String RESULT_NON_ADMIN = "The current user is not the admin user";
	public static final String TENANT_PREFIX = "tenant_";

	@Autowired
	protected ProcessEngine processEngine;

	public static String getTaskOutcomeVariable(Task task) {
		String outcomeVariable = StringUtils.EMPTY;
		if (task != null) {
			return FORM_PREFIX + task.getFormKey() + OUTCOME_SUFFIX;
		}
		return outcomeVariable;
	}

	/**
	 * 
	 * https://github.com/fcorti/aps-unit-test-utils/blob/master/src/main/java/com/alfresco/aps/testutils/UnitTestHelpers.java
	 * 
	 * @param intervalMillis
	 * @return
	 */
	public boolean waitForJobExecutorToProcessAllJobs(long intervalMillis) {
		
		// considering jobAsynch settings (processEngineConfiguration.isAsyncExecutorEnabled())
		AsyncExecutor asyncExecutor = processEngine.getProcessEngineConfiguration().getAsyncExecutor();
		
		log.debug("avvio async asyncExecutor");
		asyncExecutor.start();
		
		boolean areJobsAvailable = true;
		int iterationNumber = 0, MAX_ITERATIONS = 3;
		try {
			while (areJobsAvailable &&  iterationNumber < MAX_ITERATIONS) {
				Thread.sleep(intervalMillis);
				log.debug("eseguo sleep");
				areJobsAvailable = areJobsAvailable();
				log.debug("exist areJobsAvailable? " + areJobsAvailable);
			}
		} catch (InterruptedException e) {
			log.error("errore in waitForJobExecutorToProcessAllJobs", e);
		}

		return areJobsAvailable;
	}

	private boolean areJobsAvailable() {
		return !processEngine.getManagementService().createJobQuery().executable().list().isEmpty();
	}
}
