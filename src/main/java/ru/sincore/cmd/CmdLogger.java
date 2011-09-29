package ru.sincore.cmd;

import ru.sincore.Client;
import ru.sincore.db.dao.CmdLogDAOImpl;

public class CmdLogger
{

	public static void log(AbstractCmd cmd, Client client, String cmdResult)
	{
		if (cmd.enabled)
		{
			CmdLogDAOImpl cmdLog = new CmdLogDAOImpl();
			cmdLog.putLog(client.getClientHandler().getNI(), cmd.getCmdNames(), cmdResult, cmd.getCmdArgs());

		} else
		{
			//TODO Send user "Logging command has disabled"
		}
	}

	public static void  search()
	{
		  //TODO add implementation search log entry
	}


}
