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
import ru.sincore.cmd.AbstractCmd;
import ru.sincore.cmd.CmdUtils;
import ru.sincore.util.ClientUtils;

public class KickHandler extends AbstractCmd
{

	private static final Logger log = LoggerFactory.getLogger(KickHandler.class);
	private String marker = Marker.ANY_NON_NULL_MARKER;

	private AbstractClient client;
	private String cmd;
	private String args;

	private String nick 	= null;
	private String reason	= null;

	public void execute(String cmd, String args, AbstractClient client)
	{

		this.client = client;
		this.cmd	= cmd;
		this.args	= args;

		this.nick	= null;
		this.reason	= null;

		LongOpt[] longOpts = new LongOpt[3];

		longOpts[0] = new LongOpt("nick",LongOpt.REQUIRED_ARGUMENT,null,0);

		String[] argArray = CmdUtils.strArgToArray(args);

		Getopt getopt = new Getopt("kick",argArray,":W",longOpts);

		getopt.setOpterr(true);

		int c;

		while ((c = getopt.getopt()) != -1)
		{
			switch (c)
			{
				case 0:
					this.nick = getopt.getOptarg();
					break;
				case ':':
					sendError("Ohh.. You need an argument for option"+ (char) getopt.getOptopt());
					break;
				case '?':
					sendError("The option " + (char)getopt.getOptopt() + " is not valid");
					break;
				case 'W':
					sendError("Hmmm. You tried a -W with an incorrect long option name");
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

		kick();
	}

	private void kick()
	{
		if (nick == null)
		{
			showHelp();
		}
		else
		{
			ClientUtils.kickClient(client, nick, reason);
			sendError("nick "+nick+" reason "+reason);
		}
	}

	private void showHelp()
	{

		StringBuilder sb = new StringBuilder();

		sendError("help called");
	}

	private void sendError(String mess)
	{
		client.sendPrivateMessageFromHub(mess);
	}
}
