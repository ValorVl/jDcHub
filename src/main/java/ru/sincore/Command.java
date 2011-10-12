/*
 * Command.java
 *
 * Created on 06 martie 2007, 16:20
 *
 * DSHub AdcUtils HubSoft
 * Copyright (C) 2007,2008  Eugen Hristev
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

package ru.sincore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.Modules.Modulator;
import ru.sincore.Modules.Module;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.action.*;
import ru.sincore.client.AbstractClient;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

/**
 * Provides a parsing for each AdcUtils command received from client, and makes the states transitions
 * Updates all information and ensures stability.
 *
 * @author Eugen Hristev
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-06
 */

public class Command
{
	private static final Logger log = LoggerFactory.getLogger(Command.class);


    /**
     * Main command handling function.
     * @param client Client from whom command was recieved
     * @param rawCommand      Issued_command of String type actually identifies the given command
     *                      state also of type String Identifies tha state in which tha connection is,
     *                      meaning [ accordingly to arne's draft]:
     *                      PROTOCOL (feature support discovery), IDENTIFY (user identification, static checks),
     *                      VERIFY (password check), NORMAL (normal operation) and DATA (for binary transfers).
     *                      Calling function should send one of this params, that is calling function
     *                      request... Command class does not check params.
     * @throws CommandException Something wrong happend
     * @throws STAException
     */
    public static void handle(AbstractClient client, String rawCommand)
            throws CommandException, STAException
    {

        if (rawCommand.equals(""))
        {
            client.setLastKeepAlive(System.currentTimeMillis());
            return;
        }

        if (rawCommand.length() < 4)
        {
            new STAError(client,
                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Incorrect command");
            return;
        }

        MessageType messageType = null;

        try
        {
            messageType = MessageType.valueOf(rawCommand.substring(0, 1));
        }
        catch (IllegalArgumentException iae)
        {
            new STAError(client,
                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Invalid message type in message : \'" + rawCommand + "\'");
        }

        /*******************************MSG COMMAND*******************************/
        if (rawCommand.substring(1).startsWith("MSG"))
        {
            new MSG(messageType, Context.T, client, rawCommand);
        }
        else
        /*******************************INF COMMAND*******************************/
        if (rawCommand.substring(1).startsWith("INF"))
        {
            new INF(messageType, Context.T, client, rawCommand);
        }
        else
        /*******************************SUP COMMAND*******************************/
        if (rawCommand.substring(1).startsWith("SUP"))
        {
            new SUP(messageType, Context.T, client, rawCommand);
        }
        else
        /*******************************PAS COMMAND*******************************/
        if (rawCommand.substring(1).startsWith("PAS"))
        {
            new PAS(messageType, Context.T, client, rawCommand);
        }
        else
        /*******************************SCH COMMAND*******************************/
        if (rawCommand.substring(1).startsWith("SCH"))
        {
            new SCH(messageType, Context.T, client, rawCommand);
        }
        else
        /*******************************STA COMMAND*******************************/
        if (rawCommand.substring(1).startsWith("STA"))
        {
            new STA(messageType, Context.T, client, rawCommand);
        }
        else
        /*******************************RES COMMAND*******************************/
        if (rawCommand.substring(1).startsWith("RES ")) //direct search result, only active to passive must send this
        {
        }
        else
        /*******************************CTM COMMAND*******************************/
        if (rawCommand.substring(1).startsWith("CTM ")) //direct connect to me
        {
            new CTM(messageType, Context.T, client, rawCommand);
        }
        else
        /*******************************RCM COMMAND*******************************/
        if (rawCommand.substring(1).startsWith("RCM ")) //reverse connect to me
        {
            new RCM(messageType, Context.T, client, rawCommand);
        }


        /** calling plugins...*/

        for (Module myMod : Modulator.myModules)
        {
            myMod.onRawCommand(client, rawCommand);
        }
    }

}
