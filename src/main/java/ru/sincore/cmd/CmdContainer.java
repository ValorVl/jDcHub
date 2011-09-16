package ru.sincore.cmd;

import ru.sincore.db.dao.CmdListDAOImpl;
import ru.sincore.db.pojo.CmdListPOJO;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CmdContainer
{
	private static volatile CmdContainer instance = new CmdContainer();

	private ConcurrentHashMap<String, AbstractCmd> commands;


	public static synchronized CmdContainer getInstance()
	{
		return instance;
	}

	private CmdContainer()
	{

	}

	public void buildList()
	{

		commands = new ConcurrentHashMap<String, AbstractCmd>();

		CmdListDAOImpl cmdList = new CmdListDAOImpl();

		List<CmdListPOJO> cmds = cmdList.getCommandList();

		for (CmdListPOJO cmd : cmds)
		{
			try
			{
				AbstractCmd clazz = (AbstractCmd) Class.forName(cmd.getCommandExecutorClass()).newInstance();
				commands.put(cmd.getCommandName(),clazz);
			} catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			} catch (InstantiationException e)
			{
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	public AbstractCmd getCommandExecutor(String name)
	{
		return commands.get(name);
	}

	public void clearCommandList()
	{
		if (!commands.isEmpty())
		{
			commands.clear();
		}
	}


	public void registryCommand()
	{

	}

}
