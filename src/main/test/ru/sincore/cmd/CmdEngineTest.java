package ru.sincore.cmd;

import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.sincore.cmd.handlers.*;

public class CmdEngineTest
{

	@BeforeMethod
	public void setUp() throws Exception
	{
		PropertyConfigurator.configure("./etc/log4j.properties");
		CmdEngine engine = CmdEngine.getInstance();

        engine.removeAllCommands();
        engine.registerCommand("about",      new AboutHandler());
        engine.registerCommand("help",       new HelpHandler());
        engine.registerCommand("regme",      new ClientRegDefaultHandler());
        engine.registerCommand("info",       new InfoHandler());
        engine.registerCommand("kick",       new KickHandler());
        engine.registerCommand("grant",      new GrantHandler());
        engine.registerCommand("reload",     new ReloadHandler());
        engine.registerCommand("restart",    new RestartHandler());
        engine.registerCommand("shutdown",   new ShutdownHandler());
	}

	@Test
	public void testExecuteCmd() throws Exception
	{
		CmdEngine engine = CmdEngine.getInstance();
		engine.executeCmd("Test1","-h", null);
	}

	@Test
	public void testCommandExist() throws Exception
	{
		CmdEngine engine = CmdEngine.getInstance();
		boolean flag = engine.commandExists("Test2");
		System.out.println(flag);

	}

	@Test
	public void testRegistryCmd() throws Exception
	{
		// Command syntax: cmd add {command_name} {class executor [script_name]} weight
		CmdEngine engine = CmdEngine.getInstance();
		//cmdEngine.registryCmd("Test2","ru.sincore.cmd.handlers.StubHandler", 0,true,true);
	}
}
