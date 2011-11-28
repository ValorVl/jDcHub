/*
 * GrantHandler.java
 *
 * Copyright (C) 2011 Alexey 'lh' Antonov
 * Copyright (C) 2011 Valor
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.STAException;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.cmd.CommandUtils;
import ru.sincore.i18n.Messages;

/**
 * Command for manipulation user right weight
 *
 * @author Valor
 */
public class GrantCommand extends AbstractCommand
{
    private AbstractClient  client;
    private String          cmd;
    private String          args;

    private String          nick;
    private Integer         weight;


	@Override
	public String execute(String cmd, String args, AbstractClient client)
	{
        this.client = client;
        this.cmd	= cmd;
        this.args	= args;

        this.nick	= null;
        this.weight	= null;

        LongOpt[] longOpts = new LongOpt[3];

        longOpts[0] = new LongOpt("nick", LongOpt.REQUIRED_ARGUMENT, null, 'n');
        longOpts[1] = new LongOpt("weight", LongOpt.REQUIRED_ARGUMENT, null, 'w');
        longOpts[2] = new LongOpt("type", LongOpt.REQUIRED_ARGUMENT, null, 't');

        String[] argArray = CommandUtils.strArgToArray(args);

        Getopt getopt = new Getopt(cmd, argArray, "n:w:t:", longOpts);

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

                case ':':
                    sendError(Messages.get(Messages.ARGUMENT_REQUIRED,
                                           (char)getopt.getOptopt(),
                                           (String)client.getExtendedField("LC")));
                    break;

                case '?':
                    showHelp();
                    break;

                case 't':
                    String arg = getopt.getOptarg();
                    if (arg.equals("Op"))
                    {
                        this.weight = 70;
                    }
                    else if (arg.equals("SU"))
                    {
                        this.weight = 90;
                    }
                    break;

                case 'w':
                    String argument = getopt.getOptarg();
                    try
                    {
                        this.weight = Integer.parseInt(argument);
                    }
                    catch (NumberFormatException nfe)
                    {
                        sendError(Messages.get(Messages.INVALID_WEIGHT,
                                                   (String)client.getExtendedField("LC")));
                    }
                    break;

                default:
                    showHelp();
                    break;
            }
        }

        return changedWeight();
	}


	/**
	 *  Update weight
	 */
	private String changedWeight()
	{
        String result = null;

        if (nick == null)
        {
            result = Messages.get(Messages.NICK_REQUIRED, (String)client.getExtendedField("LC"));
            sendError(result);
            return result;
        }

        if (weight == null)
        {
            result = Messages.get(Messages.WEIGHT_REQUIRED, (String)client.getExtendedField("LC"));
            sendError(result);
            return result;
        }

        if (this.client.getWeight() < this.weight)
        {
            result = Messages.get(Messages.LOW_WEIGHT, (String)client.getExtendedField("LC"));
            sendError(result);
            return result;
        }


        AbstractClient toClient = ClientManager.getInstance().getClientByNick(nick);
        if (toClient == null)
        {
            result = Messages.get(Messages.NICK_NOT_EXISTS,
                                   nick,
                                   (String)client.getExtendedField("LC"));
            sendError(result);
            return result;
        }

        if (!toClient.isRegistred())
        {
            result = "Client you want to grant rights is not a registred user!";
            sendError(result);
            return result;
        }

        if (client.getWeight() < toClient.getWeight())
        {
            result = Messages.get(Messages.LOW_WEIGHT, (String)client.getExtendedField("LC"));
            sendError(result);
            return result;
        }

        toClient.setWeight(this.weight);
        toClient.setClientTypeByWeight(this.weight);

        try
        {
            toClient.storeInfo();
        }
        catch (STAException staex)
        {
            return staex.toString();
        }

        toClient.sendPrivateMessageFromHub(client.getNick() + " grant to you new weight.\nYour new weight is " + toClient.getWeight());
        ClientManager.getInstance().getClientBySID(ConfigurationManager.instance().getString(
                ConfigurationManager.OP_CHAT_SID)).sendPrivateMessageFromHub(
                toClient.getNick() + " get\'s new weight (" + toClient.getWeight() + ") from " + client.getNick());

        return "New weight successfully granted.";
	}


    private void sendError(String mess)
    {
        client.sendPrivateMessageFromHub(mess);
    }


    private void showHelp()
    {
        sendError(Messages.get("core.commands.grant.help_text",
                               (String)client.getExtendedField("LC")));
    }

}
