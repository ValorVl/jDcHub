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


    private boolean parseIncomingMessage(String params)
            throws STAException
    {
        StringTokenizer tokenizer = new StringTokenizer(params, " ");

        String mySID = null;
        String targetSID = null;
        StringBuilder message = new StringBuilder();
        String pmSID = null;
        boolean haveME = false;
        List<String> requiredFeatureList = new Vector<String>();
        List<String> excludedFeatureList = new Vector<String>();

        // parse header
        switch (messageType)
        {
            case INVALID_MESSAGE_TYPE:
                break;

            case B:
                // get sender SID
                mySID = tokenizer.nextToken();
                // get message
                message.append(tokenizer.nextToken());
                // get flags and parse it
                while (tokenizer.hasMoreTokens())
                {
                    String token = tokenizer.nextToken();
                    if (token.startsWith("PM"))
                    {
                        pmSID = token.substring(2);
                        if (pmSID.length() != 4)
                            new STAError(fromClient,
                                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                                         "MSG Invalid Flag.");
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
                break;

            case C:
            case I:
            case H:
                // get message
                message.append(tokenizer.nextToken());
                // get flags and parse it
                while (tokenizer.hasMoreTokens())
                {
                    String token = tokenizer.nextToken();
                    if (token.startsWith("PM"))
                    {
                        pmSID = token.substring(2);
                        if (pmSID.length() != 4)
                            new STAError(fromClient,
                                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                                         "MSG Invalid Flag.");
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
                break;

            case D:
            case E:
                // get sender SID
                mySID = tokenizer.nextToken();
                // get reciever SID
                targetSID = tokenizer.nextToken();
                // get message
                message.append(tokenizer.nextToken());
                // get flags and parse it
                while (tokenizer.hasMoreTokens())
                {
                    String token = tokenizer.nextToken();
                    if (token.startsWith("PM"))
                    {
                        pmSID = token.substring(2);
                        if (pmSID.length() != 4)
                            new STAError(fromClient,
                                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                                         "MSG Invalid Flag.");
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
                break;

            case F:
                // get sender SID
                mySID = tokenizer.nextToken();
                // get message
                message.append(tokenizer.nextToken());
                // get flags and parse it
                while (tokenizer.hasMoreTokens())
                {
                    String token = tokenizer.nextToken();
                    if (token.startsWith("PM"))
                    {
                        pmSID = token.substring(2);
                        if (pmSID.length() != 4)
                            new STAError(fromClient,
                                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                                         "MSG Invalid Flag.");
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
                break;

            case U:
                break;
        }
        return false;
    }
}
