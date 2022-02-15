package com.activiti.sdk.integrationtests.utils;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class TaskTester {

	private String taskName = StringUtils.EMPTY;
	private String outcome = StringUtils.EMPTY;
	private String assignee = StringUtils.EMPTY;

	protected ProcessTester processTester = null;
	
	private Map<String, Object> formValues = new HashMap<String, Object>();

	public TaskTester(ProcessTester processTester, String taskName, String outcome) {
		this.processTester = processTester;
		this.taskName = taskName;
		this.outcome = outcome;
	}
	
	public TaskTester withAssignee(String assignee) {
		this.assignee = assignee;
		return this;
	}
	
	public TaskTester withFormValue(String id, String value) {
		formValues.put(id, value);
		return this;
	}
	
	public TaskTester hasAssignee() {
		if (StringUtils.isEmpty(this.assignee)) {
			fail("Process does not have an assignee defined.");
		}
		return this;
	}
	
	public TaskTester withAdminAsAssignee() {
		this.assignee = APSTester.ADMIN_USER;
		return this;
	}

	/*
	public String getAssignee() {
		return assignee;
	}
	*/
	
	boolean hasFormValues() {
		if(this.formValues.size()>0) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	Map<String, Object> getFormValues() {
		return formValues;
	}

	String getName() {
		return taskName;
	}

	String getOutcome() {
		return outcome;
	}

	public TaskTester thenSubmitTask(String taskName, String outcome) {
		processTester.apsTester.thenSubmitTask(this);
		return new TaskTester(processTester, taskName, outcome);
	}

	// NOTE: return ProcessTester in case there are other check methods
	// we'd like to call at this point. If there were task checks they
	// would be called before this and would return a TaskTester.
	public ProcessTester thenCheckThatTheProcessIsFinished() {
		processTester.apsTester.thenSubmitTask(this);
		return processTester.thenCheckThatTheProcessIsFinished();
	}

	@Override
	public String toString() {
		return "Task [name=" + taskName + ", outcome=" + outcome + ", assignee=" + assignee + ", formValues=" + formValues
				+ "]";
	}
	
}
