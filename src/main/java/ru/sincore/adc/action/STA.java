package ru.sincore.adc.action;

import ru.sincore.ClientManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.client.AbstractClient;
import ru.sincore.i18n.Messages;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.StringTokenizer;

/**
 *  @author Valor
 */
public class STA extends Action
{
	public STA(MessageType messageType, int context, AbstractClient fromClient, AbstractClient toClient)
	{
		super(messageType, context, fromClient, toClient);

		availableContexts = Context.C | Context.F | Context.T | Context.U;
		availableStates   = State.ALL;
	}

	public STA(MessageType messageType, int context, AbstractClient client)
	{
		this(messageType,
             context,
             (context == Context.T ? client : null),
             (context == Context.F ? null : client));
	}

	public STA(MessageType messageType, int context, AbstractClient client, String rawCommand) throws CommandException, STAException
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
		if (!fromClient.isOverrideSpam())
        {
            String msg = "STA " + Messages.get(Messages.INVALID_CONTEXT, context, (String)fromClient.getExtendedField("LC"));
            fromClient.sendMessageFromHub(msg);
        }
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
						 Messages.NO_SID).send();
			return;
		}
		String curSid = tk.nextToken();
		if (!curSid.equals(fromClient.getSid()))
		{
			new STAError(fromClient,
						 Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
						 Messages.WRONG_SID).send();
			return;
		}
		if (!tk.hasMoreTokens())
		{
			new STAError(fromClient,
						 Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
						 Messages.NO_TARGET_SID).send();
			return;
		}
		String dSid = tk.nextToken();
		AbstractClient targetClient = ClientManager.getInstance().getClientBySID(dSid);
		if ((targetClient != null) && targetClient.isValidated())
		{
			targetClient.sendRawCommand(rawCommand);
		}
		else
		{
			new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE,
                         Messages.WRONG_TARGET_SID).send();
		}
	}

	private void prepareEcho() throws STAException
	{
		validate();

		StringTokenizer tk = new StringTokenizer(rawCommand);
		tk.nextToken();
		if (!tk.hasMoreTokens())
		{
			new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE,
                         Messages.NO_SID).send();
			return;
		}
		String curSid = tk.nextToken();
		if (!curSid.equals(fromClient.getSid()))
		{
			new STAError(fromClient,
						 Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
						 Messages.WRONG_SID).send();
			return;
		}
		if (!tk.hasMoreTokens())
		{
			new STAError(fromClient,
						 Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
						 Messages.NO_SID).send();
			return;
		}
		String eSid = tk.nextToken();

		AbstractClient targetClient = ClientManager.getInstance().getClientBySID(eSid);
		if ((targetClient != null) && targetClient.isValidated())
		{
			targetClient.sendRawCommand(rawCommand);
		}
		else
		{
			new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE,
                         Messages.WRONG_TARGET_SID).send();
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
