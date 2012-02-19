/*
* NoSearchCommand.java
*
* Created on 13 02 2012, 16:14
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

package ru.sincore.cmd.handlers;

import ru.sincore.ClientManager;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.util.ClientUtils;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-13
 */
public class NoSearchCommand extends AbstractCommand
{
    private AbstractClient  client;
    private String          cmd;
    private String          args;


    @Override
    public String execute(String cmd, String args, AbstractClient client)
    {
        this.client = client;
        this.cmd	= cmd;
        this.args	= args;

        if (args == null || args.isEmpty())
        {
            showHelp();
            return null;
        }
        else if ((args.equals("?")))
        {
            showHelp();
            return null;
        }
        else
        {
            AbstractClient clientAbout = ClientManager.getInstance().getClientByNick(args);

            if (clientAbout == null)
            {
                String result = "Client with nick \'" + args + "\' not found!";
                client.sendPrivateMessageFromHub(result);
                return result;
            }

            if (clientAbout.isNoSearch())
            {
                clientAbout.setNoSearch(false);
                ClientUtils.sendMessageToOpChat("Client with nick \'" +
                                                 client.getNick() +
                                                 "\' now can use SEARCH functions");
            }
            else
            {
                clientAbout.setNoSearch(true);
                ClientUtils.sendMessageToOpChat("Client with nick \'" +
                                                 client.getNick() +
                                                 "\' now can\'t use SEARCH functions");
            }
        }

        return "Success";
    }


    private void showHelp()
    {
        StringBuilder message = new StringBuilder();

        message.append("\nSwitch off/on (if function was switched off) SEARCH functions for client until he reconnects to hub.\n");
        message.append("Usage: !nosearch <nick>\n");
        message.append("\tWhere <nick> - client nick\n");

        client.sendPrivateMessageFromHub(message.toString());
    }
}
