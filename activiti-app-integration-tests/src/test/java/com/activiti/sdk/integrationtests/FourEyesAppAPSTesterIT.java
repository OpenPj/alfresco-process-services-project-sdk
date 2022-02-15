package com.activiti.sdk.integrationtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.activiti.sdk.integrationtests.utils.APSTester;
import com.activiti.sdk.integrationtests.utils.APSTester.APSTesterBuilder;
import com.activiti.sdk.integrationtests.utils.Process;
import com.activiti.sdk.integrationtests.utils.Task;

public class FourEyesAppAPSTesterIT {

	protected static final String ACTIVITI_APP_USERNAME = "admin@app.activiti.com";
	protected static final String ACTIVITI_APP_PASSWORD = "admin";
	protected static final String appZipFile = "aps-extensions-jar-2.0.7-SNAPSHOT-App.zip";
	protected APSTester apsTester;

	@BeforeEach
	public void initApiClient() {
		apsTester = new APSTesterBuilder(ACTIVITI_APP_USERNAME, ACTIVITI_APP_PASSWORD).build();
	}

	@Test
	@Order(1)
	public void testFourEyesApp() {
		System.out.println("--- /Start - Four Eyes App - Integration Test ---");

		// Importing the Four Eyes App in APS
		apsTester
			.afterLoadingApp(appZipFile)
			.startProcess(
				new Process()
				.start("4 Eyes Principle")
				.withFormValue("reviewtitle", "Review from APS SDK")
				.withFormValue("description", "Description from APS SDK"))
				.thenSubmitTask(
						new Task()
						.name("First approval").outcome("submit")
						.withFormValue("reviewtitle", "Review from APS SDK")
						.withFormValue("description", "Description from APS SDK"))
				.thenSubmitTask(
						new Task()
						.name("Second approval").outcome("submit")
						.withFormValue("reviewtitle", "Review from APS SDK")
						.withFormValue("description", "Description from APS SDK"))
				.thenCheckThatTheProcessIsFinished();
				//.removeAppNamed("4 Eyes Principle");

		System.out.println("--- /End - Four Eyes App - Integration Test ---");

	}

}