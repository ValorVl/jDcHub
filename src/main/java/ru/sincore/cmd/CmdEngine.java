package ru.sincore.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.Client;
import ru.sincore.db.dao.CmdListDAOImpl;

public class CmdEngine
{

	private static final Logger log = LoggerFactory.getLogger(CmdEngine.class);
	private String marker = Marker.ANY_MARKER;

	/**
	 * Execute command
	 * @param cmd command name
	 * @param client client entity
	 */
	public void executeCmd(String cmd, String args, Client client)
	{
		CmdContainer container = CmdContainer.getInstance();
		AbstractCmd cmdExec = container.getCommandExecutor(cmd);

        if (cmdExec == null)
            return;

		int clientRightWeight = 100; //This stub

		if(cmdExec.validateRights(clientRightWeight))
		{
			cmdExec.execute(cmd,args,client);
		}
	}

	/**
	 * Method check command exist in command container
	 * @param command command name
	 * @return true if and only if command exist, false otherwise.
	 */
	public boolean commandExist(String command)
	{
		CmdContainer cmdContainer = CmdContainer.getInstance();

		if (cmdContainer.getConteiner().containsKey(command))
		{
			return true;
		}

		return false;
	}

	/**
	 * Class at runtime registers a new command hub.
	 *
	 * It is understood that all the arguments of the method is valid and is in no need of validation.
	 *
	 * Other arguments command, for example - the command arguments, description, syntax,
	 * and activity logging, passed class-handler or a script ..
	 *
	 * @param cmdName name of command
	 * @param executor full FQDN class name
	 * @param weight rights weight
	 */
	public void registryCmd(String cmdName, String executor, Integer weight,Boolean enabled, Boolean logged)
	{
		// first, register command in command container
		CmdContainer container = CmdContainer.getInstance();

		try
		{
			AbstractCmd executorObject = (AbstractCmd) Class.forName(executor).newInstance();
			container.registryCommand(cmdName,executorObject);

			CmdListDAOImpl cmdListDAO = new CmdListDAOImpl();

			String args = executorObject.getCmdArgs();
			String desc = executorObject.getCmdDescription();
			String syntax = executorObject.getCmdSyntax();

			cmdListDAO.addCommand(cmdName,weight,executor,args,desc,syntax,enabled,logged);


		} catch (InstantiationException e)
		{
			log.error(marker,e);
		} catch (IllegalAccessException e)
		{
			log.error(marker,e);
		} catch (ClassNotFoundException e)
		{
			log.error(marker,e);
		}
	}
}
