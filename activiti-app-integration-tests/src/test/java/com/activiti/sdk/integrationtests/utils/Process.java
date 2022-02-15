package com.activiti.sdk.integrationtests.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Process {

	private String appName = StringUtils.EMPTY;
	private String initiator = StringUtils.EMPTY;
	private String assignee = StringUtils.EMPTY;
	
	private Map<String, Object> formValues = new HashMap<String, Object>();
		
	public Process start(String appName) {
		this.appName = appName;
		return this;
	}
	
	public Process withInitiator(String initiator) {
		this.initiator = initiator;
		return this;
	}
	
	public Process withAssignee(String assignee) {
		this.assignee = assignee;
		return this;
	}
	
	public Process withFormValue(String id, String value) {
		formValues.put(id, value);
		return this;
	}
	
	public boolean hasInitiator() {
		if(StringUtils.isNotEmpty(this.initiator)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
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
	
	public void withAdminAsInitiatorAndAssignee() {
		this.initiator = APSTester.ADMIN_USER;
		this.assignee = APSTester.ADMIN_USER;
	}
	
	public void withAdminAsInitiator() {
		this.initiator = APSTester.ADMIN_USER;
	}
	
	public void withAdminAsAssignee() {
		this.assignee = APSTester.ADMIN_USER;
	}

	public String getAppName() {
		return appName;
	}

	public String getInitiator() {
		return initiator;
	}

	public String getAssignee() {
		return assignee;
	}

	public Map<String, Object> getFormValues() {
		return formValues;
	}

	@Override
	public String toString() {
		return "StartProcess [appName=" + appName + ", initiator=" + initiator + ", assignee=" + assignee
				+ ", formValues=" + formValues + "]";
	}
	
	
	
}
