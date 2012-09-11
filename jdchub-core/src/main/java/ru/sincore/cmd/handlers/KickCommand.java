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
import ru.sincore.Exceptions.ClientProtectedException;
import ru.sincore.Exceptions.NotEnoughWeightException;
import ru.sincore.Exceptions.UserNotFoundException;
import ru.sincore.Exceptions.UserOfflineException;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.cmd.CommandUtils;
import ru.sincore.i18n.Messages;
import ru.sincore.util.AdcUtils;
import ru.sincore.util.ClientUtils;
import ru.sincore.util.Constants;

public class KickCommand extends AbstractCommand
{
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
        longOpts[1] = new LongOpt("reason", LongOpt.REQUIRED_ARGUMENT, null, 'r');

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

				case '?':
					showHelp();
					break;

				default:
					showHelp();
					break;
			}
		}

		return kick();
	}


    private String kick()
	{
		if (nick == null)
		{
			showHelp();
            return null;
		}

        try
        {
            if (!ClientUtils.kick(client.getNick(), nick, reason))
            {
                return "Client not kicked!";
            }
        }
        catch (Exception e)
        {
            client.sendPrivateMessageFromHub(e.toString());
            return "Client not kicked!";
        }

        ClientUtils.sendMessageToOpChat(Messages.get("core.opchat.client_kick",
                                                     new Object[]
                                                     {
                                                             nick,
                                                             client.getNick(),
                                                             reason
                                                     }));
        return "Client was kicked";
    }


    private void showHelp()
	{
		StringBuilder result = new StringBuilder();

        result.append("\nKick user from hub (equals to ban for 5 min.)\n");
        result.append("Usage: !kick --nick <nick> [--reason <reason>]\n");
        result.append("\tWhere\n");
        result.append("\t\t<nick> - user nick\n");
        result.append("\t\t<reason> - kick reason\n");

        client.sendPrivateMessageFromHub(result.toString());
	}
}
