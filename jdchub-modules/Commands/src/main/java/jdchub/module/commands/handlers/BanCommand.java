/*
* BanCommand.java
*
* Created on 30 11 2011, 15:28
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

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import ru.sincore.ClientManager;
import ru.sincore.Exceptions.NotEnoughWeightException;
import ru.sincore.Exceptions.UserNotFoundException;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.cmd.CommandUtils;
import ru.sincore.i18n.Messages;
import ru.sincore.util.ClientUtils;
import ru.sincore.util.Constants;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-11-30
 */
public class BanCommand extends AbstractCommand
{
    private String cmd;
    private String args;
    private AbstractClient client;

    private String  nick;
    private String  reason;
    private int     banType = Constants.BAN_PERMANENT;
    private Date    banExpiresDate;


    @Override
    public String execute(String cmd, String args, AbstractClient client)
    {
        banExpiresDate = new Date();

        this.client = client;
        this.cmd	= cmd;
        this.args	= args;

        this.nick	= null;

        this.reason	= null;

        LongOpt[] longOpts = new LongOpt[3];

        longOpts[0] = new LongOpt("nick", LongOpt.REQUIRED_ARGUMENT, null, 'n');
        longOpts[1] = new LongOpt("reason", LongOpt.REQUIRED_ARGUMENT, null, 'r');
        longOpts[2] = new LongOpt("time", LongOpt.REQUIRED_ARGUMENT, null, 't');

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

                case 'r':
                    this.reason = getopt.getOptarg();
                    break;

                case 't':
                    // Valid entries for <time> are Ns, Nm, Nh, Nd, Nw, NM, Ny
                    String timeString = getopt.getOptarg();
                    int timeType = timeString.charAt(timeString.length() - 1);
                    int time;
                    try
                    {
                        time = Integer.parseInt(timeString.substring(0, timeString.length() - 1));
                        if (time <= 0)
                        {
                            throw new NumberFormatException("Invalid time format : time must be greater than 0");
                        }
                    }
                    catch (NumberFormatException ex)
                    {
                        showError("Invalid time format.");
                        return "Invalid expires time.";
                    }

                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(banExpiresDate);

                    switch (timeType)
                    {
                        case 's':
                            timeType = Calendar.SECOND;
                            break;

                        case 'm':
                            timeType = Calendar.MINUTE;
                            break;

                        case 'h':
                            timeType = Calendar.HOUR;
                            break;

                        case 'd':
                            timeType = Calendar.DAY_OF_YEAR;
                            break;

                        case 'w':
                            timeType = Calendar.WEEK_OF_YEAR;
                            break;

                        case 'M':
                            timeType = Calendar.MONTH;
                            break;

                        case 'Y':
                            timeType = Calendar.YEAR;
                            break;

                        default:
                            showError("Invalid time format.");
                    }

                    calendar.add(timeType, time);
                    this.banExpiresDate = calendar.getTime();
                    this.banType = Constants.BAN_TEMPORARY;

                    break;

                case '?':
                    showHelp();
                    break;

                default:
                    showHelp();
                    break;
            }
        }

        return ban();
    }


    private void showError(String message)
    {
        client.sendPrivateMessageFromHub(message);
    }


    private String ban()
    {
        if (nick == null || nick.isEmpty() || nick.equals(""))
        {
            showError("--nick parameter is required!");
            return "--nick parameter is required!";
        }

        if (ClientManager.getInstance().getClientByNick(nick) == null)
        {
            showError("Client with nick \'" + nick + "\' not found.");
            return "Client not found";
        }

        try
        {
            if (!ClientUtils.ban(client.getNick(), nick, true, banType, banExpiresDate, reason))
            {
                return "Client was not banned.";
            }
        }
        catch (Exception e)
        {
            client.sendPrivateMessageFromHub(e.toString());
            return "Client was not banned.";
        }

        switch (banType)
        {
            case Constants.BAN_TEMPORARY:
                ClientUtils.sendMessageToOpChat(Messages.get("core.opchat.client_ban_temp",
                                                             new Object[]
                                                             {
                                                                     nick,
                                                                     client.getNick(),
                                                                     reason,
                                                                     banExpiresDate
                                                             }));
                break;

            case Constants.BAN_PERMANENT:
                ClientUtils.sendMessageToOpChat(Messages.get("core.opchat.client_ban_perm",
                                                             new Object[]
                                                             {
                                                                     nick,
                                                                     client.getNick(),
                                                                     reason
                                                             }));
                break;
        }

        return "Successfully banned.";
    }


    private void showHelp()
    {
        client.sendPrivateMessageFromHub(Messages.get("core.commands.ban.help_text", client.isFeature("LC")));
    }
}
