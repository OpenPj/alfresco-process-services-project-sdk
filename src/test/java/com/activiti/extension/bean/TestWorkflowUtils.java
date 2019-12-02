package com.activiti.extension.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.activiti.domain.idm.Tenant;
import com.activiti.service.idm.TenantService;

import junit.framework.AssertionFailedError;

@Component
public class TestWorkflowUtils {

	@Autowired
	protected ManagementService managementService;

	@Autowired
	protected RuntimeService runtimeService;

	@Autowired
	protected HistoryService historyService;

	@Autowired
	protected ProcessEngine processEngine;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private WorkflowUtils workflowUtils;

	@Autowired
	protected TenantService tenantService;

	private Tenant testTenant;

	public Tenant getTenant() {
		String testTenantName = "test";
		if (testTenant == null) {
			List<Tenant> elencoTenant = tenantService.findTenantsByName(testTenantName);
			if (elencoTenant.isEmpty()) {
				testTenant = tenantService.createTenant(testTenantName, null, 100, true);
			} else {
				testTenant = elencoTenant.get(0);
			}

		}
		return testTenant;
	}

	public String getTenantIdForActiviti() {
		return String.valueOf(WorkflowUtils.TENANT_PREFIX + testTenant.getId());
	}

	public String getTaskOutcomeVariable(Task task) {
		return WorkflowUtils.getTaskOutcomeVariable(task);
	}

	public void timeWarp(Date goToTime) {
		processEngine.getProcessEngineConfiguration().getClock().setCurrentTime(goToTime);
	}

	/**
	 * See:
	 * https://github.com/Activiti/Activiti/blob/c8666252c669d89f2c6d7063e4a89aadf6b73175/activiti-engine/src/test/java/org/activiti/engine/test/jobexecutor/AsyncExecutorTest.java
	 * 
	 * @param maxWaitTime
	 * @return
	 */
	public void waitForJobExecutorToProcessAllJobs(long maxWaitTime) {
		workflowUtils.waitForJobExecutorToProcessAllJobs(maxWaitTime);
	}

	public void assertProcessEnded(String processInstanceId) {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();

		if (processInstance != null) {
			throw new AssertionFailedError(
					"Expected finished process instance '" + processInstanceId + "' but it was still in the db");
		}

		// process instance
		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();

		assertEquals(processInstanceId, historicProcessInstance.getId());
		assertNotNull("Historic process instance has no start time", historicProcessInstance.getStartTime());
		assertNotNull("Historic process instance has no end time", historicProcessInstance.getEndTime());
		// tasks
		List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId).list();
		if (historicTaskInstances != null && historicTaskInstances.size() > 0) {
			for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
				assertEquals(processInstanceId, historicTaskInstance.getProcessInstanceId());
				assertNotNull("Historic task " + historicTaskInstance.getTaskDefinitionKey() + " has no start time",
						historicTaskInstance.getStartTime());
				assertNotNull("Historic task " + historicTaskInstance.getTaskDefinitionKey() + " has no end time",
						historicTaskInstance.getEndTime());
			}
		}
	}

	public void snapshotH2DB(String snapshotName) {
		jdbcTemplate.execute("SCRIPT TO './target/" + snapshotName + ".sql'");
	}

}
