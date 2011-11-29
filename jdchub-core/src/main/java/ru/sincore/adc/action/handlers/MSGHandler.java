package ru.sincore.adc.action.handlers;

import java.util.StringTokenizer;

import com.adamtaft.eb.EventBusService;
import ru.sincore.Broadcast;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.action.actions.MSG;
import ru.sincore.client.AbstractClient;
import ru.sincore.db.dao.ChatLogDAO;
import ru.sincore.db.dao.ChatLogDAOImpl;
import ru.sincore.events.UserCommandEvent;
import ru.sincore.util.AdcUtils;
import ru.sincore.util.STAError;

/**
 * MSG action handler
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 25.11.11
 *         Time: 15:46
 */
public class MSGHandler extends AbstractActionHandler<MSG>
{
    public MSGHandler(AbstractClient sourceClient,
                      AbstractClient targetClient,
                      MSG            action)
    {
        super(sourceClient, targetClient, action);
    }


    public MSGHandler(AbstractClient sourceClient, MSG action)
    {
        super(sourceClient, action);
    }


    @Override
    public void handle()
            throws STAException
    {
        try
        {
            action.tryParse();

            // try to find actionName in message and execute it
            if (parseAndExecuteCommandInMessage())
                return;

            switch (action.getMessageType())
            {
                case B:
                    Broadcast.getInstance().broadcast(action.getRawCommand(), sourceClient);
                    ChatLogDAO chatLog = new ChatLogDAOImpl();
                    chatLog.saveMessage(ClientManager.getInstance().getClientBySID(action.getSourceSID()).getNick(),
                                        AdcUtils.toAdcString(action.getMessage()));

                    break;
                case D:
                case E:
                    sendMessageToClient();
                    break;
                case F:
                    // send message dependent from features
                    Broadcast.getInstance().featuredBroadcast(action.getRawCommand(),
                                                              sourceClient,
                                                              action.getRequiredFeatureList(),
                                                              action.getExcludedFeatureList());

                    break;
            }
        }
        catch (CommandException e)
        {
            e.printStackTrace();
        }
        catch (STAException staException)
        {
            if (staException.getStaCode() > -1)
            {
                new STAError(sourceClient, staException.getStaCode(), staException.getMessage()).send();
            }
            else
            {
                throw staException;
            }

        }
    }


    /**
     * Checking message is actionName. If it is, execute it.
     *
     * @return true if it is actionName, false instead.
     * @throws ru.sincore.Exceptions.CommandException
     * @throws ru.sincore.Exceptions.STAException
     */
    private boolean parseAndExecuteCommandInMessage()
            throws CommandException, STAException
    {
        String normalMessage = action.getMessage();
        ConfigurationManager configurationManager = ConfigurationManager.instance();

        if (normalMessage.startsWith(configurationManager.getString(ConfigurationManager.OP_COMMAND_PREFIX)) ||
            normalMessage.startsWith(configurationManager.getString(ConfigurationManager.USER_COMMAND_PREFIX)))
        {
            if (normalMessage.startsWith(configurationManager.getString(ConfigurationManager.OP_COMMAND_PREFIX)) &&
                    (sourceClient.getWeight() < configurationManager.getInt(ConfigurationManager.CLIENT_WEIGHT_REGISTRED) + 1))
            {
                sourceClient.sendPrivateMessageFromHub("You don\'t have anough rights to use Op commands.");
                return true;
            }

            StringTokenizer commandTokenizer = new StringTokenizer(normalMessage, " ");

            // get command from user message
            String command = commandTokenizer.nextToken().substring(1);

            // command params is a message string without leading command name and whitespace
            String commandParams = "";
            if (command.length() != normalMessage.length())
                commandParams = normalMessage.substring(command.length() + 1);

            // publish event about user command coming
            EventBusService.publish(new UserCommandEvent(command, commandParams.trim(), sourceClient));

            return true;
        }

        return false;
    }


    private void sendMessageToClient()
            throws STAException, CommandException
    {
        targetClient.sendRawCommand(action.getRawCommand());
        if (action.getMessageType() == MessageType.E)
        {
            sourceClient.sendRawCommand(action.getRawCommand());
        }
    }


}
