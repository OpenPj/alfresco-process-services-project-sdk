package org.alfresco.activiti.utils;

import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class WorkflowUtils {
		
	private static final String FORM_PREFIX = "form";
	private static final String OUTCOME_SUFFIX = "outcome";
	public static final String ADMIN_USER_EMAIL = "admin@app.activiti.com";
	public static final String RESULT_OK = "OK";
	public static final String RESULT_NON_ADMIN = "The current user is not the admin user";
	public static final String TENANT_PREFIX = "tenant_";
	
	public static String getTaskOutcomeVariable(Task task) {
		String outcomeVariable = StringUtils.EMPTY;
		if(task != null) {
			return FORM_PREFIX + task.getFormKey() + OUTCOME_SUFFIX;
		}
		return outcomeVariable;
	}
	
}
