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

package ru.sincore.adc.action_obsolete;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.client.AbstractClient;
import ru.sincore.i18n.Messages;
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

    protected AbstractClient fromClient;
    protected AbstractClient toClient;

    protected String rawCommand;
    protected boolean paramsAreValid = false;


    protected Action(MessageType messageType,
           int context,
           AbstractClient fromClient,
           AbstractClient toClient)
    {
        this.messageType = messageType;
        this.context = context;
        this.fromClient = fromClient;
        this.toClient = toClient;
    }

	public Action()
	{
	}


	public AbstractClient getFromClient ()
    {
        return fromClient;
    }


    public AbstractClient getToClient ()
    {
        return toClient;
    }


    public void setFromClient (AbstractClient fromClient)
    {
        this.fromClient = fromClient;
    }


    public void setToClient (AbstractClient toClient)
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
            (availableStates & fromClient.getState()) == State.INVALID_STATE)
            return false;

        if ((toClient != null) &&
            (availableStates & toClient.getState()) == State.INVALID_STATE)
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


    private void checkMinimalCommandLength()
            throws STAException
    {
        int minimalCommandLength = 0;
        // check to minimal actionName length
        // ATTENTION! Magic numbers !!!
        switch (messageType)
        {
            case INVALID_MESSAGE_TYPE:
                break;

            case C:
            case I:
            case H:
                // message type (1 char) + actionName (3 chars) = 4
                minimalCommandLength = 4;
                break;

            case B:
            case F:
                // message type (1 char) + actionName (3 chars) +
                // separator (1 char) + sid (4 chars) = 9
                minimalCommandLength = 9;
                break;

            case D:
            case E:
                // message type (1 char) + actionName (3 chars) +
                // separator (1 char) + sid (4 chars) +
                // separator (1 char) + sid (4 chars)
                minimalCommandLength = 14;
                break;

            case U:
                break;
        }

        if (rawCommand.length() < minimalCommandLength)
            new STAError(fromClient,
                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         Messages.INCORRECT_COMMAND);
    }


    /**
     * Parse additional parameters.
     * For incoming messages that means incoming string without
     * 5 heading simbols containing message type, actionName and separator (space).
     *
     * @param rawCommand raw actionName string
     * @return parsing status. True if parameters were parsed, False otherwise
     * @throws CommandException
     * @throws STAException
     */
    public final void parse (String rawCommand)
            throws STAException, CommandException
    {
        this.rawCommand = rawCommand;

        checkMinimalCommandLength();

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
                    new STAError(fromClient, Constants.STA_SEVERITY_RECOVERABLE, Messages.INVALID_CONTEXT, context).send();
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


    /**
     * Parse method for context 'T'. Reimplement this function if action work in given context
     * @throws STAException
     * @throws CommandException
     */
    protected void parseIncoming()
            throws STAException, CommandException
	{
        new STAError(fromClient, Constants.STA_SEVERITY_RECOVERABLE, Messages.INVALID_CONTEXT, context).send();
    }


    /**
     * Parse method for context 'F'. Reimplement this function if action work in given context
     * @throws STAException
     * @throws CommandException
     */
    protected void parseOutgoing()
            throws STAException, CommandException
    {
        new STAError(fromClient, Constants.STA_SEVERITY_RECOVERABLE, Messages.INVALID_CONTEXT, context).send();
    }


    /**
     * Parse method for context 'C'. Reimplement this function if action work in given context
     * @throws STAException
     */
    protected void parsePassiveTransition()
            throws STAException
    {
        new STAError(fromClient, Constants.STA_SEVERITY_RECOVERABLE, Messages.INVALID_CONTEXT, context).send();
    }


    /**
     * Parse method for context 'U'. Reimplement this function if action work in given context
     * @throws STAException
     */
    protected void parseUDP()
            throws STAException
    {
        new STAError(fromClient, Constants.STA_SEVERITY_RECOVERABLE, Messages.INVALID_CONTEXT, context).send();
    }

    public abstract String toString ();
}