package com.activiti.sdk.integrationtests.utils;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.activiti.sdk.client.ApiException;
import com.activiti.sdk.client.api.TaskFormsApi;
import com.activiti.sdk.client.api.TasksApi;
import com.activiti.sdk.client.model.CompleteFormRepresentation;
import com.activiti.sdk.client.model.ResultListDataRepresentationTaskRepresentation;
import com.activiti.sdk.client.model.TaskQueryRepresentation;
import com.activiti.sdk.client.model.TaskRepresentation;

public class APSTaskUtils {

	protected TasksApi tasksApi;
	protected TaskFormsApi taskFormsApi;

	public APSTaskUtils(TasksApi tasksApi, TaskFormsApi taskFormsApi) {
		this.tasksApi = tasksApi;
		this.taskFormsApi = taskFormsApi;
	}

	public Optional<TaskRepresentation> findTaskWithName(String processInstanceId, Long appDefId, String taskName) {
		// Getting the First Approval Task Id
		TaskQueryRepresentation taskQuery = new TaskQueryRepresentation();
		taskQuery.appDefinitionId(appDefId);
		taskQuery.processInstanceId(processInstanceId);

		ResultListDataRepresentationTaskRepresentation tasks = null;
		try {
			tasks = tasksApi.listTasksUsingPOST(taskQuery);
		} catch (ApiException e) {
			fail(e.getMessage() + " " + e.getResponseBody(), e);
		}
		List<TaskRepresentation> currentTasks = tasks.getData();
		Iterator<TaskRepresentation> iteratorTasks = currentTasks.iterator();
		TaskRepresentation taskFound = null;
		while (iteratorTasks.hasNext()) {
			TaskRepresentation taskRepresentation = (TaskRepresentation) iteratorTasks.next();
			if (StringUtils.equals(taskName, taskRepresentation.getName())) {
				taskFound = taskRepresentation;
				break;
			}
		}
		return Optional.of(taskFound);
	}
	
	public Optional<List<TaskRepresentation>> findTaskWithProcessInstanceId(String processInstanceId, Long appDefId) {
		// Getting the First Approval Task Id
		TaskQueryRepresentation taskQuery = new TaskQueryRepresentation();
		taskQuery.appDefinitionId(appDefId);
		taskQuery.processInstanceId(processInstanceId);

		ResultListDataRepresentationTaskRepresentation tasks = null;
		try {
			tasks = tasksApi.listTasksUsingPOST(taskQuery);
		} catch (ApiException e) {
			fail(e.getMessage() + " " + e.getResponseBody(), e);
		}
		List<TaskRepresentation> currentTasks = tasks.getData();
		return Optional.of(currentTasks);
	}

	public void completeTask(TaskTester task, String taskId) {
		CompleteFormRepresentation form = new CompleteFormRepresentation();
		form.setOutcome(task.getOutcome());
		
		if(task.hasFormValues()) {
			form.setValues(task.getFormValues());
		}
		
		try {
			taskFormsApi.completeTaskFormUsingPOST(taskId, form);
		} catch (ApiException e) {
			fail(e.getMessage() + " " + e.getResponseBody(),e);
		}

	}

}
