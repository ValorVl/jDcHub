/*
 * SCH.java
 *
 * Created on 05 october 2011, 14:27
 *
 * Copyright (C) 2011  Alexey 'lh' Antonov
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
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.client.AbstractClient;

import java.util.StringTokenizer;

/**
 * Class for SCH action.
 * Description look at <a href="http://adc.sourceforge.net/ADC.html#_actions">ADC Actions</a>
 * section 5.3.6

 * @author Alexey 'lh' Antonov
 * @author Alexander 'hatred' Drozdov
 * @since 2011-10-05
 */
public class SCH extends MSG
{
    private static final Logger log = LoggerFactory.getLogger(SCH.class);


    public SCH(MessageType messageType, int context, AbstractClient client, String rawCommand)
            throws CommandException, STAException
    {
        super(messageType,
              context,
              (context == Context.T ? client : null),
              (context == Context.F ? null : client));

        this.availableContexts = Context.F | Context.T | Context.C;
        this.availableStates = State.NORMAL;

        this.rawCommand = rawCommand;
        parse(rawCommand);
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
                Broadcast.getInstance().broadcast(rawCommand);
                break;

            case C:
            case I:
            case H:
                break;

            case E:
            case D:
                break;

            case F:
                super.parseIncoming();
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
