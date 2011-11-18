package ru.sincore.cmd.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCmd;

public class CoreHandler extends AbstractCmd
{
	private static final Logger log = LoggerFactory.getLogger(CoreHandler.class);

	private AbstractClient client 	= null;
	private String cmd		= "";

	public CoreHandler()
	{
		this.setCmdWeight(100);
	}

	@Override
	public String execute(String cmd, String args, AbstractClient client)
	{
		this.client = client;
		this.cmd = cmd;

        return null;
	}

	private void hubShutdown()
	{

	}
}
