package ru.sincore.cmd;

import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CmdContainerTest
{
	@BeforeMethod
	public void setUp() throws Exception
	{
		PropertyConfigurator.configure("./etc/log4j.properties");
	}

	@Test
	public void testGetInstance() throws Exception
	{
		CmdContainer container = CmdContainer.getInstance();
		container.buildList();

		AbstractCmd cmd = container.getCommandExecutor("Test1");

		cmd.execute("-h");

	}
}
