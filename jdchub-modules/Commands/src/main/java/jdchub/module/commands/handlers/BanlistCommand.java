/*
* BanlistCommand.java
*
* Created on 11 12 2012, 17:06
*
* Copyright (C) 2012 Alexey 'lh' Antonov
*
* For more info read COPYRIGHT file in project root directory.
*
*/

package jdchub.module.commands.handlers;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import org.apache.commons.lang.StringUtils;
import ru.sincore.Exceptions.STAException;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.cmd.CommandUtils;
import ru.sincore.db.dao.BanListDAO;
import ru.sincore.db.dao.BanListDAOImpl;
import ru.sincore.db.pojo.BanListPOJO;
import ru.sincore.i18n.Messages;
import ru.sincore.util.Constants;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-12-11
 */
public class BanlistCommand extends AbstractCommand
{
    private AbstractClient commandOwner;

    @Override
    public String execute(String cmd, String args, AbstractClient commandOwner)
            throws STAException
    {
        this.commandOwner = commandOwner;

        LongOpt[] longOpts = new LongOpt[3];

        longOpts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longOpts[1] = new LongOpt("show", LongOpt.OPTIONAL_ARGUMENT, null, 's');
        longOpts[2] = new LongOpt("active", LongOpt.OPTIONAL_ARGUMENT, null, 'a');
        longOpts[3] = new LongOpt("expired", LongOpt.OPTIONAL_ARGUMENT, null, 'e');
        longOpts[4] = new LongOpt("nick", LongOpt.REQUIRED_ARGUMENT, null, 'n');
        longOpts[5] = new LongOpt("ip", LongOpt.REQUIRED_ARGUMENT, null, 'i');

        String[] argArray = CommandUtils.strArgToArray(args);

        Getopt getopt = new Getopt(cmd, argArray, "hsaen:i:", longOpts);

        int c;

        if ((c = getopt.getopt()) != -1)
        {
            switch (c)
            {
                case 'a':
                    show(getPage(getopt.getOptarg()), Constants.ACTIVE);
                    return "Shown active bans.";

                case 'e':
                    show(getPage(getopt.getOptarg()), Constants.EXPIRED);
                    return "Shown expired bans.";

                case 'n':
                    return find(getopt.getOptarg(), null);

                case 'i':
                    return find(null, getopt.getOptarg());

                case '?':
                case 'h':
                    showHelp();
                    return "Help shown.";

                case 's':
                default:
                    return show(getPage(getopt.getOptarg()), Constants.ALL);
            }
        }

        return "Command executed, but nothing happend.";
    }


    private String find(String nick, String ip)
    {
        if (StringUtils.isEmpty(nick) && StringUtils.isEmpty(ip))
        {
            showMessage("User or ip doesn't banned.");
            return "User or ip doesn't banned.";
        }

        BanListDAO bans = new BanListDAOImpl();
        BanListPOJO ban = bans.getBan(nick, ip);

        if (ban == null)
        {
            showMessage("User or ip doesn't banned.");
            return "User or ip doesn't banned.";
        }

        StringBuilder result = new StringBuilder();

        result.append(ban.getId()).append(" - ");

        SimpleDateFormat
                dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z", Locale.US);
        result.append(dateFormat.format(ban.getDateStop())).append(" - ");

        result.append(ban.getOpNick()).append(" - ");
        result.append(ban.getReason()).append("\n");

        showMessage(result.toString());

        return "Ban shown.";
    }


    private String show(int page, int banShowType)
    {
        BanListDAO bans = new BanListDAOImpl();
        List<BanListPOJO> banList = bans.getAllBans(banShowType, page, 10);

        if ((banList == null) || banList.isEmpty())
        {
            showMessage("No bans found.");
            return "No bans found.";
        }

        StringBuilder result = new StringBuilder();
        result.append("List of 10 bans from ").append(page*10).append(" :\n");

        for (BanListPOJO ban : banList)
        {
            result.append(ban.getId()).append(" - ");

            if (banShowType == Constants.ALL)
            {
                if ((ban.getDateStop().getTime() - System.currentTimeMillis()) > 0)
                {
                    result.append("active");
                }
                else
                {
                    result.append("expired");
                }

                result.append(" - ");
            }

            if (!StringUtils.isEmpty(ban.getNick()))
                result.append(ban.getNick());
            else
                result.append(ban.getIp());

            result.append(" - ");

            SimpleDateFormat
                    dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z", Locale.US);
            result.append(dateFormat.format(ban.getDateStop()));

            result.append(" - ");

            result.append(ban.getOpNick());

            result.append(" - ");

            result.append(ban.getReason());

            result.append("\n");
        }

        showMessage(result.toString());

        return "Bans shown.";
    }


    private int getPage(String arg)
    {
        int page = 1;

        if (arg != null)
        {
            page = Integer.valueOf(arg);

            if (page < 1)
            {
                page = 1;
            }
        }

        return page - 1;
    }


    private void showMessage(String message)
    {
        commandOwner.sendPrivateMessageFromHub(message);
    }


    private void showHelp()
    {
        commandOwner.sendPrivateMessageFromHub(Messages.get("core.commands.banlist.help_text",
                                                            commandOwner.isFeature("LC")));
    }

}
