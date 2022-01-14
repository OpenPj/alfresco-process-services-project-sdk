package org.alfresco.activiti.integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.alfresco.activiti.utils.WorkflowUtils;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.activiti.sdk.test.BaseIntegrationTestSDK;
import com.activiti.sdk.test.utils.UserBean;

public class FourEyesAppIT extends BaseIntegrationTestSDK {

	static {
		appResurceDir = "apps/fourEyes";
		processDefinitionId = "fourEyesPrinciple";
		requiredUsers = new UserBean[] {
				UserBean.createUser("admin@app.activiti.com", "admin", new String[] { groupAdminUsers }),
				UserBean.createUser("adminapi@app.activiti.com", "admin", new String[] { groupAdminUsers }),
				UserBean.createUser("nonadminapi@app.activiti.com", "admin", new String[] { groupAdminUsers }),
				UserBean.createUser("plucidi@ziaconsulting.com", "admin", new String[] { groupAdminUsers }), };
	}

	private Map<String, Object> getProcessInitVariables(Long userId) {
		Map<String, Object> processInitVariables = new HashMap<String, Object>();
		processInitVariables.put("initiator", userId);
		processInitVariables.put("assignee", userId);
		processInitVariables.put("reviewtitle", "Iterating against software");
		processInitVariables.put("description", "The quick brown fox jumps over the lazy dog.");
		return processInitVariables;
	}

	@Test
	public void testProcessInit() {
		Map<String, Object> processVars = getProcessInitVariables(adminUserForPrivateApi.getId());
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(processDefinitionId,
				processVars, testTenantIdForActiviti);

		String processInstanceId = processInstance.getId();

		assertNotNull(processInstance);
		assertEquals(1, taskService.createTaskQuery().processInstanceId(processInstanceId).count());
	}

	@Test
	public void testHappyPath() {
		Map<String, Object> processVars = getProcessInitVariables(adminUserForPrivateApi.getId());
		Map<String, Object> taskVars = new HashMap<String, Object>();

		processVars.put("user", adminUserForPrivateApi.getId());

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(processDefinitionId,
				processVars, testTenantIdForActiviti);
		String processInstanceId = processInstance.getId();

		// Validate document - First approval
		Task task = taskService.createTaskQuery()
				.processInstanceId(processInstanceId)
				.singleResult();

		processVars.put(WorkflowUtils.getTaskOutcomeVariable(task), "submit");
		taskService.complete(task.getId(), processVars);

		assertEquals(1, taskService
				.createTaskQuery()
				.processInstanceId(processInstanceId)
				.count());
		System.out.println("--- /Validate document - First approval ---");

		// Validate document - Second approval
		task = taskService.createTaskQuery()
				.processInstanceId(processInstanceId)
				.singleResult();

		taskVars = taskService.getVariables(task.getId());
		taskVars.put(WorkflowUtils.getTaskOutcomeVariable(task), "submit");
		taskService.complete(task.getId(), taskVars);

		assertEquals(0, taskService.createTaskQuery()
				.processInstanceId(processInstanceId)
				.count());

		System.out.println("--- /Second approval task ---");
		System.out.println("--- /End ---");
	}

	@Test
	public void testCustomPrivateRestEndpoint() throws Exception {
		String url = getEnterpriseApiURL("my-api-endpoint");

		// append query string parameters here

		executeGETRequest(url, adminUserForPrivateApi, HttpStatus.OK);
	}

	@Test
	public void testCustomPublicRestEndpoint() throws Exception {
		String url = getPrivateApiURL("my-rest-endpoint");

		// append query string parameters here

		executeGETRequest(url, adminUserForPrivateApi, HttpStatus.OK);
	}

}
