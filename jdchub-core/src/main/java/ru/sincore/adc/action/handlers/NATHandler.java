/*
* NATHandler.java
*
* Created on 26 06 2012, 14:45
*
* Copyright (C) 2012 Alexey 'lh' Antonov
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

package ru.sincore.adc.action.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.ClientManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.action.actions.NAT;
import ru.sincore.client.AbstractClient;
import ru.sincore.i18n.Messages;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-06-26
 */
public class NATHandler extends AbstractActionHandler<NAT>
{
    private static final Logger log = LoggerFactory.getLogger(NATHandler.class);


    public NATHandler(AbstractClient sourceClient, NAT action)
    {
        super(sourceClient, action);
    }

    @Override
    protected boolean validate()
            throws CommandException, STAException
    {
        if (!super.validate())
        {
            return false;
        }

        if (action.getMessageType() != MessageType.D &&
            action.getMessageType() != MessageType.E)
        {
            new STAError(client,
                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         Messages.INCORRECT_MESSAGE_TYPE).send();
            return false;
        }

        // looking for client by target sid
        AbstractClient targetClient = ClientManager.getInstance().getClientBySID(action.getTargetSID());
        if (targetClient == null)
        {
            //talking to inexisting client
            //not kick, maybe the other client just left after he sent the msg;
            new STAError(targetClient,
                         Constants.STA_SEVERITY_RECOVERABLE,
                         Messages.WRONG_TARGET_SID).send();

            return false;
        }

        return true;
    }

    @Override
    public void handle()
            throws STAException
    {
        if (client.checkBannedByShare() || client.checkNoTransfer())
        {
            return;
        }

        try
        {
            if (!validate())
            {
                return;
            }

            AbstractClient targetClient = ClientManager.getInstance().getClientBySID(action.getTargetSID());
            targetClient.sendRawCommand(action.getRawCommand());
            if (action.getMessageType() == MessageType.E)
            {
                client.sendRawCommand(action.getRawCommand());
            }
        }
        catch (CommandException e)
        {
            log.error(e.toString());
        }
    }
}
