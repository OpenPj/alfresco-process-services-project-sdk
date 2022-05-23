package com.activiti.sdk.integrationtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.activiti.sdk.integrationtests.utils.APSTester;
import com.activiti.sdk.integrationtests.utils.APSTester.APSTesterBuilder;

public class FourEyesAppAPSTesterIT {

	protected static final String ACTIVITI_APP_USERNAME = "admin@app.activiti.com";
	protected static final String ACTIVITI_APP_PASSWORD = "admin";
	protected static final String appZipFile = "aps-extensions-jar-2.1.4-SNAPSHOT-App.zip";
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
		// NOTE: test flows, but that methods available in each instance
		// make sense
		apsTester
			.afterLoadingAppArchive(appZipFile)
			.startProcessForApp("4 Eyes Principle")
			.withFormValue("reviewtitle", "Review from APS SDK")
			.withFormValue("description", "Description from APS SDK")
			.thenSubmitTask("First approval", "submit")
			.withFormValue("reviewtitle", "Review from APS SDK")
			.withFormValue("description", "Description from APS SDK")
			.thenSubmitTask("Second approval", "submit")
			.withFormValue("reviewtitle", "Review from APS SDK")
			.withFormValue("description", "Description from APS SDK")
			.thenCheckThatTheProcessIsFinished();
			//.removeAppNamed("4 Eyes Principle");

		System.out.println("--- /End - Four Eyes App - Integration Test ---");

	}

}