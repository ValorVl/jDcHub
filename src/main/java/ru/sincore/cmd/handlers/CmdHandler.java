package ru.sincore.cmd.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import ru.sincore.Client;
import ru.sincore.cmd.AbstractCmd;

/**
 *  A class managed command engine.
 *
 *  Implements function e.g. help,add, del, update, list, redeploy, etc..
 */
public class CmdHandler extends AbstractCmd
{

	private static final Logger log = LoggerFactory.getLogger(CmdHandler.class);

	private Marker marker = MarkerFactory.getMarker("INFO");

	@Override
	public void execute(String cmd, String args, Client client)
	{
		log.info(marker,"Cmd : " +cmd+" args : "+args);
	}

	public void showHelp()
	{

	}
}
