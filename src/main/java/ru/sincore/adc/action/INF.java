package ru.sincore.adc.action;

import ru.sincore.Client;
import ru.sincore.ConfigLoader;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.StringTokenizer;

/**
 * @author Valor
 */
public class INF extends Action
{
	public INF(MessageType messageType, int context, Client fromClient, Client toClient)
	{
		super(messageType, context, fromClient, toClient);

		this.availableContexts = Context.F | Context.T;
		this.availableStates   = State.IDENTIFY | State.NORMAL;
	}

	public INF(MessageType messageType, int context, Client client)
	{
		this(messageType,
             context,
             (context == Context.F ? client : null),
             (context == Context.T ? null : client));

	}

	@Override
	public String toString()
	{
		return null;
	}

	private boolean validateNick(Client client)
	{
		// Check nick on size
		if (client.getClientHandler().NI.length() > ConfigLoader.MAX_NICK_SIZE)
			return false;

		return true;
	}


	private void parseINF() throws STAException
	{
		if (rawCommand.length() < 10)
		{
			new STAError(fromClient, Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
						 "Incorrect protocol command");
			return;
		}

		rawCommand = rawCommand.substring(4);

		StringTokenizer infToken = new StringTokenizer(rawCommand);

		String inf = "BINF " + fromClient.getClientHandler().SID;

		String sid = infToken.nextToken();

		if (!sid.equals(fromClient.getClientHandler().SID))
		{
			new STAError(fromClient, Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
						 "Protocol Error.Wrong SID supplied.");
			return;
		}

		infToken.nextToken();

		while (infToken.hasMoreElements())
		{
			String token = infToken.nextToken();

			if (token.endsWith("ID"))
			{

				if (availableStates != State.PROTOCOL)
				{
					new STAError(fromClient, Constants.STA_SEVERITY_RECOVERABLE,"Can't change CID while connected.");

					return;
				}

				fromClient.getClientHandler().ID = token.substring(2);
				inf = inf + " ID" + fromClient.getClientHandler().ID;
			}
			else if (token.endsWith("NI"))
			{
				if (!validateNick(fromClient))
				{
					new STAError(fromClient, Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_INVALID,
								 "Nick : %s - not valid, please choose another.");
				}

				fromClient.getClientHandler().NI = token.substring(2);

				if (availableStates != State.PROTOCOL)
				{

				}
			}
		}
	}
}
