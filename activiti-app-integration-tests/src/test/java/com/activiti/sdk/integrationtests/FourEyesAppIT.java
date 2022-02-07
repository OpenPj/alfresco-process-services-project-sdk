package com.activiti.sdk.integrationtests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.activiti.sdk.client.ApiClient;
import com.activiti.sdk.client.ApiException;
import com.activiti.sdk.client.api.AboutApi;
import com.activiti.sdk.client.api.AppDefinitionsApi;
import com.activiti.sdk.client.api.ProcessDefinitionsApi;
import com.activiti.sdk.client.api.ProcessInstancesApi;
import com.activiti.sdk.client.api.RuntimeAppDefinitionsApi;
import com.activiti.sdk.client.api.TaskFormsApi;
import com.activiti.sdk.client.api.TasksApi;
import com.activiti.sdk.client.model.AppDefinitionRepresentation;
import com.activiti.sdk.client.model.CompleteFormRepresentation;
import com.activiti.sdk.client.model.CreateProcessInstanceRepresentation;
import com.activiti.sdk.client.model.ProcessDefinitionRepresentation;
import com.activiti.sdk.client.model.ProcessInstanceRepresentation;
import com.activiti.sdk.client.model.RestVariable;
import com.activiti.sdk.client.model.ResultListDataRepresentationAppDefinitionRepresentation;
import com.activiti.sdk.client.model.ResultListDataRepresentationProcessDefinitionRepresentation;
import com.activiti.sdk.client.model.ResultListDataRepresentationTaskRepresentation;
import com.activiti.sdk.client.model.TaskQueryRepresentation;
import com.activiti.sdk.client.model.TaskRepresentation;
import com.activiti.sdk.integrationtests.utils.IntegrationTestUtils;

public class FourEyesAppIT {

	protected ApiClient apiClient;
	protected AboutApi aboutApi;
	protected AppDefinitionsApi appDefinitionsApi;
	protected RuntimeAppDefinitionsApi runtimeAppDefinitionsApi;
	protected ProcessDefinitionsApi processDefinitionsApi;
	protected ProcessInstancesApi processInstancesApi;
	protected TasksApi tasksApi;
	protected TaskFormsApi taskFormsApi;

	protected static final String ACTIVITI_APP_USERNAME = "admin@app.activiti.com";
	protected static final String ACTIVITI_APP_PASSWORD = "admin";
	protected static final String BASE_PATH_PROTOCOL = "http";
	protected static final String BASE_PATH_HOSTNAME = "localhost";
	protected static final int BASE_PATH_PORT = 8080;
	
	protected static final String appZipFile = "aps-extensions-jar-2.0.4-App.zip";

	protected static final String ACTIVITI_APP_BASE_PATH = BASE_PATH_PROTOCOL + "://" + BASE_PATH_HOSTNAME + ":"
			+ BASE_PATH_PORT;
	
	protected static final String PRIVATE_ENDPOINT = ACTIVITI_APP_BASE_PATH + "/enterprise/my-api-endpoint";
	protected static final String PUBLIC_ENDPOINT = ACTIVITI_APP_BASE_PATH + "/rest/my-rest-endpoint";

	@BeforeEach
	public void initApiClient() {
		apiClient = new ApiClient();
		apiClient.setBasePath(ACTIVITI_APP_BASE_PATH);
		apiClient.setUsername(ACTIVITI_APP_USERNAME);
		apiClient.setPassword(ACTIVITI_APP_PASSWORD);

		aboutApi = new AboutApi(apiClient);
		appDefinitionsApi = new AppDefinitionsApi(apiClient);
		runtimeAppDefinitionsApi = new RuntimeAppDefinitionsApi(apiClient);
		processDefinitionsApi = new ProcessDefinitionsApi(apiClient);
		processInstancesApi = new ProcessInstancesApi(apiClient);
		tasksApi = new TasksApi(apiClient);
		taskFormsApi = new TaskFormsApi(apiClient);
	}

