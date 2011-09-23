package ru.sincore.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.db.dao.CmdListDAOImpl;
import ru.sincore.db.pojo.CmdListPOJO;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CmdContainer
{
	private static final Logger log = LoggerFactory.getLogger(CmdContainer.class);
	private String marker = Marker.ANY_MARKER;

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
				log.error(marker,e);
			} catch (InstantiationException e)
			{
				log.error(marker,e);
			} catch (IllegalAccessException e)
			{
				log.error(marker,e);
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

	public synchronized AbstractCmd getCommandExecutor(String name)
	{
		return commands.get(name);
	}

	public synchronized void clearCommandList()
	{
		if (!commands.isEmpty())
		{
			commands.clear();
		}
	}

	public synchronized ConcurrentHashMap<String, AbstractCmd> getConteiner()
	{
		return commands;
	}

	public synchronized void registryCommand(String name, AbstractCmd executor)
	{
		commands.put(name,executor);
	}

}
