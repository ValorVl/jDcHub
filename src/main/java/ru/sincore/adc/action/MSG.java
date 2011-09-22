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
import ru.sincore.Client;
import ru.sincore.ConfigLoader;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
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
    StringBuilder message = new StringBuilder();
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
             (context == Context.F ? client : null),
             (context == Context.T ? null : client));
    }


    /**
     * A chat message.
     *
     * @param messageType message type
     * @param context current command context
     * @param client client to send message or from message was recieved
     * @param params message text, additional flags
     */
    public MSG(MessageType messageType, int context, Client client, String params)
            throws CommandException, STAException
    {
        this(messageType, context, client);
        this.params = params;
        parse(params);
    }


    @Override
    public String toString()
    {
        return null;
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
                // TODO check : pm_sid must be not equal to my_sid
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
        if (messageText.length() > ConfigLoader.MAX_CHAT_MESSAGE_SIZE)
            new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE,
                         "MSG Message exceeds maximum length.");
        message.append(messageText);
        // TODO parse message for commands
    }


    private void parseMySID(StringTokenizer tokenizer)
            throws STAException
    {
        mySID = tokenizer.nextToken();

        if (mySID.length() != 4)
            new STAError(fromClient,
                     Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                     "MSG contains wrong my_sid value!");

        if (!mySID.equals(fromClient.getClientHandler().SessionID))
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

        if (targetSID.equals(fromClient.getClientHandler().SessionID))
            new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE,
                         "MSG PM not returning to self.");
    }


    @Override
    protected void parseIncoming()
            throws STAException
    {
        StringTokenizer tokenizer = new StringTokenizer(params, " ");

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
                break;

            case F:
                // get sender SID
                parseMySID(tokenizer);
                // get message
                parseFeatures(tokenizer);
                // get flags and parse it
                parseFlags(tokenizer);
                break;

            case U:
                break;
        }
    }
}
