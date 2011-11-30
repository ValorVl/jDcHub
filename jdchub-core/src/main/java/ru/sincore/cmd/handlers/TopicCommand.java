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

package ru.sincore.cmd.handlers;

import ru.sincore.BigTextManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.i18n.Messages;
import ru.sincore.util.ClientUtils;
import ru.sincore.util.MessageUtils;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-11-29
 */
public class TopicCommand extends AbstractCommand
{
    @Override
    public String execute(String cmd, String args, AbstractClient client)
    {
        if (args == null || args.isEmpty() || args.equals(""))
        {
            client.sendPrivateMessageFromHub(Messages.get("core.commands.topic.help_text",
                                                          (String) client.getExtendedField("LC")));
            return "Help shown";
        }

        BigTextManager bigTextManager = new BigTextManager();
        if (bigTextManager.setText(BigTextManager.TOPIC, args))
        {
            MessageUtils.sendMessageToOpChat("New topic set by " + client.getNick() + " : " + args);
            return "New topic set.";
        }
        else
        {
            client.sendPrivateMessageFromHub("New topic not set.");
            return "New topic not set.";
        }
    }
}
