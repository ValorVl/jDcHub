/*
 * MSG.java
 *
 * Created on 20 september 2011, 11:15
 *
 * Copyright (C) 2011 Alexey 'lh' Antonov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package ru.sincore.adc.action_obsolete;

import com.adamtaft.eb.EventBusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Broadcast;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.client.AbstractClient;
import ru.sincore.db.dao.ChatLogDAO;
import ru.sincore.db.dao.ChatLogDAOImpl;
import ru.sincore.events.UserCommandEvent;
import ru.sincore.i18n.Messages;
import ru.sincore.util.AdcUtils;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Class for MSG action.
 * Description look at <a href="http://adc.sourceforge.net/ADC.html#_actions">ADC Actions</a>
 * section 5.3.5
 *
 * @author Alexey 'lh' Antonov
 * @author Alexander 'hatred' Drozdov
 * @since 2011-09-20
 */
public class MSG extends Action
{
    private static final Logger log = LoggerFactory.getLogger(MSG.class);


    String mySID = null;
    String targetSID = null;
    String message = null;
    String pmSID = null;
    boolean haveME = false;
    List<String> requiredFeatureList = new Vector<String>();
    List<String> excludedFeatureList = new Vector<String>();


    protected MSG(MessageType messageType, int context, AbstractClient fromClient, AbstractClient toClient)
    {
        super(messageType, context, fromClient, toClient);

        super.availableContexts = Context.F | Context.T;
        super.availableStates   = State.NORMAL;
    }


    public MSG(MessageType messageType, int context, AbstractClient client)
    {
        this(messageType,
             context,
             (context == Context.T ? client : null),
             (context == Context.F ? null : client));
    }


    /**
     * A chat message.
     *
     * @param messageType message type
     * @param context current actionName context
     * @param client client to send message or from message was recieved
     * @param rawCommand message text, additional flags
     */
    public MSG(MessageType messageType, int context, AbstractClient client, String rawCommand)
            throws CommandException, STAException
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


