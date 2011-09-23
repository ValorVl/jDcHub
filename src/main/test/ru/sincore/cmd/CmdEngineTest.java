package ru.sincore.cmd;

import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CmdEngineTest
{

	@BeforeMethod
	public void setUp() throws Exception
	{
		PropertyConfigurator.configure("./etc/log4j.properties");
		CmdContainer container = CmdContainer.getInstance();
		container.buildList();
	}

	@Test
	public void testExecuteCmd() throws Exception
	{
		CmdEngine cmdEngine = new CmdEngine();
		cmdEngine.executeCmd("Test1","-h", null);
	}

	@Test
	public void testCommandExist() throws Exception
	{
		CmdEngine cmdEngine = new CmdEngine();
		boolean flag = cmdEngine.commandExist("Test2");
		System.out.println(flag);

	}

	@Test
	public void testRegistryCmd() throws Exception
	{
		// Command syntax: cmd add {command_name} {class executor [script_name]} weight
		CmdEngine cmdEngine = new CmdEngine();
		cmdEngine.registryCmd("Test2","ru.sincore.cmd.handlers.StubHandler", 0,true,true);
	}
}
