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
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;

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


    @Override
    protected boolean parse(String params)
    {
        switch (context)
        {
            case Context.F:
                break;
            case Context.T:
                return parseIncomingMessage(params);
            default:
                break;
        }

        return false;
    }

    private boolean parseIncomingMessage(String params)
    {
        return false;
    }
}
