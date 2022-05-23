package com.activiti.cli.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.activiti.cli.args.LoginArgs;
import com.activiti.sdk.client.ApiClient;
import com.activiti.sdk.client.ApiException;
import com.activiti.sdk.client.api.AboutApi;
import com.activiti.sdk.client.api.AppDefinitionsApi;
import com.activiti.sdk.client.api.ProcessDefinitionsApi;
import com.activiti.sdk.client.api.ProcessInstancesApi;
import com.activiti.sdk.client.api.RuntimeAppDefinitionsApi;
import com.activiti.sdk.client.api.TaskFormsApi;
import com.activiti.sdk.client.api.TasksApi;

import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "login", version = "2.1.4-SNAPSHOT", description = "Login to an APS instance", subcommands = GenerateCompletion.class)
public class LoginCommand extends LoginArgs implements Runnable {

	protected ApiClient apiClient;
	protected AboutApi aboutApi;
	protected AppDefinitionsApi appDefinitionsApi;
	protected RuntimeAppDefinitionsApi runtimeAppDefinitionsApi;
	protected ProcessDefinitionsApi processDefinitionsApi;
	protected ProcessInstancesApi processInstancesApi;
	protected TasksApi tasksApi;
	protected TaskFormsApi taskFormsApi;

	@Override
	public void run() {
		CommandLine cli = spec.commandLine();

		apiClient = new ApiClient();
		apiClient.setUsername(username);

		if (StringUtils.isNotEmpty(password)) {
			apiClient.setPassword(password.toString());
		} else if (StringUtils.isNotEmpty(passwordEnvironmentVariable)) {
			apiClient.setPassword((System.getenv(passwordEnvironmentVariable)));
		} else if (passwordFile != null) {
			try {
				apiClient.setPassword(new String(Files.readAllBytes(passwordFile.toPath())));
			} catch (IOException e) {
				cli.getOut().println(e.getMessage());
				e.printStackTrace(cli.getOut());
			}
		}

		spec.commandLine().getOut().println("Username: " + username);
		spec.commandLine().getOut().println("Password: " + password.toString());

		String basePath = protocol + "://" + host + ":" + port;
		apiClient.setBasePath(basePath);

		aboutApi = new AboutApi(apiClient);
		appDefinitionsApi = new AppDefinitionsApi(apiClient);
		runtimeAppDefinitionsApi = new RuntimeAppDefinitionsApi(apiClient);
		processDefinitionsApi = new ProcessDefinitionsApi(apiClient);
		processInstancesApi = new ProcessInstancesApi(apiClient);
		tasksApi = new TasksApi(apiClient);
		taskFormsApi = new TaskFormsApi(apiClient);

		Map<String, String> response = new HashMap<String, String>();
		try {
			response = aboutApi.getAppVersionUsingGET();
		} catch (ApiException e) {
			cli.getOut().println(e.getMessage() + " body: " + e.getResponseBody());
			e.printStackTrace(cli.getOut());
		}
		cli.getOut().println("User " + username + " successfully logged in APS");
		cli.getOut().println(response);

	}

	public static void main(String[] args) {
		int exitCode = new CommandLine(new LoginCommand()).execute(args);
		System.exit(exitCode);
	}

}
