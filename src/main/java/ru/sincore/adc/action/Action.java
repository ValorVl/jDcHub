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
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.util.STAError;


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


    public Client getFromClient ()
    {
        return fromClient;
    }


    public Client getToClient ()
    {
        return toClient;
    }


    public void setFromClient (Client fromClient)
    {
        this.fromClient = fromClient;
    }


    public void setToClient (Client toClient)
    {
        this.toClient = toClient;
    }


    public boolean isValid ()
    {
        if (messageType == MessageType.INVALID_MESSAGE_TYPE)
            return false;

        if ((availableContexts & context) == Context.INVALID_CONTEXT)
            return false;

        if ((fromClient != null) &&
            (availableStates & fromClient.getClientHandler().state) == State.INVALID_STATE)
            return false;

        if ((toClient != null) &&
            (availableStates & toClient.getClientHandler().state) == State.INVALID_STATE)
            return false;

        if (!paramsAreValid)
            return false;

        if (!isInternallyValid())
            return false;

        return true;
    }

    protected boolean isInternallyValid ()
    {
        return true;
    }

    /**
     * Parse additional parameters.
     * For incoming messages that means incoming string without
     * 5 heading simbols containing message type, command and separator (space).
     *
     * @param params command parameters
     * @return parsing status. True if parameters were parsed, False otherwise
     * @throws CommandException
     * @throws STAException
     */
    public final boolean parse (String params) throws STAException, CommandException
    {
        this.params = params;

        try
        {
        switch (context)
        {
            case Context.F:
                return parseOutgoing();
            case Context.T:
                return parseIncoming();
            case Context.C:
                //TODO realize passive transition message.
                return parsePassiveTransition();
            case Context.U:
                return parseUDP();
            default:
                new STAError(fromClient,100,"Invalid context : " + context);
                    break;
        }
        }
        catch (STAException sta)
        {
            //ignored
        }
        catch (CommandException ce)
        {
            //ignored
        }

        return false;
    }


    protected boolean parseIncoming()
            throws STAException
    {
        new STAError(fromClient,100,"Invalid context : " + context);
        return false;
    }

    protected boolean parseOutgoing()
            throws STAException, CommandException
    {
        new STAError(fromClient,100,"Invalid context : " + context);
        return false;
    }

    protected boolean parsePassiveTransition()
            throws STAException
    {
        new STAError(fromClient,100,"Invalid context : " + context);
        return false;
    }

    protected boolean parseUDP()
            throws STAException
    {
        new STAError(fromClient,100,"Invalid context : " + context);
        return false;
    }

    public abstract String toString ();
}
