package ru.sincore.adc.action.handlers;

import java.util.StringTokenizer;

import com.adamtaft.eb.EventBusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ru.sincore.signals.SameMessageFloodDetectedSignal;
import ru.sincore.events.UserCommandEvent;
import ru.sincore.i18n.Messages;
import ru.sincore.pipeline.Pipeline;
import ru.sincore.pipeline.PipelineFactory;
import ru.sincore.signalservice.Signal;
import ru.sincore.util.AdcUtils;
import ru.sincore.util.MessageUtils;
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
    private static final Logger log = LoggerFactory.getLogger(MSGHandler.class);

    public MSGHandler(AbstractClient sourceClient, MSG action)
    {
        super(sourceClient, action);
    }


    @Override
    public void handle()
            throws STAException
    {

        // detect chat message flood
        if (this.getMessageRecieveTime() - client.getLastMSG() <
                ConfigurationManager.instance().getLong(ConfigurationManager.CHAT_MESSAGE_INTERVAL))
        {
            client.sendPrivateMessageFromHub(Messages.get(Messages.TOO_FAST_CHATTING,
                                                          client.getExtendedField("LC")));
            return;
        }


        try
        {
            // detect same chat message flood
            if (client.getLastRawMSG().equals(action.getRawCommand()) &&
                    (this.getMessageRecieveTime() - client.getLastMSG() <
                    ConfigurationManager.instance().getLong(ConfigurationManager.CHAT_SAME_MESSAGE_SPAM_INTERVAL)))
            {
                client.sendPrivateMessageFromHub(Messages.get(Messages.SAME_MESSAGE_FLOOD,
                                                              client.getExtendedField("LC")));

                MessageUtils.sendMessageToOpChat(Messages.get(Messages.SAME_MESSAGE_FLOOD_DETECTED,
                                                              new String[]
                                                              {
                                                                      client.getNick()
                                                              }));

                // emit signal about same message flood detection
                Signal.emit(new SameMessageFloodDetectedSignal(client, action.getRawCommand()));

                return;
            }

            client.setLastRawMSG(action.getRawCommand());
        }
        catch (CommandException e)
        {
            // ignore
        }

        // save message timestamp
        client.setLastMSG(this.getMessageRecieveTime());

        try
        {
            action.tryParse();

            // try to find actionName in message and execute it
            if (parseAndExecuteCommandInMessage())
                return;

            Pipeline<MSG> pipeline = PipelineFactory.getPipeline("MSG");

            switch (action.getMessageType())
            {
                case B:
                    if (ConfigurationManager.instance().getBoolean(ConfigurationManager.USE_WORD_FILTER))
                    {
                        pipeline.process(action);
                    }
                    Broadcast.getInstance().broadcast(action.getRawCommand(), client);
                    ChatLogDAO chatLog = new ChatLogDAOImpl();
                    chatLog.saveMessage(ClientManager.getInstance().getClientBySID(action.getSourceSID()).getNick(),
                                        AdcUtils.toAdcString(action.getMessage()));

                    break;
                case D:
                case E:
                    if (ConfigurationManager.instance().getBoolean(ConfigurationManager.USE_WORD_FILTER) &&
                        ConfigurationManager.instance().getBoolean(ConfigurationManager.USE_WORD_FILTER_IN_PM))
                    {
                        pipeline.process(action);
                    }
                    sendMessageToClient();
                    break;
                case F:
                    // send message dependent from features
                    if (ConfigurationManager.instance().getBoolean(ConfigurationManager.USE_WORD_FILTER))
                    {
                        pipeline.process(action);
                    }
                    Broadcast.getInstance().featuredBroadcast(action.getRawCommand(),
                                                              client,
                                                              action.getRequiredFeatureList(),
                                                              action.getExcludedFeatureList());

                    break;
            }
        }
        catch (CommandException e)
        {
            log.debug(e.toString());
        }
        catch (STAException staException)
        {
            if (staException.getStaCode() > -1)
            {
                new STAError(client, staException.getStaCode(), staException.getMessage()).send();
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
                    (client.getWeight() < configurationManager.getInt(ConfigurationManager.CLIENT_WEIGHT_REGISTRED) + 1))
            {
                client.sendPrivateMessageFromHub("You don\'t have anough rights to use Op commands.");
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
            EventBusService.publish(new UserCommandEvent(command, commandParams.trim(), client));

            return true;
        }

        return false;
    }


    private void sendMessageToClient()
            throws STAException, CommandException
    {
        AbstractClient targetClient = ClientManager.getInstance().getClientBySID(action.getTargetSID());
        targetClient.sendAdcAction(action);
        if (action.getMessageType() == MessageType.E)
        {
            client.sendRawCommand(action.getRawCommand());
        }
    }


}
