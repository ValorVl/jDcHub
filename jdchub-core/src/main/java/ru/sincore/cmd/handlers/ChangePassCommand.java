/*
* ChangePassCommand.java
*
* Created on 28 11 2011, 11:55
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
import ru.sincore.ClientManager;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.cmd.CommandUtils;
import ru.sincore.i18n.Messages;
import ru.sincore.util.ClientUtils;


/**
 * @author Alexey 'lh' Antonov
 * @since 2011-11-28
 */
public class ChangePassCommand extends AbstractCommand
{
    private AbstractClient  client;
    private String          cmd;
    private String          args;

    private String oldPassword;
    private String newPassword;
    private String nick;

    @Override
    public String execute(String cmd, String args, AbstractClient client)
    {
        this.client = client;
        this.cmd	= cmd;
        this.args	= args;

        if (!client.isRegistred())
        {
            String result = Messages.get(Messages.REGISTER_BEFOR_CHANGE_PASSWORD,
                                         (String) client.getExtendedField("LC"));
            sendError(result);
            return null;
        }

        LongOpt[] longOpts = new LongOpt[3];

        longOpts[0] = new LongOpt("nick", LongOpt.REQUIRED_ARGUMENT, null, 'n');
        longOpts[1] = new LongOpt("old", LongOpt.REQUIRED_ARGUMENT, null, 'o');
        longOpts[2] = new LongOpt("new", LongOpt.REQUIRED_ARGUMENT, null, 'p');

        String[] argArray = CommandUtils.strArgToArray(args);

        Getopt getopt = new Getopt(cmd, argArray, "n:o:p:", longOpts);

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

                case 'o':
                    this.oldPassword = getopt.getOptarg();
                    break;

                case 'p':
                    this.newPassword = getopt.getOptarg();
                    break;

                default:
                    showHelp();
                    break;
            }
        }

        return changePassword();
    }


    private String changePassword()
    {
        String result = null;
        AbstractClient clientToChangePassword = null;

        // if nick is not null and client is op
        // get client password change for
        if (nick != null && client.isOp())
        {
            clientToChangePassword = ClientManager.getInstance().getClientByNick(nick);
        }
        else
        {
            clientToChangePassword = client;
        }

        if ((newPassword == null) || newPassword.equals("") || newPassword.isEmpty())
        {
            result = Messages.get(Messages.PASSWORD_REQUIRED, (String)client.getExtendedField("LC"));
            sendError(result);
            return result;
        }

        if (client.isOp() && client.getWeight() < clientToChangePassword.getWeight())
        {
            result = Messages.get(Messages.LOW_WEIGHT, (String)client.getExtendedField("LC"));
            sendError(result);
            return result;
        }

        if (clientToChangePassword.getPassword() != null &&
             !clientToChangePassword.getPassword().isEmpty() &&
             !clientToChangePassword.getPassword().equals("") &&
             !client.isOp() &&
             !clientToChangePassword.getPassword().equals(oldPassword))
        {
            result = Messages.get(Messages.PASSWORDS_NOT_EQUAL,
                                  (String) client.getExtendedField("LC"));
            sendError(result);
            return result;
        }

        clientToChangePassword.setPassword(newPassword);
        clientToChangePassword.sendPrivateMessageFromHub(Messages.get(Messages.PASSWORD_CHANGED,
                                                                      newPassword,
                                                                      (String)clientToChangePassword
                                                                              .getExtendedField("LC")));
        clientToChangePassword.disconnect();

        ClientUtils.sendMessageToOpChat(Messages.get("core.opchat.client_changed_password",
                                                     new Object[]
                                                     {
                                                             clientToChangePassword.getNick()
                                                     }));
        return result;
    }


    private void showHelp()
    {
        sendError(Messages.get("core.commands.changepass.help_text",
                               (String)client.getExtendedField("LC")));
    }


    private void sendError(String message)
    {
        client.sendPrivateMessageFromHub(message);
    }
}
