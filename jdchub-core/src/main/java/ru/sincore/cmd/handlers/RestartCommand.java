/*
* RestartHandler.java
*
* Created on 31 october 2011, 16:12
*
* Copyright (C) 2011 Alexey 'lh' Antonov
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package ru.sincore.cmd.handlers;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.Main;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.cmd.CommandUtils;
import ru.sincore.i18n.Messages;
import ru.sincore.util.ClientUtils;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-10-31
 */
public class RestartCommand extends AbstractCommand
{
    private static final Logger log = LoggerFactory.getLogger(RestartCommand.class);
    private String marker = Marker.ANY_NON_NULL_MARKER;

    private AbstractClient  client;
    private String          cmd;
    private String          args;

    private long            timeout = 5000;
    private String          message;


    @Override
    public String execute(String cmd, String args, AbstractClient client)
    {
        this.cmd = cmd;
        this.args = args;
        this.client = client;

        this.message = null;

        LongOpt[] longOpts = new LongOpt[3];

        longOpts[0] = new LongOpt("timeout", LongOpt.REQUIRED_ARGUMENT, null, 't');
        longOpts[1] = new LongOpt("message", LongOpt.REQUIRED_ARGUMENT, null, 'm');

        String[] argArray = CommandUtils.strArgToArray(args);

        Getopt getopt = new Getopt(cmd, argArray, "t:m:", longOpts);

        int c;

        while ((c = getopt.getopt()) != -1)
        {
            switch (c)
            {
                case 't':
                    this.timeout = Long.parseLong(getopt.getOptarg()) * 1000; // from sec to ms
                    break;

                case 'm':
                    this.message = getopt.getOptarg();
                    break;

                case '?':
                    showHelp();
                    break;
            }
        }

        ClientUtils.sendMessageToOpChat(client.getNick() + " called !restart command.");

        restartHub();

        return "Hub restarted.";
    }


    private void restartHub()
    {
        if (this.message != null)
        {
            ClientUtils.broadcastTextMessageFromHub(message);
        }

        try
        {
            Thread.sleep(timeout);
        }
        catch (InterruptedException ex)
        {
            // ignored
        }

        Main.restart();
    }


    private void showHelp()
    {
        client.sendPrivateMessageFromHub((Messages.get("core.commands.restart.help_text",
                                (String) client.getExtendedField("LC"))));
    }
}
