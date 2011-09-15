package ru.sincore.cmd;

import ru.sincore.db.dao.CmdListDAOImpl;
import ru.sincore.db.pojo.CmdListPOJO;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CmdContainer
{
	private static volatile CmdContainer instance = new CmdContainer();

	private ConcurrentHashMap<String, Class<?>> commands;


	public static synchronized CmdContainer getInstance()
	{
		return instance;
	}

	private CmdContainer()
	{
		buildList();
	}

	private void buildList()
	{

		commands = new ConcurrentHashMap<String, Class<?>>();

		CmdListDAOImpl cmdList = new CmdListDAOImpl();

		List<CmdListPOJO> cmds = cmdList.getCommandList();

		for (CmdListPOJO cmd : cmds)
		{
			try
			{
				commands.put(cmd.getCommandName(),Class.forName(cmd.getCommandExecutorClass()));
			} catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void clearCommandList()
	{
		if (commands.size() != 0)
		{
			commands.clear();
		}
	}


	public void registryCommand()
	{

	}

}
