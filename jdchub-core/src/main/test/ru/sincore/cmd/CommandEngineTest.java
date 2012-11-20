package ru.sincore.cmd;

import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.sincore.ConfigurationManager;
import jdchub.module.commands.handlers.AboutCommand;
import jdchub.module.commands.handlers.HelpCommand;
import jdchub.module.commands.handlers.InfoCommand;
import jdchub.module.commands.handlers.RegmeCommand;
import jdchub.module.commands.handlers.KickCommand;
import jdchub.module.commands.handlers.GrantCommand;
import jdchub.module.commands.handlers.ReloadCommand;
import jdchub.module.commands.handlers.RestartCommand;
import jdchub.module.commands.handlers.ShutdownCommand;

import java.lang.Exception;
import java.lang.System;

public class CommandEngineTest
{
    private CommandEngine engine;

	@BeforeMethod
	public void setUp() throws Exception
	{
		PropertyConfigurator.configure(ConfigurationManager.getInstance().getHubConfigDir() +
                                       "/log4j.properties");
		engine = new CommandEngine();

        engine.removeAllCommands();
        engine.registerCommand("about",      new AboutCommand());
        engine.registerCommand("help",       new HelpCommand());
        engine.registerCommand("regme",      new RegmeCommand());
        engine.registerCommand("info",       new InfoCommand());
        engine.registerCommand("kick",       new KickCommand());
        engine.registerCommand("grant",      new GrantCommand());
        engine.registerCommand("reload",     new ReloadCommand());
        engine.registerCommand("restart",    new RestartCommand());
        engine.registerCommand("shutdown",   new ShutdownCommand());
	}

	@Test
	public void testExecuteCmd() throws Exception
	{
		engine.executeCommand("Test1","-h", null);
	}

	@Test
	public void testCommandExist() throws Exception
	{
		boolean flag = engine.commandExists("Test2");
		System.out.println(flag);

	}

	@Test
	public void testRegistryCmd() throws Exception
	{
		// Command syntax: cmd add {command_name} {class executor [script_name]} weight
		//cmdEngine.registryCmd("Test2","ru.sincore.cmd.handlers.StubCommand", 0,true,true);
	}
}
