package com.activiti.cli.args;

import java.io.File;

import picocli.CommandLine.Option;

public class LoginArgs extends CommonArgs {
	
	private static final String DEFAULT_PROTOCOL_VALUE = "http";
	private static final String DEFAULT_HOST_VALUE = "localhost";
	private static final Integer DEFAULT_PORT_VALUE = 8080;
	private static final String DEFAULT_CONTEXT_PATH_VALUE = "/activiti-app";

	@Option(names = { "-u", "--username" }, description = "User name", required = true)
	protected String username;

	@Option(names = { "-p", "--password" }, description = "Password", arity = "0..1", interactive = true)
	protected String password;
	
	@Option(names = {"-pf" ,"--password:file"}, description = "Password from file")
    protected File passwordFile;

    @Option(names = {"-pe" ,"--password:env"}, description = "Password from property")
    protected String passwordEnvironmentVariable;
	
	@Option(names = { "-pr", "--protocol" }, description = "Protocol of your APS instance")
	protected String protocol = DEFAULT_PROTOCOL_VALUE;

	@Option(names = { "-H", "--host" }, description = "Host of your APS instance")
	protected String host = DEFAULT_HOST_VALUE;

	@Option(names = { "-P", "--port" }, description = "Port of your APS instance")
	protected Integer port = DEFAULT_PORT_VALUE;

	@Option(names = { "-cp", "--context-path" }, description = "Context path related to the Activiti App WAR")
	protected String contextPath = DEFAULT_CONTEXT_PATH_VALUE;
	
}