	@Test
	@Order(2)
	public void testFourEyesApp() {
		System.out.println("--- /Start - Four Eyes App - Integration Test ---");
		
		// Importing the Four Eyes App in APS
		IntegrationTestUtils.importApp(appZipFile, ACTIVITI_APP_USERNAME, ACTIVITI_APP_PASSWORD, BASE_PATH_PROTOCOL,
				BASE_PATH_HOSTNAME, BASE_PATH_PORT);

		// Getting the App Definition Id
		ResultListDataRepresentationAppDefinitionRepresentation apps = null;
		try {
			apps = runtimeAppDefinitionsApi.getAppDefinitionsUsingGET();
			List<AppDefinitionRepresentation> appDefs = apps.getData();
			Iterator<AppDefinitionRepresentation> iteratorApps = appDefs.iterator();
			Long appDefId = null;
			while (iteratorApps.hasNext()) {
				AppDefinitionRepresentation appDefinitionRepresentation = (AppDefinitionRepresentation) iteratorApps
						.next();
				if (StringUtils.equals(appDefinitionRepresentation.getName(), "4 Eyes Principle")) {
					appDefId = appDefinitionRepresentation.getId();
					break;
				}
			}
			System.out.println("App Definition Id: " + appDefId);

			// Getting the Process Definition Id
			ResultListDataRepresentationProcessDefinitionRepresentation process = processDefinitionsApi
					.getProcessDefinitionsUsingGET(Boolean.TRUE, appDefId, null);
			List<ProcessDefinitionRepresentation> processDefs = process.getData();
			Iterator<ProcessDefinitionRepresentation> iteratorProcesses = processDefs.iterator();
			ProcessDefinitionRepresentation fourEyesProcess = null;
			while (iteratorProcesses.hasNext()) {
				ProcessDefinitionRepresentation processDefinitionRepresentation = (ProcessDefinitionRepresentation) iteratorProcesses
						.next();
				fourEyesProcess = processDefinitionRepresentation;
			}

			System.out.println("Process Definition Id: " + fourEyesProcess.getId());

			assertEquals("4 Eyes Principle", fourEyesProcess.getName());

			// Creating a new workflow instance
			CreateProcessInstanceRepresentation newProcess = new CreateProcessInstanceRepresentation();
			newProcess.setProcessDefinitionKey(fourEyesProcess.getKey());

			RestVariable reviewTitle = new RestVariable();
			reviewTitle.setName("reviewtitle");
			reviewTitle.setValue("Review from APS SDK");
			reviewTitle.setType("string");

			RestVariable description = new RestVariable();
			description.setName("description");
			description.setValue("Description from APS SDK");
			description.setType("string");

			List<RestVariable> vars = new ArrayList<RestVariable>();
			vars.add(reviewTitle);
			vars.add(description);

			newProcess.setVariables(vars);

			ProcessInstanceRepresentation processInstance = processInstancesApi
					.startNewProcessInstanceUsingPOST(newProcess);

			System.out.println("Workflow started with instance id: " + processInstance.getId());

			// Getting the First Approval Task Id
			TaskQueryRepresentation taskQuery = new TaskQueryRepresentation();
			taskQuery.appDefinitionId(appDefId);
			taskQuery.processInstanceId(processInstance.getId());

			ResultListDataRepresentationTaskRepresentation tasks = tasksApi.listTasksUsingPOST(taskQuery);
			List<TaskRepresentation> currentTasks = tasks.getData();
			Iterator<TaskRepresentation> iteratorTasks = currentTasks.iterator();
			String taskIdFirstApproval = StringUtils.EMPTY;
			while (iteratorTasks.hasNext()) {
				TaskRepresentation taskRepresentation = (TaskRepresentation) iteratorTasks.next();
				taskIdFirstApproval = taskRepresentation.getId();
			}

			// Submitting the First Approval Task
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("reviewtitle", "Review from APS SDK");
			values.put("descritpion", "Description from APS SDK");

			CompleteFormRepresentation form = new CompleteFormRepresentation();
			form.setOutcome("submit");
			form.setValues(values);

			taskFormsApi.completeTaskFormUsingPOST(taskIdFirstApproval, form);

			System.out.println("First approval task completed");

			// Submitting the Second Approval Task
			tasks = tasksApi.listTasksUsingPOST(taskQuery);
			currentTasks = tasks.getData();
			iteratorTasks = currentTasks.iterator();
			String taskIdSecondApproval = StringUtils.EMPTY;
			while (iteratorTasks.hasNext()) {
				TaskRepresentation taskRepresentation = (TaskRepresentation) iteratorTasks.next();
				taskIdSecondApproval = taskRepresentation.getId();
			}

			taskFormsApi.completeTaskFormUsingPOST(taskIdSecondApproval, form);

			System.out.println("Second approval task completed");

			tasks = tasksApi.listTasksUsingPOST(taskQuery);
			assertEquals(0, tasks.getSize());

			System.out.println("--- /End - Four Eyes App - Integration Test ---");

		} catch (ApiException e) {
			fail(e.getMessage(), e);
		}
	}

	@Test
	@Order(3)
	public void testCustomPrivateRestEndpoint() {
		IntegrationTestUtils.executePrivateGETRequest(ACTIVITI_APP_USERNAME, ACTIVITI_APP_PASSWORD, BASE_PATH_PROTOCOL,
				BASE_PATH_HOSTNAME, BASE_PATH_PORT, PRIVATE_ENDPOINT);
	}

	@Test
	@Order(4)
	public void testCustomPublicRestEndpoint() {
		IntegrationTestUtils.executePublicGETRequest(PUBLIC_ENDPOINT);
	}

	@Test
	@Order(1)
	public void testAboutApi() {
		System.out.println("--- Start executing About Api check ---");
		Map<String, String> response = null;
		try {
			response = aboutApi.getAppVersionUsingGET();
		} catch (ApiException e) {
			fail();
		}
		String edition = response.get("edition");
		assertEquals(edition, "Alfresco Process Services (powered by Activiti)");
		System.out.println("--- End executing About Api check ---");
	}

}
