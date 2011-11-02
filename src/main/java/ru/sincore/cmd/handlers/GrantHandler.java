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
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.STAException;
import ru.sincore.client.AbstractClient;
import ru.sincore.client.Client;
import ru.sincore.cmd.AbstractCmd;
import ru.sincore.cmd.CmdUtils;
import ru.sincore.i18n.Messages;

import java.util.ArrayList;

/**
 * Command for manipulation user right weight
 *
 * @author Valor
 */
public class GrantHandler extends AbstractCmd
{
    private static final Logger log = LoggerFactory.getLogger(GrantHandler.class);
    private String marker = Marker.ANY_NON_NULL_MARKER;

    private AbstractClient  client;
    private String          cmd;
    private String          args;

    private String          nick;
    private Integer         weight;


	@Override
	public void execute(String cmd, String args, AbstractClient client)
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

        String[] argArray = CmdUtils.strArgToArray(args);

        Getopt getopt = new Getopt("grant", argArray, "n:w:t:", longOpts);

        if (argArray.length < 1)
        {
            showHelp();
            return;
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

        changedWeight();
	}


	/**
	 *  Update weight
	 */
	private void changedWeight()
	{
        if (nick == null)
        {
            sendError(Messages.get(Messages.NICK_REQUIRED, (String)client.getExtendedField("LC")));
            return;
        }

        if (weight == null)
        {
            sendError(Messages.get(Messages.WEIGHT_REQUIRED, (String)client.getExtendedField("LC")));
            return;
        }

        if (this.client.getWeight() < this.weight)
        {
            sendError(Messages.get(Messages.LOW_WEIGHT, (String)client.getExtendedField("LC")));
            return;
        }


        AbstractClient toClient = ClientManager.getInstance().getClientByNick(nick);
        if (toClient == null)
        {
            sendError(Messages.get(Messages.NICK_NOT_EXISTS,
                                   nick,
                                   (String)client.getExtendedField("LC")));
            return;
        }

        if (!toClient.isRegistred())
        {
            sendError("Client you want to grant rights is not registred user!");
            return;
        }

        if (client.getWeight() < toClient.getWeight())
        {
            sendError(Messages.get(Messages.LOW_WEIGHT, (String)client.getExtendedField("LC")));
            return;
        }

        toClient.setWeight(this.weight);
        toClient.setClientTypeByWeight(this.weight);

        try
        {
            toClient.storeInfo();
        }
        catch (STAException staex)
        {
            // ignore it
        }

        toClient.sendPrivateMessageFromHub(client.getNick() + " grant to you new weight.\nYour new weight is " + toClient.getWeight());
        ClientManager.getInstance().getClientBySID(ConfigurationManager.instance().getString(
                ConfigurationManager.OP_CHAT_SID)).sendPrivateMessageFromHub(
                toClient.getNick() + " get\'s new weight (" + toClient.getWeight() + ") from " + client.getNick());
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
