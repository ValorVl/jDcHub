package ru.sincore.cmd.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Client;
import ru.sincore.cmd.AbstractCmd;

public class CoreHandler extends AbstractCmd
{
	private static final Logger log = LoggerFactory.getLogger(CoreHandler.class);

	private Client client 	= null;
	private String cmd		= "";

	public CoreHandler()
	{
		this.setCmdWeight(100);
	}

	@Override
	public void execute(String cmd, String args, Client client)
	{
		this.client = client;
		this.cmd = cmd;

	}

	private void hubShutdown()
	{

	}
}
