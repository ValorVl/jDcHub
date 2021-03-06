/*
* NoCtmCommand.java
*
* Created on 13 02 2012, 16:13
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

package jdchub.module.commands.handlers;

import ru.sincore.ClientManager;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.util.ClientUtils;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-13
 */
public class NoCtmCommand extends AbstractCommand
{
    private AbstractClient  client;
    private String          cmd;
    private String          args;


    @Override
    public String execute(String cmd, String args, AbstractClient commandOwner)
    {
        this.client = commandOwner;
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
                commandOwner.sendPrivateMessageFromHub(result);
                return result;
            }

            if (clientAbout.isNoTransfer())
            {
                clientAbout.setNoTransfer(false);
                ClientUtils.sendMessageToOpChat("Client with nick \'" +
                                                 commandOwner.getNick() +
                                                 "\' now can connect to other users.");
            }
            else
            {
                clientAbout.setNoTransfer(true);
                ClientUtils.sendMessageToOpChat("Client with nick \'" +
                                                 commandOwner.getNick() +
                                                 "\' now can\'t connect to other users");
            }
        }

        return "Success";
    }


    private void showHelp()
    {
        StringBuilder message = new StringBuilder();

        message.append("\nSwitch off/on (if function was switched off) connection functions for client\n");
        message.append("Usage: !noctm <nick>\n");
        message.append("\tWhere <nick> - client nick\n");

        client.sendPrivateMessageFromHub(message.toString());
    }
}
