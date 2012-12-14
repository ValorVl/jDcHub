/*
* UnbanCommand.java
*
* Created on 11 12 2012, 17:06
*
* Copyright (C) 2012 Alexey 'lh' Antonov
*
* For more info read COPYRIGHT file in project root directory.
*
*/

package jdchub.module.commands.handlers;

import org.apache.commons.lang.StringUtils;
import ru.sincore.Exceptions.STAException;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.db.dao.BanListDAO;
import ru.sincore.db.dao.BanListDAOImpl;
import ru.sincore.db.pojo.BanListPOJO;
import ru.sincore.i18n.Messages;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-12-11
 */
public class UnbanCommand extends AbstractCommand
{
    private AbstractClient commandOwner;


    @Override
    public String execute(String cmd, String args, AbstractClient commandOwner)
            throws STAException
    {
        this.commandOwner = commandOwner;

        if (StringUtils.isEmpty(args))
        {
            showHelp();
            return "Help shown.";
        }

        Long banId;

        try
        {
            banId = Long.valueOf(args);
        }
        catch (NumberFormatException e)
        {
            showMessage("Invalid ban id format.");
            return "Invalid ban id format.";
        }

        BanListDAO bans = new BanListDAOImpl();
        BanListPOJO ban = bans.remove(banId);
        if (ban == null)
        {
            showMessage("No bans for unban.");
            return "No bans for unban.";
        }

        StringBuilder banInfo = new StringBuilder();

        banInfo.append("Info about deleted ban:\n");
        banInfo.append("\tid : ").append(ban.getId()).append("\n");
        banInfo.append("\tstart date : ").append(ban.getDateStart()).append("\n");
        banInfo.append("\tnick : ").append(ban.getNick()).append("\n");
        banInfo.append("\tip : ").append(ban.getIp()).append("\n");
        banInfo.append("\texpire date : ").append(ban.getDateStop()).append("\n");
        banInfo.append("\top nick : ").append(ban.getOpNick()).append("\n");
        banInfo.append("\treason : ").append(ban.getReason()).append("\n");

        showMessage(banInfo.toString());

        return "Unbaned.";
    }


    private void showMessage(String message)
    {
        commandOwner.sendPrivateMessageFromHub(message);
    }


    private void showHelp()
    {
        commandOwner.sendPrivateMessageFromHub(Messages.get("core.commands.unban.help_text",
                                                            commandOwner.isFeature("LC")));
    }
}
