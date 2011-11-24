/*
* DeleteUserHandler.java
*
* Created on 15 11 2011, 15:39
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

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.cmd.CommandUtils;
import ru.sincore.db.dao.ClientListDAO;
import ru.sincore.db.dao.ClientListDAOImpl;
import ru.sincore.i18n.Messages;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-11-15
 */
public class DeleteUserCommand extends AbstractCommand
{
    private AbstractClient client;
    private String         cmd;
    private String         args;

    private String nick;
    private String reason;


    @Override
    public String execute(String cmd, String args, AbstractClient client)
    {
        this.client = client;
        this.cmd = cmd;
        this.args = args;

        this.nick = null;
        this.reason = null;

        LongOpt[] longOpts = new LongOpt[3];

        longOpts[0] = new LongOpt("nick", LongOpt.REQUIRED_ARGUMENT, null, 'n');
        longOpts[1] = new LongOpt("reason", LongOpt.REQUIRED_ARGUMENT, null, 'r');

        String[] argArray = CommandUtils.strArgToArray(args);

        Getopt getopt = new Getopt(cmd, argArray, "n:r:", longOpts);

        if (argArray.length < 1)
        {
            showHelp();
            return null;
        }

        int c;

        while ((c = getopt.getopt()) != -1)
        {
            switch (c)
            {
                case 'n':
                    this.nick = getopt.getOptarg();
                    break;

                case '?':
                    showHelp();
                    break;

                case 'r':
                    this.reason = getopt.getOptarg();
                    break;

                default:
                    showHelp();
                    break;
            }
        }

        return deleteUser();
    }


    private String deleteUser()
    {
        if (nick == null)
        {
            String result = Messages.get(Messages.NICK_REQUIRED, (String) client.getExtendedField("LC"));
            sendError(result);
            return result;
        }

        ClientListDAO clientListDAO = new ClientListDAOImpl();

        boolean deleted = clientListDAO.delClient(nick);

        if (!deleted)
        {
            String result = "Client you want to delete is not a registred user!";
            sendError(result);
            return result;
        }

        return "Successfully deleted.";
    }


    private void sendError(String message)
    {
        client.sendPrivateMessageFromHub(message);
    }


    private void showHelp()
    {
        sendError((Messages.get("core.commands.delete_user.help_text",
                                (String) client.getExtendedField("LC"))));
    }
}
