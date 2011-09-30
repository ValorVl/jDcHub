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

package ru.sincore.adc.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Broadcast;
import ru.sincore.Client;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.cmd.CmdEngine;
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


    private MSG(MessageType messageType, int context, Client fromClient, Client toClient)
    {
        super(messageType, context, fromClient, toClient);

        super.availableContexts = Context.F | Context.T;
        super.availableStates   = State.NORMAL;
    }


    public MSG(MessageType messageType, int context, Client client)
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
     * @param context current command context
     * @param client client to send message or from message was recieved
     * @param rawCommand message text, additional flags
     */
    public MSG(MessageType messageType, int context, Client client, String rawCommand)
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
     * Checking message is command. If it is, execute it.
     *
     * @return true if it is command, false instead.
     */
    private boolean parseAndExecuteCommandInMessage()
    {
        String normalMessage = AdcUtils.retNormStr(message);
        if (normalMessage.startsWith(ConfigurationManager.instance().getString(ConfigurationManager.OP_COMMAND_PREFIX)) ||
            normalMessage.startsWith(ConfigurationManager.instance().getString(ConfigurationManager.USER_COMMAND_PREFIX)))
        {
            StringTokenizer commandTokenizer = new StringTokenizer(normalMessage, " ");

            CmdEngine cmd = new CmdEngine();
            String command = commandTokenizer.nextToken();
            if (!cmd.commandExist(command.substring(1)))
            {
                 // TODO say to client that command doesn't exist

                // return result like command was executed
                // that needed to don't broadcast message
                return true;
            }

            // command params is a message string without leading command name and whitespace
            String commandParams = message.substring(command.length());

            cmd.executeCmd(command, commandParams, fromClient);

            return true;
        }

        return false;
    }


    private void parseFeatures(StringTokenizer tokenizer)
    {
        String features = tokenizer.nextToken();
        // TODO feature parser
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
                                 "MSG Invalid flag value.");

                // TODO [lh] check what will be, if message will be sent to group
                if (pmSID.equals(mySID) && messageType != MessageType.E)
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_RECOVERABLE,
                                 "MSG Can\'t send private message to yourself.");
            }
            else if (token.startsWith("ME"))
            {
                if (token.substring(2).equals("1"))
                    haveME = true;
                else
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                                 "MSG Invalid Flag.");
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
                         "MSG Message exceeds maximum length.");

        message = messageText;
    }


    private void parseMySID(StringTokenizer tokenizer)
            throws STAException
    {
        mySID = tokenizer.nextToken();

        if (mySID.length() != 4)
            new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "MSG contains wrong my_sid value!");

        if (!mySID.equals(fromClient.getClientHandler().getSID()))
            new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "MSG my_sid not equal to sender\'s sid.");
    }


    private void parseTargetSID(StringTokenizer tokenizer)
            throws STAException
    {
        targetSID = tokenizer.nextToken();

        if (targetSID.length() != 4)
            new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "MSG contains wrong target_sid value!");

        if (targetSID.equals(fromClient.getClientHandler().getSID()))
            new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE,
                         "MSG PM not returning to self.");

        Client tempClient = ClientManager.getInstance().getClientBySID(targetSID);
        if (tempClient == null)
            new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE,
                         "MSG User not found."); //not kick, maybe the other client just left after he sent the msg;

        toClient = tempClient;
    }


    @Override
    protected void parseIncoming()
            throws STAException
    {
        StringTokenizer tokenizer = new StringTokenizer(rawCommand, " ");

        // pass first 5 symbols: message type, command name and whitespace
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
                // try to find command in message and execute it
                if (!parseAndExecuteCommandInMessage())
                    Broadcast.getInstance().broadcast(rawCommand);
                break;

            case C:
            case I:
            case H:
                // get message
                parseMySID(tokenizer);
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
                // get message
                parseFeatures(tokenizer);
                // get flags and parse it
                parseFlags(tokenizer);
                // send message dependent from features
                sendMessageDependentFromFeatures();
                break;

            case U:
                break;
        }
    }


    private void sendMessageDependentFromFeatures()
    {
    }


    private void sendMessageToClient()
            throws STAException
    {
        toClient.getClientHandler().sendToClient(rawCommand);
        if (messageType == MessageType.E)
            fromClient.getClientHandler().sendToClient(rawCommand);
    }
}
