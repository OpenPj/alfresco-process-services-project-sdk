package com.activiti.sdk.integrationtests.utils;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.activiti.sdk.client.model.ProcessInstanceRepresentation;
import com.activiti.sdk.client.model.TaskRepresentation;

import org.apache.commons.lang3.StringUtils;

public class ProcessTester {

	private String appName = StringUtils.EMPTY;
	private String initiator = StringUtils.EMPTY;
	private String assignee = StringUtils.EMPTY;

	protected APSTester apsTester = null;
	protected Long currentAppDefId = null;
	protected ProcessInstanceRepresentation currentProcessInstance = null;
	
	private Map<String, Object> formValues = new HashMap<String, Object>();
		
	public ProcessTester(APSTester apsTester, String appName) {
		this.apsTester = apsTester;
		this.appName = appName;
	}
	
	public ProcessTester withInitiator(String initiator) {
		this.initiator = initiator;
		return this;
	}
	
	public ProcessTester withAssignee(String assignee) {
		this.assignee = assignee;
		return this;
	}
	
	public ProcessTester withFormValue(String id, String value) {
		formValues.put(id, value);
		return this;
	}
	
	public ProcessTester hasInitiator() {
		if (StringUtils.isEmpty(this.initiator)) {
			fail("Process does not have an initiator defined.");
		}
		return this;
	}
	
	public ProcessTester hasAssignee() {
		if (StringUtils.isEmpty(this.assignee)) {
			fail("Process does not have an assignee defined.");
		}
		return this;
	}
	
	boolean hasFormValues() {
		if(this.formValues.size()>0) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	public ProcessTester withAdminAsInitiatorAndAssignee() {
		this.initiator = APSTester.ADMIN_USER;
		this.assignee = APSTester.ADMIN_USER;
		return this;
	}
	
	public ProcessTester withAdminAsInitiator() {
		this.initiator = APSTester.ADMIN_USER;
		return this;
	}
	
	public ProcessTester withAdminAsAssignee() {
		this.assignee = APSTester.ADMIN_USER;
		return this;
	}

	String getAppName() {
		return appName;
	}

	/*
	public String getInitiator() {
		return initiator;
	}

	public String getAssignee() {
		return assignee;
	}
	*/

	Map<String, Object> getFormValues() {
		return formValues;
	}

	public TaskTester thenSubmitTask(String taskName, String outcome) {
		currentProcessInstance = apsTester.processUtils.startProcess(this);
		currentAppDefId = apsTester.appUtils.getAppDefinitionId(this.appName);
		return new TaskTester(this, taskName, outcome);
	}

	/*
	public ProcessInstanceRepresentation getCurrentProcessInstace() {
		return this.currentProcessInstance;
	}

	public TaskRepresentation getCurrentTask() {
		return apsTester.taskUtils.findTaskWithProcessInstanceId(currentProcessInstance.getId(), currentAppDefId).get().get(0);
	}

	public List<TaskRepresentation> getCurrentTasks() {
		return apsTester.taskUtils.findTaskWithProcessInstanceId(currentProcessInstance.getId(), currentAppDefId).get();
	}
	*/

	public ProcessTester thenCheckThatTheProcessIsFinished() {
		Optional<List<TaskRepresentation>> tasks = apsTester.taskUtils
				.findTaskWithProcessInstanceId(currentProcessInstance.getId(), currentAppDefId);
		if (tasks.isPresent()) {
			List<TaskRepresentation> currentTasks = tasks.get();
			if (currentTasks.size() > 0) {
				fail("The process is not finished, tasks active: " + currentTasks);
			}
		}
		return this;
	}


	@Override
	public String toString() {
		return "StartProcess [appName=" + appName + ", initiator=" + initiator + ", assignee=" + assignee
				+ ", formValues=" + formValues + "]";
	}
	
}
