package com.activiti.sdk.integrationtests.utils;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.activiti.sdk.client.ApiClient;
import com.activiti.sdk.client.api.AboutApi;
import com.activiti.sdk.client.api.AppDefinitionsApi;
import com.activiti.sdk.client.api.ProcessDefinitionsApi;
import com.activiti.sdk.client.api.ProcessInstancesApi;
import com.activiti.sdk.client.api.RuntimeAppDefinitionsApi;
import com.activiti.sdk.client.api.RuntimeAppDeploymentsApi;
import com.activiti.sdk.client.api.TaskFormsApi;
import com.activiti.sdk.client.api.TasksApi;
import com.activiti.sdk.client.model.AppDefinitionRepresentation;
import com.activiti.sdk.client.model.ProcessInstanceRepresentation;
import com.activiti.sdk.client.model.TaskRepresentation;

public class APSTester {

	public static final String ADMIN_USER = "admin@app.activiti.com";

	private final String username;
	private final String password;

	private final String proto;
	private final String host;
	private final int port;

	protected ApiClient apiClient;
	protected AboutApi aboutApi;
	protected AppDefinitionsApi appDefinitionsApi;
	protected RuntimeAppDefinitionsApi runtimeAppDefinitionsApi;
	protected RuntimeAppDeploymentsApi runtimeAppDeploymentsApi;
	protected ProcessDefinitionsApi processDefinitionsApi;
	protected ProcessInstancesApi processInstancesApi;
	protected TasksApi tasksApi;
	protected TaskFormsApi taskFormsApi;

	protected APSAppUtils appUtils;
	protected APSProcessUtils processUtils;
	protected APSTaskUtils taskUtils;

	protected Long currentAppDefId = null;
	protected ProcessInstanceRepresentation currentProcessInstance = null;
	protected AppDefinitionRepresentation currentAppDef = null;

	public APSTester(APSTesterBuilder builder) {
		this.username = builder.username;
		this.password = builder.password;
		this.proto = builder.proto;
		this.host = builder.host;
		this.port = builder.port;

		apiClient = new ApiClient();
		apiClient.setBasePath(this.proto + "://" + this.host + ":" + this.port);
		apiClient.setUsername(this.username);
		apiClient.setPassword(this.password);

		aboutApi = new AboutApi(apiClient);
		appDefinitionsApi = new AppDefinitionsApi(apiClient);
		runtimeAppDefinitionsApi = new RuntimeAppDefinitionsApi(apiClient);
		processDefinitionsApi = new ProcessDefinitionsApi(apiClient);
		processInstancesApi = new ProcessInstancesApi(apiClient);
		tasksApi = new TasksApi(apiClient);
		taskFormsApi = new TaskFormsApi(apiClient);

		appUtils = new APSAppUtils(runtimeAppDefinitionsApi, runtimeAppDeploymentsApi, appDefinitionsApi);
		processUtils = new APSProcessUtils(processDefinitionsApi, processInstancesApi, appUtils);
		taskUtils = new APSTaskUtils(tasksApi, taskFormsApi);
	}

	public APSTester afterLoadingApp(String appZipFile) {
		appUtils.importAndPublishApp(appZipFile, this.username, this.password, this.proto, this.host, this.port);
		return this;
	}

	public APSTester removeAppNamed(String appName) {
		currentAppDefId = appUtils.getAppDefinitionId(appName);
		currentAppDef = appUtils.getAppDef(appName);
		appUtils.removeApp(currentAppDef);
		return this;
	}

	public APSTester startProcess(Process process) {
		currentProcessInstance = processUtils.startProcess(process);
		currentAppDefId = appUtils.getAppDefinitionId(process.getAppName());
		return this;
	}

	public APSTester thenSubmitTask(Task task) {
		if (StringUtils.isNotEmpty(task.getName()) || StringUtils.isNotEmpty(task.getOutcome())) {
			Optional<TaskRepresentation> currentTask = taskUtils.findTaskWithName(currentProcessInstance.getId(),
					currentAppDefId, task.getName());

			if (currentTask.isEmpty()) {
				Optional<List<TaskRepresentation>> currentTasks = taskUtils
						.findTaskWithProcessInstanceId(currentProcessInstance.getId(), currentAppDefId);
				fail("Task not found: " + task + " current active tasks are: " + currentTasks);
			}

			taskUtils.completeTask(task, currentTask.get().getId());

		} else {
			fail("Cannot submit task with no name or no outcome: " + task);
		}
		return this;
	}

	public ProcessInstanceRepresentation getCurrentProcessInstace() {
		return this.currentProcessInstance;
	}

	public TaskRepresentation getCurrentTask() {
		return taskUtils.findTaskWithProcessInstanceId(currentProcessInstance.getId(), currentAppDefId).get().get(0);
	}

	public List<TaskRepresentation> getCurrentTasks() {
		return taskUtils.findTaskWithProcessInstanceId(currentProcessInstance.getId(), currentAppDefId).get();
	}

	public APSTester thenCheckThatTheProcessIsFinished() {
		Optional<List<TaskRepresentation>> tasks = taskUtils
				.findTaskWithProcessInstanceId(currentProcessInstance.getId(), currentAppDefId);
		if (tasks.isPresent()) {
			List<TaskRepresentation> currentTasks = tasks.get();
			if (currentTasks.size() > 0) {
				fail("The process is not finished, tasks active: " + currentTasks);
			}
		}
		return this;
	}

	public APSTester reset() {
		currentProcessInstance = null;
		currentAppDefId = null;
		return this;
	}

	public static class APSTesterBuilder {

		private final String username;
		private final String password;

		private static final String DEFAULT_HTTP_PROTOCOL = "http";
		private static final String DEFAULT_HOST = "localhost";
		private static final int DEFAULT_PORT = 8080;

		private String proto = DEFAULT_HTTP_PROTOCOL;
		private String host = DEFAULT_HOST;
		private int port = DEFAULT_PORT;

		public APSTesterBuilder(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public APSTesterBuilder proto(String proto) {
			this.proto = proto;
			return this;
		}

		public APSTesterBuilder host(String host) {
			this.host = host;
			return this;
		}

		public APSTesterBuilder port(int port) {
			this.port = port;
			return this;
		}

		public APSTester build() {
			APSTester tester = new APSTester(this);
			validateAPSTester(tester);
			return tester;
		}

		private void validateAPSTester(APSTester tester) {
			if (StringUtils.isEmpty(tester.username) || StringUtils.isEmpty(tester.password)) {
				fail("Username and password must be not null");
			}
		}

	}

}
