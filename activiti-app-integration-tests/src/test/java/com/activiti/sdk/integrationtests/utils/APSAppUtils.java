package com.activiti.sdk.integrationtests.utils;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import com.activiti.sdk.client.ApiException;
import com.activiti.sdk.client.api.AppDefinitionsApi;
import com.activiti.sdk.client.api.RuntimeAppDefinitionsApi;
import com.activiti.sdk.client.api.RuntimeAppDeploymentsApi;
import com.activiti.sdk.client.model.AppDefinitionRepresentation;
import com.activiti.sdk.client.model.ResultListDataRepresentationAppDefinitionRepresentation;

public class APSAppUtils {

	private RuntimeAppDefinitionsApi runtimeAppDefinitionsApi;
	private RuntimeAppDeploymentsApi runtimeAppDeploymentsApi;
	private AppDefinitionsApi appDefinitionsApi;
	
	private static final String PUBLISH_APP_ENDPOINT = "/activiti-app/api/enterprise/app-definitions/publish-app";


	public APSAppUtils(RuntimeAppDefinitionsApi runtimeAppDefinitionsApi, RuntimeAppDeploymentsApi runtimeAppDeploymentsApi, AppDefinitionsApi appDefinitionsApi) {
		this.runtimeAppDefinitionsApi = runtimeAppDefinitionsApi;
		this.runtimeAppDeploymentsApi = runtimeAppDeploymentsApi;
		this.appDefinitionsApi = appDefinitionsApi;
	}

	public Long getAppDefinitionId(String appName) {
		ResultListDataRepresentationAppDefinitionRepresentation apps = null;
		Optional<Long> appDefId = null;
		try {
			apps = this.runtimeAppDefinitionsApi.getAppDefinitionsUsingGET();
			List<AppDefinitionRepresentation> appDefs = apps.getData();
			Iterator<AppDefinitionRepresentation> iteratorApps = appDefs.iterator();
			while (iteratorApps.hasNext()) {
				AppDefinitionRepresentation appDefinitionRepresentation = (AppDefinitionRepresentation) iteratorApps
						.next();
				if (StringUtils.equals(appDefinitionRepresentation.getName(), appName)) {
					appDefId = Optional.of(appDefinitionRepresentation.getId());
					break;
				}
			}
			if(appDefId.isEmpty()) {
				fail("App definition Id not found. Currently active apps are: "+appDefs);
			}
			System.out.println("App Definition Id: " + appDefId);
		} catch (ApiException e) {
			fail(e.getMessage(), e);
		}
		return appDefId.get();
	}
	
	public Long getAppModelId(String appName) {
		ResultListDataRepresentationAppDefinitionRepresentation apps = null;
		Optional<Long> appDefId = null;
		try {
			apps = this.runtimeAppDefinitionsApi.getAppDefinitionsUsingGET();
			List<AppDefinitionRepresentation> appDefs = apps.getData();
			Iterator<AppDefinitionRepresentation> iteratorApps = appDefs.iterator();
			while (iteratorApps.hasNext()) {
				AppDefinitionRepresentation appDefinitionRepresentation = (AppDefinitionRepresentation) iteratorApps
						.next();
				if (StringUtils.equals(appDefinitionRepresentation.getName(), appName)) {
					appDefId = Optional.of(appDefinitionRepresentation.getModelId());
					break;
				}
			}
			if(appDefId.isEmpty()) {
				fail("Model Id not found. Currently active apps are: "+appDefs);
			}
			System.out.println("App Definition Id: " + appDefId);
		} catch (ApiException e) {
			fail(e.getMessage(), e);
		}
		return appDefId.get();
	}
	
	public AppDefinitionRepresentation getAppDef(String appName) {
		ResultListDataRepresentationAppDefinitionRepresentation apps = null;
		Optional<AppDefinitionRepresentation> appDef = null;
		try {
			apps = this.runtimeAppDefinitionsApi.getAppDefinitionsUsingGET();
			List<AppDefinitionRepresentation> appDefs = apps.getData();
			Iterator<AppDefinitionRepresentation> iteratorApps = appDefs.iterator();
			while (iteratorApps.hasNext()) {
				AppDefinitionRepresentation appDefinitionRepresentation = (AppDefinitionRepresentation) iteratorApps
						.next();
				if (StringUtils.equals(appDefinitionRepresentation.getName(), appName)) {
					appDef = Optional.of(appDefinitionRepresentation);
					break;
				}
			}
			if(appDef.isEmpty()) {
				fail("App Def not found. Currently active apps are: "+appDefs);
			}
			System.out.println("App Definition Id: " + appDef);
		} catch (ApiException e) {
			fail(e.getMessage(), e);
		}
		return appDef.get();
	}

	public void removeApp(AppDefinitionRepresentation appDef) {
		try {
			appDefinitionsApi.deleteAppDefinitionUsingDELETE(appDef.getId());
			runtimeAppDeploymentsApi.deleteAppDeploymentUsingDELETE(Long.valueOf(appDef.getDeploymentId()));
			System.out.println("App removed with definition Id: " + appDef.getId());
		} catch (ApiException e) {
			fail(e.getMessage(), e);
		}
	}
	
	public void importAndPublishApp(String appZipFile, String username, String password, String protocol, String hostname,
			Integer port) {
		Path resourceFourEyesAppZip = Paths.get("target", "apps", appZipFile);
		File file = resourceFourEyesAppZip.toFile();
		try {

			final BasicScheme basicAuth = new BasicScheme();
			basicAuth.initPreemptive(new UsernamePasswordCredentials(username, password.toCharArray()));
			final HttpHost target = new HttpHost(protocol, hostname, port);
			final HttpClientContext localContext = HttpClientContext.create();
			localContext.resetAuthExchange(target, basicAuth);

			try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
				final String publishAppUrl = protocol + "://" + hostname + ":" + port.toString() + PUBLISH_APP_ENDPOINT;
				final HttpPost httppost = new HttpPost(publishAppUrl);

				final FileBody binaryFile = new FileBody(file);
				final HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", binaryFile).build();
				httppost.setEntity(reqEntity);

				System.out.println("Importing app " + httppost);
				try (final CloseableHttpResponse response = httpclient.execute(httppost, localContext)) {
					System.out.println("----------------------------------------");
					System.out.println(response);
					final HttpEntity resEntity = response.getEntity();
					if (resEntity != null) {
						System.out.println("Response content length: " + resEntity.getContentLength());
					}
					EntityUtils.consume(resEntity);
				}
			}
			System.out.println("App imported");
		} catch (FileNotFoundException e) {
			fail(e.getMessage(), e);
		} catch (IOException e) {
			fail(e.getMessage(), e);
		}
	}
}
