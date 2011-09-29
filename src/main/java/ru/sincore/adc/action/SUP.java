package ru.sincore.adc.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.Client;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.Main;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.i18n.Messages;
import ru.sincore.util.AdcUtils;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.StringTokenizer;

/**
 * Class implementation SUP action
 *
 * @author Valor
 */
public class SUP extends Action
{

	private static final Logger log = LoggerFactory.getLogger(SUP.class);
	private String marker = Marker.ANY_MARKER;

	public SUP(MessageType messageType, int context, Client fromClient, Client toClient)
	{
		super(messageType, context, fromClient, toClient);

		super.availableContexts = Context.F | Context.T | Context.C;
		super.availableStates = State.PROTOCOL | State.NORMAL;
	}

	public SUP(MessageType messageType, int context, Client client)
	{
		this(
			messageType,
			context,
			context == Context.F? client : null,
			context == Context.T? null : client
		);
	}

	@Override
	public String toString()
	{
		return null;
	}

	/**
	 *  The method handles incoming messages.
	 * @return
	 */
	@Override
    protected void parseIncoming() throws STAException, CommandException
	{
		if (messageType != MessageType.H)
		{
			throw new CommandException("FAIL state:PROTOCOL reason:NOT BASE CLIENT");
		}

		StringTokenizer incomingToken = new StringTokenizer(rawCommand);

        // pass first 5 symbols: message type, command name and whitespace
        incomingToken.nextToken();

		while (incomingToken.hasMoreTokens())
		{
			String endToken = incomingToken.nextToken();

			boolean enable = false;

			if (endToken.startsWith("AD"))
			{
				enable = true;
			}
			else if (endToken.startsWith("RM"))
			{
				enable = false;
			}
			else
			{
				new STAError(fromClient, 100, "Unknown SUP token (not an \'AD\' or \'RM\').");
			}

			//TODO [Valor] maybe rewrite this ?
			endToken = endToken.substring(2);
           	if (endToken.equals("BAS0"))
           	{
            	fromClient.getClientHandler().setBas0(enable);
            	fromClient.getClientHandler().setBase(1);
		   	}
		   	else if (endToken.equals("BASE"))
		   	{
				fromClient.getClientHandler().setBase(enable ? 2 : 0);
		   	}
		   	else if (endToken.startsWith("PIN") && endToken.length() == 4)
		   	{
				fromClient.getClientHandler().setPingExtensionSupports(enable);
		   	}
		   	else if (endToken.startsWith("UCM") && endToken.length() == 4)
		   	{
				fromClient.getClientHandler().setUcmd(enable);
		   	}
		   	else if (endToken.startsWith("TIGR") && endToken.length() == 4)
		   	{
				fromClient.getClientHandler().setTigrSupports(enable);
		   	}
		}

		// Check support old protocol version
		if (!fromClient.getClientHandler().isBas0())
		{
			new STAError(fromClient,
						 Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
						 "Your client uses a very old AdcUtils version. Please update in order to connect to this hub.");
		}

		// Check support version new ADC protocol, if 0 - not support, 1 - first version, 2 - second version..
		if (fromClient.getClientHandler().getBase() == 0)
		{
			new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "You removed BASE features therefore you can't stay on hub anymore.");
		}

