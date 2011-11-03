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
import ru.sincore.Broadcast;
import ru.sincore.Main;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCmd;
import ru.sincore.cmd.CmdUtils;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-10-31
 */
public class RestartHandler extends AbstractCmd
{
    private static final Logger log = LoggerFactory.getLogger(RestartHandler.class);
    private String marker = Marker.ANY_NON_NULL_MARKER;

    private AbstractClient  client;
    private String          cmd;
    private String          args;

    private long            timeout = 5000;
    private String          message;


    @Override
    public void execute(String cmd, String args, AbstractClient client)
    {
        this.cmd = cmd;
        this.args = args;
        this.client = client;

        this.message = null;

        LongOpt[] longOpts = new LongOpt[3];

        longOpts[0] = new LongOpt("timeout", LongOpt.REQUIRED_ARGUMENT, null, 't');
        longOpts[1] = new LongOpt("message", LongOpt.REQUIRED_ARGUMENT, null, 'm');

        String[] argArray = CmdUtils.strArgToArray(args);

        Getopt getopt = new Getopt("restart", argArray, "t:m:", longOpts);

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

        restartHub();
    }


    private void restartHub()
    {
        if (this.message != null)
        {
            // TODO [lh] broadcast text message
            Broadcast.getInstance().broadcast(message);
            log.debug("Broadcasting message.");
        }

        try
        {
            log.debug("Waiting...");
            Thread.sleep(timeout);
        }
        catch (InterruptedException ex)
        {
            // ignored
        }

        log.debug("Calling restart command from Main class.");
        Main.restart();
    }


    private void showHelp()
    {
        StringBuilder result = new StringBuilder();

        result.append("\nRestart hub.\n");
        result.append("Usage: !restart [--timeout <timeout>] [--message <message>]\n");
        result.append("\tWhere <timeout> - Timeout befor hub restart (in sec).\n");
        result.append("\t\t<message> - Message, which will be sent to all clients from hub.\n");

        client.sendPrivateMessageFromHub(result.toString());
    }
}
