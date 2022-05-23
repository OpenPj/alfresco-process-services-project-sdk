package com.activiti.cli.args;

import picocli.CommandLine.Option;

public class LoginArgs extends CommonArgs {
	
	private static final String DEFAULT_USERNAME_VALUE = "admin@app.activiti.com";
	private static final char[] DEFAULT_PASSWORD_VALUE = "admin".toCharArray();
	private static final String DEFAULT_PROTOCOL_VALUE = "http";
	private static final String DEFAULT_HOST_VALUE = "localhost";
	private static final Integer DEFAULT_PORT_VALUE = 8080;
	private static final String DEFAULT_CONTEXT_PATH_VALUE = "/activiti-app";

	@Option(names = { "-u", "--username" }, description = "User name", required = true)
	protected String username;

	@Option(names = { "-p", "--password" }, description = "Password", required = true, interactive = true)
	protected String password;
	
	@Option(names = { "-pr", "--protocol" }, description = "Protocol of your APS instance")
	protected String protocol = DEFAULT_PROTOCOL_VALUE;

	@Option(names = { "-H", "--host" }, description = "Host of your APS instance")
	protected String host = DEFAULT_HOST_VALUE;

	@Option(names = { "-P", "--port" }, description = "Port of your APS instance")
	protected Integer port = DEFAULT_PORT_VALUE;

	@Option(names = { "-cp", "--context-path" }, description = "Context path related to the Activiti App WAR")
	protected String contextPath = DEFAULT_CONTEXT_PATH_VALUE;
	
}
