/*
* BroadcastCommand.java
*
* Created on 25 11 2011, 16:48
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

import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.util.ClientUtils;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-11-25
 */
public class BroadcastCommand extends AbstractCommand
{
    private AbstractClient client;

    @Override
    public String execute(String cmd, String args, AbstractClient client)
    {
        this.client = client;

        if (args.equals("") || args.isEmpty())
        {
            showHelp();
            return null;
        }

        ClientUtils.broadcastTextMessageFromHub(args);

        return null;
    }


    private void showHelp()
    {
        StringBuilder result = new StringBuilder();

        result.append("\nBroadcast message as PM from hub.\n");
        result.append("Usage: !broadcast <message>\n");
        result.append("\tWhere\n");
        result.append("\t\t<message> - text message\n");

        client.sendPrivateMessageFromHub(result.toString());
    }
}
