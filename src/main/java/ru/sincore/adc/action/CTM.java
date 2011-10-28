/*
 * CTM.java
 *
 * Created on 30 september 2011, 15:00
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
 * @author Alexey 'lh' Antonov
 * @since 2011-09-30
 */
public class CTM extends Action
{
    private final static Logger log = LoggerFactory.getLogger(CTM.class);

    private String  clientProtocol;
    private Integer clientPort;
    private String  clientToken;


    CTM(MessageType messageType, int context, AbstractClient fromClient, AbstractClient toClient)
    {
        super(messageType, context, fromClient, toClient);

        this.availableContexts = Context.F | Context.T;
        this.availableStates = State.NORMAL;
    }

    public CTM(MessageType messageType, int context, AbstractClient client, String rawCommand)
            throws CommandException, STAException
    {
        this(messageType,
             context,
             (context == Context.T ? client : null),
             (context == Context.F ? null : client));
        this.rawCommand = rawCommand;
        parse(rawCommand);
    }


    private void parseCTM(StringTokenizer tokenizer)
            throws STAException
    {
        if (!fromClient.isActive())
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE,
                         Messages.TCP_DISABLED).send();
            return;
        }

        // getting mySID
        String mySID = tokenizer.nextToken();
        if (!mySID.equals(fromClient.getSid()))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         Messages.WRONG_SID).send();
            return;
        }

        String targetSID = tokenizer.nextToken();

        // looking for client by target sid
        AbstractClient targetClient = ClientManager.getInstance().getClientBySID(targetSID);
        if (targetClient == null)
            //talking to inexisting client
            //not kick, maybe the other client just left after he sent the msg;
            return;

        // get protocol
        clientProtocol = tokenizer.nextToken();
        // get port
        clientPort = Integer.parseInt(tokenizer.nextToken());
        // get client token
        clientToken = tokenizer.nextToken();

        targetClient.sendRawCommand(rawCommand);
        if (messageType == MessageType.E)
        {
            fromClient.sendRawCommand(rawCommand);
        }
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
                break;

            case C:
            case I:
            case H:
                break;

            case E:
            case D:
                parseCTM(tokenizer);
                break;

            case F:
                break;

            case U:
                break;
        }
    }


    @Override
    public String toString()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
