package com.activiti.sdk.integrationtests.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Task {

	private String name = StringUtils.EMPTY;
	private String outcome = StringUtils.EMPTY;
	private String assignee = StringUtils.EMPTY;
	
	private Map<String, Object> formValues = new HashMap<String, Object>();
	
	public Task name(String name) {
		this.name = name;
		return this;
	}
	
	public Task outcome(String outcome) {
		this.outcome = outcome;
		return this;
	}
	
	public Task withAssignee(String assignee) {
		this.assignee = assignee;
		return this;
	}
	
	public Task withFormValue(String id, String value) {
		formValues.put(id, value);
		return this;
	}
	
	public boolean hasAssignee() {
		if(StringUtils.isNotEmpty(this.assignee)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	public boolean hasFormValues() {
		if(this.formValues.size()>0) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	public void withAdminAsAssignee() {
		this.assignee = APSTester.ADMIN_USER;
	}

	public String getAssignee() {
		return assignee;
	}

	public Map<String, Object> getFormValues() {
		return formValues;
	}

	public String getName() {
		return name;
	}

	public String getOutcome() {
		return outcome;
	}

	@Override
	public String toString() {
		return "Task [name=" + name + ", outcome=" + outcome + ", assignee=" + assignee + ", formValues=" + formValues
				+ "]";
	}
	
	

}
