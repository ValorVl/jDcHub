package ru.sincore.cmd.handlers;

/*
 * jDcHub ADC HubSoft
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


import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.cmd.CommandUtils;
import ru.sincore.util.ClientUtils;

public class KickCommand extends AbstractCommand
{
	private static final Logger log = LoggerFactory.getLogger(KickCommand.class);
	private String marker = Marker.ANY_NON_NULL_MARKER;

	private AbstractClient client;
	private String cmd;
	private String args;

	private String nick 	= null;
	private String reason	= null;

	@Override
    public String execute(String cmd, String args, AbstractClient client)
	{
		this.client = client;
		this.cmd	= cmd;
		this.args	= args;

		this.nick	= null;
		this.reason	= null;

		LongOpt[] longOpts = new LongOpt[3];

		longOpts[0] = new LongOpt("nick", LongOpt.REQUIRED_ARGUMENT, null, 'n');

		String[] argArray = CommandUtils.strArgToArray(args);

		Getopt getopt = new Getopt(cmd, argArray, "n:", longOpts);

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
				case 0:
					this.nick = getopt.getOptarg();
					break;

				case ':':
					sendError("Ohh.. You need an argument for option" + (char) getopt.getOptopt());
					break;

				case '?':
					sendError("The option " + (char)getopt.getOptopt() + " is not valid");
					break;

				default:
					showHelp();
					break;
			}
		}

		StringBuilder sb = new StringBuilder();

		for (int i = getopt.getOptind(); i < argArray.length; i++)
		{
			sb.append(argArray[i]);
		}

		reason = sb.toString();

		return kick();
	}


    private String kick()
	{
		if (nick == null)
		{
			showHelp();
            return null;
		}

        ClientUtils.kickOrBanClient(client, nick, 0, null, reason);
        sendError("nick " + nick + " reason " + reason);

        return "Client was kicked";
    }


    private void showHelp()
	{
		StringBuilder result = new StringBuilder();

        result.append("\nGrant new weight to user.\n");
        result.append("Usage: !grant --nick <nick> (--weight <weight> | --type <type>)\n");
        result.append("\tWhere\n");
        result.append("\t\t<nick> - user nick\n");

		sendError(result.toString());
	}


    private void sendError(String mess)
	{
		client.sendPrivateMessageFromHub(mess);
	}
}
