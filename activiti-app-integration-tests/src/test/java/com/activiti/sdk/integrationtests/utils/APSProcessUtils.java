package com.activiti.sdk.integrationtests.utils;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.activiti.sdk.client.ApiException;
import com.activiti.sdk.client.api.ProcessDefinitionsApi;
import com.activiti.sdk.client.api.ProcessInstancesApi;
import com.activiti.sdk.client.model.CreateProcessInstanceRepresentation;
import com.activiti.sdk.client.model.ProcessDefinitionRepresentation;
import com.activiti.sdk.client.model.ProcessInstanceRepresentation;
import com.activiti.sdk.client.model.RestVariable;
import com.activiti.sdk.client.model.ResultListDataRepresentationProcessDefinitionRepresentation;

public class APSProcessUtils {

	private ProcessDefinitionsApi processDefinitionsApi;
	private ProcessInstancesApi processInstancesApi;
	private APSAppUtils appUtils;

	public APSProcessUtils(ProcessDefinitionsApi processDefinitionsApi, ProcessInstancesApi processInstancesApi,
			APSAppUtils appUtils) {
		this.processDefinitionsApi = processDefinitionsApi;
		this.processInstancesApi = processInstancesApi;
		this.appUtils = appUtils;
	}

	public ProcessDefinitionRepresentation getProcessDefinition(Long appDefinitionId) {
		ResultListDataRepresentationProcessDefinitionRepresentation process = null;
		try {
			process = processDefinitionsApi.getProcessDefinitionsUsingGET(Boolean.TRUE, appDefinitionId, null);
		} catch (ApiException e) {
			fail(e.getResponseBody(), e);
		}

		List<ProcessDefinitionRepresentation> processDefs = process.getData();
		Iterator<ProcessDefinitionRepresentation> iteratorProcesses = processDefs.iterator();
		ProcessDefinitionRepresentation currentProcessDefinition = null;
		while (iteratorProcesses.hasNext()) {
			ProcessDefinitionRepresentation processDefinitionRepresentation = (ProcessDefinitionRepresentation) iteratorProcesses
					.next();
			currentProcessDefinition = processDefinitionRepresentation;
		}
		System.out.println("Process Definition Id: " + currentProcessDefinition.getId());
		return currentProcessDefinition;
	}

	public ProcessInstanceRepresentation startProcess(Process process) {
		ProcessInstanceRepresentation currentProcessInstance = null;
		String appName = process.getAppName();
		if (StringUtils.isNotEmpty(appName)) {
			Long appDefId = appUtils.getAppDefinitionId(appName);
			ProcessDefinitionRepresentation processDef = getProcessDefinition(appDefId);
			String processKey = processDef.getKey();
			List<RestVariable> restVars = buildRestVars(process.getFormValues());

			// Creating a new workflow instance
			CreateProcessInstanceRepresentation newProcess = new CreateProcessInstanceRepresentation();
			newProcess.setProcessDefinitionKey(processKey);
			if (process.hasFormValues()) {
				newProcess.setVariables(restVars);
			}

			try {
				currentProcessInstance = processInstancesApi.startNewProcessInstanceUsingPOST(newProcess);
			} catch (ApiException e) {
				fail("Error during creating a new process instance: " + e.getMessage() + e.getResponseBody(), e);
			}

		} else {
			fail("Can't start the process with no appName: " + process);
		}
		return currentProcessInstance;

	}

	private List<RestVariable> buildRestVars(Map<String, Object> formValues) {
		List<RestVariable> restVars = new ArrayList<RestVariable>();
		Iterator<Entry<String, Object>> formValuesIterator = formValues.entrySet().iterator();
		while (formValuesIterator.hasNext()) {
			Map.Entry<String, Object> formValue = (Map.Entry<String, Object>) formValuesIterator.next();
			RestVariable currentFormRestVar = new RestVariable();
			currentFormRestVar.setName(formValue.getKey());
			currentFormRestVar.setValue(formValue.getValue());
			currentFormRestVar.setType("string");
			restVars.add(currentFormRestVar);
		}
		return null;
	}

}
