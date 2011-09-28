package ru.sincore.adc.action;

import ru.sincore.Client;
import ru.sincore.ClientManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.StringTokenizer;

/**
 *  @author Valor
 */
public class STA extends Action
{
	public STA(MessageType messageType, int context, Client fromClient, Client toClient)
	{
		super(messageType, context, fromClient, toClient);

		availableContexts = Context.C | Context.F | Context.T | Context.U;
		availableStates   = State.ALL;
	}

	public STA(MessageType messageType, int context, Client client)
	{
		this(messageType,
             context,
             (context == Context.F ? client : null),
             (context == Context.T ? null : client));
	}

	public STA(MessageType messageType, int context, Client client, String rawCommand) throws CommandException, STAException
	{
		this(messageType, context, client);
        this.rawCommand = rawCommand;
        parse(rawCommand);
	}

	@Override
	public String toString()
	{
		return null;
	}

	private void validate()
    {
		if (!fromClient.getClientHandler().isOverrideSpam())
			fromClient.getClientHandler().sendFromBot("STA invalid context " + context);
	}

	private void prepareDirect() throws STAException
	{
		validate();

		StringTokenizer tk = new StringTokenizer(rawCommand);
		tk.nextToken();
		if (!tk.hasMoreTokens())
		{
			new STAError(fromClient,
						 Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
						 "Must supply SID");
			return;
		}
		String curSid = tk.nextToken();
		if (!curSid.equals(fromClient.getClientHandler().getSID()))
		{
			new STAError(fromClient,
						 Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
						 "Protocol Error.Wrong SID supplied.");
			return;
		}
		if (!tk.hasMoreTokens())
		{
			new STAError(fromClient,
						 Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
						 "Must supply target SID");
			return;
		}
		String dSid = tk.nextToken();
		Client targetClient = ClientManager.getInstance().getClientBySID(dSid);
		if ((targetClient != null) && targetClient.getClientHandler().isValidated())
		{
			targetClient.getClientHandler().sendToClient(rawCommand);
		}
		else
		{
			new STAError(fromClient, Constants.STA_SEVERITY_RECOVERABLE, "Invalid Target Sid.");
		}
	}

	private void prepareEcho() throws STAException
	{
		validate();

		StringTokenizer tk = new StringTokenizer(rawCommand);
		tk.nextToken();
		if (!tk.hasMoreTokens())
		{
			new STAError(fromClient, Constants.STA_SEVERITY_RECOVERABLE, "Must supply SID");
			return;
		}
		String curSid = tk.nextToken();
		if (!curSid.equals(fromClient.getClientHandler().getSID()))
		{
			new STAError(fromClient,
						 Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
						 "Protocol Error.Wrong SID supplied.");
			return;
		}
		if (!tk.hasMoreTokens())
		{
			new STAError(fromClient,
						 Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
						 "Must supply SID");
			return;
		}
		String eSid = tk.nextToken();

		Client targetClient = ClientManager.getInstance().getClientBySID(eSid);
		if ((targetClient != null) && targetClient.getClientHandler().isValidated())
		{
			targetClient.getClientHandler().sendToClient(rawCommand);
		}
		else
		{
			new STAError(fromClient, Constants.STA_SEVERITY_RECOVERABLE, "Invalid Target Sid.");
		}
	}

	protected void parseIncoming() throws STAException
	{
		 switch (messageType)
        {
            case INVALID_MESSAGE_TYPE:
                break;

            case B:
                validate();
                break;
            case C:
            case I:
            case H:
				validate();
                break;

            case D:
				prepareDirect();
				break;

            case E:
				prepareEcho();
                break;

            case F:
				validate();
                break;
            case U:
        }

	}
}