		// Check support TIGER hash..
		if (!fromClient.getClientHandler().isTigrSupports())
		{
			new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_NO_HASH_OVERLAP,
                         "Cannot find any compatible hash function to use. Defaulting to TIGER.");
		}

        // if client in PROTOCOL state, send info about hub to him
        if (fromClient.getClientHandler().getState() == State.PROTOCOL)
            sendClientInitializationInfo();
	}


    /**
     * Handshake stage 1.
     * Sends to client initialization info about hub and his sid.
     *
     * @throws STAException
     * @throws CommandException
     */
    private void sendClientInitializationInfo() throws STAException, CommandException
	{
		// Check message type HUB if not throw exception
		if (messageType != MessageType.H)
		{
			throw new CommandException("FAIL state:PROTOCOL reason:NOT BASE CLIENT");
		}

		// Check client TIGER hash support if not, send error code 147 and reason
		if (!toClient.getClientHandler().isTigrSupports())
		{
			new STAError(fromClient,100 + Constants.STA_NO_HASH_OVERLAP, Messages.get(Messages.TIGER_ERROR));
		}

		// Check extension list, if list empty, send error message in log file and stop server
        toClient.getClientHandler().sendToClient("ISUP " +
                                                 Constants.HUB_BASE_SUP_STRING +
                                                 " " +
                                                 ConfigurationManager.instance().getString(ConfigurationManager.ADC_EXTENSION_LIST));

        // move client to IDENTIFY state
        toClient.getClientHandler().setState(State.IDENTIFY);

        toClient.getClientHandler().sendToClient("ISID " + toClient.getClientHandler().getSID());

		StringBuilder inf = new StringBuilder(8);

		inf.append("IINF CT32 VE ");
		inf.append(AdcUtils.retADCStr(ConfigurationManager.instance().getString(ConfigurationManager.HUB_VERSION)));
		inf.append("IN ");
		inf.append(AdcUtils.retADCStr(ConfigurationManager.instance().getString(ConfigurationManager.HUB_NAME)));
		//Check hub description, if empty, send  IINF without DE option
		if (!ConfigurationManager.instance().getString(ConfigurationManager.HUB_DESCRIPTION).isEmpty())
		{
			inf.append("DE ");
			inf.append(ConfigurationManager.instance().getString(ConfigurationManager.HUB_DESCRIPTION));
		}

		// Check client flag isPingExtensionSupports, if true, send PING string
		inf.append(toClient.getClientHandler().isPingExtensionSupports() ? pingQuery() : Constants.EMPTY_STR);

		toClient.getClientHandler().sendToClient(inf.toString());
    }


    /**
	 * Method build PING request string
	 * @return ping request string
	 */
	private static String pingQuery()
    {
		StringBuilder pingRequest = new StringBuilder(27);

		pingRequest.append(" HH");
		pingRequest.append(ConfigurationManager.instance().getString(ConfigurationManager.HUB_LISTEN));
		pingRequest.append(" UC");
		pingRequest.append(ClientManager.getInstance().getClientsCount());
		pingRequest.append(" SS");
		pingRequest.append(ClientManager.getInstance().getTotalShare());
		pingRequest.append(" SF");
		pingRequest.append(ClientManager.getInstance().getTotalFileCount());
		pingRequest.append(" MS");
		pingRequest.append(2048 * ConfigurationManager.instance().getLong(ConfigurationManager.MIN_SHARE_SIZE));
		pingRequest.append(" XS");
		pingRequest.append(2048 * ConfigurationManager.instance().getLong(ConfigurationManager.MAX_SHARE_SIZE));
		pingRequest.append(" ML");
		pingRequest.append(ConfigurationManager.instance().getInt(ConfigurationManager.MIN_SLOT_COUNT));
		pingRequest.append(" XL");
		pingRequest.append(ConfigurationManager.instance().getInt(ConfigurationManager.MIN_SLOT_COUNT));
		pingRequest.append(" XU");
		pingRequest.append(ConfigurationManager.instance().getInt(ConfigurationManager.MAX_HUBS_USERS));
		pingRequest.append(" XR");
		pingRequest.append(ConfigurationManager.instance().getInt(ConfigurationManager.MAX_HUBS_REGISTERED));
		pingRequest.append(" XO");
		pingRequest.append(ConfigurationManager.instance().getInt(ConfigurationManager.MAX_OP_IN_HUB));
		pingRequest.append(" MC");
		pingRequest.append(ConfigurationManager.instance().getInt(ConfigurationManager.MAX_USERS));
		pingRequest.append(" UP");
		pingRequest.append((System.currentTimeMillis() - Main.curtime));

        return pingRequest.toString();
    }

}
