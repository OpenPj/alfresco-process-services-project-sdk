package com.activiti.sdk.integrationtests.utils;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

public class IntegrationTestUtils {

	private static final String PUBLISH_APP_ENDPOINT = "/activiti-app/api/enterprise/app-definitions/publish-app";

	/**
	 * This method will import and publish an APS application against an APS instance
	 * 
	 * @param appZipFile          is the filename of the app that should be
	 *                            available in the target/apps folder of the
	 *                            Integration Test module
	 * @param username is the username of a user that can import new applications in APS
	 * @param password is the password of a user that can import new applications in APS
	 * @param protocol of the APS instance
	 * @param hostname of the APS instance
	 * @param port of the APS instance
	 */
	public static void importApp(String appZipFile, String username, String password, String protocol, String hostname,
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
		} catch (FileNotFoundException e) {
			fail(e.getMessage(), e);
		} catch (IOException e) {
			fail(e.getMessage(), e);
		}
	}
}