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
				AbstractCmd cmdInstance = (AbstractCmd) Class.forName(cmd.getCommandExecutorClass()).newInstance();
				initCmdInstance(cmd, cmdInstance);
				commands.put(cmd.getCommandName(),cmdInstance);
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

	/**
	 * Initialize command instance with values from db
	 * @param cmdPojo entity mapped to db with command properties
	 * @param cmdInstance command instance
	 */
	private void initCmdInstance(CmdListPOJO cmdPojo, AbstractCmd cmdInstance)
	{
		cmdInstance.setCmdArgs(cmdPojo.getCommandArgs());
		cmdInstance.setCmdDescription(cmdPojo.getCommandDescription());
		cmdInstance.setCmdExecutorClass(cmdPojo.getCommandExecutorClass());
		cmdInstance.setCmdNames(cmdPojo.getCommandName());
		cmdInstance.setCmdSyntax(cmdPojo.getCommandSyntax());
		cmdInstance.setCmdWeight(cmdPojo.getCommandWeight());
		cmdInstance.setEnabled(cmdPojo.getEnabled());
		cmdInstance.setLogged(cmdPojo.getLogged());
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
