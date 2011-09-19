/*
 * SID.java
 *
 * Created on 16 september 2011, 13:45
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
 * @author Alexey 'lh' Antonov
 * @since 2011-09-16
 */
public class SID extends Action
{
    private static final Logger log = LoggerFactory.getLogger(SID.class);


    SID(MessageType messageType,
           int context,
           Client toClient)
    {
        super(messageType, context, null, toClient);

        // setup available contexts and states for this action
        super.availableContexts = Context.F;
        super.availableStates = State.PROTOCOL;
    }


    SID(MessageType messageType,
           int context,
           Client toClient,
           String args)
    {
        this(messageType, context, toClient);
        this.params = args;
        parse(params);
    }


    @Override
    protected boolean parse(String args)
    {
        // TODO replace with right SID validation
        if (args.length() != 4)
            return false;

        params = args;
        paramsAreValid = true;

        return true;
    }


    @Override
    public String toString()
    {
        return messageType.toString() + "SID " + params;
    }
}
