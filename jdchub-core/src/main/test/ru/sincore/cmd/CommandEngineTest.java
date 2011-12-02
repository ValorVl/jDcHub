package ru.sincore.cmd;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.sincore.cmd.CommandEngine;
import ru.sincore.cmd.handlers.AboutCommand;
import ru.sincore.cmd.handlers.HelpCommand;
import ru.sincore.cmd.handlers.InfoCommand;
import ru.sincore.cmd.handlers.RegmeCommand;
import ru.sincore.cmd.handlers.InfoCommand;
import ru.sincore.cmd.handlers.KickCommand;
import ru.sincore.cmd.handlers.GrantCommand;
import ru.sincore.cmd.handlers.ReloadCommand;
import ru.sincore.cmd.handlers.RestartCommand;
import ru.sincore.cmd.handlers.ShutdownCommand;

import java.lang.Exception;
import java.lang.System;

public class CommandEngineTest
{
    private CommandEngine engine;

	@BeforeMethod
	public void setUp() throws Exception
	{
		PropertyConfigurator.configure(ConfigurationManager.instance().getHubConfigDir() + "/log4j.properties");
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
