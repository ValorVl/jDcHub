package ru.sincore.cmd;

import ru.sincore.Client;

public class CmdEngine
{
	/**
	 * Execute command
	 * @param cmd command name
	 * @param client client entity
	 */
	public void executeCmd(String cmd, String args, Client client)
	{
		CmdContainer container = CmdContainer.getInstance();
		AbstractCmd cmdExec = container.getCommandExecutor(cmd);

		int clientRightWeight = 100; //This stub

		if(cmdExec.validateRights(clientRightWeight))
		{
			cmdExec.execute(cmd,args,client);
		}
	}

	public void registryCmd()
	{

	}
}
