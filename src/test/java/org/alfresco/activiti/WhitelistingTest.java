package org.alfresco.activiti;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.alfresco.activiti.conf.ApplicationWhitelistingTestConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.activiti.domain.idm.AccountType;
import com.activiti.domain.idm.Tenant;
import com.activiti.domain.idm.User;
import com.activiti.domain.idm.UserStatus;
import com.activiti.security.SecurityUtils;
import com.activiti.service.api.UserService;
import com.activiti.service.idm.TenantService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationWhitelistingTestConfiguration.class)
public class WhitelistingTest {

	@Autowired
	protected RepositoryService repositoryService;

	@Autowired
	protected RuntimeService runtimeService;

	@Autowired
	protected HistoryService historyService;

	@Autowired
	protected TaskService taskService;

	@Autowired
	protected UserService userService;
	
	@Autowired
	protected TenantService tenantService;

	private User user;	
	private Tenant tenant1;
	private String tenantId;	

	public String getTaskOutcomeVariable(Task task) {
		return "form" + task.getFormKey() + "outcome";
	}

	@Before
	public void beforeTest() {

		tenant1 = tenantService.createTenant("testTenant", null, 100, true);
		tenantId = "tenant_"+String.valueOf(tenant1.getId());
		// Create test tenant and user
		user = userService.findActiveUserByEmail("p.lucidi@tai.it");
		if (user == null) {
			user = userService.createNewUser(
					"p.lucidi@tai.it", "Piergiorgio", "Lucidi", "test", null, UserStatus.ACTIVE,
					AccountType.ENTERPRISE, tenant1.getId());
		}
		SecurityUtils.assumeUser(user);
		SecurityUtils.currentUserCanManageTenant(tenant1.getId());
		
	}

	@Test
	public void whitelistedBeanInScriptTaskTest() {
		repositoryService.createDeployment()
				.addClasspathResource("app/bpmn-models/4 Eyes Principle-1.bpmn20.xml")
				.tenantId(tenantId)
				.deploy().getId();
		
		Map<String, Object> processVars = getProcessInitVariables(String.valueOf(user.getId()));
		Map<String, Object> taskVars = new HashMap<String, Object>();

		ProcessInstance processInstance = runtimeService
				.startProcessInstanceByKeyAndTenantId(
						"fourEyesPrinciple",
						processVars, tenantId);

		assertNotNull(processInstance);

		assertEquals(1, taskService.createTaskQuery().count());

		// Validate document - First approval
		Task task = taskService.createTaskQuery().singleResult();
		processVars.put(getTaskOutcomeVariable(task), "submit");
		taskService.complete(task.getId(), processVars);

		assertEquals(1, taskService.createTaskQuery().count());
		System.out.println("--- /Validate document - First approval ---");

		// Validate document - Second approval

		task = taskService.createTaskQuery().singleResult();
		taskVars = taskService.getVariables(task.getId());
		taskVars.put(getTaskOutcomeVariable(task), "submit");
		taskService.complete(task.getId(), taskVars);

		assertEquals(0, taskService.createTaskQuery().count());

		System.out.println("--- /Validate document - Second approval ---");

		System.out.println("--- /End ---");

	}

	private Map<String, Object> getProcessInitVariables(String userId) {
		Map<String, Object> processInitVariables = new HashMap<String, Object>();
		processInitVariables.put("initiator", new Long(userId));
		processInitVariables.put("reviewtitle", "Iterating against software");
		processInitVariables.put("description", "The quick brown fox jumps over the lazy dog.");
		return processInitVariables;
	}

}
