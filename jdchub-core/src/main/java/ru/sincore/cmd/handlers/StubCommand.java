package ru.sincore.cmd.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;

public class StubCommand extends AbstractCommand
{

	private static final Logger log = LoggerFactory.getLogger(StubCommand.class);

	public StubCommand()
	{
		this.setCmdArgs("wkhewher");
		this.setCmdDescription("iuwieurywr");
	}

	@Override
	public String execute(String cmd, String args, AbstractClient client)
	{
		log.info("Cmd : "+cmd+" args : "+args);

        client.sendPrivateMessageFromHub("Debug : cmd" + cmd +
                                         " | args : " + args +
                                         " | client : " + client.getNick());

        return null;
    }
}

