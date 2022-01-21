package com.activiti.sdk.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Before;

import com.activiti.sdk.client.ApiClient;
import com.activiti.sdk.client.ApiException;
import com.activiti.sdk.client.api.AboutApi;

public class FourEyesAppIT {

	protected ApiClient apiClient;
	protected AboutApi aboutApi;
	
	protected static final String ACTIVITI_APP_USERNAME = "admin@app.activiti.com";
	protected static final String ACTIVITI_APP_PASSWORD = "admin";
	
	protected static final String ACTIVITI_APP_BASE_PATH = "http://localhost:8080/activiti-app/api";
	
	@Before
	public void initApiClient() {
		apiClient = new ApiClient();
		apiClient.setBasePath(ACTIVITI_APP_BASE_PATH);
		apiClient.setUsername(ACTIVITI_APP_USERNAME);
		apiClient.setPassword(ACTIVITI_APP_PASSWORD);
		
		aboutApi = new AboutApi(apiClient);
		
	}
	
	@org.junit.Test
	public void testAboutApi() {
		Map<String, String> response = null;
		try {
			response = aboutApi.getAppVersionUsingGET();
		} catch (ApiException e) {
			fail();
		}
		String edition = response.get("edition");
		assertEquals(edition, "Alfresco Process Services (powered by Activiti)");
		
	}
	
}
