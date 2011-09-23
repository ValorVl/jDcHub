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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.Client;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;


/**
 * Base class for all ADC actions.
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-16
 */
public abstract class Action
{
    private final static Logger log = LoggerFactory.getLogger(Action.class);
    private String marker = Marker.ANY_MARKER;

    protected int   availableContexts = Context.INVALID_CONTEXT;
    protected int   availableStates   = State.INVALID_STATE;

    protected int         context     = Context.INVALID_CONTEXT;
    protected MessageType messageType = MessageType.INVALID_MESSAGE_TYPE;

    protected Client fromClient;
    protected Client toClient;

    protected String rawCommand;
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

	public Action()
	{
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
     * @param rawCommand raw command string
     * @return parsing status. True if parameters were parsed, False otherwise
     * @throws CommandException
     * @throws STAException
     */
    public final void parse (String rawCommand) throws STAException, CommandException
    {
        this.rawCommand = rawCommand;

        try
        {
            switch (context)
            {
                case Context.F:
                    parseOutgoing();
                    break;
                case Context.T:
                    parseIncoming();
                    break;
                case Context.C:
                    parsePassiveTransition();
                    break;
                case Context.U:
                    parseUDP();
                    break;
                default:
                    new STAError(fromClient, Constants.STA_SEVERITY_RECOVERABLE, "Invalid context : " + context);
                    break;
            }
        }
        catch (STAException sta)
        {
            log.debug(marker, sta);
        }
        catch (CommandException ce)
        {
            log.debug(marker, ce);
        }

        paramsAreValid = true;
    }


    protected void parseIncoming()
            throws STAException, CommandException
	{
        new STAError(fromClient, Constants.STA_SEVERITY_RECOVERABLE, "Invalid context : " + context);
    }

    protected void parseOutgoing()
            throws STAException, CommandException
    {
        new STAError(fromClient, Constants.STA_SEVERITY_RECOVERABLE, "Invalid context : " + context);
    }

    protected void parsePassiveTransition()
            throws STAException
    {
        new STAError(fromClient, Constants.STA_SEVERITY_RECOVERABLE, "Invalid context : " + context);
    }

    protected void parseUDP()
            throws STAException
    {
        new STAError(fromClient, Constants.STA_SEVERITY_RECOVERABLE, "Invalid context : " + context);
    }

    public abstract String toString ();
}
