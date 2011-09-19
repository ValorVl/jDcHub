/*
 * Context.java
 *
 * Created on 16 september 2011, 12:00
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

import ru.sincore.Client;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;


/**
 * Base class for all ADC actions.
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-16
 */
public abstract class Action
{
    protected int   availableContexts = Context.INVALID_CONTEXT;
    protected int   availableStates   = State.INVALID_STATE;

    protected int         context     = Context.INVALID_CONTEXT;
    protected MessageType messageType = MessageType.INVALID_MESSAGE_TYPE;

    protected Client fromClient;
    protected Client toClient;

    protected String params;
    protected boolean paramsAreValid = false;


    protected Action(MessageType messageType,
           int context,
           Client fromClient,
           Client toClient)
    {
        this.messageType = messageType;
        this.context = context;
        this.fromClient = fromClient;
        this.toClient = toClient;
    }


    public Client getFromClient()
    {
        return fromClient;
    }


    public Client getToClient()
    {
        return toClient;
    }


    public void setFromClient(Client fromClient)
    {
        this.fromClient = fromClient;
    }


    public void setToClient(Client toClient)
    {
        this.toClient = toClient;
    }


    public boolean isValid ()
    {
        if (messageType == MessageType.INVALID_MESSAGE_TYPE)
            return false;

        if ((availableContexts & context) == Context.INVALID_CONTEXT)
            return false;

        if ((toClient != null) &&
            (availableStates & toClient.getClientHandler().state) == State.INVALID_STATE)
            return false;

        if (!paramsAreValid)
            return false;

        return true;
    }

    public abstract String toString();
    protected abstract boolean parse(String args);
}
