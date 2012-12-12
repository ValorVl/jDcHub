/*
* TopicCommand.java
*
* Created on 29 11 2011, 12:47
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

package jdchub.module.commands.handlers;

import ru.sincore.BigTextManager;
import ru.sincore.Broadcast;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.adc.Flags;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.action.actions.INF;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.i18n.Messages;
import ru.sincore.util.AdcUtils;
import ru.sincore.util.ClientUtils;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-11-29
 */
public class TopicCommand extends AbstractCommand
{
    @Override
    public String execute(String cmd, String args, AbstractClient commandOwner)
    {
        if (args == null || args.isEmpty() || args.equals(""))
        {
            commandOwner.sendPrivateMessageFromHub(Messages.get("core.commands.topic.help_text",
                                                          (String) commandOwner.getExtendedField("LC")));
            return "Help shown";
        }

        BigTextManager bigTextManager = new BigTextManager();
        if (bigTextManager.setText(BigTextManager.TOPIC, AdcUtils.toAdcString(args)))
        {
            ClientUtils.sendMessageToOpChat(Messages.get("core.commands.topic.changed",
                                                         new Object[]
                                                         {
                                                                 commandOwner.getNick(),
                                                                 args
                                                         }));

            // send new description of hub bot to all clients
            try
            {
                // get hub bot sid
                String botSID = ConfigurationManager.getInstance()
                                                     .getString(ConfigurationManager.HUB_SID);
                // create hub bot inf message with description field only
                INF inf = new INF();
                // set message type to broadcast
                inf.setMessageType(MessageType.B);
                // set hub bot sid
                inf.setSourceSID(botSID);
                // set new hub bot description
                inf.setFlagValue(Flags.DESCRIPTION, AdcUtils.toAdcString(args));

                // broadcast new inf
                Broadcast.getInstance()
                         .broadcast(inf.getRawCommand(),
                                    ClientManager.getInstance().getClientBySID(botSID));
            }
            catch (Exception ex)
            {
                //ignore
            }

            return "New topic set.";
        }
        else
        {
            commandOwner.sendPrivateMessageFromHub("New topic not set.");
            return "New topic not set.";
        }
    }
}