    /**
     * Checking message is actionName. If it is, execute it.
     *
     * @return true if it is actionName, false instead.
     */
    private boolean parseAndExecuteCommandInMessage()
    {
        ConfigurationManager configurationManager = ConfigurationManager.instance();

        String normalMessage = AdcUtils.fromAdcString(message);
        if (normalMessage.startsWith(configurationManager.getString(ConfigurationManager.OP_COMMAND_PREFIX)) ||
            normalMessage.startsWith(configurationManager.getString(ConfigurationManager.USER_COMMAND_PREFIX)))
        {
            if (normalMessage.startsWith(configurationManager.getString(ConfigurationManager.OP_COMMAND_PREFIX)) &&
                    (fromClient.getWeight() < configurationManager.getInt(ConfigurationManager.CLIENT_WEIGHT_REGISTRED) + 1))
            {
                fromClient.sendPrivateMessageFromHub("You don\'t have anough rights to use Op commands.");
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
            EventBusService.publish(new UserCommandEvent(command, commandParams.trim(), fromClient));

            return true;
        }

        return false;
    }


    private void parseFeatures(StringTokenizer tokenizer)
    {
        String features = tokenizer.nextToken();
        int    pos      = 0;

        log.debug("Unparsed feature list: " + features);

        while (pos < features.length() && (features.charAt(pos) == '+' || features.charAt(pos) == '-'))
        {
            List<String> featureList;
            if (features.charAt(pos++) == '+')
            {
                featureList = requiredFeatureList;
            }
            else
            {
                featureList = excludedFeatureList;
            }

            String feature = features.substring(pos, pos + 4);
            pos += 4;

            featureList.add(feature);
        }

        log.debug("Required feature list: " + requiredFeatureList);
        log.debug("Excluded feature list: " + excludedFeatureList);

    }


    private void parseFlags(StringTokenizer tokenizer)
            throws STAException
    {
        while (tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken();
            if (token.startsWith("PM"))
            {
                pmSID = token.substring(2);
                if (pmSID.length() != 4)
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                                 Messages.INVALID_FLAG_VALUE).send();

                // TODO [lh] check what will be, if message will be sent to group
                if (pmSID.equals(mySID) && messageType != MessageType.E)
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_RECOVERABLE,
                                 Messages.PM_TO_SELF).send();
                }
            }
            else if (token.startsWith("ME"))
            {
                if (token.substring(2).equals("1"))
                    haveME = true;
                else
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                                 Messages.INVALID_FLAG).send();
            }
        }
    }


    private void parseMessage(StringTokenizer tokenizer)
            throws STAException
    {
        String messageText = tokenizer.nextToken();

        if (messageText.length() > ConfigurationManager.instance().getInt(ConfigurationManager.MAX_CHAT_MESSAGE_SIZE))
            new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE,
                         Messages.MESSAGE_EXCEED_MAX_LENGTH).send();

        message = messageText;
    }


    private void parseMySID(StringTokenizer tokenizer)
            throws STAException
    {
        mySID = tokenizer.nextToken();

        if (mySID.length() != 4)
            new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         Messages.WRONG_MY_SID).send();

        if (!mySID.equals(fromClient.getSid()))
            new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         Messages.WRONG_SENDER).send();
    }


    private void parseTargetSID(StringTokenizer tokenizer)
            throws STAException
    {
        targetSID = tokenizer.nextToken();

        if (targetSID.length() != 4)
            new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         Messages.WRONG_TARGET_SID).send();

        if (targetSID.equals(fromClient.getSid()))
            new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE,
                         Messages.PM_RETURN_TO_SELF).send();

        AbstractClient tempClient = ClientManager.getInstance().getClientBySID(targetSID);
        if (tempClient == null)
            new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE,
                         Messages.USER_NOT_FOUND).send(); //not kick, maybe the other client just left after he sent the msg;

        toClient = tempClient;
    }


    @Override
    protected void parseIncoming()
            throws STAException
    {
        StringTokenizer tokenizer = new StringTokenizer(rawCommand, " ");

        // pass first 5 symbols: message type, actionName name and whitespace
        tokenizer.nextToken();

        // parse header
        switch (messageType)
        {
            case INVALID_MESSAGE_TYPE:
                break;

            case B:
                // get sender SID
                parseMySID(tokenizer);
                // get message
                parseMessage(tokenizer);
                // get flags and parse it
                parseFlags(tokenizer);
                // try to find actionName in message and execute it
                if (parseAndExecuteCommandInMessage())
                    break;

                Broadcast.getInstance().broadcast(rawCommand, fromClient);
                ChatLogDAO chatLog = new ChatLogDAOImpl();
                chatLog.saveMessage(ClientManager.getInstance().getClientBySID(mySID).getNick(),
                                    message);
                break;

            case I:
            case H:
                // In this case SID does not present
                // get flags and parse it
                parseFlags(tokenizer);
                break;

            case D:
            case E:
                // get sender SID
                parseMySID(tokenizer);
                // get reciever SID
                parseTargetSID(tokenizer);
                // get message
                parseMessage(tokenizer);
                // get flags and parse it
                parseFlags(tokenizer);
                // send message directly to client
                sendMessageToClient();
                break;

            case F:
                // get sender SID
                parseMySID(tokenizer);
                // get features include/exclude lists
                parseFeatures(tokenizer);
                // get flags and parse it
                parseFlags(tokenizer);

                // send message dependent from features
                Broadcast.getInstance().featuredBroadcast(rawCommand,
                                                          fromClient,
                                                          requiredFeatureList,
                                                          excludedFeatureList);

                break;

            case C:
            case U:
                // these should never be seen on a hub
                break;
        }
    }


    private void sendMessageToClient()
            throws STAException
    {
        toClient.sendRawCommand(rawCommand);
        if (messageType == MessageType.E)
            fromClient.sendRawCommand(rawCommand);
    }
}
