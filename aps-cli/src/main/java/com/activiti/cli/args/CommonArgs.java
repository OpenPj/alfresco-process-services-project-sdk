package com.activiti.cli.args;

import picocli.CommandLine.Model;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

public class CommonArgs {

	@Option(names = { "-h", "--help" }, usageHelp = true, description = "display this help message")
	protected boolean usageHelpRequested;
	
	@Option(names = {"-v", "--version"}, versionHelp = true, description = "display version info")
	boolean versionInfoRequested;

	@Spec
	protected Model.CommandSpec spec;

}
